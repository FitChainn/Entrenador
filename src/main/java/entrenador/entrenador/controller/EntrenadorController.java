package entrenador.entrenador.controller;


import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.service.EntrenadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrenadores")
public class EntrenadorController {

    private EntrenadorService entrenadorService;

    @GetMapping
    public ResponseEntity <List<EntrenadorResponseDTO>> obtenerTodos(){
        List<EntrenadorResponseDTO> entrenadores = entrenadorService.obtenerTodos();
        if (entrenadores.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(entrenadores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntrenadorResponseDTO> obtenerEntrenador (@PathVariable Long id){
        return entrenadorService.obtenerPorId(id).
                map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Entrenador> RegistrarEntrenador(@RequestBody Entrenador nuevo) {
        return ResponseEntity.status(201).body(entrenadorService.saveEntrenador(nuevo));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> eliminar (@PathVariable Long id){
        if(entrenadorService.obtenerEntrenador(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        entrenadorService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
