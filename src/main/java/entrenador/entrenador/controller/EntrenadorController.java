package entrenador.entrenador.controller;


import entrenador.entrenador.service.EntrenadorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/libros")
public class EntrenadorController {

    private EntrenadorService entrenadorService;

    @GetMapping
    public ResponseEntity<EntrenadorService> obtenerTodos(){
        return ResponseEntity.ok(entrenadorService.obtenerTodos());
    }

    //
}
