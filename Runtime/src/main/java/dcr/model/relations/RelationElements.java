package dcr.model.relations;

import dcr.common.data.computation.BoolLiteral;
import dcr.common.data.computation.ComputationExpression;
import dcr.common.relations.ControlFlowRelation;
import dcr.model.GraphElement;
import dcr.model.RecursiveGraphElement;
import org.apache.commons.lang3.NotImplementedException;

// TODO [revise]
public final class RelationElements {


    private static final ComputationExpression UNCONSTRAINED = BoolLiteral.TRUE;

    public static SpawnRelationElement newSpawnRelation(String elementId, String sourceId,
            String triggerId, ComputationExpression guard, RecursiveGraphElement model,
            ComputationExpression instantiationConstraint) {
        return new SpawnElement(elementId, sourceId, triggerId, guard, model,
                instantiationConstraint);
    }

    public static ControlFlowRelationElement newControlFlowRelation(String elementId,
            String sourceId, String targetId, ComputationExpression guard,
            ControlFlowRelation.Type relationType, ComputationExpression instantiationConstraint) {
        return new ControlFlowElement(elementId, sourceId, guard, targetId, relationType,
                instantiationConstraint);
    }
}

record ControlFlowElement(String endpointElementUID, String sourceId, ComputationExpression guard,
                          String targetId, Type relationType,
                          ComputationExpression instantiationConstraint)
        implements ControlFlowRelationElement {
    @Override
    public String unparse() {
        throw new NotImplementedException("unparse(): not yet implemented");
    }
}

record SpawnElement(String endpointElementUID, String sourceId, String triggerId,
                    ComputationExpression guard, GraphElement subGraph,
                    ComputationExpression instantiationConstraint)
        implements SpawnRelationElement {

    // TODO
    @Override
    public String unparse() {
        throw new NotImplementedException("unparse(): not yet implemented");
    }
}