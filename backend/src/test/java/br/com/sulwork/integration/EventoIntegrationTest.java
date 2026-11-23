package br.com.sulwork.integration;

import br.com.sulwork.domain.StatusEvento;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import br.com.sulwork.domain.Evento;
import br.com.sulwork.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

@SpringBootTest
@Testcontainers
public class EventoIntegrationTest {

    @Autowired
    private EventoRepository eventoRepository;

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("sulwork_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configurarPropriedades(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void deveSalvarEConsultarEventoNoPostgres() {

        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(30);
        OffsetDateTime prazoAlteracoes = OffsetDateTime.now().plusDays(20);

        Evento evento = new Evento(
                "Evento de Integração",
                "Teste de persistência com PostgreSQL",
                "SOLIDARIO",
                dataHora,
                prazoAlteracoes
        );

        Evento eventoSalvo = eventoRepository.save(evento);

        assertNotNull(eventoSalvo.getId());

        Evento eventoEncontrado = eventoRepository.findById(eventoSalvo.getId())
                .orElseThrow();

        assertEquals("Evento de Integração", eventoEncontrado.getNome());
        assertEquals("Teste de persistência com PostgreSQL", eventoEncontrado.getDescricao());
        assertEquals("SOLIDARIO", eventoEncontrado.getTipo());
        assertEquals(StatusEvento.RASCUNHO, eventoEncontrado.getStatus());
        assertTrue(
                Duration.between(dataHora.toInstant(), eventoEncontrado.getDataHora().toInstant())
                        .abs()
                        .toNanos() <= 1_000
        );

        assertTrue(
                Duration.between(prazoAlteracoes.toInstant(), eventoEncontrado.getPrazoAlteracoes().toInstant())
                        .abs()
                        .toNanos() <= 1_000
        );

    }

    @Test
    void deveAtualizarStatusDoEventoNoPostgres() {
        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(30);
        OffsetDateTime prazoAlteracoes = OffsetDateTime.now().plusDays(20);

        Evento evento = new Evento(
                "Evento para atualizar",
                "Teste de atualização de status",
                "SOLIDARIO",
                dataHora,
                prazoAlteracoes
        );

        Evento eventoSalvo = eventoRepository.save(evento);

        eventoSalvo.alterarStatus(StatusEvento.ABERTO);

        eventoRepository.save(eventoSalvo);

        Evento eventoEncontrado = eventoRepository.findById(eventoSalvo.getId())
                .orElseThrow();

        assertEquals(StatusEvento.ABERTO, eventoEncontrado.getStatus());
    }

    @Test
    void deveExcluirEventoDoPostgres() {
        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(30);
        OffsetDateTime prazoAlteracoes = OffsetDateTime.now().plusDays(20);

        Evento evento = new Evento(
                "Evento para excluir",
                "Teste de exclusão",
                "SOLIDARIO",
                dataHora,
                prazoAlteracoes
        );

        Evento eventoSalvo = eventoRepository.save(evento);

        eventoRepository.deleteById(eventoSalvo.getId());

        assertFalse(eventoRepository.existsById(eventoSalvo.getId()));
    }
}