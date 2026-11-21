package br.com.sulwork.repository;

import br.com.sulwork.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @EntityGraph(attributePaths = "evento")
    List<Categoria> findByEventoId(Long eventoId);
}