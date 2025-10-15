package dcr.common.data.computation;

import dcr.common.data.ASTComparable;
import dcr.common.data.values.IntVal;
import dcr.common.data.values.Value;
import dcr.common.Environment;

import java.util.Objects;

public final class IntLiteral
        implements ComputationExpression, ASTComparable<IntVal> {

    // TODO same comment placed in NumberVal -> this just should be renamed to
    // ASTInteger (but ReGraDa would have to go first)
    private final IntVal value;

    private IntLiteral(IntVal intVal) {
        this.value = intVal;
    }

    public static IntLiteral of(IntVal value) {
        return new IntLiteral(Objects.requireNonNull(value));
    }

    public static IntLiteral of(int value) {
        return new IntLiteral(IntVal.of(value));
    }

    public int value() {return value.value();}

    @Override
    public IntVal eval(Environment<Value> env) {
        return this.value;
    }

    @Override
    public String toString() {return value.toString();}

    @Override
    public boolean isEqualTo(IntVal other) {
        return this.value.value() == other.value();
    }

}