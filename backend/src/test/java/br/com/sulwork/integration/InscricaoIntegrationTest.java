
package br.com.sulwork.integration;

import br.com.sulwork.domain.Evento;
import br.com.sulwork.domain.Inscricao;
import br.com.sulwork.domain.Participante;
import br.com.sulwork.repository.EventoRepository;
import br.com.sulwork.repository.InscricaoRepository;
import br.com.sulwork.repository.ParticipanteRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class InscricaoIntegrationTest {

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

    @Autowired
    private InscricaoRepository inscricaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;


    @Test
    @Transactional
    void deveSalvarEConsultarInscricaoNoPostgres() {

        Evento evento = new Evento(
                "Evento de Teste",
                "Descrição do evento",
                "SOLIDARIO",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(5)
        );

        evento = eventoRepository.saveAndFlush(evento);

        Participante participante = new Participante(
                "Participante Teste",
                "12345678901",
                "Sem restrições"
        );

        participante = participanteRepository.saveAndFlush(participante);

        Inscricao novaInscricao = new Inscricao(evento, participante);

        Inscricao inscricaoSalva =
                inscricaoRepository.saveAndFlush(novaInscricao);

        assertNotNull(inscricaoSalva.getId());

        assertTrue(
                inscricaoRepository.existsByEventoIdAndParticipanteId(
                        evento.getId(),
                        participante.getId()
                )
        );

        Inscricao encontrada = inscricaoRepository.findAll()
                .stream()
                .filter(i -> i.getId().equals(inscricaoSalva.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(evento.getId(), encontrada.getEvento().getId());
        assertEquals(participante.getId(), encontrada.getParticipante().getId());
    }


    @Test
    @Transactional
    void naoDevePermitirInscricaoDuplicadaNoMesmoEvento() {

        Evento evento = new Evento(
                "Evento de Teste",
                "Descrição do evento",
                "SOLIDARIO",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(5)
        );

        evento = eventoRepository.saveAndFlush(evento);

        Participante participante = new Participante(
                "Participante Teste",
                "12345678902",
                "Sem restrições"
        );

        participante = participanteRepository.saveAndFlush(participante);

        Inscricao primeira = new Inscricao(evento, participante);

        inscricaoRepository.saveAndFlush(primeira);

        Inscricao duplicada = new Inscricao(evento, participante);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> inscricaoRepository.saveAndFlush(duplicada)
        );
    }
}
