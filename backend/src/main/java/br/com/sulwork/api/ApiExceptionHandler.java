package br.com.sulwork.api;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import br.com.sulwork.exception.InscricaoDuplicadaException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> argumento(IllegalArgumentException e) {
        return Map.of("erro", e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> estado(IllegalStateException e) {
        return Map.of("erro", e.getMessage());
    }
    @ExceptionHandler(InscricaoDuplicadaException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> inscricaoDuplicada(InscricaoDuplicadaException e) {
        return Map.of("erro", e.getMessage());
    }
}