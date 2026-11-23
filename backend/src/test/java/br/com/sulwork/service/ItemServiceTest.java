
package br.com.sulwork.service;

import br.com.sulwork.domain.Categoria;
import br.com.sulwork.domain.Item;
import br.com.sulwork.repository.CategoriaRepository;
import br.com.sulwork.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ItemService service;


    @Test
    void deveCriarItemQuandoDadosForemValidos() {

        Categoria categoria = mock(Categoria.class);

        Item itemSalvo = mock(Item.class);

        when(itemSalvo.getId()).thenReturn(1L);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(repository.save(any(Item.class)))
                .thenReturn(itemSalvo);

        when(repository.buscarPorIdComCategoriaEEvento(1L))
                .thenReturn(Optional.of(itemSalvo));

        Item resultado = service.criar(
                1L,
                "Refrigerante",
                new BigDecimal("10"),
                true,
                "Glúten"
        );

        assertNotNull(resultado);

        verify(categoriaRepository).findById(1L);

        verify(repository).save(any(Item.class));

        verify(repository).buscarPorIdComCategoriaEEvento(1L);

    }

    @Test
    void deveLancarExcecaoQuandoNomeForNulo() {

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        null,
                        new BigDecimal("10"),
                        true,
                        "Glúten"
                )
        );

        verify(categoriaRepository, never()).findById(anyLong());

        verify(repository, never()).save(any(Item.class));
    }

    @Test
    void deveLancarExcecaoQuandoNomeEstiverVazio() {

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        "   ",
                        new BigDecimal("10"),
                        true,
                        "Glúten"
                )
        );

        verify(categoriaRepository, never()).findById(anyLong());

        verify(repository, never()).save(any(Item.class));
    }

    @Test
    void deveLancarExcecaoQuandoNomeUltrapassar120Caracteres() {

        String nome = "A".repeat(121);

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        nome,
                        new BigDecimal("10"),
                        true,
                        "Glúten"
                )
        );

        verify(categoriaRepository, never()).findById(anyLong());

        verify(repository, never()).save(any(Item.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForNula() {

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        "Refrigerante",
                        null,
                        true,
                        "Glúten"
                )
        );

        verify(categoriaRepository, never()).findById(anyLong());

        verify(repository, never()).save(any(Item.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForZero() {

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        "Refrigerante",
                        BigDecimal.ZERO,
                        true,
                        "Glúten"
                )
        );

        verify(categoriaRepository, never()).findById(anyLong());

        verify(repository, never()).save(any(Item.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForNegativa() {

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        "Refrigerante",
                        new BigDecimal("-5"),
                        true,
                        "Glúten"
                )
        );

        verify(categoriaRepository, never()).findById(anyLong());

        verify(repository, never()).save(any(Item.class));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExistir() {

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        "Refrigerante",
                        new BigDecimal("10"),
                        true,
                        "Glúten"
                )
        );

        verify(categoriaRepository).findById(1L);

        verify(repository, never()).save(any(Item.class));
    }

    @Test
    void deveListarItensPorCategoria() {

        List<Item> itens = List.of(
                mock(Item.class),
                mock(Item.class)
        );

        when(repository.findByCategoriaId(1L))
                .thenReturn(itens);

        List<Item> resultado = service.listarPorCategoria(1L);

        assertEquals(2, resultado.size());

        verify(repository).findByCategoriaId(1L);
    }


}
