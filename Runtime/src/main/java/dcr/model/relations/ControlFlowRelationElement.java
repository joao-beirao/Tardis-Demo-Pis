package dcr.model.relations;


import dcr.common.relations.ControlFlowRelation;

public sealed interface ControlFlowRelationElement
        extends RelationElement, ControlFlowRelation
        permits ControlFlowElement {
}
