package app.presentation.mappers;

import app.presentation.endpoint.EndpointDTO;
import app.presentation.endpoint.EndpointsDTO;
import app.presentation.endpoint.GraphDTO;
import app.presentation.endpoint.RoleDTO;
import app.presentation.endpoint.data.computation.*;
import app.presentation.endpoint.data.types.EventTypeDTO;
import app.presentation.endpoint.data.types.RecordTypeDTO;
import app.presentation.endpoint.data.types.TypeDTO;
import app.presentation.endpoint.data.types.ValueTypeDTO;
import app.presentation.endpoint.data.values.*;
import app.presentation.endpoint.events.ComputationEventDTO;
import app.presentation.endpoint.events.EventDTO;
import app.presentation.endpoint.events.InputEventDTO;
import app.presentation.endpoint.events.ReceiveEventDto;
import app.presentation.endpoint.events.participants.*;
import app.presentation.endpoint.relations.ControlFlowRelationDTO;
import app.presentation.endpoint.relations.RelationDTO;
import app.presentation.endpoint.relations.SpawnRelationDTO;
import dcr.common.Record;
import dcr.common.data.computation.*;
import dcr.common.data.types.*;
import dcr.common.data.values.*;
import dcr.common.events.userset.expressions.*;
import dcr.common.relations.ControlFlowRelation;
import dcr.common.relations.Relation;
import dcr.model.GraphElement;
import dcr.model.GraphModelBuilder;
import dcr.model.RecursiveGraphElement;
import dcr.model.events.*;
import dcr.model.relations.ControlFlowRelationElement;
import dcr.model.relations.RelationElement;
import dcr.model.relations.RelationElements;
import dcr.model.relations.SpawnRelationElement;
import org.apache.commons.lang3.NotImplementedException;
import protocols.application.Endpoint;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static dcr.common.data.computation.BinaryOpExpr.OpType.AND;


public final class EndpointMapper {

    // TODO java-doc entry-point
    public static Endpoint mapEndpoint(EndpointDTO endpointDTO) {
        return new Endpoint(mapRole(endpointDTO.role()),
                mapGraphModel(endpointDTO.graph()));
    }

    // TODO java-doc entry-point
    public static GraphDTO toGraphDTO(GraphElement graph) {
        var events = StreamSupport.stream(graph.events().spliterator(), false)
                .map(EndpointMapper::toEventDTO).toList();
        var relations = StreamSupport.stream(graph.relations().spliterator(), false)
                .map(EndpointMapper::toRelationDTO).toList();
        return new GraphDTO(events, relations);
    }

    private static Endpoint.Role mapRole(RoleDTO dto) {
        var label = dto.label();
        var params = Record.ofEntries(dto.params().stream()
                .map(p -> Record.Field.of(p.name(), fromTypeDTO(p.type())))
                .collect(Collectors.toMap(Record.Field::name, Record.Field::value)));
        return new Endpoint.Role(label, params);
    }

    private static RecursiveGraphElement mapGraphModel(GraphDTO dto) {
        return mapGraphDTO(dto, new GraphModelBuilder()).build();
    }

    private static RecursiveGraphElement mapGraphModel(String endpointElementUID,
                                                       GraphDTO dto) {
        return mapGraphDTO(dto, new GraphModelBuilder(endpointElementUID)).build();
    }

    private static GraphModelBuilder mapGraphDTO(GraphDTO graphDTO,
                                                 GraphModelBuilder builder) {
        for (EventDTO eventDTO : graphDTO.events())
            builder = fromEventDTO(eventDTO, builder);
        for (RelationDTO relationDTO : graphDTO.relations())
            builder = fromRelationDTO(relationDTO, builder);
        return builder;
    }

    private static TypeDTO toTypeDTO(Type type) {
        return switch (type) {
            case BooleanType ignored -> ValueTypeDTO.BOOL;
            case IntegerType ignored -> ValueTypeDTO.INT;
            case StringType ignored -> ValueTypeDTO.STRING;
            case VoidType ignored -> ValueTypeDTO.VOID;
            case EventType eventType -> new EventTypeDTO(eventType.typeAlias());
            case RecordType recordType -> new RecordTypeDTO(recordType.fields().stream()
                    .map(f -> new RecordTypeDTO.FieldDTO(f.name(), toTypeDTO(f.value())))
                    .collect(Collectors.toList()));
        };
    }

    private static Type fromTypeDTO(TypeDTO dto) {
        return switch (dto) {
            case ValueTypeDTO type -> switch (type) {
                case BOOL -> BooleanType.singleton();
                case INT -> IntegerType.singleton();
                case STRING -> StringType.singleton();
                case VOID -> VoidType.singleton();
            };
            case EventTypeDTO type -> new EventType(type.eventType());
            case RecordTypeDTO type -> RecordType.of(Record.ofEntries(
                    type.fields().stream().collect(
                            Collectors.toMap(RecordTypeDTO.FieldDTO::name,
                                    x -> fromTypeDTO(x.value())))));
        };
    }

    private static ValueDTO toValueDTO(Value value) {
        return switch (value) {
            case BoolVal boolVal -> new BoolValDTO(boolVal.value());
            case IntVal intVal -> new IntValDTO(intVal.value());
            case StringVal stringVal -> new StringValDTO(stringVal.value());
            case RecordVal recordVal -> new RecordValDTO(recordVal.fields().stream()
                    .map(f -> new RecordValDTO.FieldDTO(f.name(), toValueDTO(f.value())))
                    .toList());
            // TODO overlooking event vals temporarily - assuming none
            case EventVal eventVal -> throw new RuntimeException("not implemented yet");
            case UndefinedVal<?> undefinedVal -> null;
            case VoidVal voidVal -> null;
        };
    }

    private static Value fromValueDTO(ValueDTO dto) {
        return switch (dto) {
            case BoolValDTO v -> BoolVal.of(v.value());
            case IntValDTO v -> IntVal.of(v.value());
            case StringValDTO v -> StringVal.of(v.value());
            case RecordValDTO v -> RecordVal.of(Record.ofEntries(v.fields().stream()
                    .collect(Collectors.toMap(RecordValDTO.FieldDTO::name,
                            x -> fromValueDTO(x.value())))));
        };
    }

    private static ComputationExprDTO toExprDTO(ComputationExpression expr) {
        return switch (expr) {
            case BoolLiteral boolLit -> new BoolLiteralDTO(boolLit.value());
            case IntLiteral intLit -> new IntLiteralDTO(intLit.value());
            case StringLiteral stringLit -> new StringLiteralDTO(stringLit.value());
            case RefExpr refExpr -> new RefExprDTO(refExpr.eventId());
            case RecordExpr recordExpr -> new RecordExprDTO(recordExpr.fields().stream()
                    .map(f -> new RecordExprDTO.FieldDTO(f.name(), toExprDTO(f.value())))
                    .toList());
            case PropDerefExpr derefExpr -> new PropDerefExprDTO(
                    (PropBasedExprDTO) toExprDTO(derefExpr.propBasedExpr),
                    derefExpr.propName);
            case BooleanExpression booleanExpression ->
                    toExprDTO(booleanExpression.expr());
            case BinaryOpExpr binaryOpExpr -> {
                var left = toExprDTO(binaryOpExpr.left());
                var right = toExprDTO(binaryOpExpr.right());
                var opType = switch (binaryOpExpr.opType()) {
                    case AND -> BinaryOpExprDTO.OpTypeDTO.AND;
                    case OR -> BinaryOpExprDTO.OpTypeDTO.OR;
                    case EQ -> BinaryOpExprDTO.OpTypeDTO.EQ;
                    case NEQ -> BinaryOpExprDTO.OpTypeDTO.NEQ;
                    case INT_ADD -> BinaryOpExprDTO.OpTypeDTO.INT_ADD;
                    case STR_CONCAT -> BinaryOpExprDTO.OpTypeDTO.STR_CONCAT;
                    case INT_LT -> BinaryOpExprDTO.OpTypeDTO.INT_LT;
                    case INT_GT -> BinaryOpExprDTO.OpTypeDTO.INT_GT;
                    case INT_LEQ -> BinaryOpExprDTO.OpTypeDTO.INT_LEQ;
                    case INT_GEQ -> BinaryOpExprDTO.OpTypeDTO.INT_GEQ;
                };
                yield new BinaryOpExprDTO(left, right, opType);
            }
            case NegationExpr negationExpr ->
                    throw new NotImplementedException("not implemented yet");
            case IfThenElseExpr ifThenElseExpr ->
                    throw new NotImplementedException("not implemented yet");
        };
    }

    private static ComputationExpression fromExprDTO(ComputationExprDTO dto) {
        return switch (dto) {
            case BoolLiteralDTO expr -> BoolLiteral.of(expr.value());
            case IntLiteralDTO expr -> IntLiteral.of(expr.value());
            case StringLiteralDTO expr -> StringLiteral.of(expr.value());
            case RefExprDTO expr -> new RefExpr(expr.value());
            case RecordExprDTO expr -> RecordExpr.of(Record.ofEntries(
                    expr.fields().stream().collect(
                            Collectors.toMap(RecordExprDTO.FieldDTO::name,
                                    x -> fromExprDTO(x.value())))));
            case PropDerefExprDTO expr ->
                    PropDerefExpr.of(fromExprDTO(expr.expr()), expr.prop());
            case BinaryOpExprDTO expr -> {
                var left = fromExprDTO(expr.left());
                var right = fromExprDTO(expr.right());
                yield switch (expr.optType()) {
                    case AND -> BinaryOpExpr.of(left, right, AND);
                    case OR -> BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.OR);
                    case EQ -> BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.EQ);
                    case NEQ -> BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.NEQ);
                    case INT_ADD ->
                            BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.INT_ADD);
                    case STR_CONCAT ->
                            BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.STR_CONCAT);
                    case INT_LT ->
                            BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.INT_LT);
                    case INT_GT ->
                            BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.INT_GT);
                    case INT_LEQ ->
                            BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.INT_LEQ);
                    case INT_GEQ ->
                            BinaryOpExpr.of(left, right, BinaryOpExpr.OpType.INT_GEQ);
                };
            }
        };
    }

    private static List<UserSetExprDTO> toUserSetExprDTO(UserSetExpression userSetExpr) {
        return switch (userSetExpr) {
            case InitiatorExpr initiatorExpr ->
                    List.of(new InitiatorExprDTO(initiatorExpr.eventId()));
            case ReceiverExpr receiverExpr ->
                    List.of(new ReceiverExprDTO(receiverExpr.eventId()));
            case RoleExpr roleExpr -> {
                var paramDTOs = new LinkedList<RoleExprDTO.ParamDTO>();
                roleExpr.unconstrainedParams().forEach(name -> paramDTOs.add(
                        new RoleExprDTO.ParamDTO(name, Optional.empty())));
                roleExpr.constrainedParams().forEach(param -> paramDTOs.add(
                        new RoleExprDTO.ParamDTO(param.name(),
                                Optional.of(toExprDTO(param.value())))));
                new RoleExprDTO(roleExpr.role(), paramDTOs);
                throw new NotImplementedException("not implemented yet");
            }
            case SetDiffExpr ignored ->
                    throw new NotImplementedException("not implemented yet");
            case SetUnionExpr setUnionExpr -> setUnionExpr.userSetExprs().stream()
                    .map(EndpointMapper::toUserSetExprDTO).flatMap(Collection::stream)
                    .collect(Collectors.toList());
        };
    }

    // TODO [monitor] under the current assumptions, membershipDTO will always be parameterised
    //  - may change
    private static UserSetExpression fromUserSetExprDTO(UserSetExprDTO dto) {
        return switch (dto) {
            case RoleExprDTO e -> {
                if (e.params().isEmpty()) {
                    yield RoleExpr.of(e.label());
                } else {
                    yield RoleExpr.of(e.label(),
                            e.params().stream().filter(x -> x.value().isEmpty())
                                    .map(RoleExprDTO.ParamDTO::name)
                                    .collect(Collectors.toSet()), Record.ofEntries(
                                    e.params().stream().filter(x -> x.value().isPresent())
                                            .collect(Collectors.toMap(
                                                    RoleExprDTO.ParamDTO::name,
                                                    x -> fromExprDTO(x.value().get())))));
                }
            }
            case InitiatorExprDTO e -> InitiatorExpr.of(e.eventId());
            case ReceiverExprDTO e -> ReceiverExpr.of(e.eventId());
            case UserSetDiffExprDTO userSetDiffExprDTO ->
                    throw new NotImplementedException("not implemented yet");
        };
    }

    private static EventDTO toEventDTO(EventElement event) {
        EventDTO.Common common;
        {
            var markingDTO =
                    new EventDTO.MarkingDTO(event.isIncluded(), event.isPending(),
                            Optional.ofNullable(switch (event.marking().value()) {
                                case UndefinedVal<?> ignored -> null;
                                default -> toValueDTO(event.marking().value());
                            }));
            var instantiationConstraint = Optional.ofNullable(
                    event.instantiationConstraint().expr().equals(BoolLiteral.TRUE) ?
                            null : toExprDTO(event.instantiationConstraint()));
            var ifcConstraint = Optional.ofNullable(
                    event.ifcConstraint().expr().equals(BoolLiteral.TRUE) ? null :
                            toExprDTO(event.ifcConstraint()));
            common = new EventDTO.Common(event.choreoElementUID(),
                    event.endpointElementUID(), event.localId(), event.label(),
                    toTypeDTO(event.valueType()), markingDTO, instantiationConstraint,
                    ifcConstraint);
        }
        Function<Optional<UserSetExpression>, List<UserSetExprDTO>> toReceiversDTO =
                opt -> opt.map(EndpointMapper::toUserSetExprDTO)
                        .orElse(Collections.emptyList());
        return switch (event) {
            case ComputationEventElement element -> new ComputationEventDTO(common,
                    toExprDTO(element.computationExpression()),
                    toReceiversDTO.apply(element.receivers()));
            case InputEventElement element ->
                    new InputEventDTO(common, toReceiversDTO.apply(element.receivers()));
            case ReceiveEventElement element -> new ReceiveEventDto(common,
                    toUserSetExprDTO(element.getSenderExpr()));
        };
    }

    private static GraphModelBuilder fromEventDTO(EventDTO dto,
                                                  GraphModelBuilder builder) {
        var choreoElementUID = dto.common.choreoElementUID();
        var endpointElementUID = dto.common.endpointElementUID();
        var id = dto.common.id();
        var eventType = dto.common.label();
        ImmutableMarkingElement marking;
        {
            var isPending = dto.common.marking().isPending();
            var isIncluded = dto.common.marking().isIncluded();
            var value = dto.common.marking().value().map(EndpointMapper::fromValueDTO)
                    .orElse(UndefinedVal.of(fromTypeDTO(dto.common.dataType())));
            marking = new ImmutableMarkingElement(isPending, isIncluded, value);
        }
        var instantiationConstraint =
                dto.common.instantiationConstraint().map(EndpointMapper::fromExprDTO)
                        .orElse(BoolLiteral.TRUE);
        var ifcConstraint = dto.common.ifcConstraint().map(EndpointMapper::fromExprDTO)
                .orElse(BoolLiteral.TRUE);
        return switch (dto) {
            case ComputationEventDTO e -> {
                var expr = fromExprDTO(e.dataExpr);
                if (e.receivers.isEmpty()) {
                    yield builder.addLocalComputationEvent(choreoElementUID,
                            endpointElementUID, id, eventType, expr, marking,
                            BooleanExpression.of(instantiationConstraint),
                            BooleanExpression.of(ifcConstraint));
                } else {
                    var receiversExpr = SetUnionExpr.of(
                            e.receivers.stream().map(EndpointMapper::fromUserSetExprDTO)
                                    .collect(Collectors.toCollection(ArrayList::new)));
                    yield builder.addComputationEvent(choreoElementUID,
                            endpointElementUID, id, eventType, expr, receiversExpr,
                            marking, BooleanExpression.of(instantiationConstraint),
                            BooleanExpression.of(ifcConstraint));
                }
            }
            case InputEventDTO e -> {
                if (e.receivers.isEmpty()) {
                    yield builder.addLocalInputEvent(choreoElementUID, endpointElementUID,
                            id, eventType, marking,
                            BooleanExpression.of(instantiationConstraint),
                            BooleanExpression.of(ifcConstraint));
                } else {
                    var receiversExpr = SetUnionExpr.of(
                            e.receivers.stream().map(EndpointMapper::fromUserSetExprDTO)
                                    .collect(Collectors.toCollection(ArrayList::new)));
                    yield builder.addInputEvent(choreoElementUID, endpointElementUID, id,
                            eventType, receiversExpr, marking,
                            BooleanExpression.of(instantiationConstraint),
                            BooleanExpression.of(ifcConstraint));
                }
            }
            case ReceiveEventDto e -> {
                var initiatorsExpr = SetUnionExpr.of(
                        e.initiators.stream().map(EndpointMapper::fromUserSetExprDTO)
                                .collect(Collectors.toCollection(ArrayList::new)));
                yield builder.addReceiveEvent(choreoElementUID, endpointElementUID, id,
                        eventType, initiatorsExpr, marking,
                        BooleanExpression.of(instantiationConstraint),
                        BooleanExpression.of(ifcConstraint));
            }
        };
    }

    private static ControlFlowRelationDTO.RelationTypeDTO toRelationTypeDTO(
            ControlFlowRelation.Type relType) {
        return switch (relType) {
            case CONDITION -> ControlFlowRelationDTO.RelationTypeDTO.CONDITION;
            case MILESTONE -> ControlFlowRelationDTO.RelationTypeDTO.MILESTONE;
            case RESPONSE -> ControlFlowRelationDTO.RelationTypeDTO.RESPONSE;
            case INCLUDE -> ControlFlowRelationDTO.RelationTypeDTO.INCLUDE;
            case EXCLUDE -> ControlFlowRelationDTO.RelationTypeDTO.EXCLUDE;
        };
    }

    private static ControlFlowRelation.Type fromRelationTypeDTO(
            ControlFlowRelationDTO.RelationTypeDTO dto) {
        return switch (dto) {
            case INCLUDE -> ControlFlowRelation.Type.INCLUDE;
            case EXCLUDE -> ControlFlowRelation.Type.EXCLUDE;
            case RESPONSE -> ControlFlowRelation.Type.RESPONSE;
            case CONDITION -> ControlFlowRelation.Type.CONDITION;
            case MILESTONE -> ControlFlowRelation.Type.MILESTONE;
        };
    }


    private static RelationDTO toRelationDTO(RelationElement relation) {
        RelationDTO.Common common;
        {
            var endpointElementUID = relation.endpointElementUID();
            var sourceId = relation.sourceId();
            Optional<ComputationExprDTO> guard =
                    relation.guard().equals(Relation.DEFAULT_GUARD) ? Optional.empty() :
                            Optional.of(toExprDTO(relation.guard()));
            Optional<ComputationExprDTO> instantiationConstraint =
                    relation.instantiationConstraint()
                            .equals(Relation.DEFAULT_INSTANTIATION_CONSTRAINT) ?
                            Optional.empty() :
                            Optional.of(toExprDTO(relation.instantiationConstraint()));
            common = new RelationDTO.Common(endpointElementUID, sourceId, guard,
                    instantiationConstraint);
        }
        return switch (relation) {
            case ControlFlowRelationElement rel ->
                    new ControlFlowRelationDTO(common, rel.targetId(),
                            toRelationTypeDTO(rel.relationType()));
            case SpawnRelationElement rel -> new SpawnRelationDTO(common, rel.triggerId(),
                    toGraphDTO(rel.subGraph()));
        };
    }

    private static GraphModelBuilder fromRelationDTO(RelationDTO dto,
                                                     GraphModelBuilder builder) {
        var endpointElementUID = dto.common().endpointElementUID();
        var srcId = dto.common().sourceId();
        var guard = dto.common().guard().map(EndpointMapper::fromExprDTO)
                .orElse(Relation.DEFAULT_GUARD);
        var instantiationConstraint =
                dto.common().instantiationConstraint().map(EndpointMapper::fromExprDTO)
                        .orElse(Relation.DEFAULT_INSTANTIATION_CONSTRAINT);
        return switch (dto) {
            case ControlFlowRelationDTO r -> builder.addControlFlowRelation(
                    RelationElements.newControlFlowRelation(endpointElementUID, srcId,
                            r.targetId(), guard, fromRelationTypeDTO(r.relationType()),
                            instantiationConstraint));
            case SpawnRelationDTO r -> builder.addSpawnRelation(
                    RelationElements.newSpawnRelation(endpointElementUID, srcId,
                            r.triggerId(), guard,
                            mapGraphModel(endpointElementUID, r.graph()),
                            instantiationConstraint));
        };
    }

}