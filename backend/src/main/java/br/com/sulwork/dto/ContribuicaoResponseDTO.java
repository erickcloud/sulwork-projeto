package br.com.sulwork.dto;

import java.math.BigDecimal;

public record ContribuicaoResponseDTO(
        Long id,
        Long participanteId,
        String participanteNome,
        Long itemId,
        String itemNome,
        BigDecimal quantidade
) {
}