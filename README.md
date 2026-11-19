# Sulwork — Gestão de eventos de alimentação

Aplicação full-stack para organizar eventos, participantes, categorias, itens e contribuições.

## Stack
Java 21, Spring Boot 4.1.2, Spring Data JPA, PostgreSQL, Flyway, Springdoc OpenAPI, JUnit 5, Mockito, Testcontainers, React + TypeScript + Vite.

## Executar
Pré-requisitos: Docker e Docker Compose.
```bash
docker compose up --build
```
- Frontend: http://localhost:5173
- API: http://localhost:8080/api
- Swagger: http://localhost:8080/swagger-ui/index.html
- PostgreSQL: localhost:5432 (sulwork/sulwork)

## Testes
Backend: `cd backend && mvn test`
Frontend: `cd frontend && npm install && npm test`

## Regras de negócio
- Transições: RASCUNHO -> ABERTO/CANCELADO; ABERTO -> ENCERRADO/CANCELADO; ENCERRADO -> REALIZADO/CANCELADO. REALIZADO e CANCELADO são finais.
- Contribuição só é aceita quando status=ABERTO e instante atual <= prazo_alteracoes. Prazo e status são verificações independentes.
- A soma de contribuições de um item não pode superar sua quantidade necessária.
- Cobertura quantitativa = soma contribuída (limitada à necessidade) / soma necessária. Cobertura obrigatória = itens obrigatórios completamente atendidos / total de itens obrigatórios. A API também apresenta cobertura por categoria.
- Restrições e alérgenos são armazenados como texto separado por vírgulas e comparados sem diferenciar maiúsculas/minúsculas. Alertas são informativos, não substituem orientação médica.

## Feature livre
O endpoint de alertas cruza restrições dos participantes inscritos com alérgenos dos itens que receberam contribuições, sinalizando possíveis incompatibilidades.

## Endpoints iniciais
- `POST/GET /api/eventos`
- `PATCH /api/eventos/{id}/status`
- `POST/GET /api/eventos/{id}/participantes`
- `POST /api/eventos/{id}/categorias`
- `POST /api/categorias/{id}/itens`
- `POST /api/itens/{id}/contribuicoes`
- `GET /api/eventos/{id}/cobertura`
- `GET /api/eventos/{id}/alertas-alimentares`

## Limitações desta primeira entrega
É uma base funcional para desenvolvimento e demonstração, não um sistema pronto para produção. Autenticação, autorização, auditoria, edição/exclusão completa de cadastros e política formal de proteção do CPF ainda precisam ser definidas conforme o escopo final da empresa. Revise e execute os testes antes de entregar.
