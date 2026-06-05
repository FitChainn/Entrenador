package entrenador.entrenador.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClienteClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${cliente.service.url}")
    private String clienteServiceUrl;

    // Verifica que el cliente existe antes de asignarlo
    public void verificarClienteExiste(Long clienteId) {
        log.info("Verificando que existe cliente con id {}", clienteId);
        try {
            webClientBuilder.build()
                    .get()
                    .uri(clienteServiceUrl + "/v1/clientes/" + clienteId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.error("Cliente con id {} no encontrado", clienteId);
            throw new NoSuchElementException("El cliente con ID " + clienteId + " no existe");
        } catch (Exception e) {
            log.error("Error al conectar con microservicio Cliente: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio de clientes");
        }
    }

    // Obtiene los alumnos simples de un entrenador
    public List<Object> obtenerAlumnosPorEntrenador(Long entrenadorId) {
        log.info("Obteniendo alumnos del entrenador {}", entrenadorId);
        try {
            return webClientBuilder.build()
                    .get()
                    .uri(clienteServiceUrl + "/v1/clientes/entrenador/" + entrenadorId + "/simple")
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.warn("No se pudieron obtener alumnos del entrenador {}", entrenadorId);
            return List.of();
        }
    }

    // Asigna el entrenador al cliente llamando al endpoint interno de Cliente
    public void asignarEntrenadorACliente(Long clienteId, Long entrenadorId) {
        log.info("Asignando entrenador {} al cliente {}", entrenadorId, clienteId);
        try {
            webClientBuilder.build()
                    .put()
                    .uri(clienteServiceUrl + "/v1/clientes/" + clienteId + "/asignar-entrenador/" + entrenadorId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new NoSuchElementException("El cliente con ID " + clienteId + " no existe");
        } catch (Exception e) {
            log.error("Error al asignar entrenador al cliente: {}", e.getMessage());
            throw new RuntimeException("Error al conectar con el microservicio de clientes");
        }
    }
}