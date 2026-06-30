package com.restosync.comandas.dto.request;

import com.restosync.comandas.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;
 
@Data
public class CreateUserRequest {
 
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;
 
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Size(max = 150, message = "El email no puede superar 150 caracteres")
    private String email;
 
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 128, message = "La contraseña debe tener entre 8 y 128 caracteres")
    private String password;
 
    @NotNull(message = "El rol es obligatorio")
    private UserRole role;
}
 
