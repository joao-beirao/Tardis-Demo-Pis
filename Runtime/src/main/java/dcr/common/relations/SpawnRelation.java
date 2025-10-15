package dcr.common.relations;

import dcr.common.DCRGraph;

public interface SpawnRelation extends Relation{
  String triggerId();
  DCRGraph subGraph();
}