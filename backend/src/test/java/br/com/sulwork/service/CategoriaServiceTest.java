package br.com.sulwork.service;

import br.com.sulwork.domain.Categoria;
import br.com.sulwork.domain.Evento;
import br.com.sulwork.repository.CategoriaRepository;
import br.com.sulwork.repository.EventoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private CategoriaService service;

    @Test
    void deveCriarCategoriaQuandoEventoExistir() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco da turma",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        Categoria categoria = new Categoria(
                evento,
                "Bebidas",
                new BigDecimal("100.00")
        );

        when(eventoRepository.findById(1L))
                .thenReturn(Optional.of(evento));

        when(repository.save(any(Categoria.class)))
                .thenReturn(categoria);

        Categoria resultado = service.criar(
                1L,
                "Bebidas",
                new BigDecimal("100.00")
        );

        assertNotNull(resultado);
        assertEquals("Bebidas", resultado.getNome());

        verify(eventoRepository).findById(1L);
        verify(repository).save(any(Categoria.class));
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExistir() {

        when(eventoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        "Bebidas",
                        new BigDecimal("100.00")
                )
        );

        verify(eventoRepository).findById(1L);

        verify(repository, never()).save(any(Categoria.class));
    }

    @Test
    void deveListarCategoriasPorEvento() {

        Categoria categoria1 = mock(Categoria.class);
        Categoria categoria2 = mock(Categoria.class);

        when(repository.findByEventoId(1L))
                .thenReturn(List.of(categoria1, categoria2));

        List<Categoria> resultado = service.listarPorEvento(1L);

        assertEquals(2, resultado.size());

        verify(repository).findByEventoId(1L);
    }

}