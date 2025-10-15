package app.presentation.endpoint.data.types;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeName("recordType")
public record RecordTypeDTO( @JsonProperty(value = "fields",required = true) List<FieldDTO> fields)
        implements RefTypeDTO {

    public record FieldDTO(@JsonProperty(value = "name", required = true) String name,
                           @JsonProperty(value = "type", required = true) TypeDTO value) {

        @NotNull
        @Override
        public String toString() {
            return String.format("%s: %s", name, value);
        }
    }

    @NotNull
    @Override
    public String toString() {
        return fields.stream()
                .map(FieldDTO::toString)
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
