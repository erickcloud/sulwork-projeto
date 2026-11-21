package br.com.sulwork.service;

import br.com.sulwork.repository.ItemRepository;
import br.com.sulwork.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import br.com.sulwork.domain.Item;
import br.com.sulwork.domain.Categoria;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ItemService {

    private final ItemRepository repository;
    private final CategoriaRepository categoriaRepository;

    public ItemService(
            ItemRepository repository,
            CategoriaRepository categoriaRepository) {

        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    public Item criar(
            Long categoriaId,
            String nome,
            BigDecimal quantidadeNecessaria,
            boolean obrigatorio,
            String alergenos) {


        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do item é obrigatório.");
        }


        if (nome.length() > 120) {
            throw new IllegalArgumentException(
                    "O nome do item deve ter no máximo 120 caracteres."
            );
        }


        if (quantidadeNecessaria == null ||
                quantidadeNecessaria.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade necessária deve ser maior que zero."
            );
        }

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));

        Item item = new Item(
                categoria,
                nome,
                quantidadeNecessaria,
                obrigatorio,
                alergenos
        );

        Item itemSalvo = repository.save(item);

        return repository.buscarPorIdComCategoriaEEvento(itemSalvo.getId())
                .orElseThrow();
    }

    public List<Item> listarPorCategoria(Long categoriaId) {
        return repository.findByCategoriaId(categoriaId);
    }

}