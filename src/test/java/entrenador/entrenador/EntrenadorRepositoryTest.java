package entrenador.entrenador;

import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("PRUEBAS UNITARIAS DEL REPOSITORY DE ENTRENADOR")
public class EntrenadorRepositoryTest {

    @Autowired
    private EntrenadorRepository repo;

    @Autowired
    private TestEntityManager em;

    private Entrenador crearEntrenador(String run, String nombre, String especialidad, Long establecimientoId) {
        Entrenador e = new Entrenador();
        e.setRun(run);
        e.setNombre(nombre);
        e.setEspecialidad(especialidad);
        e.setFechaNacimiento(LocalDate.of(1990, 5, 20));
        e.setEstablecimientoId(establecimientoId);
        return em.persistAndFlush(e);
    }

    @Test
    @DisplayName("DEBE ENCONTRAR UN ENTRENADOR POR ID")
    void findById_ShouldReturnEntrenador() {
        Entrenador e = crearEntrenador("12.345.678-9", "PEPE ENTRENADOR", "BOXEO", 1L);

        Optional<Entrenador> result = repo.findById(e.getId());

        assertTrue(result.isPresent());
        assertEquals("PEPE ENTRENADOR", result.get().getNombre());
    }

    @Test
    @DisplayName("DEBE RETORNAR VACIO SI ENTRENADOR NO EXISTE")
    void findById_ShouldReturnEmpty() {
        Optional<Entrenador> result = repo.findById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("DEBE ENCONTRAR ENTRENADORES POR ESTABLECIMIENTO ID")
    void findByEstablecimientoId_ShouldReturnEntrenadores() {
        crearEntrenador("12.345.678-9", "PEPE ENTRENADOR", "BOXEO", 1L);
        crearEntrenador("9.876.543-2", "JUAN PEREZ", "FITNESS", 1L);
        crearEntrenador("1.111.111-1", "OTRO ENTRENADOR", "YOGA", 2L);

        List<Entrenador> result = repo.findByEstablecimientoId(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("DEBE RETORNAR LISTA VACIA SI ESTABLECIMIENTO NO TIENE ENTRENADORES")
    void findByEstablecimientoId_ShouldReturnEmpty() {
        List<Entrenador> result = repo.findByEstablecimientoId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DEBE GUARDAR UN ENTRENADOR")
    void save_ShouldPersistEntrenador() {
        Entrenador e = new Entrenador();
        e.setRun("12.345.678-9");
        e.setNombre("PEPE ENTRENADOR");
        e.setEspecialidad("BOXEO");
        e.setFechaNacimiento(LocalDate.of(1990, 5, 20));
        e.setEstablecimientoId(1L);

        Entrenador saved = repo.save(e);

        assertNotNull(saved.getId());
        assertEquals("PEPE ENTRENADOR", saved.getNombre());
    }

    @Test
    @DisplayName("DEBE ELIMINAR UN ENTRENADOR")
    void delete_ShouldRemoveEntrenador() {
        Entrenador e = crearEntrenador("12.345.678-9", "PEPE ENTRENADOR", "BOXEO", 1L);
        Long id = e.getId();

        repo.deleteById(id);
        em.flush();

        assertFalse(repo.findById(id).isPresent());
    }
}