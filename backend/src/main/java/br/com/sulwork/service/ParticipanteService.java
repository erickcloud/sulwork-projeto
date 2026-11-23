
package br.com.sulwork.service;

import br.com.sulwork.domain.Participante;
import br.com.sulwork.repository.ParticipanteRepository;
import br.com.sulwork.repository.InscricaoRepository;
import br.com.sulwork.repository.ContribuicaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParticipanteService {

    private final ParticipanteRepository repository;
    private final InscricaoRepository inscricaoRepository;
    private final ContribuicaoRepository contribuicaoRepository;

    public ParticipanteService(
            ParticipanteRepository repository,
            InscricaoRepository inscricaoRepository,
            ContribuicaoRepository contribuicaoRepository
    ) {
        this.repository = repository;
        this.inscricaoRepository = inscricaoRepository;
        this.contribuicaoRepository = contribuicaoRepository;
    }

    private boolean cpfValido(String cpf) {
        if (cpf == null) {
            return false;
        }

        cpf = cpf.replaceAll("\\D", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int primeiroDigito = calcularPrimeiroDigito(cpf);
        int segundoDigito = calcularSegundoDigito(cpf, primeiroDigito);

        return primeiroDigito == Character.getNumericValue(cpf.charAt(9))
                && segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    private int calcularDigito(int soma) {
        int resto = soma % 11;

        if (resto < 2) {
            return 0;
        }

        return 11 - resto;
    }

    private int calcularPrimeiroDigito(String cpf) {
        int soma = 0;

        for (int i = 0; i < 9; i++) {
            int numero = Character.getNumericValue(cpf.charAt(i));
            int peso = 10 - i;

            soma += numero * peso;
        }

        return calcularDigito(soma);
    }

    private int calcularSegundoDigito(String cpf, int primeiroDigito) {
        int soma = 0;

        for (int i = 0; i < 9; i++) {
            int numero = Character.getNumericValue(cpf.charAt(i));
            int peso = 11 - i;

            soma += numero * peso;
        }

        soma += primeiroDigito * 2;

        return calcularDigito(soma);
    }

    public Participante criar(String nome, String cpf, String restricoes) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório.");
        }

        if (!cpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        cpf = cpf.replaceAll("\\D", "");

        if (repository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        Participante participante = new Participante(
                nome.trim(),
                cpf,
                restricoes
        );

        return repository.save(participante);
    }

    public List<Participante> listar() {
        return repository.findAll();
    }

    @Transactional
    public void excluir(Long id) {

        Participante participante = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Participante não encontrado.")
                );

        contribuicaoRepository.deleteByParticipante_Id(id);

        inscricaoRepository.deleteByParticipante_Id(id);

        repository.delete(participante);
    }
}
