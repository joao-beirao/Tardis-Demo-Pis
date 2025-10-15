package app.presentation.endpoint.data.computation;

public sealed interface PropBasedExprDTO extends ComputationExprDTO
        permits PropDerefExprDTO, RecordExprDTO, RefExprDTO {
}
