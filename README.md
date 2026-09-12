# 🏥 Pulse Care

Pulse Care é um backend hospitalar modular desenvolvido para o **Tech Challenge da Fase 3** da Pós-Tech FIAP, em Arquitetura e Desenvolvimento Java. O sistema cobre agendamento de consultas, controle de acesso por perfil (médico, enfermeiro, paciente) e envio assíncrono de lembretes, através de três serviços independentes que se comunicam via RabbitMQ.

Em um ambiente hospitalar, é essencial contar com um sistema que garanta o agendamento eficaz de consultas, o controle de quem pode ver o quê, e o aviso automático de consultas aos pacientes. O sistema resolve isso com:

- Autenticação e autorização por perfil (JWT + Spring Security)
- Consulta flexível de histórico médico via **GraphQL**
- Comunicação assíncrona entre serviços via **RabbitMQ**
- Persistência em **PostgreSQL**, com todo o ambiente reproduzível via **Docker Compose**

## 🧱 Arquitetura

O sistema é dividido em três serviços Spring Boot independentes, que não se conhecem diretamente — toda comunicação entre eles acontece de forma assíncrona, via RabbitMQ. O `scheduling-service` publica um único evento por consulta criada/editada, e **dois consumidores independentes** (`notification-service` e `history-service`) recebem cada um a sua própria cópia da mensagem, cada qual com sua fila:

```
scheduling-service (porta 8081)
  REST + GraphQL, dados em Postgres próprio
    │
    │  publica evento (appointment-exchange / appointment.created)
    ▼
RabbitMQ (appointment-exchange)
    │
    ├── fila appointment-queue ────────▶ notification-service (porta 8082)
    │                                      loga o lembrete ao paciente
    │
    └── fila appointment-history-queue ─▶ history-service (porta 8083)
                                           grava o evento em Postgres próprio
                                           e expõe o histórico via GraphQL
```

- **scheduling-service**: responsável por usuários, autenticação e o CRUD de consultas. Publica um evento no RabbitMQ toda vez que uma consulta é criada ou editada.
- **notification-service**: escuta essa fila (`appointment-queue`) e processa o lembrete do paciente (hoje simulado via log — não há integração real de e-mail/SMS, o foco do desafio é a comunicação assíncrona entre serviços).
- **history-service**: escuta a mesma publicação em uma fila própria (`appointment-history-queue`) e grava cada evento como uma nova linha num banco Postgres dele mesmo — ou seja, o histórico é um log completo (cria consulta → uma linha `CREATED`; edita a mesma consulta → uma nova linha `UPDATED`, sem apagar a anterior). Expõe uma query GraphQL própria pra consultar esse histórico por paciente. É o serviço opcional citado no PDF ("armazena o histórico de consultas e disponibiliza dados via GraphQL").

Dentro do `scheduling-service`, a organização é em camadas convencionais do Spring Boot:

```
controllers/   → entrada REST e GraphQL
services/      → regras de negócio
repositories/  → acesso a dados (Spring Data JPA)
models/        → entidades JPA
dtos/          → contratos de entrada/saída (requests, responses, eventos)
mappers/       → conversão entre entidade e DTO
security/      → JWT, filtro de autenticação, UserDetailsService
exceptions/    → exceções de negócio + handler centralizado
messaging/     → publicação de eventos no RabbitMQ
config/        → configuração do RabbitMQ (filas, exchange, binding)
```

## ⚙️ Stack e Tecnologias

| Categoria | Tecnologias |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.0.8 |
| Banco de dados | PostgreSQL 16 |
| Mensageria | RabbitMQ 3 (management) |
| Consulta flexível | Spring for GraphQL |
| Segurança | Spring Security + JWT (java-jwt) |
| Build | Maven (wrapper incluso) |
| Containerização | Docker / Docker Compose |
| Testes de API | Postman / Newman |

## 📂 Estrutura do Repositório

```
adjt-fase3-pulse-care/
├── docker-compose.yml          # orquestra Postgres (x2) + RabbitMQ + os tres servicos
├── scheduling-service/         # agendamento, usuarios, auth, GraphQL
│   └── Dockerfile
├── notification-service/       # consumidor da fila de lembretes
│   └── Dockerfile
├── history-service/             # consumidor da fila de historico, expoe GraphQL
│   └── Dockerfile
└── docs/
    └── postman/                # collection + testes automatizados de API
```

## 🔐 Perfis de Acesso

| Perfil | Pode |
|---|---|
| **PACIENTE** | Visualizar apenas as próprias consultas (histórico completo ou só as futuras) |
| **ENFERMEIRO** | Registrar novas consultas e visualizar o histórico de qualquer paciente |
| **MÉDICO** | Registrar, editar e visualizar consultas; é o único perfil que pode listar a equipe (`/v1/users/staff`) |

Regras aplicadas via Spring Security (por rota) e reforçadas na camada de serviço (regra de "dono do recurso" para pacientes).

## 🗄️ Banco de Dados

- Cada serviço com estado tem seu **próprio banco Postgres** (um por container, sem compartilhar schema) — `scheduling-service` e `history-service`, cada um com o seu.
- Schema gerado automaticamente pelo Hibernate (`ddl-auto: update`) — sem migrations versionadas (Flyway não foi utilizado neste projeto).
- `scheduling-service` (`pulse_care_scheduling`): `users` (médicos, enfermeiros e pacientes, com `role` diferenciando o perfil) e `appointments` (consultas, com FK para paciente e médico).
- `history-service` (`pulse_care_history`): `appointment_history` — uma linha por evento recebido do RabbitMQ (log append-only, nunca sobrescrito).

## 🐰 Comunicação Assíncrona (RabbitMQ)

| Elemento | Nome |
|---|---|
| Exchange | `appointment-exchange` (direct) |
| Routing key | `appointment.created` |
| Queue — notification-service | `appointment-queue` |
| Queue — history-service | `appointment-history-queue` |

Ao criar (`POST /v1/appointments`) ou editar (`PUT /v1/appointments/{id}`) uma consulta, o `scheduling-service` publica **uma única mensagem** (`AppointmentEventDTO`, com `eventType: CREATED` ou `UPDATED`) na exchange. Como as duas filas estão vinculadas à mesma exchange/routing key, cada uma recebe sua própria cópia da mensagem — é assim que `notification-service` e `history-service` conseguem reagir ao mesmo evento de forma independente, sem um saber da existência do outro. O processamento é assíncrono e desacoplado: o agendamento funciona normalmente mesmo que os dois consumidores estejam fora do ar.

> Como os dois serviços consumidores publicam DTOs equivalentes mas em pacotes Java diferentes, o conversor de mensagens de cada um foi configurado com `TypePrecedence.INFERRED` — em vez de confiar no header `__TypeId__` (que aponta pra uma classe que só existe no `scheduling-service`), o tipo é inferido a partir do parâmetro do método `@RabbitListener`.

## 🐳 Execução com Docker Compose (recomendado)

Pré-requisito: Docker e Docker Compose instalados.

```bash
docker compose up --build -d
```

Isso sobe, em ordem, com healthcheck entre as etapas:
- `postgres-scheduling` e `postgres-history` (um Postgres por serviço com banco próprio, cada um com volume persistente)
- `rabbitmq` (com painel de gerenciamento)
- `scheduling-service` → `http://localhost:8081`
- `notification-service` → `http://localhost:8082`
- `history-service` → `http://localhost:8083`

Acompanhar os eventos sendo processados em tempo real:
```bash
docker logs -f pulse-care-notification-service
docker logs -f pulse-care-history-service
```

Parar tudo:
```bash
docker compose down       # mantém os dados do banco
docker compose down -v    # remove também os dados (reset completo)
```

## 💻 Execução Local (sem Docker)

Pré-requisitos: Java 21, Maven 3.9+.

Sobe só a infraestrutura via Docker e roda os serviços pela IDE/terminal:
```bash
docker compose up -d postgres-scheduling postgres-history rabbitmq

cd scheduling-service && ./mvnw spring-boot:run
cd notification-service && ./mvnw spring-boot:run
cd history-service && ./mvnw spring-boot:run
```
As configurações padrão em `application.yml` já apontam para `localhost` nas portas expostas pelo Docker.

## 🌍 Variáveis de Ambiente

Usadas pelo `docker-compose.yml` para conectar os serviços entre si pela rede interna do Docker (fora do Docker, os defaults do `application.yml` já apontam para `localhost`):

| Variável | Serviço | Descrição | Default (local) |
|---|---|---|---|
| `SPRING_DATASOURCE_URL` | scheduling-service | URL JDBC do Postgres | `jdbc:postgresql://localhost:5432/pulse_care_scheduling` |
| `SPRING_DATASOURCE_URL` | history-service | URL JDBC do Postgres | `jdbc:postgresql://localhost:5433/pulse_care_history` |
| `SPRING_DATASOURCE_USERNAME` | scheduling-service, history-service | Usuário do banco | `pulsecare` |
| `SPRING_DATASOURCE_PASSWORD` | scheduling-service, history-service | Senha do banco | `pulsecare` |
| `SPRING_RABBITMQ_HOST` | os três | Host do RabbitMQ | `localhost` |

## 📘 Documentação da API

### REST — `scheduling-service` (`http://localhost:8081`)

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/v1/auth/login` | Público | Autentica e gera token JWT |
| POST | `/v1/users` | Público | Cadastra usuário (paciente, enfermeiro ou médico) |
| GET | `/v1/users/{id}` | Autenticado | Consulta usuário por ID |
| GET | `/v1/users/staff` | MÉDICO | Lista médicos e enfermeiros |
| POST | `/v1/appointments` | MÉDICO, ENFERMEIRO | Cria consulta |
| PUT | `/v1/appointments/{id}` | MÉDICO, ENFERMEIRO | Atualiza consulta |
| GET | `/v1/appointments/{id}` | Autenticado¹ | Busca consulta por ID |
| GET | `/v1/appointments/patient/{patientId}?onlyFuture=` | Autenticado¹ | Histórico do paciente (completo ou só futuras) |
| GET | `/v1/appointments/doctor/{doctorId}?start=&end=` | Autenticado | Consultas do médico por período |

¹ Paciente só pode acessar as próprias consultas — tentar ver as de outro paciente retorna `403 Forbidden`.

### GraphQL — `POST http://localhost:8081/graphql`

Interface interativa (GraphiQL): `http://localhost:8081/graphiql`

```graphql
query($patientId: ID!, $onlyFuture: Boolean) {
  patientAppointments(patientId: $patientId, onlyFuture: $onlyFuture) {
    id
    appointmentDateTime
    status
    specialty
    doctor { name crm }
  }
}
```

Mesma regra de negócio da rota REST equivalente (reutiliza o mesmo service) — inclusive a restrição de dono para pacientes.

### GraphQL — `POST http://localhost:8083/graphql` (history-service)

Interface interativa (GraphiQL): `http://localhost:8083/graphiql`

```graphql
query($patientId: ID!) {
  patientAppointmentHistory(patientId: $patientId) {
    eventType
    specialty
    doctorName
    appointmentDateTime
    location
    recordedAt
  }
}
```

Diferente da query do `scheduling-service` (que reflete o estado atual das consultas), essa retorna o **log completo de eventos** de um paciente — uma entrada `CREATED` e, se a consulta foi editada, uma entrada `UPDATED` adicional para cada edição, ordenadas da mais recente para a mais antiga. Sem restrição de dono (o serviço não tem autenticação própria — ver observação abaixo).

> **Nota de escopo**: por ser um serviço opcional e focado em demonstrar a comunicação assíncrona via RabbitMQ, o `history-service` não reimplementa a autenticação JWT/Spring Security do `scheduling-service`. Numa evolução real, o ideal seria validar o mesmo token JWT aqui também (ou colocar um API Gateway na frente dos três serviços).

## 📬 Postman Collections

**Arquivos disponíveis em `docs/postman`:**
- `Pulse Care - Tech Challenge Fase 3.postman_collection.json`

**Como usar:**
1. Importar no Postman
2. Selecionar o ambiente “Pulse Care - Tech Challenge Fase 3”
3. Executar a pasta `0) Cenarios de Teste Automatico` para validar todos os endpoints

## 🧪 Testes

O projeto não possui testes unitários/integração automatizados além do teste padrão de carregamento de contexto do Spring Boot.

## ✅ Status do Projeto

| Requisito do PDF | Status |
|---|---|
| Autenticação com Spring Security + níveis de acesso por perfil | ✅ |
| Consulta flexível de histórico via GraphQL (todas ou só futuras) | ✅ |
| Registro/edição de consulta restrito a médico e enfermeiro | ✅ |
| Serviço de agendamento (scheduling-service) | ✅ |
| Serviço de notificações (notification-service) | ✅ |
| Serviço de histórico (opcional) — grava e disponibiliza via GraphQL | ✅ |
| Comunicação assíncrona via RabbitMQ (evento em criar/editar consulta) | ✅ |
| Documentação do projeto (arquitetura, endpoints, execução) | ✅ |
| Collection do Postman (cenários válidos e inválidos) | ✅ |
| Repositório de código aberto | ✅ |

## 🎓 Contexto Acadêmico

Projeto individual desenvolvido para o **Tech Challenge — Fase 3** da **Pós-Tech FIAP**, disciplina de Arquitetura e Desenvolvimento Java.

## 🔗 Links Úteis

- GraphiQL (scheduling-service) → http://localhost:8081/graphiql
- GraphiQL (history-service) → http://localhost:8083/graphiql
- RabbitMQ Management → http://localhost:15672 (guest/guest)
- Postman Collection → [`docs/postman/`](docs/postman)
