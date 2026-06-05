package entrenador.entrenador.controller;

import entrenador.entrenador.dto.EntrenadorCliente;
import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import entrenador.entrenador.service.EntrenadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Tag(name = "ENTRENADORES", description = "GESTIÓN DE ENTRENADORES")
@Slf4j
@RestController
@RequestMapping("/v1/entrenadores")
public class EntrenadorController {

    @Autowired
    private EntrenadorService entrenadorService;
    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Operation(summary = "OBTENER TODOS LOS ENTRENADORES", description = "Retorna la lista de todos los entrenadores. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "204", description = "NO HAY ENTRENADORES REGISTRADOS"),
            @ApiResponse(responseCode = "403", description = "SIN PERMISOS SUFICIENTES")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<EntrenadorResponseDTO>> obtenerTodos() {
        List<EntrenadorResponseDTO> entrenadores = entrenadorService.obtenerTodos();
        if (entrenadores.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(entrenadores);
    }

    @Operation(summary = "OBTENER ENTRENADOR POR ID", description = "Retorna un entrenador específico por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ENTRENADOR ENCONTRADO"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR NO ENCONTRADO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntrenadorResponseDTO> obtenerEntrenador(@PathVariable Long id) {
        return entrenadorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "REGISTRAR ENTRENADOR", description = "Crea un nuevo entrenador. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "ENTRENADOR REGISTRADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "400", description = "DATOS INVÁLIDOS"),
            @ApiResponse(responseCode = "403", description = "SIN PERMISOS SUFICIENTES")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EntrenadorResponseDTO> registrarEntrenador(@Valid @RequestBody EntrenadorRequestDTO nuevo) {
        return ResponseEntity.status(201).body(entrenadorService.guardarEntrenador(nuevo));
    }

    @Operation(summary = "ELIMINAR ENTRENADOR", description = "Elimina un entrenador por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "ENTRENADOR ELIMINADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR NO ENCONTRADO")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (entrenadorService.obtenerPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        entrenadorService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "OBTENER ENTRENADOR SIMPLE (INTERNO)", description = "Retorna nombre y especialidad de un entrenador. Endpoint interno usado por WebClient. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ENTRENADOR ENCONTRADO"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR NO ENCONTRADO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}/simple")
    public ResponseEntity<EntrenadorCliente> obtenerEntrenadorSimple(@PathVariable Long id) {
        Entrenador entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Entrenador no encontrado con ID: " + id));
        return ResponseEntity.ok(new EntrenadorCliente(entrenador.getNombre(), entrenador.getEspecialidad()));
    }

    @Operation(summary = "OBTENER ENTRENADORES POR ESTABLECIMIENTO", description = "Retorna todos los entrenadores de un establecimiento. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "204", description = "NO HAY ENTRENADORES PARA ESE ESTABLECIMIENTO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/establecimiento/{establecimientoId}")
    public ResponseEntity<List<EntrenadorResponseDTO>> obtenerPorEstablecimiento(
            @PathVariable Long establecimientoId) {
        List<EntrenadorResponseDTO> entrenadores = entrenadorService.obtenerPorEstablecimiento(establecimientoId);
        if (entrenadores.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(entrenadores);
    }

    @Operation(summary = "ASIGNAR ESTABLECIMIENTO A ENTRENADOR", description = "Asigna un establecimiento a un entrenador. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ESTABLECIMIENTO ASIGNADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR O ESTABLECIMIENTO NO ENCONTRADO")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{entrenadorId}/establecimiento/{establecimientoId}")
    public ResponseEntity<EntrenadorResponseDTO> asignarEstablecimiento(
            @PathVariable Long entrenadorId,
            @PathVariable Long establecimientoId) {
        return ResponseEntity.ok(entrenadorService.asignarEstablecimiento(entrenadorId, establecimientoId));
    }

    @Operation(summary = "ASIGNAR CLIENTE A ENTRENADOR", description = "Asigna un cliente a un entrenador. Acceso: ADMIN, ENTRENADOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CLIENTE ASIGNADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR O CLIENTE NO ENCONTRADO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR')")
    @PutMapping("/{entrenadorId}/cliente/{clienteId}")
    public ResponseEntity<?> asignarCliente(
            @PathVariable Long entrenadorId,
            @PathVariable Long clienteId) {
        entrenadorService.asignarCliente(entrenadorId, clienteId);
        return ResponseEntity.ok("Cliente " + clienteId + " asignado al entrenador " + entrenadorId);
    }
}