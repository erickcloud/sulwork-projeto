
package br.com.sulwork.repository;

import br.com.sulwork.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @EntityGraph(attributePaths = {"categoria", "categoria.evento"})
    List<Item> findByCategoriaId(Long categoriaId);

    @Query("""
                SELECT i FROM Item i
                JOIN FETCH i.categoria c
                JOIN FETCH c.evento
                WHERE i.id = :id
            """)
    Optional<Item> buscarPorIdComCategoriaEEvento(Long id);

    void deleteByCategoria_Evento_Id(Long eventoId);
}
