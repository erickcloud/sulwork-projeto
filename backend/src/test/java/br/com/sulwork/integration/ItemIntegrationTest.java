
package br.com.sulwork.integration;

import br.com.sulwork.domain.Categoria;
import br.com.sulwork.domain.Evento;
import br.com.sulwork.domain.Item;
import br.com.sulwork.repository.CategoriaRepository;
import br.com.sulwork.repository.EventoRepository;
import br.com.sulwork.repository.ItemRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class ItemIntegrationTest {

    @Autowired
    private ItemRepository itemRepository;

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
    void deveSalvarEConsultarItemNoPostgres() {

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

        Item item = new Item(
                categoriaSalva,
                "Arroz",
                new BigDecimal("10.00"),
                true,
                "Glúten"
        );

        Item itemSalvo = itemRepository.save(item);

        assertNotNull(itemSalvo.getId());

        List<Item> itens = itemRepository
                .findByCategoriaId(categoriaSalva.getId());

        assertEquals(1, itens.size());

        Item encontrado = itens.get(0);

        assertEquals("Arroz", encontrado.getNome());
        assertEquals(new BigDecimal("10.00"), encontrado.getQuantidadeNecessaria());
        assertTrue(encontrado.isObrigatorio());
        assertEquals("Glúten", encontrado.getAlergenos());

        Optional<Item> itemComRelacionamentos =
                itemRepository.buscarPorIdComCategoriaEEvento(itemSalvo.getId());

        assertTrue(itemComRelacionamentos.isPresent());

        Item itemCompleto = itemComRelacionamentos.orElseThrow();

        assertEquals("Alimentação", itemCompleto.getCategoria().getNome());
        assertEquals(
                eventoSalvo.getId(),
                itemCompleto.getCategoria().getEvento().getId()
        );
    }
}
