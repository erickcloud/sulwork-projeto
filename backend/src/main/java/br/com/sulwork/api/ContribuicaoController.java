
package br.com.sulwork.api;

import br.com.sulwork.service.ContribuicaoService;
import br.com.sulwork.dto.ContribuicaoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/contribuicoes")
public class ContribuicaoController {

    private final ContribuicaoService service;

    public ContribuicaoController(ContribuicaoService service) {
        this.service = service;
    }

    public record ContribuicaoRequest(
            Long participanteId,
            Long itemId,
            BigDecimal quantidade
    ) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContribuicaoResponseDTO criar(
            @RequestBody ContribuicaoRequest request) {

        return service.criar(
                request.participanteId(),
                request.itemId(),
                request.quantidade()
        );
    }

    @GetMapping
    public List<ContribuicaoResponseDTO> listar() {
        return service.listar();
    }
}
