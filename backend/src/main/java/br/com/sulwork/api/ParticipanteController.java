package br.com.sulwork.api;

import br.com.sulwork.domain.Participante;
import br.com.sulwork.service.ParticipanteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/participantes")
@CrossOrigin(origins = "http://localhost:5173")
public class ParticipanteController {

    private final ParticipanteService service;

    public ParticipanteController(ParticipanteService service) {
        this.service = service;
    }

    public record CriarParticipanteRequest(
            String nome,
            String cpf,
            String restricoes
    ) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Participante criar(@RequestBody CriarParticipanteRequest request) {
        return service.criar(
                request.nome(),
                request.cpf(),
                request.restricoes()
        );
    }

    @GetMapping
    public List<Participante> listar() {
        return service.listar();
    }

}