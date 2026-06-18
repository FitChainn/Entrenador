package entrenador.entrenador.service;

import entrenador.entrenador.WebClient.ClienteClient;
import entrenador.entrenador.dto.EntrenadorRequestDTO;
import entrenador.entrenador.dto.EntrenadorResponseDTO;
import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class EntrenadorService {

    @Autowired
    private EntrenadorRepository entrenadorRepository;

    @Autowired
    private ClienteClient clienteClient;

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
            dto.setAlumnos(clienteClient.obtenerAlumnosPorEntrenador(entrenador.getId()));
            log.info("ALUMNOS DEL ENTRENADOR CON ID {} OBTENIDOS CORRECTAMENTE", entrenador.getId());
        } catch (Exception e) {
            log.warn("NO SE PUDIERON OBTENER LOS ALUMNOS DEL ENTRENADOR CON ID {}: {}", entrenador.getId(), e.getMessage());
        }
        return dto;
    }

    public List<EntrenadorResponseDTO> obtenerTodos() {
        log.info("LISTANDO TODOS LOS ENTRENADORES");
        return entrenadorRepository.findAll()
                .stream()
                .map(this::mapToDTOConAlumnos)
                .collect(Collectors.toList());
    }

    public Optional<EntrenadorResponseDTO> obtenerPorId(Long id) {
        log.info("BUSCANDO ENTRENADOR CON ID: {}", id);
        return entrenadorRepository.findById(id).map(e -> {
            log.info("ENTRENADOR CON ID {} ENCONTRADO", id);
            return mapToDTOConAlumnos(e);
        });
    }

    public List<EntrenadorResponseDTO> obtenerPorEstablecimiento(Long establecimientoId) {
        log.info("BUSCANDO ENTRENADORES DEL ESTABLECIMIENTO CON ID: {}", establecimientoId);
        return entrenadorRepository.findByEstablecimientoId(establecimientoId)
                .stream()
                .map(this::mapToDTOConAlumnos)
                .collect(Collectors.toList());
    }

    public EntrenadorResponseDTO guardarEntrenador(EntrenadorRequestDTO dto) {
        log.info("GUARDANDO ENTRENADOR: {}", dto.getNombre());
        Entrenador entrenador = new Entrenador();
        entrenador.setRun(dto.getRun());
        entrenador.setNombre(dto.getNombre());
        entrenador.setEspecialidad(dto.getEspecialidad());
        entrenador.setFechaNacimiento(dto.getFechaNacimiento());
        entrenador.setEstablecimientoId(dto.getEstablecimientoId());
        Entrenador guardado = entrenadorRepository.save(entrenador);
        log.info("ENTRENADOR GUARDADO EXITOSAMENTE CON ID: {}", guardado.getId());
        return mapToDTO(guardado);
    }

    public void asignarCliente(Long entrenadorId, Long clienteId) {
        log.info("ASIGNANDO CLIENTE ID: {} AL ENTRENADOR ID: {}", clienteId, entrenadorId);
        entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new NoSuchElementException("Entrenador no encontrado con ID: " + entrenadorId));
        clienteClient.verificarClienteExiste(clienteId);
        clienteClient.asignarEntrenadorACliente(clienteId, entrenadorId);
        log.info("CLIENTE {} ASIGNADO AL ENTRENADOR {} EXITOSAMENTE", clienteId, entrenadorId);
    }

    public void eliminarPorId(Long id) {
        log.info("ELIMINANDO ENTRENADOR CON ID: {}", id);
        entrenadorRepository.deleteById(id);
        log.info("ENTRENADOR CON ID {} ELIMINADO EXITOSAMENTE", id);
    }
}
