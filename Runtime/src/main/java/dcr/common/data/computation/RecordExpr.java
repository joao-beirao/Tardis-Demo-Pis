package dcr.common.data.computation;


import dcr.common.Environment;
import dcr.common.Record;
import dcr.common.data.values.PropBasedVal;
import dcr.common.data.values.RecordVal;
import dcr.common.data.values.Value;

import java.util.Objects;

/**
 * A Record node
 */
public final class RecordExpr
        implements PropBasedExpr {

    private final Record<ComputationExpression> exprRecord;

    private RecordExpr(Record<ComputationExpression> exprRecord) {

        this.exprRecord = exprRecord;
    }

    public static RecordExpr of(Record<ComputationExpression> recordExpr) {
        return new RecordExpr(Objects.requireNonNull(recordExpr));
    }

    public Record<ComputationExpression> fields() {return  exprRecord;}

    @Override
    public PropBasedVal eval(Environment<Value> env) {
        Record.Builder<Value> builder = new Record.Builder<>();
        for (var fieldExpr : exprRecord.fields()) {
            builder.addFieldWithParams(fieldExpr.name(), fieldExpr.value().eval(env));
        }
        return RecordVal.of(builder.build());
    }

    @Override
    public String toString() {return exprRecord.toString();}



    // TODO remove
    // Usage Example
    public static void main(String[] args) {

        // flat record (via .ofEntries())
        RecordExpr flatExpr = RecordExpr.of(
                Record.ofEntries(Record.Field.of("f1", IntLiteral.of(1)),
                        Record.Field.of("f2", StringLiteral.of("2"))));

        // nested records (via .ofEntries())
        RecordExpr nestedExpr = RecordExpr.of(
                Record.ofEntries(Record.Field.of("f1", IntLiteral.of(1)),
                        Record.Field.of("f2", StringLiteral.of("2")), Record.Field.of("f3",
                                RecordExpr.of(Record.ofEntries(
                                        Record.Field.of("f1", IntLiteral.of(1)),
                                        Record.Field.of("f2", StringLiteral.of("2"))))),
                        Record.Field.of("f4", BoolLiteral.of(true)),
                        Record.Field.of("f5", BoolLiteral.of(false))));

        // nested records (mixing builder() and .ofEntries()
        var recordFromBuilder = new Record.Builder<ComputationExpression>().addField(
                        Record.Field.of("f1", IntLiteral.of(1)))
                .addField(Record.Field.of("f2", StringLiteral.of("2")))
                .addField(Record.Field.of("f3",
                        RecordExpr.of(Record.ofEntries(
                                Record.Field.of("f1", IntLiteral.of(1)),
                                Record.Field.of("f2", StringLiteral.of("2"))))))
                .build();
        System.err.println(flatExpr);
        System.err.println(nestedExpr);
        System.err.println();

        System.err.println();
        System.err.println(recordFromBuilder);

    }
}