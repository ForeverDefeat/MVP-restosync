package com.restosync.comandas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CancelOrderRequest {

    @NotBlank(message = "El motivo de cancelacion es obligatorio")
    @Size(max = 300, message = "El motivo de cancelacion no puede superar 300 caracteres")
    private String reason;
}
