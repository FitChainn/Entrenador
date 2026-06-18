package entrenador.entrenador;

import entrenador.entrenador.assembler.EntrenadorModelAssembler;
import entrenador.entrenador.config.SecurityConfig;
import entrenador.entrenador.controller.EntrenadorController;
import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.filter.RolHeaderFilter;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import entrenador.entrenador.service.EntrenadorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.hateoas.EntityModel;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EntrenadorController.class)
@Import({SecurityConfig.class, RolHeaderFilter.class})
@DisplayName("PRUEBAS UNITARIAS DEL CONTROLLER DE ENTRENADOR")
public class EntrenadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EntrenadorService entrenadorService;

    @MockBean
    private EntrenadorRepository entrenadorRepository;

    @MockBean
    private EntrenadorModelAssembler assembler;

    @Autowired
    private ObjectMapper objectMapper;

    private EntrenadorResponseDTO eResponse;
    private EntrenadorRequestDTO eRequest;
    private Entrenador entrenador;

    @BeforeEach
    void setUp() {
        entrenador = new Entrenador(1L, "12.345.678-9", "PEPE ENTRENADOR", "BOXEO", LocalDate.of(1985, 1, 1), 1L);
        eResponse = new EntrenadorResponseDTO(1L, "12.345.678-9", "PEPE ENTRENADOR", "BOXEO", LocalDate.of(1985, 1, 1), Collections.emptyList(), 1L);
        eRequest = new EntrenadorRequestDTO("12.345.678-9", "PEPE ENTRENADOR", "BOXEO", LocalDate.of(1985, 1, 1), 1L);
        when(assembler.toModel(any(EntrenadorResponseDTO.class))).thenAnswer(inv -> EntityModel.of(inv.getArgument(0)));
    }

    @Test
    @DisplayName("DEBE RETORNAR TODOS LOS ENTRENADORES")
    void GET_obtenerTodos() throws Exception {
        when(entrenadorService.obtenerTodos()).thenReturn(List.of(eResponse));

        mockMvc.perform(get("/v1/entrenadores")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("PEPE ENTRENADOR"));
    }

    @Test
    @DisplayName("DEBE RETORNAR 204 SI NO HAY ENTRENADORES")
    void GET_obtenerTodos_vacio() throws Exception {
        when(entrenadorService.obtenerTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/entrenadores")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DEBE CREAR UN ENTRENADOR (201)")
    void POST_registrar201() throws Exception {
        when(entrenadorService.guardarEntrenador(any(EntrenadorRequestDTO.class))).thenReturn(eResponse);

        mockMvc.perform(post("/v1/entrenadores")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("PEPE ENTRENADOR"));
    }

    @Test
    @DisplayName("DEBE RETORNAR ERROR 400 AL CREAR ENTRENADOR CON DATOS INVALIDOS")
    void POST_validation_registrar() throws Exception {
        EntrenadorRequestDTO reqInvalido = new EntrenadorRequestDTO();

        mockMvc.perform(post("/v1/entrenadores")
                        .header("X-User-Rol", "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DEBE OBTENER UN ENTRENADOR POR ID")
    void GET_obtenerPorId() throws Exception {
        when(entrenadorService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));

        mockMvc.perform(get("/v1/entrenadores/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nombre").value("PEPE ENTRENADOR"))
                .andExpect(jsonPath("$.especialidad").value("BOXEO"));
    }

    @Test
    @DisplayName("DEBE RETORNAR 404 SI ENTRENADOR NO EXISTE")
    void GET_obtenerIdNotFound() throws Exception {
        when(entrenadorService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/v1/entrenadores/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("ENTRENADOR CON EL ID 99 NO ENCONTRADO"));
    }

    @Test
    @DisplayName("DEBE OBTENER ENTRENADOR SIMPLE POR ID")
    void GET_obtenerEntrenadorSimple() throws Exception {
        when(entrenadorRepository.findById(1L)).thenReturn(Optional.of(entrenador));

        mockMvc.perform(get("/v1/entrenadores/1/simple")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("PEPE ENTRENADOR"))
                .andExpect(jsonPath("$.especialidad").value("BOXEO"));
    }

    @Test
    @DisplayName("DEBE OBTENER ENTRENADORES POR ESTABLECIMIENTO")
    void GET_obtenerPorEstablecimiento() throws Exception {
        when(entrenadorService.obtenerPorEstablecimiento(1L)).thenReturn(List.of(eResponse));

        mockMvc.perform(get("/v1/entrenadores/establecimiento/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].establecimientoId").value(1L));
    }

    @Test
    @DisplayName("DEBE RETORNAR 204 SI ESTABLECIMIENTO NO TIENE ENTRENADORES")
    void GET_obtenerPorEstablecimiento_vacio() throws Exception {
        when(entrenadorService.obtenerPorEstablecimiento(99L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/v1/entrenadores/establecimiento/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());
    }


    @Test
    @DisplayName("DEBE ASIGNAR CLIENTE A ENTRENADOR")
    void PUT_asignarCliente() throws Exception {
        doNothing().when(entrenadorService).asignarCliente(1L, 2L);

        mockMvc.perform(put("/v1/entrenadores/1/cliente/2")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN ENTRENADOR")
    void DELETE_eliminar() throws Exception {
        when(entrenadorService.obtenerPorId(1L)).thenReturn(Optional.of(eResponse));
        doNothing().when(entrenadorService).eliminarPorId(1L);

        mockMvc.perform(delete("/v1/entrenadores/1")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNoContent());

        verify(entrenadorService, times(1)).eliminarPorId(1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR 404 AL ELIMINAR ENTRENADOR QUE NO EXISTE")
    void DELETE_eliminar_noExiste() throws Exception {
        when(entrenadorService.obtenerPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/v1/entrenadores/99")
                        .header("X-User-Rol", "ADMIN"))
                .andExpect(status().isNotFound());
    }
}