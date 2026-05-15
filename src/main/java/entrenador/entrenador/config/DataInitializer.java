package entrenador.entrenador.config;

import entrenador.entrenador.model.Entrenador;
import entrenador.entrenador.repository.EntrenadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EntrenadorRepository entrenadorRepository;

    @Override
    public void run(String... args) {
        if (entrenadorRepository.count() > 0) {
            log.info(">>> DataInitializer: la BD ya tiene datos, se omite la carga inicial.");
            return;
        }

        log.info(">>> DataInitializer: BD vacía detectada, insertando datos de prueba...");

        entrenadorRepository.save(new Entrenador(null,"19.482.021-0", "Guillermo Salas", LocalDate.parse(1980,12,10)));
        entrenadorRepository.save(new Entrenador(null,"20.493.492-1", "Marcelo Donal", LocalDate.parse(2001,1,16)));
        entrenadorRepository.save(new Entrenador(null,"19.902.142-k","Juan Perez", LocalDate.parse(1990, 9, 21)));
        entrenadorRepository.save(new Entrenador(null,"21.901.389-2","Cristofer Cifuentes", LocalDate.parse(2005,7,28)));
        log.info(">>> DataInitializer: {} Entrenadores insertados correctamente.",
                entrenadorRepository.count());
    }

}

