package br.com.sulwork.api;

import br.com.sulwork.domain.*;
import br.com.sulwork.service.EventoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:5173")
public class EventoController {
    private final EventoService service;

    public EventoController(EventoService service) {
        this.service = service;
    }

    public record CriarEventoRequest(@NotBlank String nome, String descricao, @NotBlank String tipo,
                                     @NotNull OffsetDateTime dataHora, @NotNull OffsetDateTime prazoAlteracoes) {
    }

    public record StatusRequest(@NotNull StatusEvento status) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Evento criar(@Valid @RequestBody CriarEventoRequest r) {
        return service.criar(r.nome(), r.descricao(), r.tipo(), r.dataHora(), r.prazoAlteracoes());
    }

    @GetMapping
    public List<Evento> listar() {
        return service.listar();
    }

    @PatchMapping("/{id}/status")
    public Evento status(@PathVariable Long id, @Valid @RequestBody StatusRequest r) {
        return service.alterarStatus(id, r.status());
    }
}
