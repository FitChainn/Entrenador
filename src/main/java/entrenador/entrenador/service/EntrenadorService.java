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
        // Antes: WebClient inline con URL incorrecta
        // Ahora: clase separada con URL correcta
        dto.setAlumnos(clienteClient.obtenerAlumnosPorEntrenador(entrenador.getId()));
        return dto;
    }

    public List<EntrenadorResponseDTO> obtenerTodos() {
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
        Entrenador guardado = entrenadorRepository.save(entrenador);
        log.info("Entrenador guardado con ID: {}", guardado.getId());
        return mapToDTO(guardado);
    }


    public void asignarCliente(Long entrenadorId, Long clienteId) {
        log.info("Asignando cliente ID: {} al entrenador ID: {}", clienteId, entrenadorId);
        // Verificar que el entrenador existe
        entrenadorRepository.findById(entrenadorId)
                .orElseThrow(() -> new NoSuchElementException("Entrenador no encontrado con ID: " + entrenadorId));
        // Verificar que el cliente existe
        clienteClient.verificarClienteExiste(clienteId);
        // Llamar al endpoint interno de Cliente para actualizar su entrenadorId
        clienteClient.asignarEntrenadorACliente(clienteId, entrenadorId);
        log.info("Cliente {} asignado al entrenador {} correctamente", clienteId, entrenadorId);
    }

    public void eliminarPorId(Long id) {
        log.info("Eliminando entrenador con ID: {}", id);
        entrenadorRepository.deleteById(id);
    }
}