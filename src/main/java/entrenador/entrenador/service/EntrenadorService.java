package entrenador.entrenador.service;

import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EntrenadorService {
    @Autowired
    private  EntrenadorRepository entrenadorRepository;
    @Autowired
    private WebClient.Builder webClientBuilder;

    private EntrenadorResponseDTO mapToDTO(Entrenador entrenador) {
        return new EntrenadorResponseDTO(
                entrenador.getId(),
                entrenador.getRun(),
                entrenador.getNombre(),
                entrenador.getEspecialidad(),
                entrenador.getFechaNacimiento(),
                null,
                entrenador.getEstablecimientoId()
        );
    }
    private EntrenadorResponseDTO mapToDTOConAlumnos(Entrenador entrenador) {
        EntrenadorResponseDTO dto = mapToDTO(entrenador);
        try {
            List<Object> alumnos = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/v1/clientes/entrenador/{id}/simple", entrenador.getId())
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
            dto.setAlumnos(alumnos);
        } catch (Exception e) {
            dto.setAlumnos(List.of());
        }
        return dto;
    }public List<EntrenadorResponseDTO> obtenerTodos() {
        return entrenadorRepository.findAll()
                .stream()
                .map(this::mapToDTOConAlumnos)
                .collect(Collectors.toList());
    }

    public Optional<EntrenadorResponseDTO> obtenerPorId(Long id) {
        return entrenadorRepository.findById(id).map(this::mapToDTOConAlumnos);
    }

    public List<EntrenadorResponseDTO> obtenerPorEstablecimiento(Long establecimientoId) {
        return entrenadorRepository.findByEstablecimientoId(establecimientoId)
                .stream()
                .map(this::mapToDTOConAlumnos)
                .collect(Collectors.toList());
    }
    public EntrenadorResponseDTO guardarEntrenador(EntrenadorRequestDTO dto) {
        log.info("Guardando entrenador: {}", dto.getNombre());
        Entrenador entrenador = new Entrenador();
        entrenador.setRun(dto.getRun());
        entrenador.setNombre(dto.getNombre());
        entrenador.setEspecialidad(dto.getEspecialidad());
        entrenador.setFechaNacimiento(dto.getFechaNacimiento());
        entrenador.setEstablecimientoId(dto.getEstablecimientoId());
        log.info("Entrenador guardado con ID: {}", entrenador.getId());
        return mapToDTO(entrenadorRepository.save(entrenador));
    }
    public EntrenadorResponseDTO asignarEstablecimiento(Long entrenadorId, Long establecimientoId) {
        log.info("Asignando establecimiento ID: {} al entrenador ID: {}", establecimientoId, entrenadorId);
        Entrenador entrenador = entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
        entrenador.setEstablecimientoId(establecimientoId);
        return mapToDTOConAlumnos(entrenadorRepository.save(entrenador));
    }
    public void eliminarPorId (Long id){
        log.info("Eliminando entrenador con ID: {}", id);
        entrenadorRepository.deleteById(id);}

}
