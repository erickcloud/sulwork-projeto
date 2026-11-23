package br.com.sulwork.dto;

import java.math.BigDecimal;
import java.util.List;

public record CoberturaEventoResponseDTO(
        Long eventoId,
        String eventoNome,
        BigDecimal coberturaGeralPercentual,
        int totalItens,
        int obrigatoriosConcluidos,
        int obrigatoriosPendentes,
        List<CategoriaCoberturaDTO> categorias
) {
    public record CategoriaCoberturaDTO(
            Long categoriaId,
            String categoriaNome,
            BigDecimal meta,
            BigDecimal totalContribuido,
            BigDecimal percentualMeta
    ) {}
}