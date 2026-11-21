package br.com.sulwork.exception;

public class InscricaoDuplicadaException extends RuntimeException {

    public InscricaoDuplicadaException(String mensagem) {
        super(mensagem);
    }
}