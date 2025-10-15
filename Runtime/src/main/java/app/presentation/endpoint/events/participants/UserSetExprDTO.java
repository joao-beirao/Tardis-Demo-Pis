package app.presentation.endpoint.events.participants;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({@JsonSubTypes.Type(RoleExprDTO.class), @JsonSubTypes.Type(InitiatorExprDTO.class),
        @JsonSubTypes.Type(ReceiverExprDTO.class), @JsonSubTypes.Type(UserSetDiffExprDTO.class)})
public sealed interface UserSetExprDTO
        permits InitiatorExprDTO, ReceiverExprDTO, RoleExprDTO, UserSetDiffExprDTO {}
