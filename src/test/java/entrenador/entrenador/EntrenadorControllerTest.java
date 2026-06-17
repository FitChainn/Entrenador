package entrenador.entrenador;

import entrenador.entrenador.controller.EntrenadorController;
import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.repository.EntrenadorRepository;
import entrenador.entrenador.service.EntrenadorService;
import entrenador.entrenador.config.SecurityConfig;
import entrenador.entrenador.filter.RolHeaderFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntrenadorController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
public class EntrenadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntrenadorService entrenadorService;

    @MockBean
    private EntrenadorRepository entrenadorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private EntrenadorResponseDTO eResponse;
    private EntrenadorRequestDTO eRequest;

    @BeforeEach
    void setUp() {
        eResponse = new EntrenadorResponseDTO(1L, "12.345.678-9", "PEPE ENTRENADOR", "BOXEO", LocalDate.of(1985, 1, 1), Collections.emptyList(), 1L);
        eRequest = new EntrenadorRequestDTO("12.345.678-9", "PEPE ENTRENADOR", "BOXEO", LocalDate.of(1985, 1, 1), 1L);
    }

    @Test
    void Get_obtenerTodos() throws Exception {
        when(entrenadorService.obtenerTodos()).thenReturn(List.of(eResponse));

        mockMvc.perform(get("/v1/entrenadores")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("PEPE ENTRENADOR"));
    }

    @Test
    void Post_registrar201() throws Exception {
        when(entrenadorService.guardarEntrenador(any(EntrenadorRequestDTO.class))).thenReturn(eResponse);

        mockMvc.perform(post("/v1/entrenadores")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void Get_obtenerPorId() throws Exception {
        when(entrenadorService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));

        mockMvc.perform(get("/v1/entrenadores/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("PEPE ENTRENADOR"));
    }

    @Test
    void Delete_eliminar() throws Exception {
        when(entrenadorService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));

        mockMvc.perform(delete("/v1/entrenadores/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }
}
