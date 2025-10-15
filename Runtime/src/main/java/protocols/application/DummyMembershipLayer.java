package protocols.application;

import dcr.common.Record;
import dcr.common.data.values.IntVal;
import dcr.common.data.values.StringVal;
import dcr.common.events.userset.values.*;
import dcr.runtime.communication.MembershipLayer;
import org.apache.commons.lang3.NotImplementedException;
import pt.unl.fct.di.novasys.babel.protocols.membership.VersionedPeer;
import pt.unl.fct.di.novasys.network.data.Host;

import java.util.*;
import java.util.stream.Collectors;

// TODO [replace with actual membership protocol]
// Simulates a membership layer: currently consisting of hardcoded participants
// (! forces adjustment according to the use case)
public final class DummyMembershipLayer
        implements MembershipLayer {

    public record DummyNeighbour(UserVal user, String hostName)
            implements Neighbour {
        @Override
        public Host host() {
            throw new NotImplementedException("host() not yet implemented");
        }
    }

    private static final DummyMembershipLayer singleton = new DummyMembershipLayer();

    static {
        // USE CASE EDP V1 - single parameter 'id' (single community)
        // singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
        //         Record.ofEntries(Record.Field.of("id", StringVal.of("1")))), "P_1"));
        // singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
        //         Record.ofEntries(Record.Field.of("id", StringVal.of("2")))), "P_2"));
        // singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
        //         Record.ofEntries(Record.Field.of("id", StringVal.of("3")))), "P_3"));
        // singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
        //         Record.ofEntries(Record.Field.of("id", StringVal.of("4")))), "P_4"));

        // USE CASE EDP V3 - (id, cid) parameters (multiple communities)
        // EC 1
        singleton.onNeighborUp(new DummyNeighbour(
                UserVal.of("CO", Record.ofEntries(Record.Field.of("cid", IntVal.of(1)))), "co-1"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("1")),
                        Record.Field.of("cid", IntVal.of(1)))), "p-1-1"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("2")),
                        Record.Field.of("cid", IntVal.of(1)))), "p-2-1"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("3")),
                        Record.Field.of("cid", IntVal.of(1)))), "p-3-1"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("4")),
                        Record.Field.of("cid", IntVal.of(1)))), "p-4-1"));
        // EC 2
        singleton.onNeighborUp(new DummyNeighbour(
                UserVal.of("CO", Record.ofEntries(Record.Field.of("cid", IntVal.of(2)))), "co-2"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("1")),
                        Record.Field.of("cid", IntVal.of(2)))), "p-1-2"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("2")),
                        Record.Field.of("cid", IntVal.of(2)))), "p-2-2"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("3")),
                        Record.Field.of("cid", IntVal.of(2)))), "p-3-2"));
        singleton.onNeighborUp(new DummyNeighbour(UserVal.of("P",
                Record.ofEntries(Record.Field.of("id", StringVal.of("4")),
                        Record.Field.of("cid", IntVal.of(2)))), "p-4-2"));
    }

    private final Map<UserVal, Neighbour> neighbourMapping;
    private final Map<String, Set<Neighbour>> neighboursByRole;

    public static DummyMembershipLayer instance() {
        return singleton;
    }

    private static <K, V> void putIfAbsent(Map<K, Set<V>> mapping, K key, V value) {
        Optional.ofNullable(mapping.get(key)).ifPresentOrElse(set -> set.add(value), () -> {
            Set<V> set = new HashSet<>();
            set.add(value);
            mapping.put(key, set);
        });
    }

    private DummyMembershipLayer() {
        neighbourMapping = new HashMap<>();
        neighboursByRole = new HashMap<>();
    }

    @Override
    public void onNeighborUp(Neighbour neighbourUp) {
        if (neighbourMapping.putIfAbsent(neighbourUp.user(), neighbourUp) == null) {
            putIfAbsent(neighboursByRole, neighbourUp.role(), neighbourUp);
        }
    }

    @Override
    public void onNeighborDown(Neighbour neighbourDown) {
        Optional.ofNullable(neighbourMapping.remove(neighbourDown.user()))
                .ifPresent(dummyNeighbour -> neighboursByRole.get(dummyNeighbour.role())
                        .remove(dummyNeighbour));
    }

    public Set<Neighbour> resolveParticipants(UserSetVal receivers) {
        Set<Neighbour> evalResult = new HashSet<>();
        switch (receivers) {
            case UserVal user -> {
                for (var key : neighbourMapping.keySet())
                    Optional.ofNullable(neighbourMapping.get(user)).ifPresent(evalResult::add);
            }
            // TODO subsequent filter according to params
            case RoleVal role -> {
                var candidates = neighboursByRole.getOrDefault(role.role(), Collections.emptySet());
                for (var param : role.params()) {
                    candidates = candidates.stream()
                            .filter(u -> param.value()
                                    .equals(u.user()
                                            .getParamsAsRecordVal()
                                            .fetchProp(param.name())))
                            .collect(Collectors.toSet());
                }
                evalResult.addAll(candidates);
            }
            case SetDiffVal setDiff -> {
                var positiveSet = new HashSet<>(resolveParticipants(setDiff.positiveSet()));
                var negativeSet = new HashSet<>(resolveParticipants(setDiff.negativeSet()));
                positiveSet.removeAll(negativeSet);
                evalResult.addAll(positiveSet);
            }
            case SetUnionVal unionSet -> unionSet.userSetVals()
                    .forEach(expr -> evalResult.addAll(resolveParticipants(expr)));
        }
        return evalResult;
    }

//    private Set<UserVal> dummySend(UserSetVal receivers) {
//        var evalResult = resolveParticipants(receivers);
//        // TODO [not yet implemented] actual send (a DCR Protocol callback?)
//        return evalResult.stream().map(Neighbour::user).collect(Collectors.toUnmodifiableSet());
//    }
}
