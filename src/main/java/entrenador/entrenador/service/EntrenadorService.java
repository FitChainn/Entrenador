package entrenador.entrenador.service;

import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EntrenadorService {
    private EntrenadorRepository entrenadorRepository;

    private EntrenadorResponseDTO mapToDTO(Entrenador entrenador) {
        return new EntrenadorResponseDTO(
                entrenador.getId(),
                entrenador.getRun(),
                entrenador.getNombre(),
                entrenador.getEspecialidad(),
                entrenador.getFechaNacimiento(),
                null
        );
    }

    public List<EntrenadorResponseDTO> obtenerTodos() {
        return entrenadorRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EntrenadorResponseDTO> obtenerPorId(Long id) {
        return entrenadorRepository.findById(id).map(entrenador -> {
            EntrenadorResponseDTO dto = mapToDTO(entrenador);

            try {
                // Viajamos al microservicio de Clientes a buscar los alumnos asignados
                List<ClienteResponseDTO> alumnosAsignados = webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8081/v1/clientes/entrenador/{id}", id)
                        .retrieve()
                        .bodyToFlux(ClienteResponseDTO.class)
                        .collectList()
                        .block(); // Llamada síncrona

                dto.setAlumnos(alumnosAsignados);
            } catch (Exception e) {
                // Si el microservicio de clientes está apagado, se envía la lista vacía para que no se caiga todo el sistema
                dto.setAlumnos(List.of());
            }

            return dto;
        });
    }

    public EntrenadorResponseDTO guardarEntrenador(EntrenadorRequestDTO dto) {
        Entrenador entrenador = new Entrenador();
        entrenador.setRun(dto.getRun());
        entrenador.setNombre(dto.getNombre());
        entrenador.setEspecialidad(dto.getEspecialidad());
        entrenador.setFechaNacimiento(dto.getFechaNacimiento());

        return mapToDTO(entrenadorRepository.save(entrenador));
    }

    public void eliminarPorId (Long id){entrenadorRepository.deleteById(id);}

}
