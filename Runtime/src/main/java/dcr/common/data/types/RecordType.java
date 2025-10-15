package dcr.common.data.types;

import dcr.common.Record;

import java.io.Serial;
import java.util.Objects;

// TODO [javadoc]

/**
 * RecordType
 */
public final class RecordType
        implements Type, DereferableType {
    @Serial
    private static final long serialVersionUID = 3695622628239071917L;
    private static final RecordType EMPTY_INSTANCE = new RecordType(Record.empty());
    public final Record<? extends Type> typeRecord;

    public static RecordType empty() {return EMPTY_INSTANCE;}

    // TODO test typeRecord and return EMPTY_INSTANCE if so
    // TODO add isEmpty to Record
    public static RecordType of(Record<Type> typeRecord) {
        return new RecordType(Objects.requireNonNull(typeRecord));
    }

    private RecordType(Record<? extends Type> typeRecord) {
        this.typeRecord = typeRecord;
    }

    public Record<? extends Type> fields() {return typeRecord;}

    @Override
    public int hashCode() {
        return typeRecord.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null) {return false;}
        if (!(getClass().equals(obj.getClass()))) {return false;}
        return typeRecord.equals(((RecordType) obj).typeRecord);
    }

    @Override
    public String toString() {
        return typeRecord.toString();
    }

    // @Override
    // public String unparse() {return typeRecord.unparseFields(Type::unparse);}

    // TODO [discard - move to Unit Testing]
    public static void main(String[] args) {
        var t1 = RecordType.of(Record.ofEntries(Record.Field.of("int1", IntegerType.singleton()),
                Record.Field.of("int2", IntegerType.singleton())));

        // diff number of fields
        var t2 = RecordType.of(Record.ofEntries(Record.Field.of("int1", IntegerType.singleton())));

        // same fields
        var t3 = RecordType.of(Record.ofEntries(Record.Field.of("int1", IntegerType.singleton()),
                Record.Field.of("int2", IntegerType.singleton())));

        // same fields but in diff order
        var t4 = RecordType.of(Record.ofEntries(Record.Field.of("int2", IntegerType.singleton()),
                Record.Field.of("int1", IntegerType.singleton())));

        // new batch

        // mix int string
        var t5 = RecordType.of(Record.ofEntries(Record.Field.of("int", IntegerType.singleton()),
                Record.Field.of("string", StringType.singleton())));

        // vs t5, swapped order - still true
        var t6 = RecordType.of(Record.ofEntries(Record.Field.of("int", IntegerType.singleton()),
                Record.Field.of("string", StringType.singleton())));

        // nested records (int, int)
        var t7 = RecordType.of(Record.ofEntries(Record.Field.of("rec", t1)));

        // vs t7 - diff record - (str, str) - false
        var t8 = RecordType.of(Record.ofEntries(Record.Field.of("rec", t5)));

        // vs. t7 same record, swapped order of field names (int, int)) - true
        var t9 = RecordType.of(Record.ofEntries(Record.Field.of("rec", t4)));

        // mixing names in nested records

        var t10 = RecordType.of(Record.ofEntries(Record.Field.of("int", IntegerType.singleton()),
                Record.Field.of("string", StringType.singleton())));

        // vs t10, same fields but diff types
        var t11 = RecordType.of(Record.ofEntries(Record.Field.of("string", IntegerType.singleton()),
                Record.Field.of("int", StringType.singleton())));


        var t12 = RecordType.of(Record.ofEntries(Record.Field.of("rec", t10)));

        // vs. t12 nested record swapped types, same fields
        var t13 = RecordType.of(Record.ofEntries(Record.Field.of("rec", t11)));


        var t14 = RecordType.of(Record.ofEntries(Record.Field.of("rec_1",
                RecordType.of(Record.ofEntries(Record.Field.of("rec_2", t11))))));


        // vs t4 - most inner records has types swapped - expect false
        var t15 = RecordType.of(Record.ofEntries(Record.Field.of("rec_1",
                RecordType.of(Record.ofEntries(Record.Field.of("rec_2", t10))))));

        System.err.println(t1.equals(t2));// false
        System.err.println(t1.equals(t3));// true
        System.err.println(t1.equals(t4));// true
        System.err.println(t5.equals(t6));// true
        System.err.println(" = nested records =");
        System.err.println(t7.equals(t8) ? "Fail" : "Pass"); // expect false
        System.err.println(t7.equals(t9) ? "Pass" : "Fail"); // expect true
        System.err.println(" = same fields, swapped types = ");
        System.err.println(t10.equals(t11) ? "Fail" : "Pass"); // expect false
        System.err.println(" = same fields, swapped types - in nested record = ");
        System.err.println("t12 vs t13: " + (t12.equals(t13) ? "Fail" : "Pass")); // expect false
        System.err.println("t14 vs t15: " + (t14.equals(t15) ? "Fail" : "Pass")); // expect false
    }
}

