
package br.com.sulwork.repository;

import br.com.sulwork.domain.Contribuicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ContribuicaoRepository extends JpaRepository<Contribuicao, Long> {

    @Query(
            value = "SELECT SUM(quantidade) FROM contribuicao WHERE item_id = :itemId",
            nativeQuery = true
    )
    BigDecimal somarQuantidadePorItem(@Param("itemId") Long itemId);

    boolean existsByParticipante_Id(Long participanteId);

    void deleteByParticipante_Id(Long participanteId);

    void deleteByItem_Categoria_Evento_Id(Long eventoId);
}
