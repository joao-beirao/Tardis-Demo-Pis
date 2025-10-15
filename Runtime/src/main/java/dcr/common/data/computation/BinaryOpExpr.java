package dcr.common.data.computation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import dcr.common.Environment;
import dcr.common.data.values.BoolVal;
import dcr.common.data.values.IntVal;
import dcr.common.data.values.StringVal;
import dcr.common.data.values.Value;
import org.jetbrains.annotations.NotNull;

public final class BinaryOpExpr implements ComputationExpression {
    private final ComputationExpression left;
    private final ComputationExpression right;
    private final OpType op;

    private BinaryOpExpr(ComputationExpression left, ComputationExpression right, OpType op) {
        this.left = left;
        this.right = right;
        this.op = op;
    }

    public static BinaryOpExpr of(ComputationExpression left, ComputationExpression right, OpType op) {
        return new BinaryOpExpr(left, right, op);
    }

    public ComputationExpression left() {return left;}

    public ComputationExpression right() {return right;}

    public OpType opType() {return op;}

    @Override
    public Value eval(Environment<Value> env) {
        Value left = this.left.eval(env);
        Value right = this.right.eval(env);
        return switch (op) {
            case AND -> BoolVal.of(((BoolVal)left).value() && ((BoolVal)right).value());
            case OR -> BoolVal.of(((BoolVal)left).value() || ((BoolVal)right).value());
            case EQ -> BoolVal.of(left.equals(right));
            case NEQ -> BoolVal.of(!left.equals(right));
            case INT_ADD -> IntVal.of(((IntVal)left).value() + ((IntVal)right).value());
            case STR_CONCAT -> StringVal.of(((StringVal)left).value() + ((StringVal)right).value());
            case INT_LT -> BoolVal.of(((IntVal)left).value() < ((IntVal)right).value());
            case INT_GT -> BoolVal.of(((IntVal)left).value() > ((IntVal)right).value());
            case INT_LEQ -> BoolVal.of(((IntVal)left).value() <= ((IntVal)right).value());
            case INT_GEQ -> BoolVal.of(((IntVal)left).value() >= ((IntVal)right).value());
        };
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", left.toString(), op,  right.toString());
    }

    public enum OpType {
        AND("and"), OR("or"), EQ("equals"), NEQ("notEquals"), INT_ADD(
                "intAdd"), STR_CONCAT("stringConcat"), INT_LT("intLessThan"), INT_GT(
                "intGreaterThan"), INT_LEQ("intLessThanOrEqual"), INT_GEQ("intGreaterThanOrEqual");

        @JsonProperty(value = "op")
        private final String op;

        @JsonCreator
        OpType(@JsonProperty(value = "op", required = true) String op) {this.op = op;}

        @JsonValue
        public String getOp() {return op;}

        @NotNull
        @Override
        public String toString() {
            return switch (this) {
                case AND -> "&&";
                case OR -> "||";
                case EQ -> "==";
                case NEQ -> "<>";
                case INT_ADD, STR_CONCAT -> "+";
                case INT_LT -> "<";
                case INT_GT -> ">";
                case INT_LEQ -> "<=";
                case INT_GEQ -> ">=";
            };
        }
    }
}

