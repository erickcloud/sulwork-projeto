package br.com.sulwork.service;

import br.com.sulwork.repository.EventoRepository;
import br.com.sulwork.repository.InscricaoRepository;
import br.com.sulwork.repository.ParticipanteRepository;
import org.springframework.stereotype.Service;
import br.com.sulwork.domain.Evento;
import br.com.sulwork.domain.Participante;
import br.com.sulwork.domain.Inscricao;
import br.com.sulwork.exception.InscricaoDuplicadaException;
import java.util.List;
import br.com.sulwork.domain.StatusEvento;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InscricaoService {

    private final InscricaoRepository repository;

    private final EventoRepository eventoRepository;
    private final ParticipanteRepository participanteRepository;

    public InscricaoService(
            InscricaoRepository repository,
            EventoRepository eventoRepository,
            ParticipanteRepository participanteRepository) {

        this.repository = repository;
        this.eventoRepository = eventoRepository;
        this.participanteRepository = participanteRepository;
    }

    private Evento buscarEvento(Long eventoId) {
        return eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));
    }

    private Participante buscarParticipante(Long participanteId) {
        return participanteRepository.findById(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado."));
    }

    private void validarInscricaoDuplicada(Long eventoId, Long participanteId) {

        if (repository.existsByEventoIdAndParticipanteId(eventoId, participanteId)) {
            throw new InscricaoDuplicadaException(
                    "Participante já está inscrito neste evento."
            );
        }
    }

    @Transactional
    public Inscricao inscrever(Long eventoId, Long participanteId) {

        Evento evento = buscarEvento(eventoId);
        Participante participante = buscarParticipante(participanteId);

        validarInscricaoDuplicada(eventoId, participanteId);

        if (evento.getStatus() != StatusEvento.ABERTO) {
            throw new IllegalStateException(
                    "Não é possível realizar inscrição em um evento que não esteja aberto."
            );
        }

        Inscricao inscricao = new Inscricao(evento, participante);

        return repository.save(inscricao);
    }

    @Transactional(readOnly = true)
    public List<Inscricao> listar() {
        return repository.findAll();
    }
}