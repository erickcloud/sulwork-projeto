package br.com.sulwork.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(precision = 10, scale = 2)
    private BigDecimal meta;

    public Categoria(Evento evento, String nome, BigDecimal meta) {
        this.evento = evento;
        this.nome = nome;
        this.meta = meta;
    }

    protected Categoria() {
    }

    public Long getId() {
        return id;
    }

    public Evento getEvento() {
        return evento;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getMeta() {
        return meta;
    }

}