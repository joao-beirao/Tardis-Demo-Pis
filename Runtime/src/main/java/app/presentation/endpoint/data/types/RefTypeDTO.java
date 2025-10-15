package app.presentation.endpoint.data.types;

public sealed interface RefTypeDTO extends TypeDTO
        permits EventTypeDTO, RecordTypeDTO {
}
