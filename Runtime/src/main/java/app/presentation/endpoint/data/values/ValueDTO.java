package app.presentation.endpoint.data.values;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(BoolValDTO.class), @JsonSubTypes.Type(IntValDTO.class),
        @JsonSubTypes.Type(StringValDTO.class), @JsonSubTypes.Type(RecordValDTO.class)})
public sealed interface ValueDTO
        permits BoolValDTO, IntValDTO, StringValDTO, RecordValDTO {}