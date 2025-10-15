// package dcr1.ast;
//
// import java.util.*;
// import java.util.function.Function;
// import java.util.stream.Collectors;
//
// public final class Record<V> {
//
//     private static final Record<?> EMPTY_RECORD = new Record<>(Collections.emptyMap());
//
//     // Unmodifiable map backed by a LinkedHashMap (preserves the order in which the entries were
//     // added)
//     private final Map<String, Field<V>> fields;
//
//     public record Field<V>(String name, V value) {
//
//         public static <V> Field<V> of(String name, V value) {return new Field<>(name, value);}
//
//         // equality test based solely on the Field's name - regardless of the type of Value
//         @Override
//         public boolean equals(Object other) {
//             if (this == other) {return true;}
//             if (other == null) {return false;}
//             if (!(other instanceof Field<?> field)) {return false;}
//             return field.name.equalsIgnoreCase(this.name);
//         }
//
//         @Override
//         public int hashCode() {return name.hashCode();}
//     }
//
//     public static final class Builder<V> {
//         private final LinkedHashMap<String, Field<V>> fields;
//
//         // replaces if already existed, keeps the order in which entries first appeared
//         // TODO [validate args]
//         public Builder<V> addField(String name, V value) {
//             fields.put(name, Field.of(name, value));
//             return this;
//         }
//
//         // public static <V> RecordBuilder<V> builder() {return new Builder<>();}
//
//         public Builder() {
//             fields = new LinkedHashMap<>();
//         }
//
//         public Record<V> build() {
//             return new Record<>(Collections.unmodifiableMap(fields));
//         }
//     }
//
//     public static <V> Record<V> empty() {
//         @SuppressWarnings("unchecked") var empty = (Record<V>) EMPTY_RECORD;
//         return empty;
//     }
//
//
//     // replaces if already existed, keeps the order in which entries first appeared
//     @SafeVarargs
//     public static <V> Record<V> ofEntries(Field<V>... entries) {
//         if (entries.length == 0) { // implicit null check of entries array
//             @SuppressWarnings("unchecked") var record = (Record<V>) EMPTY_RECORD;
//             return record;
//         }
//         Map<String, Field<V>> fields = HashMap.newHashMap(entries.length);
//         for (var field : entries) {
//             var name = Objects.requireNonNull(field).name;
//             fields.put(name, field);
//         }
//         return new Record<>(Collections.unmodifiableMap(fields));
//     }
//
//     private Record(Map<String, Field<V>> fields) {this.fields = fields;}
//
//     public Optional<? extends V> get(String name) {
//         return Optional.ofNullable(fields.get(name)).map(Field::value);
//     }
//
//     // Unmodifiable Set view of the fields
//     public Set<Field<? extends V>> fields() {
//         return Set.copyOf(fields.values());
//     }
//
//
//     private String fieldsToString(Function<V, String> valueToString) {
//         return fields().stream()
//                 .map(f -> String.format("'%s': %s", f.name, valueToString.apply(f.value)))
//                 .collect(Collectors.joining("; "));
//     }
//
//     @Override
//     public String toString() {
//         // var fieldsAsString = fields().stream()
//         //         .map(f -> String.format("'%s': %s", f.name, f.value))
//         //         .collect(Collectors.joining("; "));
//         return String.format("[%s]", fieldsToString(Object::toString));
//     }
//
//     public String unparse(Function<V, String> valueUnparser) {
//         // var fieldsAsString = fields().stream()
//         //         .map(f -> String.format("'%s': %s", f.name, unparseValue.apply(f.value)))
//         //         .collect(Collectors.joining("; "));
//         return String.format("[%s]", fieldsToString(valueUnparser));
//     }
// }

// TODO [discard after all expressions implemented]
package dcr.common;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO [sanitize args]
// TODO [javadoc]

/**
 * Record
 * @param <V>
 */
public final class Record<V>
        implements Iterable<Record.Field<? extends V>>, Serializable {

    @Serial
    private static final long serialVersionUID = -1559084809488762998L;
    private static final Record<?> EMPTY_RECORD = new Record<>(Collections.emptyMap());
    // Unmodifiable map backed by a LinkedHashMap (preserves the order in which the entries were
    // added)
    private final Map<String, Field<? extends V>> fields;

    public record Field<V>(String name, V value) implements Serializable {
        public static <V> Field<V> of(String name, V value) {return new Field<>(name, value);}

        // equality test based solely on the Field's name - regardless of the type of Value
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {return true;}
            if (obj == null) {return false;}
            if (!(obj instanceof Field<?> field)) {return false;}
            return name.equals(field.name) && value.equals(field.value);
        }

        @Override
        public int hashCode() {return Objects.hash(name, value);}
    }

    public static final class Builder<V> {
        private final LinkedHashMap<String, Field<? extends V>> fields;

        // replaces if already existed, keeps the order in which entries first appeared
        // TODO [validate args]
        // TODO [throw on repeated field?]
        public Builder<V> addFieldWithParams(String name, V value) {
            fields.put(name, Field.of(name, value));
            return this;
        }

        public Builder<V> addField(Field<? extends V> field) {
            fields.put(field.name, field);
            return this;
        }

        public Builder() {
            fields = new LinkedHashMap<>();
        }

        public Builder(int numFields) {fields = LinkedHashMap.newLinkedHashMap(numFields);}

        public Record<V> build() {
            return new Record<>(Collections.unmodifiableMap(fields));
        }
    }

    public static <V> Record<V> empty() {
        @SuppressWarnings("unchecked") var empty = (Record<V>) EMPTY_RECORD;
        return empty;
    }

    // replaces if already existed, keeps the order in which entries first appeared
    @SafeVarargs
    public static <V> Record<V> ofEntries(Field<? extends V>... entries) {
        if (entries.length == 0) { // implicit null check of entries array
            return empty();
        }
        var builder = new Builder<V>();
        for (var field : entries) {
            builder.addField(field);
        }
        return builder.build();
    }

    public static <V> Record<V> ofEntries(Map<String, ? extends V> entries) {
        if (entries.isEmpty()) { // implicit null check of entries array
            return empty();
        }
        var builder = new Builder<V>();
        for (var field : entries.entrySet()) {
            builder.addFieldWithParams(field.getKey(), field.getValue());
        }
        return builder.build();
    }


    public static <V> Builder<V> builder() {
        return new Builder<>();
    }

    private Record(Map<String, Field<? extends V>> fields) {this.fields = fields;}

    public boolean isEmpty() {return fields.isEmpty();}

    public Optional<? extends V> get(String name) {
        // TODO check field name
        return Optional.ofNullable(fields.get(name)).map(Field::value);
    }

    // Unmodifiable Set view of the fields
    public Set<Field<? extends V>> fields() {
        return new LinkedHashSet<>(fields.values());
    }

    private String fieldsToString(Function<V, String> valueToString) {
        return fields().stream()
                .map(f -> String.format("%s: %s", f.name, valueToString.apply(f.value)))
                .collect(Collectors.joining(" ; "));
    }

    // must have the same fields: for each name, test is based on value.equals(other)
    // value V must implement equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {return true;}
        if (obj == null) {return false;}
        if (!(obj instanceof Record<?> record)) {return false;}
        if (this.fields.size() != record.fields.size()) {return false;}
        for (var tField : this.fields.values()) {
            var oField = record.fields.get(tField.name);
            if (oField == null || !oField.value.equals(tField.value)) {return false;}
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields);
    }

    @NotNull
    @Override
    public Iterator<Field<? extends V>> iterator() {
        return fields.values().iterator();
    }

    public Stream<Field<? extends V>> stream() {return fields.values().stream();}



    @Override
    public String toString() {
        return String.format("{%s}", fieldsToString(Object::toString));
    }

    public String unparseFields(Function<V, String> valueUnparser) {
        return String.format("%s", fieldsToString(Objects.requireNonNull(valueUnparser)));
    }
}