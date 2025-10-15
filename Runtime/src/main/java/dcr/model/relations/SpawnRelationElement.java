package dcr.model.relations;

import dcr.common.relations.SpawnRelation;
import dcr.model.GraphElement;

public sealed interface SpawnRelationElement
        extends RelationElement, SpawnRelation
        permits SpawnElement {

  @Override
  GraphElement subGraph();
}
