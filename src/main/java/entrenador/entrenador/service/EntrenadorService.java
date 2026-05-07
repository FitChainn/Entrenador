package entrenador.entrenador.service;

import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntrenadorService {

    private final EntrenadorRepository entrenadorRepository;

    private EntrenadorResponseDTO mapToDTO(Entrenador entrenador) {
        return new EntrenadorResponseDTO(
                entrenador.getId(),
                entrenador.getRun(),
                entrenador.getRun(),
                entrenador.getFechaNacimiento()
        );
    }

    public List<EntrenadorResponseDTO> obtenerTodos() {
        return entrenadorRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EntrenadorResponseDTO> obtenerPorId(Long id) {
        return entrenadorRepository.findById(id).map(this::mapToDTO);
    }

    public EntrenadorResponseDTO guardar(EntrenadorRequestDTO dto) {
        Entrenador entrenador = entrenadorRepository
                .findById(dto.getEntrenadorId())
                .orElseThrow(() -> new RuntimeException(
                        "Entrenador no encontrado con id" + dto.getEntrenadorId()));
        Entrenador entrenador = new Entrenador(
                null,
                dto.getRun(),
                dto.getNombre(),
                dto.getFechaNacimiento()

        );
        return mapToDTO(entrenadorRepository.save(entrenador));
    }

    public Optional<EntrenadorResponseDTO> actualizar(Long id, EntrenadorRequestDTO dto) {
        return entrenadorRepository.findById(id).map(existente ->
        {
            Entrenador entrenador = entrenadorRepository
                    .findById(dto.getEntrenadorId())
                    .orElseThrow(() -> new RuntimeException(
                            "Entrenador no econtrado con id: " + dto.getEntrenadorId()));
            existente.setRun(dto.getRun());
            existente.setNombre(dto.getNombre());
            existente.setFechaNacimiento(dto.getFechaNacimiento());
            return mapToDTO(entrenadorRepository.save(existente));
        });
    }

    public void eliminar(Long id) { entrenadorRepository.deleteById(id);}

    public List<EntrenadorResponseDTO> buscarPorNombre(String texto) {
        return entrenadorRepository.buscarPorNombre(texto)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

}
