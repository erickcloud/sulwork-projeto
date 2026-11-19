package br.com.sulwork.domain;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.*;
class EventoTest {
 private Evento evento(){var agora=OffsetDateTime.now();return new Evento("Almoço","Equipe","ALMOCO",agora.plusDays(2),agora.plusDays(1));}
 @Test void devePermitirAbrirRascunho(){var e=evento();e.alterarStatus(StatusEvento.ABERTO);assertEquals(StatusEvento.ABERTO,e.getStatus());}
 @Test void naoDevePermitirRealizarEventoAberto(){var e=evento();assertThrows(IllegalStateException.class,()->e.alterarStatus(StatusEvento.REALIZADO));}
 @Test void estadoFinalNaoPodeSerAlterado(){var e=evento();e.alterarStatus(StatusEvento.CANCELADO);assertThrows(IllegalStateException.class,()->e.alterarStatus(StatusEvento.ABERTO));}
}
