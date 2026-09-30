# DataFlow

Sistema de processamento e análise de arquivos de transações.

O objetivo deste projeto é servir como um **projeto prático de recuperação e atualização de conhecimentos em Java**, evoluindo gradualmente de uma aplicação Java pura para uma aplicação backend com Spring Boot.

A ideia não é utilizar todos os recursos de uma vez. O projeto deve evoluir por etapas, introduzindo cada conceito quando houver uma necessidade real para a implementação.

---

## Objetivos

Este projeto deve permitir praticar:

- Programação orientada a objetos
- Classes, objetos e encapsulamento
- Interfaces e abstrações
- Generics
- Collections
- Exceptions
- I/O e NIO
- Lambdas
- Streams
- Optional
- `java.time`
- Recursos modernos da linguagem Java
- Concorrência
- Executors
- `CompletableFuture`
- Virtual Threads
- Testes automatizados
- HTTP e JSON
- Spring Boot
- REST
- Persistência
- PostgreSQL
- JPA
- Transações
- Docker
- Observabilidade

O projeto deverá ser desenvolvido **incrementalmente**.

---

# Domínio

O sistema recebe arquivos contendo transações financeiras.

Exemplo:

```csv
id,customerId,amount,date,status
1,1001,150.90,2026-09-01,PAID
2,1002,89.50,2026-09-01,PENDING
3,1001,250.00,2026-09-02,PAID
4,1003,-50.00,2026-09-02,PAID
```

O sistema deve:

1. Ler o arquivo.
2. Converter os registros em objetos.
3. Validar os dados.
4. Separar registros válidos e inválidos.
5. Processar as transações.
6. Gerar estatísticas.
7. Gerar um relatório.
8. Permitir consultar o resultado.
9. Evoluir posteriormente para processamento concorrente e assíncrono.

---

# Fases do projeto

## Fase 1 — Orientação a Objetos

### Conceitos

- Classes
- Objetos
- Encapsulamento
- Construtores
- Métodos
- Enums
- Composição
- Interfaces

### Aplicação

Criar o modelo de domínio:

```text
Transaction
Customer
ProcessingResult
ProcessingError
```

Exemplo:

```java
public class Transaction {
    private Long id;
    private Long customerId;
    private BigDecimal amount;
    private LocalDate date;
    private TransactionStatus status;
}
```

Criar uma enumeração:

```java
public enum TransactionStatus {
    PENDING,
    PAID,
    CANCELLED
}
```

Criar interfaces para separar responsabilidades:

```java
public interface TransactionReader {
    List<Transaction> read(Path file);
}
```

```java
public interface TransactionProcessor {
    ProcessingResult process(List<Transaction> transactions);
}
```

### Objetivo

Praticar modelagem de objetos e separação de responsabilidades antes de introduzir frameworks.

---

# Fase 2 — Collections

## Conceitos

- `List`
- `Set`
- `Map`
- `Queue`
- `equals`
- `hashCode`
- `Comparator`

### Aplicação

Utilizar:

```java
List<Transaction>
```

para representar as transações.

Utilizar:

```java
Set<Long>
```

para identificar clientes únicos.

Utilizar:

```java
Map<Long, List<Transaction>>
```

para agrupar transações por cliente.

Utilizar:

```java
Map<TransactionStatus, Long>
```

para contabilizar transações por status.

Criar ordenações por:

- valor;
- data;
- cliente;
- status.

### Objetivo

Praticar a escolha da estrutura de dados adequada para cada problema.

---

# Fase 3 — Generics

## Conceitos

- Classes genéricas
- Interfaces genéricas
- Métodos genéricos
- Bounds, quando necessário

### Aplicação

Criar componentes reutilizáveis:

```java
public interface Reader<T> {
    Stream<T> read(Path file);
}
```

Criar um resultado genérico:

```java
public class ProcessingResult<T> {
    private List<T> successful;
    private List<ProcessingError> errors;
}
```

### Objetivo

Entender como criar componentes tipados e reutilizáveis sem recorrer a `Object`.

---

# Fase 4 — Exceptions

## Conceitos

- Checked exceptions
- Unchecked exceptions
- Exceções customizadas
- Tratamento de exceções
- `try/catch`
- `try-with-resources`

### Aplicação

Criar exceções específicas:

```text
InvalidTransactionException
InvalidAmountException
InvalidDateException
UnsupportedFileException
```

O sistema deve distinguir:

- arquivo inexistente;
- arquivo inválido;
- registro inválido;
- valor inválido;
- data inválida.

Registros inválidos não devem necessariamente interromper todo o processamento.

Eles devem ser registrados como erros:

```text
Processed: 10000
Successful: 9842
Invalid: 158
```

### Objetivo

Praticar tratamento de erros sem utilizar `Exception` genericamente para tudo.

---

# Fase 5 — Java moderno

## Conceitos

- Lambdas
- Streams
- `Optional`
- `record`
- `switch expressions`
- `java.time`
- Pattern Matching
- Sealed Classes
- Text Blocks

Nem todos os recursos precisam ser utilizados obrigatoriamente se não houver uma aplicação natural.

---

## Records

Utilizar `record` para objetos imutáveis de transporte de dados.

Exemplo:

```java
public record Transaction(
    Long id,
    Long customerId,
    BigDecimal amount,
    LocalDate date,
    TransactionStatus status
) {}
```

---

## Lambdas

Utilizar lambdas em operações de:

- filtragem;
- ordenação;
- transformação;
- processamento.

---

## Streams

O processamento das transações deve utilizar Streams onde isso melhorar a clareza.

Exemplo:

```java
BigDecimal total =
    transactions.stream()
        .filter(transaction -> transaction.status() == TransactionStatus.PAID)
        .map(Transaction::amount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
```

Criar relatórios utilizando:

```text
filter
map
sorted
groupingBy
partitioningBy
counting
reducing
```

Não utilizar Streams apenas para substituir um `for` simples quando isso tornar o código menos legível.

---

## Optional

Utilizar `Optional` em operações que podem não encontrar um resultado.

Exemplo:

```java
Optional<Transaction> findById(Long id)
```

Evitar utilizar `Optional` indiscriminadamente em atributos ou parâmetros.

---

## java.time

Utilizar:

```text
LocalDate
LocalDateTime
Instant
Duration
```

para datas e horários.

Não utilizar `java.util.Date` ou `Calendar` para novas implementações.

---

## Switch Expressions

Utilizar para processamento baseado no status:

```java
return switch (transaction.status()) {
    case PAID -> processPaid(transaction);
    case PENDING -> processPending(transaction);
    case CANCELLED -> processCancelled(transaction);
};
```

---

## Pattern Matching

Utilizar quando houver necessidade de verificar tipos diferentes durante o processamento.

O recurso deve ser introduzido quando surgir um caso real, e não apenas para demonstrar sintaxe.

---

## Sealed Classes

Podem ser utilizadas para representar resultados de processamento:

```java
public sealed interface ProcessingResult
    permits Success, Failure {
}
```

---

# Fase 6 — I/O e NIO

## Conceitos

- `Path`
- `Files`
- Streams de arquivo
- Leitura incremental
- Escrita de arquivos
- `try-with-resources`

### Aplicação

Implementar:

```text
CSV → Transaction
```

Primeira implementação:

```java
Files.readAllLines(...)
```

Depois evoluir para processamento incremental:

```java
Files.lines(...)
```

O objetivo é perceber a diferença entre carregar todo o arquivo na memória e processá-lo incrementalmente.

### Requisito

O sistema deve conseguir processar arquivos grandes sem carregar todo o conteúdo simultaneamente na memória.

---

# Fase 7 — Processamento de dados

O sistema deve gerar pelo menos:

### Total de transações

```text
Total transactions: 10000
```

### Transações por status

```text
PAID:      7200
PENDING:   2100
CANCELLED:  700
```

### Valor total

```text
Total paid: R$ 1.245.932,45
```

### Estatísticas por cliente

```text
Customer 1001
Transactions: 42
Total: R$ 8.421,90
```

### Registros inválidos

```text
Invalid transactions: 158
```

---

# Fase 8 — Testes

## Conceitos

- JUnit
- Assertions
- Testes parametrizados
- Testes de exceção
- Mockito, quando houver necessidade
- Testes de integração

Criar testes para:

```text
TransactionValidator
TransactionProcessor
CsvReader
ReportGenerator
```

Testar casos como:

- transação válida;
- valor negativo;
- data inválida;
- status desconhecido;
- arquivo inexistente;
- arquivo vazio;
- arquivo com registros inválidos;
- arquivo grande.

### Requisito

Toda regra de negócio relevante deve possuir teste automatizado.

---

# Fase 9 — Concorrência

A implementação inicial deve ser sequencial.

Somente depois que ela estiver funcionando corretamente, implementar versões concorrentes.

## Conceitos

- `Thread`
- `Runnable`
- `ExecutorService`
- `Callable`
- `Future`
- Concurrent Collections
- Synchronization
- Race Conditions
- `CompletableFuture`
- Virtual Threads

---

## Problema

Processar um grande conjunto de arquivos:

```text
transactions/
├── 001.csv
├── 002.csv
├── 003.csv
├── ...
└── 100.csv
```

Comparar:

```text
Processamento sequencial
        ↓
ExecutorService
        ↓
CompletableFuture
        ↓
Virtual Threads
```

Medir o tempo de execução de cada abordagem.

Exemplo:

```text
Sequential:       XX.XX s
ExecutorService:  XX.XX s
CompletableFuture:XX.XX s
Virtual Threads:  XX.XX s
```

Os valores devem ser obtidos durante a execução, não definidos manualmente.

### Objetivo

Entender na prática os benefícios e limitações de diferentes modelos de concorrência.

---

# Fase 10 — HTTP e JSON

Criar um cliente para uma API externa.

Exemplo:

```text
GET /exchange-rates
GET /customers/{id}
```

Utilizar o HTTP Client da JDK.

Praticar:

- HTTP;
- headers;
- status codes;
- timeout;
- JSON;
- tratamento de erros;
- DTOs;
- records.

---

# Fase 11 — Spring Boot

Depois de concluir a implementação Java pura, transformar o DataFlow em uma aplicação Spring Boot.

## Objetivo

Não simplesmente "refazer o projeto".

A ideia é comparar:

```text
Java puro
    ↓
Spring Boot
```

e entender quais problemas o framework resolve.

---

## API

Criar endpoints:

```http
POST /imports
GET /imports
GET /imports/{id}
GET /imports/{id}/status
GET /imports/{id}/report
```

---

## Conceitos Spring

Praticar:

- Dependency Injection
- `@Component`
- `@Service`
- `@Repository`
- `@RestController`
- Configuration
- Profiles
- Validation
- Exception Handling
- DTOs

---

# Fase 12 — PostgreSQL

Substituir a persistência em memória por PostgreSQL.

Entidades principais:

```text
Import
Transaction
ProcessingError
```

Praticar:

- SQL;
- modelagem relacional;
- índices;
- constraints;
- relacionamentos;
- paginação;
- transações.

---

# Fase 13 — JPA

Utilizar:

- Spring Data JPA;
- Entities;
- Repositories;
- Queries;
- Transactions;
- Lazy/Eager loading;
- paginação.

Investigar o SQL gerado pela aplicação em vez de tratar JPA como uma "caixa preta".

---

# Fase 14 — Flyway

As alterações do banco devem ser versionadas.

Exemplo:

```text
V1__create_import.sql
V2__create_transaction.sql
V3__create_processing_error.sql
V4__add_transaction_indexes.sql
```

---

# Fase 15 — Processamento assíncrono

Evoluir o processo de importação.

Em vez de:

```text
POST /imports
       ↓
processa tudo
       ↓
responde
```

implementar:

```text
POST /imports
       ↓
cria importação
       ↓
retorna ID
       ↓
processamento em background
```

Consultar:

```http
GET /imports/{id}/status
```

Exemplo:

```json
{
  "id": 42,
  "status": "PROCESSING",
  "processed": 750000,
  "total": 1000000
}
```

---

# Fase 16 — Docker

Containerizar a aplicação.

Serviços:

```text
Application
PostgreSQL
```

Utilizar Docker Compose para execução local.

Exemplo:

```bash
docker compose up
```

---

# Fase 17 — Observabilidade

Adicionar:

- Health Check
- métricas;
- logs estruturados;
- tempo de processamento;
- quantidade de registros processados;
- quantidade de erros;
- memória utilizada;
- duração das etapas.

Métricas desejadas:

```text
imports_total
transactions_processed_total
transactions_invalid_total
import_processing_duration
```

Opcionalmente:

- Micrometer
- Prometheus
- Grafana
- OpenTelemetry

---

# Requisitos finais

Ao final da evolução, o sistema deverá:

- [ ] Receber arquivos CSV
- [ ] Validar registros
- [ ] Processar registros
- [ ] Gerar relatórios
- [ ] Tratar registros inválidos
- [ ] Processar arquivos grandes
- [ ] Possuir testes automatizados
- [ ] Demonstrar processamento sequencial e concorrente
- [ ] Utilizar Java moderno
- [ ] Expor uma API REST
- [ ] Persistir dados em PostgreSQL
- [ ] Utilizar migrations
- [ ] Permitir processamento assíncrono
- [ ] Possuir Docker Compose
- [ ] Possuir documentação
- [ ] Possuir métricas básicas

---

# Princípios do projeto

## 1. Não utilizar tecnologia sem motivo

Não adicionar uma biblioteca ou ferramenta apenas para aumentar a lista de tecnologias.

A pergunta deve ser:

> "Qual problema isso resolve?"

---

## 2. Primeiro implementação simples

Sempre que possível:

```text
Implementação simples
        ↓
Teste
        ↓
Medição
        ↓
Identificação do problema
        ↓
Otimização
```

---

## 3. Evitar abstrações prematuras

Não criar interfaces, factories, strategies e outras abstrações apenas porque "boas práticas mandam".

A abstração deve surgir de uma necessidade real.

---

## 4. Medir antes de otimizar

Especialmente na parte de concorrência.

Não assumir que:

> "mais threads = mais rápido".

Medir.

---

## 5. Evolução incremental

O projeto deve continuar funcionando ao final de cada fase.

```text
v1 → Java puro
v2 → Java moderno
v3 → testes
v4 → concorrência
v5 → Spring Boot
v6 → PostgreSQL
v7 → processamento assíncrono
v8 → Docker
v9 → observabilidade
```

---

# Estrutura esperada

Durante a fase Java puro:

```text
dataflow/
├── src/
│   ├── main/
│   │   └── java/
│   └── test/
│       └── java/
├── data/
├── reports/
├── README.md
└── pom.xml
```

A estrutura poderá ser reorganizada quando o projeto migrar para Spring Boot.

---

# Objetivo pessoal

Este projeto não tem como objetivo apenas produzir uma aplicação.

Ele serve para recuperar fluência em Java através de um problema único e progressivamente mais complexo.

A prioridade é:

```text
Entender
   ↓
Implementar
   ↓
Testar
   ↓
Medir
   ↓
Refatorar
   ↓
Evoluir
```

O resultado final deverá ser simultaneamente:

1. um exercício de atualização técnica;
2. um laboratório para experimentar Java moderno;
3. uma base para estudar Spring Boot;
4. um projeto que possa posteriormente ser transformado em uma peça de portfólio.