package app.presentation.endpoint.data.values;


import app.presentation.endpoint.data.computation.IntLiteralDTO;
import app.presentation.endpoint.data.types.ValueTypeDTO;
import app.presentation.endpoint.events.ComputationEventDTO;
import app.presentation.endpoint.events.EventDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import java.util.Collections;
import java.util.Optional;

public class Tester {

    public static String formatJson(String input) {
        return input.replaceAll("'", "\"");
    }


    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());

        {
            var testSrc = """
                        {
                              "computationEvent" : {
                                "common" : {
                                  "choreoElementUID" : "choreoElementUID",
                                  "endpointElementUID" : "endpointElementUID",
                                  "id" : "id",
                                  "eventType" : {},
                                  "dataType" : {
                                    "valueType" : "string"
                                  },
                                  "marking" : {
                                    "isIncluded" : true,
                                    "isPending" : true
                                  }
                                },
                                "dataExpr" : {
                                  "intLit" : {
                                    "value" : 3
                                  }
                                }
                              }
                            }
                    """;
            try {

                EventDTO eventDTO = new ComputationEventDTO(
                        new EventDTO.Common("choreoElementUID", "endpointElementUID", "id", "E0",
                                ValueTypeDTO.STRING,
                                new EventDTO.MarkingDTO(true, true, Optional.empty()),
                                Optional.empty(), Optional.empty()), new IntLiteralDTO(3),
                        Collections.emptyList());


                // EventDTO deserializedEventDTO = objectMapper.readValue(testSrc, EventDTO.class);


                String serializedUserEventDTO =
                        objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
                                .writeValueAsString(eventDTO);

                System.err.println(serializedUserEventDTO);


            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        //
        // // ==== Marking test =====
        //
        //
        // IntValDTO intVal = new IntValDTO(3);
        //
        // // Map<String, ValueDTO> fields = new HashMap<>();
        // // fields.put("pid", new IntValDTO(3));
        // // fields.put("id", new StringValDTO("a_string"));
        //
        // List<RecordValDTO.FieldDTO> fields =
        //         List.of(new RecordValDTO.FieldDTO("pid", new IntValDTO(3)));
        //
        // RecordValDTO recVal = new RecordValDTO(fields);
        //
        //
        // RecordValDTO recordVal;
        // {
        //     var requesterFields = List.of(new RecordValDTO.FieldDTO("id", new StringValDTO
        //     ("#p4")),
        //             new RecordValDTO.FieldDTO("cid", new IntValDTO(5)),
        //             new RecordValDTO.FieldDTO("event", new BoolValDTO(false)));
        //     recordVal = new RecordValDTO(
        //             List.of(new RecordValDTO.FieldDTO("request_id", new StringValDTO
        //             ("#00834238")),
        //                     new RecordValDTO.FieldDTO("kw", new IntValDTO(13)),
        //                     new RecordValDTO.FieldDTO("requester",
        //                             new RecordValDTO(requesterFields))));
        // }
        //
        // try {
        //     String jsonRec = objectMapper.writeValueAsString(recVal);
        //
        //     System.out.println(jsonRec);
        //
        //     ValueDTO deserializedRecVal = objectMapper.readValue(jsonRec, ValueDTO.class);
        //
        //     System.out.println("Deserialized valueDTO Class: " + deserializedRecVal);
        //
        //     String serializedIntval = objectMapper.writeValueAsString(intVal);
        //
        //     ValueDTO deseriliazedIntVal = objectMapper.readValue(serializedIntval,
        //     ValueDTO.class);
        //
        //     System.out.println(
        //             "Deserialized intVal Class: " + deseriliazedIntVal.getClass()
        //             .getSimpleName());
        //
        //     // ==== fields test =====
        //
        //     System.out.println("\n==== fields test =====\n");
        //
        //     String serializedRecordVal = objectMapper.writeValueAsString(recordVal);
        //
        //     System.out.println("Serialized RecordValDTO: " + serializedRecordVal);
        //
        //
        //     // ==== Marking test =====
        //
        //     System.out.println("\n==== Marking tests =====\n");
        //
        //     System.out.println("= No-Value Marking test\n");
        //
        //     EventDTO.MarkingDTO noValueMarkingDTO = new EventDTO.MarkingDTO(true,
        //             true, Optional.empty());
        //
        //     String serializedNoValueMarkingDTO =
        //             objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        //                     .writeValueAsString(noValueMarkingDTO);
        //
        //     System.out.println("Serialized no-value MarkingDTO: " +
        //     serializedNoValueMarkingDTO);
        //
        //     EventDTO.MarkingDTO deserializedMarkingNoVal =
        //             objectMapper.readValue(serializedNoValueMarkingDTO, EventDTO
        //             .MarkingDTO.class);
        //
        //     System.out.println("Deserialized no-value MarkingDTO: " +
        //     deserializedMarkingNoVal);
        //
        //
        //     System.out.println("\n= Value Marking test\n");
        //
        //     EventDTO.MarkingDTO intValuedMarkingDTO = new EventDTO.MarkingDTO(true, true,
        //             Optional.of(intVal));
        //
        //     String serializedIntValuedMarkingDTO =
        //             objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        //                     .writeValueAsString(intValuedMarkingDTO);
        //
        //     System.out.println(
        //             "Serialized int-valued MarkingDTO: " + serializedIntValuedMarkingDTO);
        //
        //     EventDTO.MarkingDTO deserializedIntValuedMarking =
        //             objectMapper.readValue(serializedIntValuedMarkingDTO,
        //                     EventDTO.MarkingDTO.class);
        //
        //     System.out.println(
        //             "Deserialized int-value MarkingDTO: " + deserializedIntValuedMarking);
        //
        //
        //     // ==== Types test =====
        //
        //     System.out.println("\n==== Types tests =====\n");
        //
        //     TypeDTO recordTypeDTO;
        //     {
        //         // var nestedParams = List.of(new RecordTypeDTO.FieldDTO("param3",
        //         //                 new ValueTypeDTO(ValueTypeDTO.TypeDTO.STRING)),
        //         //         new RecordTypeDTO.FieldDTO("param4",
        //         //                 new ValueTypeDTO(ValueTypeDTO.TypeDTO.INT)));
        //         var nestedParams = List.of(new RecordTypeDTO.FieldDTO("param3",
        //                         ValueTypeDTO.STRING),
        //                 new RecordTypeDTO.FieldDTO("param4",
        //                         ValueTypeDTO.INT));
        //         var params =
        //                 List.of(new RecordTypeDTO.FieldDTO("param1", new EventTypeDTO
        //                 ("Consume")),
        //                         new RecordTypeDTO.FieldDTO("param2",
        //                                 new RecordTypeDTO(nestedParams)));
        //         recordTypeDTO = new RecordTypeDTO(params);
        //     }
        //
        //     String serializedRecordTypeDTO = objectMapper.enable(SerializationFeature
        //     .INDENT_OUTPUT)
        //             .writeValueAsString(recordTypeDTO);
        //
        //     System.out.println("Serialized primitive TypeDTO:\n" + serializedRecordTypeDTO);
        //
        //     TypeDTO deserialisedRecordTypeDTO =
        //             objectMapper.readValue(serializedRecordTypeDTO, TypeDTO.class);
        //
        //     System.out.println("Deserialized RecordTypeDTO:\n" + deserialisedRecordTypeDTO);
        //
        //     // ==== Computation exprs test =====
        //
        //     System.out.println("\n==== Expressions tests =====\n");
        //
        //     ComputationExprDTO computationExprDTO;
        //     {
        //
        //         var nestedParams =
        //                 List.of(new RecordExprDTO.FieldDTO("p1", new BoolLiteralDTO(true)),
        //                         new RecordExprDTO.FieldDTO("p2",
        //                                 new BinaryOpExprDTO(new IntLiteralDTO(3),
        //                                         new IntLiteralDTO(4),
        //                                         BinaryOpExprDTO.OpTypeDTO.INT_ADD)),
        //                         new RecordExprDTO.FieldDTO("p3", new RefExprDTO("e0")),
        //                         new RecordExprDTO.FieldDTO("p4", new PropDerefExprDTO(
        //                                 new PropDerefExprDTO(new RefExprDTO("e5"),
        //                                 "value"), "cid"))
        //
        //                 );
        //         var params = List.of(new RecordExprDTO.FieldDTO("p5", new BoolLiteralDTO
        //         (false)),
        //                 new RecordExprDTO.FieldDTO("p6", new RecordExprDTO(nestedParams)));
        //
        //         computationExprDTO = new RecordExprDTO(params);
        //     }
        //
        //     String serializedComputationExprDTO =
        //             objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        //                     .writeValueAsString(computationExprDTO);
        //
        //     System.out.println("Serialized ComputationExprDTO:\n" +
        //     serializedComputationExprDTO);
        //
        //     ComputationExprDTO deserializedComputationExprDTO =
        //             objectMapper.readValue(serializedComputationExprDTO,
        //             ComputationExprDTO.class);
        //
        //     System.out.println(
        //             "Deserialized ComputationExprDTO:\n" + deserializedComputationExprDTO);
        //
        //
        //     // ==== Computation exprs test =====
        //
        //     System.out.println("\n==== User-Set Expr tests =====\n");
        //
        //     UserSetExprDTO roleExprDTO;
        //     UserSetExprDTO receiverExprDTO;
        //     UserSetExprDTO userSetDiffExprDTO;
        //
        //     {
        //         roleExprDTO = new RoleExprDTO("P", List.of(new RoleExprDTO.Param("id",
        //                         Optional.empty()),
        //                 new RoleExprDTO.Param("cid",
        //                         Optional.of(new StringLiteralDTO("1")))
        //         ));
        //
        //         receiverExprDTO = new ReceiverExprDTO("e0");
        //
        //         userSetDiffExprDTO = new UserSetDiffExprDTO(receiverExprDTO, roleExprDTO);
        //
        //
        //     }
        //
        //     // user-set role
        //     String serializedRoleExprDTO =
        //             objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        //                     .writeValueAsString(roleExprDTO);
        //
        //     System.out.println("Serialized RoleExprDTO:\n" + serializedRoleExprDTO);
        //
        //     UserSetExprDTO deserializedRoleExprDTO =
        //             objectMapper.readValue(serializedRoleExprDTO, UserSetExprDTO.class);
        //
        //     System.out.println(
        //             "Deserialized RoleExprDTO:\n" + deserializedRoleExprDTO);
        //
        //     // user-set receiver
        //     System.out.println();
        //
        //     String serializedReceiverExprDTO =
        //             objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        //                     .writeValueAsString(receiverExprDTO);
        //
        //     System.out.println("Serialized ReceiverExprDTO:\n" + serializedReceiverExprDTO);
        //
        //     UserSetExprDTO deserializedReceiverExprDTO =
        //             objectMapper.readValue(serializedReceiverExprDTO, UserSetExprDTO.class);
        //
        //     System.out.println(
        //             "Deserialized ReceiverExprDTO:\n" + deserializedReceiverExprDTO);
        //
        //
        //     // user-set diff
        //     System.out.println();
        //
        //     String serializedUserSetDiffExprDTO =
        //             objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
        //                     .writeValueAsString(userSetDiffExprDTO);
        //
        //     System.out.println("Serialized UserSetDiffExprDTO:\n" +
        //     serializedUserSetDiffExprDTO);
        //
        //     UserSetExprDTO deserializedUserSetDiffExprDTO =
        //             objectMapper.readValue(serializedUserSetDiffExprDTO, UserSetExprDTO
        //             .class);
        //
        //     System.out.println(
        //             "Deserialized UserSetDiffExprDTO:\n" + deserializedUserSetDiffExprDTO);
        //
        //     // ==== Event test =====
        //
        //     System.out.println("\n==== Event tests =====\n");
        //
        //     EventDTO computationEventDTO;
        //     {
        //         var choreoElementUID = "e0_0_tx";
        //         var id = "e0";
        //         computationEventDTO = new ComputationEventDTO(choreoElementUID, id,
        //         intValuedMarkingDTO,
        //                 null,
        //                 new BoolLiteralDTO(true), new BoolLiteralDTO(true),
        //                 computationExprDTO);
        //     }
        //
        //     String serializedEventDTO = objectMapper.enable(SerializationFeature
        //     .INDENT_OUTPUT)
        //             .writeValueAsString(computationEventDTO);
        //
        //     System.out.println("Serialized EventDTO:\n" + serializedEventDTO);
        //
        //     EventDTO deserializedEventDTO =
        //             objectMapper.readValue(serializedEventDTO, EventDTO.class);
        //
        //     System.out.println("Deserialized EventDTO:\n" + deserializedEventDTO);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }


    }
}
