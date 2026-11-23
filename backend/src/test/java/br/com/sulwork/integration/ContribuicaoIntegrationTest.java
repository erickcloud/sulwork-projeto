
package br.com.sulwork.integration;

import br.com.sulwork.domain.*;
import br.com.sulwork.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class ContribuicaoIntegrationTest {

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
    private ContribuicaoRepository contribuicaoRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Transactional
    void deveSalvarEConsultarContribuicaoNoPostgres() {

        Participante participante = participanteRepository.saveAndFlush(
                new Participante(
                        "Participante Teste",
                        "12345678901",
                        "Sem restrições"
                )
        );

        Evento evento = eventoRepository.saveAndFlush(
                new Evento(
                        "Evento de Teste",
                        "Descrição do evento",
                        "SOLIDARIO",
                        OffsetDateTime.now().plusDays(10),
                        OffsetDateTime.now().plusDays(5)
                )
        );

        Categoria categoria = categoriaRepository.saveAndFlush(
                new Categoria(
                        evento,
                        "Alimentos",
                        new BigDecimal("100.00")
                )
        );

        Item item = itemRepository.saveAndFlush(
                new Item(
                        categoria,
                        "Arroz",
                        new BigDecimal("50.00"),
                        true,
                        ""
                )
        );

        Contribuicao contribuicao = contribuicaoRepository.saveAndFlush(
                new Contribuicao(
                        participante,
                        item,
                        new BigDecimal("10.00")
                )
        );

        assertNotNull(contribuicao.getId());

        Contribuicao encontrada = contribuicaoRepository
                .findById(contribuicao.getId())
                .orElseThrow();

        assertEquals(participante.getId(),
                encontrada.getParticipante().getId());

        assertEquals(item.getId(),
                encontrada.getItem().getId());

        assertEquals(
                0,
                new BigDecimal("10.00").compareTo(encontrada.getQuantidade())
        );
    }

    @Test
    @Transactional
    void deveSomarQuantidadeDeContribuicoesPorItemNoPostgres() {

        Participante participante = participanteRepository.saveAndFlush(
                new Participante(
                        "Participante Teste",
                        "98765432100",
                        "Sem restrições"
                )
        );

        Evento evento = eventoRepository.saveAndFlush(
                new Evento(
                        "Evento de Teste",
                        "Descrição do evento",
                        "SOLIDARIO",
                        OffsetDateTime.now().plusDays(10),
                        OffsetDateTime.now().plusDays(5)
                )
        );

        Categoria categoria = categoriaRepository.saveAndFlush(
                new Categoria(
                        evento,
                        "Alimentos",
                        new BigDecimal("100.00")
                )
        );

        Item item = itemRepository.saveAndFlush(
                new Item(
                        categoria,
                        "Feijão",
                        new BigDecimal("50.00"),
                        true,
                        ""
                )
        );

        contribuicaoRepository.saveAndFlush(
                new Contribuicao(
                        participante,
                        item,
                        new BigDecimal("10.00")
                )
        );

        contribuicaoRepository.saveAndFlush(
                new Contribuicao(
                        participante,
                        item,
                        new BigDecimal("15.00")
                )
        );

        BigDecimal total = contribuicaoRepository
                .somarQuantidadePorItem(item.getId());

        assertNotNull(total);

        assertEquals(
                0,
                new BigDecimal("25.00").compareTo(total)
        );
    }
}
