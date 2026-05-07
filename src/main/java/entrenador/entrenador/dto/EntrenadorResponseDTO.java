package entrenador.entrenador.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntrenadorResponseDTO {

    private Long id;
    private String run;
    private String nombre;
    private Date fechaNacimiento;

}
