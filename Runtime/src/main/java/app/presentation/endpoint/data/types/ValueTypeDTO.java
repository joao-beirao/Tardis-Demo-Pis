package app.presentation.endpoint.data.types;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName("valueType")
public enum ValueTypeDTO
        implements TypeDTO {
    BOOL("bool"), INT("int"), STRING("string"), VOID("void");

    @JsonProperty(value = "value")
    public final String value;

    @JsonCreator ValueTypeDTO(
            @JsonProperty(value = "value", required = true) String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {return value;}

    @NotNull
    @Override
    public String toString() {return getValue();}
}