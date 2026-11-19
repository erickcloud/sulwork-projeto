CREATE TABLE evento (id BIGSERIAL PRIMARY KEY, nome VARCHAR(140) NOT NULL, descricao TEXT, tipo VARCHAR(80) NOT NULL, data_hora TIMESTAMPTZ NOT NULL, prazo_alteracoes TIMESTAMPTZ NOT NULL, status VARCHAR(20) NOT NULL);
CREATE TABLE participante (id BIGSERIAL PRIMARY KEY, nome VARCHAR(140) NOT NULL, cpf VARCHAR(11) NOT NULL UNIQUE, restricoes TEXT NOT NULL DEFAULT '');
CREATE TABLE inscricao (id BIGSERIAL PRIMARY KEY, evento_id BIGINT NOT NULL REFERENCES evento(id), participante_id BIGINT NOT NULL REFERENCES participante(id), CONSTRAINT uk_inscricao UNIQUE(evento_id, participante_id));
CREATE TABLE categoria (id BIGSERIAL PRIMARY KEY, evento_id BIGINT NOT NULL REFERENCES evento(id), nome VARCHAR(100) NOT NULL, meta NUMERIC(10,2));
CREATE TABLE item (id BIGSERIAL PRIMARY KEY, categoria_id BIGINT NOT NULL REFERENCES categoria(id), nome VARCHAR(120) NOT NULL, quantidade_necessaria NUMERIC(10,2) NOT NULL CHECK(quantidade_necessaria > 0), obrigatorio BOOLEAN NOT NULL DEFAULT TRUE, alergenos TEXT NOT NULL DEFAULT '');
CREATE TABLE contribuicao (id BIGSERIAL PRIMARY KEY, item_id BIGINT NOT NULL REFERENCES item(id), participante_id BIGINT NOT NULL REFERENCES participante(id), quantidade NUMERIC(10,2) NOT NULL CHECK(quantidade > 0));
CREATE INDEX idx_categoria_evento ON categoria(evento_id);
CREATE INDEX idx_item_categoria ON item(categoria_id);
CREATE INDEX idx_contribuicao_item ON contribuicao(item_id);
