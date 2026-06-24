package entrenador.entrenador.controller;

import entrenador.entrenador.assembler.EntrenadorModelAssembler;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "ENTRENADORES", description = "GESTIÓN DE ENTRENADORES")
@Slf4j
@RestController
@RequestMapping("/v1/entrenadores")
public class EntrenadorController {

    @Autowired
    private EntrenadorModelAssembler assembler;

    @Autowired
    private EntrenadorService entrenadorService;

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Operation(summary = "OBTENER TODOS LOS ENTRENADORES", description = "Retorna la lista de todos los entrenadores. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "LISTA OBTENIDA CON ÉXITO"),
            @ApiResponse(responseCode = "403", description = "SIN PERMISOS SUFICIENTES")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EntrenadorResponseDTO>>> obtenerTodos() {
        log.info("GET /v1/entrenadores - LISTAR TODOS");
        List<EntityModel<EntrenadorResponseDTO>> entrenadores = entrenadorService.obtenerTodos().stream()
                .map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(entrenadores,
                linkTo(methodOn(EntrenadorController.class).obtenerTodos()).withSelfRel()));
    }

    @Operation(summary = "OBTENER ENTRENADOR POR ID", description = "Retorna un entrenador específico por su ID. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ENTRENADOR ENCONTRADO"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR NO ENCONTRADO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EntrenadorResponseDTO>> obtenerEntrenador(@PathVariable Long id) {
        log.info("GET /v1/entrenadores/{} - BUSCAR POR ID", id);
        EntrenadorResponseDTO entrenador = entrenadorService.obtenerPorId(id)
                .orElseThrow(() -> new NoSuchElementException("ENTRENADOR CON EL ID " + id + " NO ENCONTRADO"));
        return ResponseEntity.ok(assembler.toModel(entrenador));
    }

    @Operation(summary = "REGISTRAR ENTRENADOR", description = "Crea un nuevo entrenador. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "ENTRENADOR REGISTRADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "400", description = "DATOS INVÁLIDOS"),
            @ApiResponse(responseCode = "403", description = "SIN PERMISOS SUFICIENTES")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EntityModel<EntrenadorResponseDTO>> registrarEntrenador(@Valid @RequestBody EntrenadorRequestDTO nuevo) {
        log.info("POST /v1/entrenadores - REGISTRAR ENTRENADOR nombre={}", nuevo.getNombre());
        return ResponseEntity.status(201).body(assembler.toModel(entrenadorService.guardarEntrenador(nuevo)));
    }

    @Operation(summary = "ELIMINAR ENTRENADOR", description = "Elimina un entrenador por su ID. Acceso: ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "ENTRENADOR ELIMINADO EXITOSAMENTE"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR NO ENCONTRADO")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<EntityModel<EntrenadorResponseDTO>> eliminar(@PathVariable Long id) {
        log.info("DELETE /v1/entrenadores/{} - ELIMINAR ENTRENADOR", id);
        EntrenadorResponseDTO entrenador = entrenadorService.obtenerPorId(id)
                .orElseThrow(() -> new NoSuchElementException("ENTRENADOR CON ID " + id + " NO ENCONTRADO"));
        entrenadorService.eliminarPorId(id);
        return ResponseEntity.ok(assembler.toModel(entrenador));
    }

    @Operation(summary = "OBTENER ENTRENADOR SIMPLE (INTERNO)", description = "Retorna nombre y especialidad de un entrenador. Endpoint interno usado por WebClient. Acceso: ADMIN, ENTRENADOR, CLIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ENTRENADOR ENCONTRADO"),
            @ApiResponse(responseCode = "404", description = "ENTRENADOR NO ENCONTRADO")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}/simple")
    public ResponseEntity<EntrenadorCliente> obtenerEntrenadorSimple(@PathVariable Long id) {
        log.info("GET /v1/entrenadores/{}/simple - BUSCAR SIMPLE (INTERNO)", id);
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
    public ResponseEntity<CollectionModel<EntityModel<EntrenadorResponseDTO>>> obtenerPorEstablecimiento(
            @PathVariable Long establecimientoId) {
        log.info("GET /v1/entrenadores/establecimiento/{} - BUSCAR POR ESTABLECIMIENTO", establecimientoId);
        List<EntityModel<EntrenadorResponseDTO>> entrenadores = entrenadorService.obtenerPorEstablecimiento(establecimientoId)
                .stream().map(assembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(entrenadores,
                linkTo(methodOn(EntrenadorController.class).obtenerPorEstablecimiento(establecimientoId)).withSelfRel()));
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
        log.info("PUT /v1/entrenadores/{}/cliente/{} - ASIGNAR CLIENTE", entrenadorId, clienteId);
        entrenadorService.asignarCliente(entrenadorId, clienteId);
        return ResponseEntity.ok("Cliente " + clienteId + " asignado al entrenador " + entrenadorId);
    }
}
