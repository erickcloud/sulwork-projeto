# Sulwork — Gestão de eventos de alimentação

Aplicação full-stack para organizar eventos, participantes, inscrições, categorias, itens e contribuições.

## Tecnologias

- Java 21
- Spring Boot 4.1.2
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Springdoc OpenAPI / Swagger
- JUnit 5, Mockito e Testcontainers
- React, TypeScript e Vite
- Docker Compose

## Como executar

### Pré-requisitos

- Docker
- Docker Compose

Na raiz do projeto, execute:

```bash
docker compose up --build
```

A aplicação estará disponível em:

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Frontend: http://localhost:5173
- API REST: http://localhost:8080/api/eventos
- OpenAPI JSON: http://localhost:8080/v3/api-docs

As credenciais e configurações do banco devem ser consultadas no arquivo `docker-compose.yml`.

## Funcionalidades implementadas

- Cadastro e consulta de eventos.
- Atualização de eventos.
- Controle de status e transições permitidas.
- Cadastro e consulta de participantes.
- Inscrição de participantes em eventos, com prevenção de duplicidade.
- Cadastro e consulta de categorias por evento.
- Cadastro e consulta de itens por categoria.
- Definição de quantidade necessária, obrigatoriedade e alérgenos dos itens.
- Registro e consulta de contribuições.
- Validação de quantidades e prazos de contribuição.
- Consulta de cobertura geral e por categoria.
- Interface web para gerenciamento das principais funcionalidades.

## Endpoints disponíveis

| Método | Rota | Finalidade |
|---|---|---|
| GET | `/api/eventos` | Listar eventos |
| POST | `/api/eventos` | Criar evento |
| PUT | `/api/eventos/{id}` | Atualizar evento |
| PATCH | `/api/eventos/{id}/status` | Alterar status |
| GET | `/api/participantes` | Listar participantes |
| POST | `/api/participantes` | Cadastrar participante |
| POST | `/api/inscricoes` | Criar inscrição |
| GET | `/api/inscricoes` | Listar inscrições |
| POST | `/api/categorias` | Criar categoria |
| GET | `/api/categorias/evento/{eventoId}` | Listar categorias do evento |
| POST | `/api/itens` | Cadastrar item |
| GET | `/api/itens/categoria/{categoriaId}` | Listar itens da categoria |
| POST | `/api/contribuicoes` | Registrar contribuição |
| GET | `/api/contribuicoes` | Listar contribuições |
| GET | `/api/eventos/{id}/cobertura` | Consultar cobertura do evento |

A documentação interativa completa está disponível no Swagger UI.

## Regras de negócio

### Status dos eventos

- RASCUNHO → ABERTO ou CANCELADO.
- ABERTO → ENCERRADO ou CANCELADO.
- ENCERRADO → REALIZADO ou CANCELADO.
- REALIZADO e CANCELADO são estados finais.

### Participantes e inscrições

- O CPF deve possuir formato válido.
- Um participante não pode possuir inscrição duplicada no mesmo evento.

### Itens e contribuições

- A quantidade necessária de um item deve ser positiva.
- A quantidade de uma contribuição deve ser positiva.
- Um item pode receber contribuições de vários participantes.
- A soma das contribuições não pode ultrapassar a quantidade necessária.
- As contribuições são permitidas quando o evento está ABERTO e o prazo de alterações ainda não terminou.

## Cálculo da cobertura

A cobertura geral é calculada pela média dos percentuais de atendimento de todos os itens cadastrados no evento.

Para cada item:

`Percentual do item = (menor valor entre quantidade contribuída e necessária / quantidade necessária) × 100`

A cobertura geral corresponde à soma dos percentuais dos itens dividida pelo número total de itens.

O cálculo também apresenta:

- Quantidade de itens cadastrados.
- Itens obrigatórios totalmente atendidos.
- Itens obrigatórios pendentes.
- Total contribuído por categoria.
- Percentual de atendimento da meta da categoria, quando existe meta positiva cadastrada.

Os itens obrigatórios e opcionais participam da cobertura geral. O percentual individual de cada item é limitado a 100%.

**Premissa:** a cobertura geral considera a média dos percentuais por item, sem somar diretamente quantidades de unidades diferentes. Já a cobertura por categoria soma as quantidades contribuídas dos itens daquela categoria e compara o resultado com a meta cadastrada. Por isso, a interpretação da meta deve considerar a unidade utilizada no cadastro.

## Restrições alimentares e alérgenos

O sistema permite cadastrar restrições alimentares dos participantes e alérgenos dos itens.

Essas informações são armazenadas para consulta e apoio à organização do evento. A aplicação não realiza atualmente o cruzamento automático para gerar alertas de incompatibilidade alimentar.

## Testes

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm install
npm test
npm run build
```

## Arquitetura

O backend utiliza organização em camadas:

- `api`: controllers e endpoints REST.
- `service`: regras de negócio.
- `repository`: acesso a dados com Spring Data JPA.
- `domain`: entidades.
- `dto`: objetos de transferência de dados.

O PostgreSQL é gerenciado por migrations do Flyway. O frontend consome a API REST por meio de requisições HTTP.

O projeto possui testes automatizados com JUnit 5, Mockito e Testcontainers. Também utiliza consulta SQL nativa para somar as quantidades contribuídas por item.

## Limitações conhecidas

Esta entrega é uma aplicação para demonstração técnica. Autenticação, autorização, auditoria, cruzamento automático de restrições alimentares e alérgenos e operações completas de edição e exclusão ainda podem ser evoluídos.

O tratamento e a proteção dos dados pessoais, especialmente CPF, também precisam de controles adicionais antes de utilização em produção.

## Uso de inteligência artificial

A inteligência artificial foi utilizada como ferramenta de apoio durante o desenvolvimento, incluindo auxílio na análise de erros, revisão de código, organização da documentação e elaboração de testes. As alterações foram verificadas por meio da execução da aplicação, dos testes automatizados e da compilação do frontend.
