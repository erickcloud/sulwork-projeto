package br.com.sulwork.repository;

import br.com.sulwork.domain.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    boolean existsByEventoIdAndParticipanteId(Long eventoId, Long participanteId);

    @EntityGraph(attributePaths = {"evento", "participante"})
    List<Inscricao> findAll();
}