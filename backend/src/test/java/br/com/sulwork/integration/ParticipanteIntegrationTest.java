package br.com.sulwork.integration;

import br.com.sulwork.domain.Participante;
import br.com.sulwork.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Testcontainers
public class ParticipanteIntegrationTest {

    @Autowired
    private ParticipanteRepository participanteRepository;

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

    @Transactional
    @Test
    void deveSalvarEConsultarParticipanteNoPostgres() {
        Participante participante = new Participante(
                "Erick",
                "12345678901",
                "Alergia a amendoim"
        );

        Participante salvo = participanteRepository.save(participante);

        assertNotNull(salvo.getId());

        Participante encontrado = participanteRepository
                .findById(salvo.getId())
                .orElseThrow();

        assertEquals("Erick", encontrado.getNome());
        assertEquals("12345678901", encontrado.getCpf());
        assertEquals("Alergia a amendoim", encontrado.getRestricoes());
    }

    @Transactional
    @Test
    void naoDevePermitirDoisParticipantesComMesmoCpf() {
        Participante primeiro = new Participante(
                "Erick",
                "98765432100",
                "Alergia a amendoim"
        );

        Participante segundo = new Participante(
                "Outro Participante",
                "98765432100",
                "Sem restrições"
        );

        participanteRepository.saveAndFlush(primeiro);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> participanteRepository.saveAndFlush(segundo)
        );
    }

}