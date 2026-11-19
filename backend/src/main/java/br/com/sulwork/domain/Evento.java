package br.com.sulwork.domain;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
@Entity @Table(name="evento")
public class Evento {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=140) private String nome;
 @Column(columnDefinition="text") private String descricao;
 @Column(nullable=false,length=80) private String tipo;
 @Column(name="data_hora",nullable=false) private OffsetDateTime dataHora;
 @Column(name="prazo_alteracoes",nullable=false) private OffsetDateTime prazoAlteracoes;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private StatusEvento status=StatusEvento.RASCUNHO;
 protected Evento() {}
 public Evento(String nome,String descricao,String tipo,OffsetDateTime dataHora,OffsetDateTime prazoAlteracoes) { this.nome=nome;this.descricao=descricao;this.tipo=tipo;this.dataHora=dataHora;this.prazoAlteracoes=prazoAlteracoes; }
 public Long getId(){return id;} public String getNome(){return nome;} public String getDescricao(){return descricao;} public String getTipo(){return tipo;} public OffsetDateTime getDataHora(){return dataHora;} public OffsetDateTime getPrazoAlteracoes(){return prazoAlteracoes;} public StatusEvento getStatus(){return status;}
 public void alterarStatus(StatusEvento novo) {
  boolean permitido=switch(status) {
   case RASCUNHO -> novo==StatusEvento.ABERTO || novo==StatusEvento.CANCELADO;
   case ABERTO -> novo==StatusEvento.ENCERRADO || novo==StatusEvento.CANCELADO;
   case ENCERRADO -> novo==StatusEvento.REALIZADO || novo==StatusEvento.CANCELADO;
   case REALIZADO,CANCELADO -> false;
  };
  if(!permitido) throw new IllegalStateException("Transição de status não permitida: "+status+" -> "+novo);
  status=novo;
 }
}
