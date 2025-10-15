package dto;

import app.presentation.endpoint.EndpointDTO;
import app.presentation.endpoint.GraphDTO;
import app.presentation.endpoint.data.computation.ComputationExprDTO;
import app.presentation.endpoint.data.types.TypeDTO;
import app.presentation.endpoint.data.values.ValueDTO;
import app.presentation.endpoint.events.EventDTO;
import app.presentation.endpoint.events.participants.UserSetExprDTO;
import app.presentation.endpoint.relations.RelationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonEncodingTests {

    @Test
    public void givenSerializedVal_ifDeserializeAndThenSerialize_thenSameJson() throws Exception {
        var testSrc = """
                 {
                  "recordVal": {
                    "fields": [
                      { "name": "p3", "value": { "stringLit": { "value": "a_string" } } },
                      {
                        "name": "p4",
                        "value": {
                          "recordVal": {
                            "fields": [
                              {
                                "name": "p5",
                                "value": {
                                  "recordVal": {
                                    "fields": [
                                      {
                                        "name": "p1",
                                        "value": { "boolLit": { "value": true } }
                                      },
                                      { "name": "p2", "value": { "intLit": { "value": 3 } } }
                                    ]
                                  }
                                }
                              }
                            ]
                          }
                        }
                      }
                    ]
                  }
                }
                """;
        ValueDTO deserializedValueDTO =
                new ObjectMapper().readerFor(ValueDTO.class).readValue(testSrc);
        String serializedValueDTO = new ObjectMapper().writerFor(ValueDTO.class)
                .writeValueAsString(deserializedValueDTO);
        {
            ObjectMapper objectMapper = new ObjectMapper();
            assertEquals(objectMapper.readTree(testSrc), objectMapper.readTree(serializedValueDTO));
        }
    }

    @Test
    public void givenSerializedNoValueMarking_ifDeserializeAndThenSerialize_thenSameJson()
            throws Exception {
        var testSrc = """
                { "isPending": false, "isIncluded": true }
                """;
        EventDTO.MarkingDTO deserializedMarkingDTO =
                new ObjectMapper().readerFor(EventDTO.MarkingDTO.class).readValue(testSrc);
        String serializedMarkingDTO = new ObjectMapper().writerFor(EventDTO.MarkingDTO.class)
                .writeValueAsString(deserializedMarkingDTO);
        {
            ObjectMapper objectMapper = new ObjectMapper();
            assertEquals(objectMapper.readTree(testSrc),
                    objectMapper.readTree(serializedMarkingDTO));
        }
    }

    @Test
    public void givenSerializedIntValueMarking_ifDeserializeAndThenSerialize_thenSameJson()
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        var testSrc = """
                 {
                   "isPending": false,
                   "isIncluded": true,
                   "defaultValue": { "intLit": { "value": 3 } }
                 }
                """;
        EventDTO.MarkingDTO deserializedMarkingDTO =
                objectMapper.readValue(testSrc, EventDTO.MarkingDTO.class);
        String serializedMarkingDTO = objectMapper.writeValueAsString(deserializedMarkingDTO);
        {
            assertEquals(objectMapper.readTree(testSrc),
                    objectMapper.readTree(serializedMarkingDTO));
        }
    }

    @Test
    public void givenFlatSerializedTypeExpr_ifDeserializeAndThenSerialize_thenSameJson()
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        var testSrc = """
                { "valueType": "int" }
                """;
        TypeDTO deserializedTypeDTO = objectMapper.readValue(testSrc, TypeDTO.class);
        String serializedTypeDTO = objectMapper.writeValueAsString(deserializedTypeDTO);
        {
            assertEquals(objectMapper.readTree(testSrc), objectMapper.readTree(serializedTypeDTO));
        }
    }

    @Test
    public void givenNestedSerializedTypeExpr_ifDeserializeAndThenSerialize_thenSameJson()
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        var testSrc = """
                 {
                   "recordType": {
                     "fields": [
                       {
                         "name": "p3",
                         "type": {
                           "recordType": {
                             "fields": [
                               { "name": "p2", "type": { "valueType": "bool" } },
                               { "name": "p1", "type": { "valueType": "string" } }
                             ]
                           }
                         }
                       },
                       { "name": "pp4", "type": { "eventType": { "label": "E0" } } }
                     ]
                   }
                 }
                """;
        TypeDTO deserializedTypeDTO = objectMapper.readValue(testSrc, TypeDTO.class);
        String serializedTypeDTO = objectMapper.writeValueAsString(deserializedTypeDTO);
        {
            assertEquals(objectMapper.readTree(testSrc), objectMapper.readTree(serializedTypeDTO));
        }
    }

    @Test
    public void givenNestedSerializedExpr_ifDeserializeAndThenSerialize_thenSameJson()
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        var testSrc = """
                {
                  "record": {
                    "fields": [
                      { "name": "p3", "value": { "boolLit": { "value": true } } },
                      {
                        "name": "p4",
                        "value": {
                          "record": {
                            "fields": [
                              { "name": "p1", "value": { "boolLit": { "value": true } } },
                              { "name": "p2", "value": { "eventRef": { "value": "E0" } } }
                            ]
                          }
                        }
                      },
                      {
                        "name": "p5",
                        "value": {
                          "propDeref": {
                            "propBasedExpr": {
                              "propDeref": {
                                "propBasedExpr": { "eventRef": { "value": "E0" } },
                                "prop": "value"
                              }
                            },
                            "prop": "cid"
                          }
                        }
                      },
                      {
                        "name": "p6",
                        "value": {
                          "binaryOp": {
                            "expr1": { "intLit": { "value": 2 } },
                            "expr2": { "intLit": { "value": 3 } },
                            "op": "intAdd"
                          }
                        }
                      }
                    ]
                  }
                }
                """;
        ComputationExprDTO deserializedExprDTO =
                objectMapper.readValue(testSrc, ComputationExprDTO.class);
        String serializedExprDTO = objectMapper.writeValueAsString(deserializedExprDTO);
        {
            assertEquals(objectMapper.readTree(testSrc), objectMapper.readTree(serializedExprDTO));
        }
    }

    @Test
    public void givenSerializedUserSetExpr_ifDeserializeAndThenSerialize_thenSameJson()
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        {
            var testSrc = """
                    {
                         "roleExpr": {
                           "roleLabel": "P",
                           "params": [
                             { "name": "p1" },
                             { "name": "p2", "value": { "intLit": { "value": 2 } } }
                           ]
                         }
                       }
                    """;
            UserSetExprDTO deserializedUserSetExprDTO =
                    objectMapper.readValue(testSrc, UserSetExprDTO.class);
            String serializedUserSetExprDTO =
                    objectMapper.writeValueAsString(deserializedUserSetExprDTO);

            assertEquals(objectMapper.readTree(testSrc),
                    objectMapper.readTree(serializedUserSetExprDTO));
        }
        {
            var testSrc = """
                    {
                        "receiverExpr": {
                            "eventId": "e0"
                        }
                    }
                    """;
            UserSetExprDTO deserializedUserSetExprDTO =
                    objectMapper.readValue(testSrc, UserSetExprDTO.class);
            String serializedUserSetExprDTO =
                    objectMapper.writeValueAsString(deserializedUserSetExprDTO);

            assertEquals(objectMapper.readTree(testSrc),
                    objectMapper.readTree(serializedUserSetExprDTO));
        }
    }


    @Test
    public void givenComputationEvent_ifDeserializeAndThenSerialize_thenSameJson()
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        {
            var testSrc = """
                    {
                         "computationEvent": {
                           "common": {
                             "choreoElementUID": "e0_0_tx",
                             "endpointElementUID": "e0_0_tx",
                             "id": "e0",
                             "label": "e0",
                             "dataType": { "valueType": "bool" },
                             "marking": {
                               "isPending": false,
                               "isIncluded": true,
                               "defaultValue": { "boolLit": { "value": true } }
                             },
                             "ifcConstraint": { "boolLit": { "value": true } }
                           },
                           "dataExpr": { "boolLit": { "value": true } },
                           "receivers": [
                             {
                               "roleExpr": {
                                 "roleLabel": "P",
                                 "params": [
                                   { "name": "p1" },
                                   { "name": "p2", "value": { "intLit": { "value": 2 } } }
                                 ]
                               }
                             }
                           ]
                         }
                       }
                    """;
            EventDTO deserializedEventDTO = objectMapper.readValue(testSrc, EventDTO.class);
            String serializedUserEventDTO = objectMapper.writeValueAsString(deserializedEventDTO);
            assertEquals(objectMapper.readTree(testSrc),
                    objectMapper.readTree(serializedUserEventDTO));
        }
        {
            var testSrc = """
                    {
                       "inputEvent": {
                         "common": {
                           "choreoElementUID": "e0_0_tx",
                           "endpointElementUID": "e0_0_tx",
                           "id": "e0",
                           "label": "e0",
                           "dataType": { "valueType": "bool" },
                           "marking": {
                             "isPending": false,
                             "isIncluded": true,
                             "defaultValue": { "boolLit": { "value": true } }
                           },
                           "instantiationConstraint": { "boolLit": { "value": true } },
                           "ifcConstraint": { "boolLit": { "value": true } }
                         },
                         "receivers": [
                           {
                             "roleExpr": {
                               "roleLabel": "P",
                               "params": [
                                 { "name": "p1" },
                                 { "name": "p2", "value": { "intLit": { "value": 2 } } }
                               ]
                             }
                           }
                         ]
                       }
                     }
                    """;
            EventDTO deserializedEventDTO = objectMapper.readValue(testSrc, EventDTO.class);
            String serializedUserEventDTO = objectMapper.writeValueAsString(deserializedEventDTO);
            assertEquals(objectMapper.readTree(testSrc),
                    objectMapper.readTree(serializedUserEventDTO));
        }

        {
            var testSrc = """
                     {
                         "receiveEvent": {
                           "common": {
                             "choreoElementUID": "e0_0_tx",
                             "endpointElementUID": "e0_0_tx",
                             "id": "e0",
                             "label": "e0",
                             "dataType": { "valueType": "bool" },
                             "marking": {
                               "isPending": false,
                               "isIncluded": true,
                               "defaultValue": { "boolLit": { "value": true } }
                             },
                             "instantiationConstraint": { "boolLit": { "value": true } }
                           },
                           "initiators": [
                               {
                                 "roleExpr": {
                                   "roleLabel": "P",
                                   "params": [
                                     { "name": "p1" },
                                     { "name": "p2", "value": { "intLit": { "value": 2 } } }
                                   ]
                                 }
                               }
                           ]
                         }
                    }
                    """;
            EventDTO deserializedEventDTO = objectMapper.readValue(testSrc, EventDTO.class);
            String serializedUserEventDTO = objectMapper.writeValueAsString(deserializedEventDTO);
            assertEquals(objectMapper.readTree(testSrc),
                    objectMapper.readTree(serializedUserEventDTO));
        }
    }


    @Test
    public void givenSomeRelation_ifDeserializeAndThenSerialize_thenSameJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        {
            var testSrc = """
                     {
                      "spawnRelation": {
                        "relationCommon": { "endpointElementUID": "1", "sourceId": "e0_0_Rx" },
                        "triggerId": "_@trigger$e0",
                        "graph": {
                          "events": [
                            {
                              "inputEvent": {
                                "common": {
                                  "choreoElementUID": "e0_0_tx",
                                  "endpointElementUID": "e0_0_tx",
                                  "id": "e1_1_TxO",
                                  "label": "E1",
                                  "dataType": { "valueType": "void" },
                                  "marking": { "isPending": false, "isIncluded": true }
                                },
                                "receivers": [ { "initiatorExpr": { "eventId": "e0" } } ]
                              }
                            }
                          ]
                          }
                        }
                      }
                    }
                    """;
            RelationDTO deserializedDTO = objectMapper.readValue(testSrc, RelationDTO.class);
            String serializedDTO = objectMapper.writeValueAsString(deserializedDTO);
            assertEquals(objectMapper.readTree(testSrc), objectMapper.readTree(serializedDTO));
        }

    }


    @Test
    public void givenSomeGraph_ifDeserializeAndThenSerialize_thenSameJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        {
            var testSrc = """
                      {
                      "events": [
                        {
                          "inputEvent": {
                            "common": {
                              "choreoElementUID": "e0_0_Tx",
                              "endpointElementUID": "e0_0_Tx",
                              "id": "e0_0_Tx",
                              "label": "E0",
                              "dataType": { "valueType": "void" },
                              "marking": { "isPending": false, "isIncluded": true }
                            },
                            "receivers": [
                              {
                                "roleExpr": {
                                  "roleLabel": "P",
                                  "params": [
                                    {
                                      "name": "cid",
                                      "value": {
                                        "propDeref": {
                                          "propBasedExpr": {
                                            "propDeref": {
                                              "propBasedExpr": {
                                                "eventRef": { "value": "_@self" }
                                              },
                                              "prop": "params"
                                            }
                                          },
                                          "prop": "cid"
                                        }
                                      }
                                    },
                                    { "name": "pid" }
                                  ]
                                }
                              }
                            ]
                          }
                        },
                        {
                          "receiveEvent": {
                            "common": {
                            "choreoElementUID": "e0_0_Rx",
                            "endpointElementUID": "e0_0_Rx",
                              "id": "e0_0_Rx",
                              "label": "E0",
                              "dataType": { "valueType": "void" },
                              "marking": { "isPending": false, "isIncluded": true }
                            },
                            "initiators": [
                              {
                                "roleExpr": {
                                  "roleLabel": "P",
                                  "params": [
                                    {
                                      "name": "cid",
                                      "value": {
                                        "propDeref": {
                                          "propBasedExpr": {
                                            "propDeref": {
                                              "propBasedExpr": {
                                                "eventRef": { "value": "_@self" }
                                              },
                                              "prop": "params"
                                            }
                                          },
                                          "prop": "cid"
                                        }
                                      }
                                    },
                                    { "name": "pid" }
                                  ]
                                }
                              }
                            ]
                          }
                        }
                      ],
                      "relations": [
                        {
                          "spawnRelation": {
                            "relationCommon": { "endpointElementUID": "1", "sourceId": "e0_0_Rx" },
                            "triggerId": "_@trigger$e0",
                            "graph": {
                              "events": [
                                {
                                  "inputEvent": {
                                    "common": {
                                    "choreoElementUID": "e1_1_TxO",
                                    "endpointElementUID": "e1_1_TxO",
                                      "id": "e1_1_TxO",
                                      "label": "E1",
                                      "dataType": { "valueType": "void" },
                                      "marking": { "isPending": false, "isIncluded": true }
                                    },
                                    "receivers": [ { "initiatorExpr": { "eventId": "e0" } } ]
                                  }
                                }
                              ]
                            }
                          }
                        },
                        {
                          "spawnRelation": {
                            "relationCommon": { "endpointElementUID": "1", "sourceId": "e0_0_Tx" },
                            "triggerId": "_@trigger$e0",
                            "graph": {
                              "events": [
                                {
                                  "receiveEvent": {
                                    "common": {
                                    "choreoElementUID": "e1_1_RxO",
                                    "endpointElementUID": "e1_1_RxO",
                                      "id": "e1_1_RxO",
                                      "label": "E1",
                                      "dataType": { "valueType": "void" },
                                      "marking": { "isPending": false, "isIncluded": true }
                                    },
                                    "initiators": [
                                      {
                                        "roleExpr": {
                                          "roleLabel": "P",
                                          "params": [
                                            {
                                              "name": "cid",
                                              "value": {
                                                "propDeref": {
                                                  "propBasedExpr": {
                                                    "propDeref": {
                                                      "propBasedExpr": {
                                                        "eventRef": { "value": "_@trigger$e0" }
                                                      },
                                                      "prop": "initiator"
                                                    }
                                                  },
                                                  "prop": "cid"
                                                }
                                              }
                                            },
                                            { "name": "pid" }
                                          ]
                                        }
                                      }
                                    ]
                                  }
                                }
                              ]
                            }
                          }
                        }
                      ]
                    }
                    """;
            GraphDTO deserializedDTO = objectMapper.readValue(testSrc, GraphDTO.class);
            String serializedDTO = objectMapper.writeValueAsString(deserializedDTO);
            assertEquals(objectMapper.readTree(testSrc), objectMapper.readTree(serializedDTO));
        }
    }

    @Test
    public void givenAnEndpoint_ifDeserializeAndThenSerialize_thenSameJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        {
            var testSrc = """
                      {
                        "role": {
                          "label": "P",
                          "params": [
                            { "name": "cid", "type": { "valueType": "int" } },
                            { "name": "pid", "type": { "valueType": "int" } }
                          ]
                        },
                        "graph": {
                          "events": [
                            {
                              "inputEvent": {
                                "common": {
                                  "choreoElementUID": "e0_0_Tx",
                                  "endpointElementUID": "e0_0_Tx",
                                  "id": "e0_0_Tx",
                                  "label": "E0",
                                  "dataType": { "valueType": "void" },
                                  "marking": { "isPending": false, "isIncluded": true }
                                },
                                "receivers": [
                                  {
                                    "roleExpr": {
                                      "roleLabel": "P",
                                      "params": [
                                        {
                                          "name": "cid",
                                          "value": {
                                            "propDeref": {
                                              "propBasedExpr": {
                                                "propDeref": {
                                                  "propBasedExpr": {
                                                    "eventRef": { "value": "_@self" }
                                                  },
                                                  "prop": "params"
                                                }
                                              },
                                              "prop": "cid"
                                            }
                                          }
                                        },
                                        { "name": "pid" }
                                      ]
                                    }
                                  }
                                ]
                              }
                            },
                            {
                              "receiveEvent": {
                                "common": {
                                  "choreoElementUID": "e0_0_Rx",
                                  "endpointElementUID": "e0_0_Rx",
                                  "id": "e0_0_Rx",
                                  "label": "E0",
                                  "dataType": { "valueType": "void" },
                                  "marking": { "isPending": false, "isIncluded": true }
                                },
                                "initiators": [
                                  {
                                    "roleExpr": {
                                      "roleLabel": "P",
                                      "params": [
                                        {
                                          "name": "cid",
                                          "value": {
                                            "propDeref": {
                                              "propBasedExpr": {
                                                "propDeref": {
                                                  "propBasedExpr": {
                                                    "eventRef": { "value": "_@self" }
                                                  },
                                                  "prop": "params"
                                                }
                                              },
                                              "prop": "cid"
                                            }
                                          }
                                        },
                                        { "name": "pid" }
                                      ]
                                    }
                                  }
                                ]
                              }
                            }
                          ],
                          "relations": [
                            {
                              "spawnRelation": {
                                "relationCommon": { "endpointElementUID": "1", "sourceId": "e0_0_Rx" },
                                "triggerId": "_@trigger$e0",
                                "graph": {
                                  "events": [
                                    {
                                      "inputEvent": {
                                        "common": {
                                          "choreoElementUID": "e1_1_TxO",
                                          "endpointElementUID": "e1_1_TxO",
                                          "id": "e1_1_TxO",
                                          "label": "E1",
                                          "dataType": { "valueType": "void" },
                                          "marking": { "isPending": false, "isIncluded": true }
                                        },
                                        "receivers": [ { "initiatorExpr": { "eventId": "e0" } } ]
                                      }
                                    }
                                  ]
                                }
                              }
                            },
                            {
                              "spawnRelation": {
                                "relationCommon": { "endpointElementUID": "1", "sourceId": "e0_0_Tx" },
                                "triggerId": "_@trigger$e0",
                                "graph": {
                                  "events": [
                                    {
                                      "receiveEvent": {
                                        "common": {
                                          "choreoElementUID": "e1_1_RxO",
                                          "endpointElementUID": "e1_1_RxO",
                                          "id": "e1_1_RxO",
                                          "label": "E1",
                                          "dataType": { "valueType": "void" },
                                          "marking": { "isPending": false, "isIncluded": true }
                                        },
                                        "initiators": [
                                          {
                                            "roleExpr": {
                                              "roleLabel": "P",
                                              "params": [
                                                {
                                                  "name": "cid",
                                                  "value": {
                                                    "propDeref": {
                                                      "propBasedExpr": {
                                                        "propDeref": {
                                                          "propBasedExpr": {
                                                            "eventRef": { "value": "_@trigger$e0" }
                                                          },
                                                          "prop": "initiator"
                                                        }
                                                      },
                                                      "prop": "cid"
                                                    }
                                                  }
                                                },
                                                { "name": "pid" }
                                              ]
                                            }
                                          }
                                        ]
                                      }
                                    }
                                  ]
                                }
                              }
                            }
                          ]
                        }
                      }
                    """;
            EndpointDTO deserializedDTO = objectMapper.readValue(testSrc, EndpointDTO.class);
            String serializedDTO = objectMapper.writeValueAsString(deserializedDTO);
            assertEquals(objectMapper.readTree(testSrc), objectMapper.readTree(serializedDTO));
        }
    }


}

