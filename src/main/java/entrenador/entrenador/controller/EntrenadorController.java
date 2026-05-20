package entrenador.entrenador.controller;

import entrenador.entrenador.dto.EntrenadorCliente;
import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import entrenador.entrenador.service.EntrenadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/entrenadores")
public class EntrenadorController {

    @Autowired
    private EntrenadorService entrenadorService;
    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping
    public ResponseEntity<List<EntrenadorResponseDTO>> obtenerTodos() {
        List<EntrenadorResponseDTO> entrenadores = entrenadorService.obtenerTodos();
        if (entrenadores.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(entrenadores);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntrenadorResponseDTO> obtenerEntrenador(@PathVariable Long id) {
        return entrenadorService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EntrenadorResponseDTO> RegistrarEntrenador(@RequestBody EntrenadorRequestDTO nuevo) {
        return ResponseEntity.status(201).body(entrenadorService.guardarEntrenador(nuevo));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (entrenadorService.obtenerPorId(id).isEmpty()) return ResponseEntity.notFound().build();
        entrenadorService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/{id}/simple")
    public ResponseEntity<EntrenadorCliente> obtenerEntrenadorSimple(@PathVariable Long id) {
        Entrenador entrenador = entrenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
        return ResponseEntity.ok(new EntrenadorCliente(entrenador.getNombre(), entrenador.getEspecialidad()));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ENTRENADOR', 'CLIENTE')")
    @GetMapping("/establecimiento/{establecimientoId}")
    public ResponseEntity<List<EntrenadorResponseDTO>> obtenerPorEstablecimiento(
            @PathVariable Long establecimientoId) {
        List<EntrenadorResponseDTO> entrenadores = entrenadorService.obtenerPorEstablecimiento(establecimientoId);
        if (entrenadores.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(entrenadores);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{entrenadorId}/establecimiento/{establecimientoId}")
    public ResponseEntity<EntrenadorResponseDTO> asignarEstablecimiento(
            @PathVariable Long entrenadorId,
            @PathVariable Long establecimientoId) {
        return ResponseEntity.ok(entrenadorService.asignarEstablecimiento(entrenadorId, establecimientoId));
    }
}
