package app.presentation.endpoint.data.computation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonTypeName("binaryOp")
public record BinaryOpExprDTO(
        @JsonProperty(value = "expr1", required = true) ComputationExprDTO left,
        @JsonProperty(value = "expr2", required = true) ComputationExprDTO right,
        @JsonProperty(value = "op", required = true) OpTypeDTO optType)
        implements ComputationExprDTO {

    @JsonTypeName("op")
    public enum OpTypeDTO {
        AND("and"), OR("or"), EQ("equals"), NEQ("notEquals"), INT_ADD("intAdd"), STR_CONCAT(
                "stringConcat"), INT_LT("intLessThan"), INT_GT("intGreaterThan"), INT_LEQ(
                "intLessThanOrEqual"), INT_GEQ("intGreaterThanOrEqual");

        @JsonProperty(value = "op", required = true)
        private final String op;

        @JsonCreator
        OpTypeDTO(@JsonProperty(value = "op", required = true) String op) {this.op = op;}

        @JsonValue
        public String getOp() {return op;}
    }
}
