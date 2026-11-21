package br.com.sulwork.api;

import br.com.sulwork.service.ItemService;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import br.com.sulwork.domain.Item;
import java.util.List;

@RestController
@RequestMapping("/api/itens")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    public record ItemRequest(
            Long categoriaId,
            String nome,
            BigDecimal quantidadeNecessaria,
            boolean obrigatorio,
            String alergenos
    ) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item criar(@RequestBody ItemRequest request) {

        return service.criar(
                request.categoriaId(),
                request.nome(),
                request.quantidadeNecessaria(),
                request.obrigatorio(),
                request.alergenos()
        );
    }

    @GetMapping("/categoria/{categoriaId}")
    public List<Item> listarPorCategoria(@PathVariable Long categoriaId) {
        return service.listarPorCategoria(categoriaId);
    }

}