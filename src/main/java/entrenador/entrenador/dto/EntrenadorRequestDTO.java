package entrenador.entrenador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorRequestDTO {

    @NotBlank(message = "El run no puede estar vacio")
    private String run;

    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;

    @NotBlank(message = "La fecha de nacimiento no pueda estar vacia")
    private Date fechaNacimiento;

    @NotNull(message = "El entrenadorId es obligatorio")
    private Long entrenadorId;
}
