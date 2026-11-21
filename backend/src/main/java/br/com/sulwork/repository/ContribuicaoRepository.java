package br.com.sulwork.repository;

import br.com.sulwork.domain.Contribuicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;


public interface ContribuicaoRepository extends JpaRepository<Contribuicao, Long> {

    @Query("SELECT SUM(c.quantidade) FROM Contribuicao c WHERE c.item.id = :itemId")
    BigDecimal somarQuantidadePorItem(@Param("itemId") Long itemId);

}
