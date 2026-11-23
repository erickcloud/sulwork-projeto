
package br.com.sulwork.service;

import br.com.sulwork.domain.Categoria;
import br.com.sulwork.domain.Evento;
import br.com.sulwork.domain.Item;
import br.com.sulwork.dto.CoberturaEventoResponseDTO;
import br.com.sulwork.repository.CategoriaRepository;
import br.com.sulwork.repository.ContribuicaoRepository;
import br.com.sulwork.repository.EventoRepository;
import br.com.sulwork.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoberturaService {

    private final EventoRepository eventoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ItemRepository itemRepository;
    private final ContribuicaoRepository contribuicaoRepository;

    public CoberturaService(
            EventoRepository eventoRepository,
            CategoriaRepository categoriaRepository,
            ItemRepository itemRepository,
            ContribuicaoRepository contribuicaoRepository) {
        this.eventoRepository = eventoRepository;
        this.categoriaRepository = categoriaRepository;
        this.itemRepository = itemRepository;
        this.contribuicaoRepository = contribuicaoRepository;
    }

    @Transactional(readOnly = true)
    public CoberturaEventoResponseDTO calcular(Long eventoId) {

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado."));

        List<Categoria> categorias =
                categoriaRepository.findByEventoId(eventoId);

        List<CoberturaEventoResponseDTO.CategoriaCoberturaDTO> resultadoCategorias =
                new ArrayList<>();

        int totalItens = 0;
        int obrigatoriosConcluidos = 0;
        int obrigatoriosPendentes = 0;

        BigDecimal somaPercentuaisItens = BigDecimal.ZERO;

        for (Categoria categoria : categorias) {

            List<Item> itens = itemRepository.findByCategoriaId(categoria.getId());

            BigDecimal totalContribuidoCategoria = BigDecimal.ZERO;

            for (Item item : itens) {

                totalItens++;

                BigDecimal contribuido =
                        contribuicaoRepository.somarQuantidadePorItem(item.getId());

                if (contribuido == null) {
                    contribuido = BigDecimal.ZERO;
                }

                BigDecimal necessario = item.getQuantidadeNecessaria();

                BigDecimal percentualItem = contribuido
                        .min(necessario)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(necessario, 2, RoundingMode.HALF_UP);

                somaPercentuaisItens =
                        somaPercentuaisItens.add(percentualItem);

                totalContribuidoCategoria =
                        totalContribuidoCategoria.add(contribuido);

                if (item.isObrigatorio()) {
                    if (contribuido.compareTo(necessario) >= 0) {
                        obrigatoriosConcluidos++;
                    } else {
                        obrigatoriosPendentes++;
                    }
                }
            }

            BigDecimal percentualMeta = null;

            if (categoria.getMeta() != null
                    && categoria.getMeta().compareTo(BigDecimal.ZERO) > 0) {

                percentualMeta = totalContribuidoCategoria
                        .min(categoria.getMeta())
                        .multiply(BigDecimal.valueOf(100))
                        .divide(categoria.getMeta(), 2, RoundingMode.HALF_UP);
            }

            resultadoCategorias.add(
                    new CoberturaEventoResponseDTO.CategoriaCoberturaDTO(
                            categoria.getId(),
                            categoria.getNome(),
                            categoria.getMeta(),
                            totalContribuidoCategoria,
                            percentualMeta
                    )
            );
        }

        BigDecimal coberturaGeral = BigDecimal.ZERO;

        if (totalItens > 0) {
            coberturaGeral = somaPercentuaisItens
                    .divide(BigDecimal.valueOf(totalItens), 2, RoundingMode.HALF_UP);
        }

        return new CoberturaEventoResponseDTO(
                evento.getId(),
                evento.getNome(),
                coberturaGeral,
                totalItens,
                obrigatoriosConcluidos,
                obrigatoriosPendentes,
                resultadoCategorias
        );
    }
}
