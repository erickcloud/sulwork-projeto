package br.com.sulwork.service;

import br.com.sulwork.repository.EventoRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import br.com.sulwork.domain.Evento;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import br.com.sulwork.domain.StatusEvento;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EventoServiceTest {

    @Mock
    private EventoRepository repository;

    @InjectMocks
    private EventoService service;

    @Test
    void deveCriarEventoQuandoDadosForemValidos() {

        OffsetDateTime agora = OffsetDateTime.now();

        OffsetDateTime dataHora = agora.plusDays(5);
        OffsetDateTime prazo = agora.plusDays(3);

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco da turma",
                "CHURRASCO",
                dataHora,
                prazo
        );

        when(repository.save(any(Evento.class))).thenReturn(evento);

        Evento resultado = service.criar(
                "Churrasco",
                "Churrasco da turma",
                "CHURRASCO",
                dataHora,
                prazo
        );

        assertNotNull(resultado);
        assertEquals("Churrasco", resultado.getNome());

        verify(repository).save(any(Evento.class));
    }

    @Test
    void deveLancarExcecaoQuandoPrazoForPosteriorADataDoEvento() {

        OffsetDateTime agora = OffsetDateTime.now();

        OffsetDateTime dataHora = agora.plusDays(3);
        OffsetDateTime prazo = agora.plusDays(5);

        assertThrows(IllegalArgumentException.class, () ->
                service.criar(
                        "Churrasco",
                        "Churrasco da turma",
                        "CHURRASCO",
                        dataHora,
                        prazo
                )
        );

        verify(repository, never()).save(any(Evento.class));

    }

    @Test
    void deveListarTodosOsEventos() {

        Evento evento1 = new Evento(
                "Churrasco",
                "Churrasco da turma",
                "CHURRASCO",
                OffsetDateTime.now().plusDays(5),
                OffsetDateTime.now().plusDays(3)
        );

        Evento evento2 = new Evento(
                "Aniversário",
                "Aniversário da turma",
                "ANIVERSARIO",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(8)
        );

        when(repository.findAll()).thenReturn(List.of(evento1, evento2));

        List<Evento> resultado = service.listar();

        assertEquals(2, resultado.size());
        verify(repository).findAll();
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExistirAoAlterarStatus() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.alterarStatus(1L, StatusEvento.ABERTO)
        );

        verify(repository).findById(1L);
    }

    @Test
    void deveAtualizarEventoQuandoDadosForemValidos() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco antigo",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.atualizar(
                1L,
                "Churrasco atualizado",
                "Nova descrição",
                "ANIVERSARIO",
                agora.plusDays(15),
                agora.plusDays(8)
        );

        assertEquals("Churrasco atualizado", resultado.getNome());
        assertEquals("Nova descrição", resultado.getDescricao());

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoTransicaoDeStatusForInvalida() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco da turma",
                "CHURRASCO",
                agora.plusDays(5),
                agora.plusDays(3)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalStateException.class, () ->
                service.alterarStatus(1L, StatusEvento.ENCERRADO)
        );

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoEventoNaoExistirAoAtualizar() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.atualizar(
                        1L,
                        "Churrasco atualizado",
                        "Nova descrição",
                        "ANIVERSARIO",
                        OffsetDateTime.now().plusDays(15),
                        OffsetDateTime.now().plusDays(8)
                )
        );

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoEventoEstiverEncerradoAoAtualizar() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco antigo",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);
        evento.alterarStatus(StatusEvento.ENCERRADO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalStateException.class, () ->
                service.atualizar(
                        1L,
                        "Churrasco atualizado",
                        "Nova descrição",
                        "ANIVERSARIO",
                        agora.plusDays(15),
                        agora.plusDays(8)
                )
        );

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoPrazoDeAlteracoesEstiverExpirado() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco antigo",
                "CHURRASCO",
                agora.plusDays(10),
                agora.minusDays(1)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalStateException.class, () ->
                service.atualizar(
                        1L,
                        "Churrasco atualizado",
                        "Nova descrição",
                        "ANIVERSARIO",
                        agora.plusDays(15),
                        agora.plusDays(8)
                )
        );

        verify(repository).findById(1L);

    }

    @Test
    void deveLancarExcecaoQuandoNovoPrazoForPosteriorADataDoEvento() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco antigo",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalArgumentException.class, () ->
                service.atualizar(
                        1L,
                        "Churrasco atualizado",
                        "Nova descrição",
                        "ANIVERSARIO",
                        agora.plusDays(10),
                        agora.plusDays(15)
                )
        );

        verify(repository).findById(1L);
    }


    @Test
    void deveLancarExcecaoQuandoEventoEstiverCanceladoAoAtualizar() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco antigo",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        evento.alterarStatus(StatusEvento.CANCELADO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalStateException.class, () ->
                service.atualizar(
                        1L,
                        "Churrasco atualizado",
                        "Nova descrição",
                        "ANIVERSARIO",
                        agora.plusDays(15),
                        agora.plusDays(8)
                )
        );

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoEventoEstiverRealizadoAoAtualizar() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco antigo",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);
        evento.alterarStatus(StatusEvento.ENCERRADO);
        evento.alterarStatus(StatusEvento.REALIZADO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalStateException.class, () ->
                service.atualizar(
                        1L,
                        "Churrasco atualizado",
                        "Nova descrição",
                        "ANIVERSARIO",
                        agora.plusDays(15),
                        agora.plusDays(8)
                )
        );

        verify(repository).findById(1L);
    }

    @Test
    void deveAtualizarEventoQuandoEstiverAberto() {

        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Churrasco antigo",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.atualizar(
                1L,
                "Churrasco atualizado",
                "Nova descrição",
                "ANIVERSARIO",
                agora.plusDays(15),
                agora.plusDays(8)
        );

        assertEquals("Churrasco atualizado", resultado.getNome());
        assertEquals("Nova descrição", resultado.getDescricao());
        assertEquals("ANIVERSARIO", resultado.getTipo());
        assertEquals(StatusEvento.ABERTO, resultado.getStatus());

        verify(repository).findById(1L);
    }

    @Test
    void deveAlterarStatusDeRascunhoParaAberto() {
        Evento evento = new Evento(
                "Churrasco",
                "Evento de confraternização",
                "CHURRASCO",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(5)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.alterarStatus(1L, StatusEvento.ABERTO);

        assertEquals(StatusEvento.ABERTO, resultado.getStatus());

        verify(repository).findById(1L);
    }

    @Test
    void deveAlterarStatusDeAbertoParaEncerrado() {
        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Evento de confraternização",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.alterarStatus(1L, StatusEvento.ENCERRADO);

        assertEquals(StatusEvento.ENCERRADO, resultado.getStatus());

        verify(repository).findById(1L);
    }

    @Test
    void deveAlterarStatusDeEncerradoParaRealizado() {
        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Evento de confraternização",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);
        evento.alterarStatus(StatusEvento.ENCERRADO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.alterarStatus(1L, StatusEvento.REALIZADO);

        assertEquals(StatusEvento.REALIZADO, resultado.getStatus());

        verify(repository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoTentarReabrirEventoRealizado() {
        OffsetDateTime agora = OffsetDateTime.now();

        Evento evento = new Evento(
                "Churrasco",
                "Evento de confraternização",
                "CHURRASCO",
                agora.plusDays(10),
                agora.plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);
        evento.alterarStatus(StatusEvento.ENCERRADO);
        evento.alterarStatus(StatusEvento.REALIZADO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        assertThrows(IllegalStateException.class, () ->
                service.alterarStatus(1L, StatusEvento.ABERTO)
        );

        verify(repository).findById(1L);
    }

    @Test
    void deveAlterarStatusDeRascunhoParaCancelado() {
        Evento evento = new Evento(
                "Churrasco",
                "Evento de confraternização",
                "CHURRASCO",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(5)
        );

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.alterarStatus(1L, StatusEvento.CANCELADO);

        assertEquals(StatusEvento.CANCELADO, resultado.getStatus());

        verify(repository).findById(1L);
    }

    @Test
    void deveAlterarStatusDeAbertoParaCancelado() {
        Evento evento = new Evento(
                "Churrasco",
                "Evento de confraternização",
                "CHURRASCO",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.alterarStatus(1L, StatusEvento.CANCELADO);

        assertEquals(StatusEvento.CANCELADO, resultado.getStatus());

        verify(repository).findById(1L);
    }

    @Test
    void deveAlterarStatusDeEncerradoParaCancelado() {
        Evento evento = new Evento(
                "Churrasco",
                "Evento de confraternização",
                "CHURRASCO",
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(5)
        );

        evento.alterarStatus(StatusEvento.ABERTO);
        evento.alterarStatus(StatusEvento.ENCERRADO);

        when(repository.findById(1L)).thenReturn(Optional.of(evento));

        Evento resultado = service.alterarStatus(1L, StatusEvento.CANCELADO);

        assertEquals(StatusEvento.CANCELADO, resultado.getStatus());

        verify(repository).findById(1L);
    }


}