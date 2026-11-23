package br.com.sulwork.api;

import br.com.sulwork.domain.*;
import br.com.sulwork.service.EventoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.List;
import br.com.sulwork.dto.CoberturaEventoResponseDTO;
import br.com.sulwork.service.CoberturaService;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin(origins = "http://localhost:5173")
public class EventoController {

    private final EventoService service;
    private final CoberturaService coberturaService;

    public EventoController(
            EventoService service,
            CoberturaService coberturaService) {
        this.service = service;
        this.coberturaService = coberturaService;
    }

    public record CriarEventoRequest(@NotBlank String nome, String descricao, @NotBlank String tipo,
                                     @NotNull OffsetDateTime dataHora, @NotNull OffsetDateTime prazoAlteracoes) {
    }

    public record StatusRequest(@NotNull StatusEvento status) {
    }

    public record AtualizarEventoRequest(
            @NotBlank String nome,
            String descricao,
            @NotBlank String tipo,
            @NotNull OffsetDateTime dataHora,
            @NotNull OffsetDateTime prazoAlteracoes
    ) {
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

    @PutMapping("/{id}")
    public Evento atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarEventoRequest request) {

        return service.atualizar(
                id,
                request.nome(),
                request.descricao(),
                request.tipo(),
                request.dataHora(),
                request.prazoAlteracoes()
        );
    }

    @GetMapping("/{id}/cobertura")
    public CoberturaEventoResponseDTO cobertura(@PathVariable Long id) {
        return coberturaService.calcular(id);
    }

}
