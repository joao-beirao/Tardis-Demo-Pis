package dcr.common.data.values;

import dcr.common.data.types.RecordType;

import dcr.common.Record;
import dcr.common.data.types.Type;

import java.io.Serial;
import java.util.Objects;

/**
 * A record-type of value storing a collection of name-value mappings, called
 * fields.
 * </p>
 * A RecordVal instance has a fixed structure, and it is not possible to add,
 * remove, or rename fields. It is also not possible to assign a new value (in
 * the sense of replacement) to a field. A field value can still change if
 * it is inherently mutable (e.g., if the field holds a RefVal instance, one
 * cannot change the instance, but can still mutate its internal state).
 * </p>
 * The type of RecordVal is indicated by... TODO [javadoc]
 */
public final class RecordVal implements PropBasedVal {

  @Serial
  private static final long serialVersionUID = -1287514074906057952L;
  private final Record<Value> valRecord;

  // public static Undefined<RecordType> undefined() {return UNDEFINED;}

  // public static Undefined<RecordType> undefined(RecordType typeRecord) {return Undefined.of(typeRecord);}

  public RecordVal(Record<Value> valRecord) {
    this.valRecord = valRecord;
  }

  public static RecordVal of(Record<Value> valueRecord) {
    return new RecordVal(Objects.requireNonNull(valueRecord));
  }

  public Record<Value> value() {
    return valRecord;
  }


  // TODO store this instead(?)
  @Override
  public RecordType type() {
    var builder = new Record.Builder<Type>();
    valRecord.fields().forEach(field -> builder.addFieldWithParams(field.name(), field.value().type()));
    return RecordType.of(builder.build());
  }

  public Record<Value> fields() {return valRecord;}

  @Override
  public int hashCode() {
    return valRecord.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null) {return false;}
    if(this == obj) {return true;}
    if(getClass() != obj.getClass()) {return false;}
    return valRecord.equals(((RecordVal)obj).valRecord);
  }

  @Override
  public String toString() {
    return valRecord.toString();
  }

  @Override
  public Value fetchProp(String propName) {
    // TODO revisit
    return valRecord.get(propName).orElseThrow();
  }
}
