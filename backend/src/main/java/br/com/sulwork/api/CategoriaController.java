package br.com.sulwork.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.sulwork.service.CategoriaService;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import br.com.sulwork.domain.Categoria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    public record CategoriaRequest(
            Long eventoId,
            String nome,
            BigDecimal meta
    ) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Categoria criar(@RequestBody CategoriaRequest request) {
        return service.criar(
                request.eventoId(),
                request.nome(),
                request.meta()
        );
    }

    @GetMapping("/evento/{eventoId}")
    public List<Categoria> listarPorEvento(@PathVariable Long eventoId) {
        return service.listarPorEvento(eventoId);
    }
}
