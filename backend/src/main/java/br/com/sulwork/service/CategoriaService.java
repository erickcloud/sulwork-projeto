package br.com.sulwork.service;

import br.com.sulwork.repository.CategoriaRepository;
import br.com.sulwork.repository.EventoRepository;
import org.springframework.stereotype.Service;
import br.com.sulwork.domain.Categoria;
import br.com.sulwork.domain.Evento;
import java.math.BigDecimal;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final EventoRepository eventoRepository;

    public CategoriaService(
            CategoriaRepository repository,
            EventoRepository eventoRepository) {

        this.repository = repository;
        this.eventoRepository = eventoRepository;
    }

    public Categoria criar(Long eventoId, String nome, BigDecimal meta) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        Categoria categoria = new Categoria(evento, nome, meta);
        return repository.save(categoria);
    }

    public List<Categoria> listarPorEvento(Long eventoId) {
        return repository.findByEventoId(eventoId);
    }

}