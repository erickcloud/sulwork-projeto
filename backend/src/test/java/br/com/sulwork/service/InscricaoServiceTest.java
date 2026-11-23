package br.com.sulwork.service;

import br.com.sulwork.domain.Evento;
import br.com.sulwork.domain.Participante;
import br.com.sulwork.domain.StatusEvento;
import br.com.sulwork.repository.EventoRepository;
import br.com.sulwork.repository.InscricaoRepository;
import br.com.sulwork.repository.ParticipanteRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.util.Optional;
import static org.mockito.Mockito.when;
import br.com.sulwork.domain.Inscricao;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import br.com.sulwork.exception.InscricaoDuplicadaException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class InscricaoServiceTest {

    @Mock
    private InscricaoRepository repository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private InscricaoService service;

    @Test
    void deveRealizarInscricaoQuandoEventoEstiverAberto() {

        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(10);

        Evento evento = new Evento(
                "Evento Teste",
                "Descrição do evento",
                "TESTE",
                dataHora,
                dataHora.minusDays(1)
        );

        evento.alterarStatus(StatusEvento.ABERTO);

        Participante participante = new Participante(
                "Participante Teste",
                "12345678909",
                "Nenhuma"
        );

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));

        when(participanteRepository.findById(2L)).thenReturn(Optional.of(participante));

        when(repository.existsByEventoIdAndParticipanteId(1L, 2L))
                .thenReturn(false);

        doAnswer(invocation -> invocation.getArgument(0))
                .when(repository).save(any(Inscricao.class));

        Inscricao resultado = service.inscrever(1L, 2L);

        assertNotNull(resultado);

        verify(repository).save(any(Inscricao.class));

    }

    @Test
    void deveLancarExcecaoQuandoInscricaoJaExistir() {

        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(10);

        Evento evento = new Evento(
                "Evento Teste",
                "Descrição do evento",
                "TESTE",
                dataHora,
                dataHora.minusDays(1)
        );

        evento.alterarStatus(StatusEvento.ABERTO);

        Participante participante = new Participante(
                "Participante Teste",
                "12345678909",
                "Nenhuma"
        );

        when(eventoRepository.findById(1L))
                .thenReturn(Optional.of(evento));

        when(participanteRepository.findById(2L))
                .thenReturn(Optional.of(participante));

        when(repository.existsByEventoIdAndParticipanteId(1L, 2L))
                .thenReturn(true);

        assertThrows(
                InscricaoDuplicadaException.class,
                () -> service.inscrever(1L, 2L)
        );

        verify(repository, never()).save(any(Inscricao.class));

    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoEstiverAberto() {

        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(10);

        Evento evento = new Evento(
                "Evento Teste",
                "Descrição do evento",
                "TESTE",
                dataHora,
                dataHora.minusDays(1)
        );

        Participante participante = new Participante(
                "Participante Teste",
                "12345678909",
                "Nenhuma"
        );

        when(eventoRepository.findById(1L))
                .thenReturn(Optional.of(evento));

        when(participanteRepository.findById(2L))
                .thenReturn(Optional.of(participante));

        when(repository.existsByEventoIdAndParticipanteId(1L, 2L))
                .thenReturn(false);

        assertThrows(
                IllegalStateException.class,
                () -> service.inscrever(1L, 2L)

        );

        verify(repository, never()).save(any(Inscricao.class));

    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExistir() {

        when(eventoRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.inscrever(1L, 2L)

        );

        verify(repository, never()).save(any(Inscricao.class));

    }

    @Test
    void deveLancarExcecaoQuandoParticipanteNaoExistir() {

        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(10);

        Evento evento = new Evento(
                "Evento Teste",
                "Descrição do evento",
                "TESTE",
                dataHora,
                dataHora.minusDays(1)
        );

        evento.alterarStatus(StatusEvento.ABERTO);

        when(eventoRepository.findById(1L))
                .thenReturn(Optional.of(evento));

        when(participanteRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.inscrever(1L, 2L)
        );

        verify(repository, never()).save(any(Inscricao.class));

    }

    @Test
    void deveListarInscricoes() {

        OffsetDateTime dataHora = OffsetDateTime.now().plusDays(10);

        Evento evento = new Evento(
                "Evento Teste",
                "Descrição do evento",
                "TESTE",
                dataHora,
                dataHora.minusDays(1)
        );

        Participante participante = new Participante(
                "Participante Teste",
                "12345678909",
                "Nenhuma"
        );

        Inscricao inscricao = new Inscricao(evento, participante);

        List<Inscricao> inscricoes = List.of(inscricao);

        when(repository.findAll()).thenReturn(inscricoes);

        List<Inscricao> resultado = service.listar();

        assertEquals(1, resultado.size());

        verify(repository).findAll();

    }

}