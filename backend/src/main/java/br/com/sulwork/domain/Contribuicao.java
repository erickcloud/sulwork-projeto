package br.com.sulwork.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "contribuicao")
public class Contribuicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidade;

    protected Contribuicao() {
    }

    public Contribuicao(Participante participante, Item item, BigDecimal quantidade) {
        this.participante = participante;
        this.item = item;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public Participante getParticipante() {
        return participante;
    }

    public Item getItem() {
        return item;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

}