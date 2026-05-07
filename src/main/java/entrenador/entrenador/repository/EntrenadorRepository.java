package entrenador.entrenador.repository;

import entrenador.entrenador.model.Entrenador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface EntrenadorRepository extends JpaRepository<Entrenador, Long> {

    List<Entrenador> buscarPorNombre(String nombre);

    List<Entrenador> buscarPorRun(String run);

    @Query("SELECT e FROM Entrenador e WHERE e.entrenador.id = :entrenadorId")
    List<Entrenador> buscarPorId(@Param("entrenadorId") Long entrenadorId);
}
