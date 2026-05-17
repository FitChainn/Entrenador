package entrenador.entrenador.controller;


import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.service.EntrenadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrenadores")
public class EntrenadorController {
    @Autowired
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
    public ResponseEntity<EntrenadorResponseDTO> RegistrarEntrenador(@RequestBody EntrenadorRequestDTO nuevo) {
        return ResponseEntity.status(201).body(entrenadorService.guardarEntrenador(nuevo));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> eliminar (@PathVariable Long id){
        if(entrenadorService.obtenerPorId(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        entrenadorService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }
}
