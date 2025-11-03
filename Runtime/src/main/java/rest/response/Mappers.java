package rest.response;

import dcr.common.Record;
import dcr.common.data.types.*;
import dcr.common.data.values.*;
import dcr.common.events.Event;
import dcr.common.events.userset.values.*;
import dcr.runtime.elements.events.ComputationEventInstance;
import dcr.runtime.elements.events.EventInstance;
import dcr.runtime.elements.events.InputEventInstance;
import dcr.runtime.elements.events.ReceiveEventInstance;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Mappers {

    public static EndpointDTO fromEndpoint(UserVal user, List<EventInstance> events) {
        return new EndpointDTO(fromUserVal(user),
                events.stream().map(Mappers::toEventDTO).toList());
    }

    public static UserDTO fromUserVal(UserVal user) {
        return new UserDTO(user.role(), user.params().fields().stream().collect(
                Collectors.toMap(Record.Field::name, f -> fromValue(f.value()))));
    }

    public static TypeDTO fromType(Type type) {
        return switch (type) {
            case VoidType ignored -> new UnitTypeDTO();
            case BooleanType ignored -> new BooleanTypeDTO();
            case IntegerType ignored -> new IntegerTypeDTO();
            case StringType ignored -> new StringTypeDTO();
            case RecordType ty -> new RecordTypeDTO(ty.fields().stream().collect(
                    Collectors.toMap(Record.Field::name, f -> fromType(f.value()))));
            default -> throw new NotImplementedException("Unsupported type: " + type);
        };
    }

    public static Value toValue(ValueDTO dto) {
        return switch (dto) {
            case UnitDTO ignored -> VoidVal.instance();
            case BooleanDTO v -> BoolVal.of(v.value());
            case IntDTO v -> IntVal.of(v.value());
            case StringDTO v -> StringVal.of(v.value());
            case RecordDTO v -> RecordVal.of(Record.ofEntries(
                    v.value().entrySet().stream().collect(
                            Collectors.toMap(Map.Entry::getKey,
                                    e -> toValue(e.getValue())))));
        };
    }

    public static ValueDTO fromValue(Value v) {
        return switch (v) {
            case BoolVal val -> new BooleanDTO(val.value());
            case IntVal val -> new IntDTO(val.value());
            case StringVal val -> new StringDTO(val.value());
            case UndefinedVal<?> ignored -> new UnitDTO("");
            case VoidVal ignored -> new UnitDTO("");
            case RecordVal val -> new RecordDTO(val.fields().stream().collect(
                    Collectors.toMap(Record.Field::name, f -> fromValue(f.value()))));
            case EventVal ignored ->
                    throw new NotImplementedException("EventVal not implemented");
        };
    }


    public static UserSetValDTO toUserSetValDTO(UserSetVal userSetVal) {
        return switch (userSetVal) {
            case RoleVal val -> {
                var roleValDTO = new RoleValDTO(val.role(), val.params().stream().collect(
                        Collectors.toMap(Record.Field::name, f -> fromValue(f.value()))),
                        val.unconstrainedParams());
                yield new UserSetValDTO(List.of(roleValDTO));
            }
            case SetDiffVal setDiffVal ->
                    throw new NotImplementedException("SetDiffVal not implemented");
            case SetUnionVal val ->
                    val.userSetVals().stream().map(Mappers::toUserSetValDTO)
                            .reduce(new UserSetValDTO(List.of()), (acc, elem) -> {
                                var base = new ArrayList<>(acc.userVals());
                                base.addAll(elem.userVals());
                                return new UserSetValDTO(base);
                            });
            case UserVal val -> new UserSetValDTO(List.of(new RoleValDTO(val.role(),
                    val.params().stream().collect(Collectors.toMap(Record.Field::name,
                            f -> fromValue(f.value()))), Collections.emptySet())));
        };
    }

    public static EventDTO toEventDTO(EventInstance e) {
        var id = e.remoteID();
        var label = e.label();
        var typeExpr = fromType(e.baseElement().valueType());
        var marking = fromMarking(e.marking());
        var timestamp = e.creationTimestamp();
        return switch (e) {
            case ComputationEventInstance e1 -> EventDTO.newComputationEventDTO(id, label,
                    toUserSetValDTO(e1.receiverUsers()), typeExpr,
                    e1.receivers().isPresent() ? KindDTO.COMPUTATION_SEND :
                            KindDTO.COMPUTATION, marking, timestamp);
            case InputEventInstance e2 -> EventDTO.newInputEventDTO(id, label,
                    toUserSetValDTO(e2.receiverUsers()), typeExpr,
                    e2.receivers().isPresent() ? KindDTO.INPUT_SEND : KindDTO.INPUT,
                    marking, timestamp);
            case ReceiveEventInstance e3 -> EventDTO.newReceiveEventDTO(id, label,
                    toUserSetValDTO(e3.initiatorUsers()), typeExpr, marking, timestamp);
            default -> throw new IllegalStateException("Unexpected value: " + e);
        };
    }

    static MarkingDTO fromMarking(Event.Marking marking) {
        return new MarkingDTO(marking.hasExecuted(), marking.isPending(),
                marking.isIncluded(), fromValue(marking.value()));
    }
}
