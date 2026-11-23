
package br.com.sulwork.integration;

import br.com.sulwork.domain.Categoria;
import br.com.sulwork.domain.Evento;
import br.com.sulwork.repository.CategoriaRepository;
import br.com.sulwork.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class CategoriaIntegrationTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

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
    void deveSalvarEConsultarCategoriaVinculadaAoEventoNoPostgres() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Evento de Teste",
                "Descrição do evento",
                "Festa",
                agora.plusDays(30),
                agora.plusDays(20)
        );

        Evento eventoSalvo = eventoRepository.save(evento);

        Categoria categoria = new Categoria(
                eventoSalvo,
                "Alimentação",
                new BigDecimal("500.00")
        );

        Categoria categoriaSalva = categoriaRepository.save(categoria);

        assertNotNull(categoriaSalva.getId());

        List<Categoria> categorias = categoriaRepository
                .findByEventoId(eventoSalvo.getId());

        assertEquals(1, categorias.size());

        Categoria encontrada = categorias.get(0);

        assertEquals("Alimentação", encontrada.getNome());
        assertEquals(new BigDecimal("500.00"), encontrada.getMeta());
        assertEquals(eventoSalvo.getId(), encontrada.getEvento().getId());
    }
}
