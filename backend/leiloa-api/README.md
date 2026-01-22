# Leiloa API

Backend da plataforma **Leiloa**, um sistema de leilões online desenvolvido como projeto pessoal full stack.  
Esta API foi construída com foco em **boas práticas**, **segurança**, **organização de código**, **documentação** e **evolução incremental**.

---

## 📌 Visão Geral

A Leiloa API é uma API REST responsável por:

- Autenticação e registro de usuários
- Controle de acesso com JWT e roles
- Gerenciamento de categorias
- Cadastro, edição e exclusão lógica de itens
- Listagem pública de itens ativos com filtros e paginação


A API segue o padrão REST e utiliza **JSON** como formato de troca de dados.

---

## 🛠️ Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **Flyway** (versionamento de banco)
- **JWT (jjwt)**
- **Swagger / OpenAPI (springdoc-openapi)**
- **Lombok**
- **Maven**

---


## ▶️ Executando o Projeto

### Pré-requisitos

Antes de executar a aplicação, é necessário ter instalado:

- **Java 21**
- **Maven** (ou usar o Maven Wrapper)
- **PostgreSQL** (versão compatível com Flyway)
- **Git**

---

### 1️⃣ Configurando o banco de dados

Crie um banco de dados PostgreSQL para a aplicação, por exemplo:

```sql
CREATE DATABASE leiloa;
```

Os dados iniciais serão criados automaticamente pelo Flyway na primeira execução.

### 2️⃣ Configurando variáveis de ambiente (.env)

A aplicação utiliza variáveis de ambiente para evitar o versionamento de dados sensíveis.

Crie um arquivo `.env` na raiz do projeto (mesmo nível de pom.xml por exemplo) backend com o seguinte conteúdo:

```bash
DB_URL=jdbc:postgresql://localhost:5432/leiloa
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=suachavesecreta
JWT_EXPIRATION=1000000
```

Descrição das variáveis
Variável	    Descrição
DB_URL	        URL de conexão com o PostgreSQL
DB_USERNAME	    Usuário do banco
DB_PASSWORD	    Senha do banco
JWT_SECRET	    Chave usada para assinar os tokens JWT
JWT_EXPIRATION	Tempo de expiração do token (em ms)

### 3️⃣ application.properties

O arquivo application.properties já está preparado para ler as variáveis do .env, por exemplo:

```bash
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

O uso do spring-dotenv permite que o Spring Boot carregue automaticamente o arquivo .env.

### 4️⃣ Executando a aplicação

Com tudo configurado, execute o projeto:

```bash
mvn spring-boot:run
```

ou, utilizando o Maven Wrapper (Windows):

```bash
.\mvnw spring-boot:run
```

### 5️⃣ Acessando a aplicação

Após iniciar, a API estará disponível em:

Swagger UI
http://localhost:8080/swagger-ui.html

OpenAPI JSON
http://localhost:8080/v3/api-docs

---

## 🏗️ Arquitetura

A aplicação segue uma separação clara de responsabilidades:


### Camadas principais

- **Controller**  
  Exposição dos endpoints REST e integração com Swagger.

- **Service**  
  Contém as regras de negócio e validações de domínio.

- **Repository**  
  Acesso ao banco de dados via Spring Data JPA.

- **DTOs**  
  Utilizados para entrada e saída de dados, evitando expor entidades diretamente.

- **Domain (Entities + Enums)**  
  Representação do modelo de negócio.

---

## 🔐 Autenticação e Autorização

A API utiliza **JWT (JSON Web Token)** com autenticação stateless.

### Fluxo de autenticação

1. Usuário realiza login ou registro
2. A API retorna um token JWT
3. O token deve ser enviado nas requisições protegidas via header: Authorization: Bearer <token>


### Roles existentes

- `ROLE_USER` – usuário comum
- `ROLE_ADMIN` – administrador do sistema

O controle de acesso é feito através de:
- `@PreAuthorize`
- Configuração centralizada no `SecurityConfig`

---

## 📄 Documentação da API (Swagger)

A API é totalmente documentada utilizando **Swagger / OpenAPI**.

### URLs disponíveis

- **Swagger UI**  
  http://localhost:8080/swagger-ui.html

- **OpenAPI JSON**  
  http://localhost:8080/v3/api-docs

### Funcionalidades do Swagger

- Visualização de todos os endpoints
- Documentação de parâmetros, DTOs e enums
- Visualização de paginação (`Pageable`)
- Execução de requisições diretamente no navegador
- Autenticação via botão **Authorize** (JWT)

---

## 🔑 Usando JWT no Swagger

1. Acesse o Swagger UI
2. Clique em **Authorize**
3. Informe o token no formato: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...
4. Confirme

Após isso, endpoints protegidos poderão ser testados normalmente.

---

## 🗄️ Banco de Dados

### Banco utilizado

- **PostgreSQL**

### Versionamento de schema

O controle do banco é feito exclusivamente pelo **Flyway**.

As migrations ficam em: `src/main/resources/db/migration`


### Convenção de migrations

- `V1__*.sql` → criação do schema
- `V2__*.sql` → dados iniciais (roles e admin)
- `V3__*.sql` → evoluções futuras

O Flyway garante que o banco esteja sempre consistente com a aplicação.

---





