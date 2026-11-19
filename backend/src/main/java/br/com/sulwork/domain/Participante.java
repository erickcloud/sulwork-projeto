
package br.com.sulwork.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "participante")
public class Participante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, columnDefinition = "text")
    private String restricoes;

    protected Participante() {}

    public Participante(String nome, String cpf, String restricoes) {
        this.nome = nome;
        this.cpf = cpf;
        this.restricoes = restricoes == null ? "" : restricoes;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getRestricoes() {
        return restricoes;
    }
}
