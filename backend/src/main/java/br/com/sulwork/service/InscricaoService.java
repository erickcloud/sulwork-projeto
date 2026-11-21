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

    private void validarExistencia(Long eventoId, Long participanteId) {

        if (!eventoRepository.existsById(eventoId)) {
            throw new IllegalArgumentException("Evento não encontrado.");
        }

        if (!participanteRepository.existsById(participanteId)) {
            throw new IllegalArgumentException("Participante não encontrado.");
        }
    }

    private void validarInscricaoDuplicada(Long eventoId, Long participanteId) {

        if (repository.existsByEventoIdAndParticipanteId(eventoId, participanteId)) {
            throw new InscricaoDuplicadaException(
                    "Participante já está inscrito neste evento."
            );
        }
    }

    public Inscricao inscrever(Long eventoId, Long participanteId) {

        validarExistencia(eventoId, participanteId);

        validarInscricaoDuplicada(eventoId, participanteId);

        Evento evento = eventoRepository.findById(eventoId).orElseThrow();
        Participante participante = participanteRepository.findById(participanteId).orElseThrow();

        Inscricao inscricao = new Inscricao(evento, participante);

        return repository.save(inscricao);
    }

    public List<Inscricao> listar() {
        return repository.findAll();
    }
}