package entrenador.entrenador;

import entrenador.entrenador.WebClient.ClienteClient;
import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import entrenador.entrenador.service.EntrenadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PRUEBAS UNITARIAS DEL SERVICE DE ENTRENADOR")
public class EntrenadorServiceTest {

    @Mock
    private EntrenadorRepository entrenadorRepository;

    @Mock
    private ClienteClient clienteClient;

    @InjectMocks
    private EntrenadorService entrenadorService;

    private Entrenador entrenador;
    private EntrenadorRequestDTO eRequest;

    @BeforeEach
    void setUp() {
        entrenador = new Entrenador(1L, "12.345.678-9", "PEPE ENTRENADOR", "BOXEO", LocalDate.of(1985, 1, 1), 1L);
        eRequest = new EntrenadorRequestDTO("12.345.678-9", "PEPE ENTRENADOR", "BOXEO", LocalDate.of(1985, 1, 1), 1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR TODOS LOS ENTRENADORES")
    void shouldReturnTodosLosEntrenadores() {
        when(entrenadorRepository.findAll()).thenReturn(List.of(entrenador));
        when(clienteClient.obtenerAlumnosPorEntrenador(1L)).thenReturn(Collections.emptyList());

        List<EntrenadorResponseDTO> result = entrenadorService.obtenerTodos();

        assertFalse(result.isEmpty());
        assertEquals("PEPE ENTRENADOR", result.get(0).getNombre());
        verify(entrenadorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("DEBE RETORNAR UN ENTRENADOR POR ID")
    void shouldReturnEntrenadorById() {
        when(entrenadorRepository.findById(1L)).thenReturn(Optional.of(entrenador));
        when(clienteClient.obtenerAlumnosPorEntrenador(1L)).thenReturn(Collections.emptyList());

        Optional<EntrenadorResponseDTO> result = entrenadorService.obtenerPorId(1L);

        assertTrue(result.isPresent());
        assertEquals("PEPE ENTRENADOR", result.get().getNombre());
        verify(entrenadorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI ENTRENADOR NO EXISTE")
    void shouldReturnEmptyWhenEntrenadorNotFound() {
        when(entrenadorRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<EntrenadorResponseDTO> result = entrenadorService.obtenerPorId(99L);

        assertFalse(result.isPresent());
        verify(entrenadorRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("DEBE GUARDAR UN ENTRENADOR")
    void shouldGuardarEntrenador() {
        when(entrenadorRepository.save(any(Entrenador.class))).thenReturn(entrenador);

        EntrenadorResponseDTO result = entrenadorService.guardarEntrenador(eRequest);

        assertNotNull(result);
        assertEquals("PEPE ENTRENADOR", result.getNombre());
        assertEquals("BOXEO", result.getEspecialidad());
        assertEquals("12.345.678-9", result.getRun());
        verify(entrenadorRepository, times(1)).save(any(Entrenador.class));
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN ENTRENADOR POR ID")
    void shouldEliminarEntrenador() {
        doNothing().when(entrenadorRepository).deleteById(1L);

        entrenadorService.eliminarPorId(1L);

        verify(entrenadorRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR ENTRENADORES POR ESTABLECIMIENTO")
    void shouldReturnEntrenadoresByEstablecimiento() {
        when(entrenadorRepository.findByEstablecimientoId(1L)).thenReturn(List.of(entrenador));
        when(clienteClient.obtenerAlumnosPorEntrenador(1L)).thenReturn(Collections.emptyList());

        List<EntrenadorResponseDTO> result = entrenadorService.obtenerPorEstablecimiento(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEstablecimientoId());
        verify(entrenadorRepository, times(1)).findByEstablecimientoId(1L);
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI ESTABLECIMIENTO NO TIENE ENTRENADORES")
    void shouldReturnEmptyListWhenEstablecimientoHasNoEntrenadores() {
        when(entrenadorRepository.findByEstablecimientoId(99L)).thenReturn(Collections.emptyList());

        List<EntrenadorResponseDTO> result = entrenadorService.obtenerPorEstablecimiento(99L);

        assertTrue(result.isEmpty());
        verify(entrenadorRepository, times(1)).findByEstablecimientoId(99L);
    }


    @Test
    @DisplayName("DEBE ASIGNAR CLIENTE A ENTRENADOR")
    void shouldAsignarCliente() {
        when(entrenadorRepository.findById(1L)).thenReturn(Optional.of(entrenador));
        doNothing().when(clienteClient).verificarClienteExiste(2L);
        doNothing().when(clienteClient).asignarEntrenadorACliente(2L, 1L);

        assertDoesNotThrow(() -> entrenadorService.asignarCliente(1L, 2L));

        verify(entrenadorRepository, times(1)).findById(1L);
        verify(clienteClient, times(1)).verificarClienteExiste(2L);
        verify(clienteClient, times(1)).asignarEntrenadorACliente(2L, 1L);
    }

    @Test
    @DisplayName("DEBE LANZAR EXCEPCION AL ASIGNAR CLIENTE A ENTRENADOR QUE NO EXISTE")
    void shouldThrowWhenAsignarClienteEntrenadorNotFound() {
        when(entrenadorRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> entrenadorService.asignarCliente(99L, 2L));

        assertEquals("Entrenador no encontrado con ID: 99", ex.getMessage());
        verify(clienteClient, never()).verificarClienteExiste(any());
        verify(clienteClient, never()).asignarEntrenadorACliente(any(), any());
    }
}