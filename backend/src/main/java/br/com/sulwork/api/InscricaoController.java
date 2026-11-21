package br.com.sulwork.api;

import br.com.sulwork.domain.Inscricao;
import br.com.sulwork.service.InscricaoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
@CrossOrigin(origins = "http://localhost:5173")
public class InscricaoController {

    private final InscricaoService service;

    public InscricaoController(InscricaoService service) {
        this.service = service;
    }

    public record CriarInscricaoRequest(
            Long eventoId,
            Long participanteId
    ) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inscricao inscrever(@RequestBody CriarInscricaoRequest request) {
        return service.inscrever(
                request.eventoId(),
                request.participanteId()
        );
    }

    @GetMapping
    public List<Inscricao> listar() {
        return service.listar();
    }

}