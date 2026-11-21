package br.com.sulwork.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "inscricao",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inscricao",
                        columnNames = {"evento_id", "participante_id"}
                )
        }
)
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    protected Inscricao() {
    }

    public Inscricao(Evento evento, Participante participante) {
        this.evento = evento;
        this.participante = participante;
    }

    public Long getId() {
        return id;
    }

    public Evento getEvento() {
        return evento;
    }

    public Participante getParticipante() {
        return participante;
    }
}
