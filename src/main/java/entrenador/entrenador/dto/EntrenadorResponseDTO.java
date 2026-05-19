package entrenador.entrenador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntrenadorResponseDTO {
    private Long id;
    private String run;
    private String nombre;
    private String especialidad;
    private LocalDate fechaNacimiento;
    private List<Object> alumnos; // Se llena mediante comunicación con microservicio Cliente
    private Long establecimientoId;
}