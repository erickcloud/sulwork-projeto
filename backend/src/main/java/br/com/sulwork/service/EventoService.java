package br.com.sulwork.service;

import br.com.sulwork.domain.*;
import br.com.sulwork.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class EventoService {
    private final EventoRepository repository;

    public EventoService(EventoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Evento criar(String nome, String descricao, String tipo, OffsetDateTime dataHora, OffsetDateTime prazo) {
        if (prazo.isAfter(dataHora))
            throw new IllegalArgumentException("O prazo de alterações não pode ser posterior ao evento.");
        return repository.save(new Evento(nome, descricao, tipo, dataHora, prazo));
    }

    @Transactional(readOnly = true)
    public List<Evento> listar() {
        return repository.findAll();
    }

    @Transactional
    public Evento alterarStatus(Long id, StatusEvento status) {
        Evento evento = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
        evento.alterarStatus(status);
        return evento;
    }

    @Transactional
    public Evento atualizar(
            Long id,
            String nome,
            String descricao,
            String tipo,
            OffsetDateTime dataHora,
            OffsetDateTime prazoAlteracoes) {

        Evento evento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        if (evento.getStatus() == StatusEvento.ENCERRADO ||
                evento.getStatus() == StatusEvento.REALIZADO ||
                evento.getStatus() == StatusEvento.CANCELADO) {

            throw new IllegalStateException(
                    "Não é possível alterar um evento encerrado, realizado ou cancelado.");
        }

        if (!OffsetDateTime.now().isBefore(evento.getPrazoAlteracoes())) {
            throw new IllegalStateException(
                    "O prazo para alterações deste evento já terminou.");
        }

        if (prazoAlteracoes.isAfter(dataHora)) {
            throw new IllegalArgumentException(
                    "O prazo de alterações não pode ser posterior ao evento.");
        }

        evento.atualizarDados(nome, descricao, tipo, dataHora, prazoAlteracoes);

        return evento;
    }
}
