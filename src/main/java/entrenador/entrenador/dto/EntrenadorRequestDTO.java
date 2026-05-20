package entrenador.entrenador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntrenadorRequestDTO {

    @NotBlank(message = "El RUN es obligatorio.")
    @Size(min = 9, max = 13, message = "El RUN debe tener un formato válido.")
    private String run;

    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "La especialidad es obligatoria.")
    private String especialidad;

    @NotNull(message = "La fecha de nacimiento es obligatoria.")
    private LocalDate fechaNacimiento;
    @NotNull(message = "El establecimiento es obligatorio")
    private Long establecimientoId;
}