package dcr.runtime;

import dcr.common.data.computation.ComputationExpression;
import dcr.model.GraphElement;
import dcr.model.relations.*;
import dcr.runtime.elements.relations.ControlFlowRelationInstance;
import dcr.runtime.elements.relations.RelationInstance;
import dcr.runtime.elements.relations.SpawnRelationInstance;

class Relations {
    static InstantiatedControlFlowRelation newControlFlowRelation(
            ControlFlowRelationElement baseElement, GenericEventInstance source,
            GenericEventInstance target) {
        return new InstantiatedControlFlowRelation(baseElement, source, target);
    }

    static InstantiatedSpawnRelation newSpawnRelationInstance(SpawnRelationElement baseElement,
            GenericEventInstance source) {
        return new InstantiatedSpawnRelation(baseElement, source);
    }
}

abstract class GenericRelationInstance
        implements RelationInstance {

    // TODO [revise] storing baseElement here - it seems we have to map everything but the relation
    //  type; then again, we are still missing the guard, which might be something to eval
    private final RelationElement baseElement;
    private final GenericEventInstance source;


    GenericRelationInstance(RelationElement baseElement, GenericEventInstance source) {
        this.baseElement = baseElement;
        this.source = source;
    }

    @Override
    public String sourceId() {
        return source.localUID();
    }

    @Override
    public ComputationExpression guard() {
        return baseElement.guard();
    }

    @Override
    public GenericEventInstance getSource() {
        return source;
    }

    @Override
    public RelationElement baseElement() {
        return baseElement;
    }
}

class InstantiatedSpawnRelation
        extends GenericRelationInstance
        implements SpawnRelationInstance {

    InstantiatedSpawnRelation(SpawnRelationElement baseElement, GenericEventInstance source) {
        super(baseElement, source);
    }

    @Override
    public String triggerId() {
        return ((SpawnRelationElement) baseElement()).triggerId();
    }

    @Override
    public GraphElement subGraph() {
        return ((SpawnRelationElement) baseElement()).subGraph();
    }

    // TODO [not yet implemented]
    @Override
    public String toString() {
        return super.toString();
    }

    // TODO [not yet implemented]
    public String unparse(String indentation) {
        return super.toString();
    }
}

final class InstantiatedControlFlowRelation
        extends GenericRelationInstance
        implements ControlFlowRelationInstance {

    private final GenericEventInstance target;

    InstantiatedControlFlowRelation(ControlFlowRelationElement baseElement,
            GenericEventInstance source, GenericEventInstance target) {
        super(baseElement, source);
        this.target = target;
    }

    @Override
    public String targetId() {
        return target.localUID();
    }

    @Override
    public GenericEventInstance getTarget() {
        return target;
    }

    @Override
    public ControlFlowRelationElement.Type relationType() {
        return ((ControlFlowRelationElement) baseElement()).relationType();
    }

    // FIXME
    // TODO use relation type label value to get a one liner
    @Override
    public String toString() {
        return unparse("");
    }

    //
    public String unparse(String indentation) {
        return switch (relationType()) {
            case INCLUDE ->
                    String.format("%s%s -->+ %s", indentation, sourceId(), targetId());
            case EXCLUDE ->
                    String.format("%s%s -->%% %s", indentation, sourceId(), targetId());
            case RESPONSE ->
                    String.format("%s%s *--> %s", indentation, sourceId(), targetId());
            case CONDITION ->
                    String.format("%s%s -->* %s", indentation, sourceId(), targetId());
            case MILESTONE ->
                    String.format("%s%s --><> %s", indentation, sourceId(), targetId());
        };
    }


}