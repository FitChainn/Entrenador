package entrenador.entrenador.service;

import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EntrenadorService {
    @Autowired
    private EntrenadorRepository entrenadorRepository;

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

    public Entrenador saveEntrenador(Entrenador entrenador){
        return entrenadorRepository.save(entrenador);
    }
    public void eliminarPorId (Long id){entrenadorRepository.deleteById(id);}

}
