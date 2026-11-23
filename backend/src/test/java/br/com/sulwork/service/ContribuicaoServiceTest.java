
package br.com.sulwork.service;

import br.com.sulwork.domain.*;
import br.com.sulwork.dto.ContribuicaoResponseDTO;
import br.com.sulwork.repository.*;
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

@ExtendWith(MockitoExtension.class)
class ContribuicaoServiceTest {

    @Mock
    private ContribuicaoRepository repository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ContribuicaoService service;

    @Test
    void deveCriarContribuicaoQuandoDadosForemValidos() {

        Participante participante = mock(Participante.class);
        Item item = mock(Item.class);
        Categoria categoria = mock(Categoria.class);
        Evento evento = mock(Evento.class);
        Contribuicao contribuicaoSalva = mock(Contribuicao.class);


        when(participante.getId()).thenReturn(1L);
        when(participante.getNome()).thenReturn("Erick");


        when(item.getId()).thenReturn(10L);
        when(item.getNome()).thenReturn("Arroz");
        when(item.getCategoria()).thenReturn(categoria);
        when(item.getQuantidadeNecessaria())
                .thenReturn(new BigDecimal("10"));


        when(categoria.getEvento()).thenReturn(evento);


        when(evento.getStatus()).thenReturn(StatusEvento.ABERTO);
        when(evento.getPrazoAlteracoes())
                .thenReturn(OffsetDateTime.now().plusDays(1));

        when(participanteRepository.existsById(1L))
                .thenReturn(true);

        when(participanteRepository.findById(1L))
                .thenReturn(Optional.of(participante));

        when(itemRepository.existsById(10L))
                .thenReturn(true);

        when(itemRepository.buscarPorIdComCategoriaEEvento(10L))
                .thenReturn(Optional.of(item));

        when(repository.somarQuantidadePorItem(10L))
                .thenReturn(BigDecimal.ZERO);

        when(contribuicaoSalva.getId()).thenReturn(100L);

        when(repository.save(any(Contribuicao.class)))
                .thenReturn(contribuicaoSalva);

        when(contribuicaoSalva.getParticipante())
                .thenReturn(participante);

        when(contribuicaoSalva.getItem())
                .thenReturn(item);

        when(contribuicaoSalva.getQuantidade())
                .thenReturn(new BigDecimal("2"));

        ContribuicaoResponseDTO resultado = service.criar(
                1L,
                10L,
                new BigDecimal("2")
        );

        assertNotNull(resultado);
        assertEquals(100L, resultado.id());
        assertEquals(1L, resultado.participanteId());
        assertEquals("Erick", resultado.participanteNome());
        assertEquals(10L, resultado.itemId());
        assertEquals("Arroz", resultado.itemNome());
        assertEquals(new BigDecimal("2"), resultado.quantidade());

        verify(repository).save(any(Contribuicao.class));

    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoExistir() {

        when(participanteRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        10L,
                        new BigDecimal("2")
                )
        );

        verify(participanteRepository).existsById(1L);
        verify(repository, never()).save(any(Contribuicao.class));
    }

    @Test
    void deveLancarExcecaoQuandoItemNaoExistir() {

        when(participanteRepository.existsById(1L))
                .thenReturn(true);

        when(itemRepository.existsById(10L))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        10L,
                        new BigDecimal("2")
                )
        );

        verify(participanteRepository).existsById(1L);
        verify(itemRepository).existsById(10L);
        verify(repository, never()).save(any(Contribuicao.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForZero() {

        when(participanteRepository.existsById(1L))
                .thenReturn(true);

        when(itemRepository.existsById(10L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        10L,
                        BigDecimal.ZERO
                )
        );

        verify(repository, never()).save(any(Contribuicao.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForNegativa() {

        when(participanteRepository.existsById(1L))
                .thenReturn(true);

        when(itemRepository.existsById(10L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        10L,
                        new BigDecimal("-2")
                )
        );

        verify(repository, never()).save(any(Contribuicao.class));
    }

    @Test
    void deveLancarExcecaoQuandoQuantidadeForNula() {

        when(participanteRepository.existsById(1L))
                .thenReturn(true);

        when(itemRepository.existsById(10L))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        1L,
                        10L,
                        null
                )
        );

        verify(repository, never()).save(any(Contribuicao.class));
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoEstiverAberto() {

        Participante participante = mock(Participante.class);
        Item item = mock(Item.class);
        Categoria categoria = mock(Categoria.class);
        Evento evento = mock(Evento.class);

        when(participanteRepository.existsById(1L))
                .thenReturn(true);

        when(itemRepository.existsById(10L))
                .thenReturn(true);

        when(participanteRepository.findById(1L))
                .thenReturn(Optional.of(participante));

        when(itemRepository.buscarPorIdComCategoriaEEvento(10L))
                .thenReturn(Optional.of(item));

        when(item.getCategoria()).thenReturn(categoria);
        when(categoria.getEvento()).thenReturn(evento);

        when(evento.getStatus()).thenReturn(StatusEvento.ENCERRADO);

        assertThrows(IllegalStateException.class, () ->
                service.criar(
                        1L,
                        10L,
                        new BigDecimal("2")
                )
        );

        verify(repository, never()).save(any(Contribuicao.class));
    }


    @Test
    void deveLancarExcecaoQuandoPrazoDeContribuicoesEstiverExpirado() {

        Participante participante = mock(Participante.class);
        Item item = mock(Item.class);
        Categoria categoria = mock(Categoria.class);
        Evento evento = mock(Evento.class);

        when(participanteRepository.existsById(1L))
                .thenReturn(true);

        when(itemRepository.existsById(10L))
                .thenReturn(true);

        when(participanteRepository.findById(1L))
                .thenReturn(Optional.of(participante));

        when(itemRepository.buscarPorIdComCategoriaEEvento(10L))
                .thenReturn(Optional.of(item));

        when(item.getCategoria()).thenReturn(categoria);
        when(categoria.getEvento()).thenReturn(evento);

        when(evento.getStatus()).thenReturn(StatusEvento.ABERTO);

        when(evento.getPrazoAlteracoes())
                .thenReturn(OffsetDateTime.now().minusDays(1));

        assertThrows(IllegalStateException.class, () ->
                service.criar(
                        1L,
                        10L,
                        new BigDecimal("2")
                )
        );

        verify(repository, never()).save(any(Contribuicao.class));
    }



}
