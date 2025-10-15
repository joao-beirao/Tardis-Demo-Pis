package dcr.model.relations;


import dcr.common.relations.Relation;
import dcr.model.ModelElement;

public sealed interface RelationElement
        extends Relation, ModelElement
        permits ControlFlowRelationElement, SpawnRelationElement {}
