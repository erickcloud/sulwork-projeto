package br.com.sulwork.service;

import br.com.sulwork.domain.Contribuicao;
import org.springframework.stereotype.Service;
import br.com.sulwork.repository.ContribuicaoRepository;
import br.com.sulwork.repository.ParticipanteRepository;
import br.com.sulwork.repository.ItemRepository;
import java.math.BigDecimal;
import br.com.sulwork.domain.Item;
import br.com.sulwork.domain.Participante;
import br.com.sulwork.domain.Evento;
import br.com.sulwork.domain.StatusEvento;
import java.time.OffsetDateTime;
import org.springframework.transaction.annotation.Transactional;
import br.com.sulwork.dto.ContribuicaoResponseDTO;


@Service
public class ContribuicaoService {

    private final ContribuicaoRepository repository;
    private final ParticipanteRepository participanteRepository;
    private final ItemRepository itemRepository;

    public ContribuicaoService(
            ContribuicaoRepository repository,
            ParticipanteRepository participanteRepository,
            ItemRepository itemRepository) {

        this.repository = repository;
        this.participanteRepository = participanteRepository;
        this.itemRepository = itemRepository;
    }

    private void validarParticipante(Long participanteId) {

        if (!participanteRepository.existsById(participanteId)) {
            throw new IllegalArgumentException("Participante não encontrado.");
        }

    }

    private void validarItem(Long itemId) {

        if (!itemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item não encontrado.");
        }

    }

    private void validarQuantidade(BigDecimal quantidade) {

        if (quantidade == null ||
                quantidade.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade da contribuição deve ser maior que zero."
            );
        }
    }

    private void validarLimite(Item item, BigDecimal quantidade) {
        BigDecimal totalAtual = repository.somarQuantidadePorItem(item.getId());

        if (totalAtual == null) {
            totalAtual = BigDecimal.ZERO;
        }

        BigDecimal totalComNovaContribuicao = totalAtual.add(quantidade);

        if (totalComNovaContribuicao.compareTo(item.getQuantidadeNecessaria()) > 0) {
            throw new IllegalArgumentException(
                    "A quantidade total de contribuições ultrapassa a quantidade necessária do item."
            );
        }
    }


    @Transactional
    public Contribuicao criar(Long participanteId, Long itemId, BigDecimal quantidade) {

        validarParticipante(participanteId);
        validarItem(itemId);
        validarQuantidade(quantidade);

        Participante participante = participanteRepository.findById(participanteId)
                .orElseThrow(() -> new IllegalArgumentException("Participante não encontrado."));

        Item item = itemRepository.buscarPorIdComCategoriaEEvento(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));

        Evento evento = item.getCategoria().getEvento();
        if (evento.getStatus() != StatusEvento.ABERTO) {
            throw new IllegalStateException(
                    "O evento não está aberto para contribuições."
            );
        }
        if (!OffsetDateTime.now().isBefore(evento.getPrazoAlteracoes())) {
            throw new IllegalStateException(
                    "O prazo para contribuições deste evento já terminou."
            );
        }

        validarLimite(item, quantidade);

        Contribuicao contribuicao = new Contribuicao(
                participante,
                item,
                quantidade
        );

        return repository.save(contribuicao);

    }

    private ContribuicaoResponseDTO converterParaDTO(Contribuicao contribuicao) {
        return new ContribuicaoResponseDTO(
                contribuicao.getId(),
                contribuicao.getParticipante().getId(),
                contribuicao.getParticipante().getNome(),
                contribuicao.getItem().getId(),
                contribuicao.getItem().getNome(),
                contribuicao.getQuantidade()
        );
    }


}




