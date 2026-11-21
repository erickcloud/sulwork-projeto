package br.com.sulwork.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(name = "quantidade_necessaria", precision = 10, scale = 2, nullable = false)
    private BigDecimal quantidadeNecessaria;

    @Column(nullable = false)
    private boolean obrigatorio = true;

    @Column(columnDefinition = "text", nullable = false)
    private String alergenos = "";

    protected Item() {
    }

    public Item(Categoria categoria, String nome,
                BigDecimal quantidadeNecessaria,
                boolean obrigatorio, String alergenos) {

        this.categoria = categoria;
        this.nome = nome;
        this.quantidadeNecessaria = quantidadeNecessaria;
        this.obrigatorio = obrigatorio;
        this.alergenos = alergenos == null ? "" : alergenos;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public BigDecimal getQuantidadeNecessaria() {
        return quantidadeNecessaria;
    }

    public boolean isObrigatorio() {
        return obrigatorio;
    }

    public String getAlergenos() {
        return alergenos;
    }

}