package app.presentation.endpoint.data.computation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(BoolLiteralDTO.class), @JsonSubTypes.Type(IntLiteralDTO.class),
        @JsonSubTypes.Type(StringLiteralDTO.class),
        @JsonSubTypes.Type(PropDerefExprDTO.class),
        @JsonSubTypes.Type(RecordExprDTO.class),
        @JsonSubTypes.Type(RefExprDTO.class),
        @JsonSubTypes.Type(BinaryOpExprDTO.class)})
public sealed interface ComputationExprDTO
        permits BinaryOpExprDTO,
                BoolLiteralDTO,
                IntLiteralDTO,
                PropBasedExprDTO,
                StringLiteralDTO {}
