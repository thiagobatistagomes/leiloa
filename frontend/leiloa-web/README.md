# Leiloa Web

Frontend da plataforma **Leiloa**, um sistema de leilões online desenvolvido como projeto pessoal full stack.

Esta aplicação foi construída com foco em **clareza de uso**, **organização de código**, **experiência do usuário**, **integração com a API** e **evolução incremental**.

---

## Visão Geral

O Leiloa Web é a interface web responsável por permitir que usuários e administradores interajam com a plataforma de leilões.

A aplicação permite:

- Visualização da página inicial da plataforma
- Listagem pública de leilões
- Busca e filtro de leilões por categoria, status e texto
- Visualização dos detalhes de um leilão
- Cadastro e login de usuários
- Controle de sessão com JWT
- Criação e gerenciamento de itens do usuário
- Criação e acompanhamento de leilões
- Realização e visualização de lances
- Comentários em leilões
- Cadastro e manutenção de endereços
- Finalização de pagamentos simulados
- Acompanhamento de notificações
- Atualização de dados da conta
- Área administrativa para usuários com permissão de administrador

O frontend consome a **Leiloa API** e utiliza JSON como formato de troca de dados.

---

## Tecnologias Utilizadas

- **Angular 21**
- **TypeScript**
- **Angular Router**
- **Angular Forms**
- **Angular HttpClient**
- **Angular Signals**
- **RxJS**
- **SCSS**
- **Vitest**
- **npm**

---

## Executando o Projeto

### Pré-requisitos

Antes de executar a aplicação, é necessário ter instalado:

- **Node.js** em versão compatível com Angular 21
- **npm**
- **Git**
- **Leiloa API** configurada e em execução

> A API deve estar disponível em `http://localhost:8080`.

---

### 1. Instalando as dependências

Na raiz do frontend, execute:

```bash
npm install
```

---

### 2. Configurando a integração com o backend

O projeto utiliza o arquivo `proxy.conf.json` para redirecionar chamadas iniciadas com `/api` para a API local.

Configuração atual:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "pathRewrite": {
      "^/api": ""
    }
  }
}
```

Com isso, uma chamada feita pelo frontend para:

```text
/api/auth/login
```

é enviada para:

```text
http://localhost:8080/auth/login
```

---

### 3. Executando a API

Antes de iniciar o frontend, execute o backend seguindo as instruções do README da **Leiloa API**.

Por padrão, a API deve responder em:

```text
http://localhost:8080
```

---

### 4. Executando o frontend

Com as dependências instaladas e a API em execução, rode:

```bash
npm start
```

ou:

```bash
ng serve
```

Após iniciar, a aplicação estará disponível em:

```text
http://localhost:4200
```

O servidor de desenvolvimento recarrega automaticamente quando arquivos do projeto são alterados.

---

## Rotas Principais

- `/` - Página inicial
- `/leiloes` - Catálogo público de leilões
- `/leiloes/:id` - Detalhes de um leilão
- `/entrar` - Login
- `/cadastro` - Criação de conta
- `/minha-area` - Área do usuário autenticado
- `/meus-itens` - Itens cadastrados pelo usuário
- `/enderecos` - Endereços do usuário
- `/pagamentos/:id` - Finalização de pagamento
- `/notificacoes` - Notificações do usuário
- `/conta` - Configurações da conta
- `/admin` - Área administrativa

Algumas rotas exigem autenticação. A rota `/admin` exige usuário com perfil de administrador.

---

## Autenticação e Autorização

A aplicação utiliza autenticação baseada em **JWT**.

### Fluxo de autenticação

1. O usuário faz login ou cria uma conta
2. A API retorna um token JWT
3. O frontend armazena a sessão no `localStorage`
4. O interceptor HTTP envia o token nas requisições protegidas
5. Guards controlam o acesso às rotas privadas

### Perfis utilizados

- `ROLE_USER` - Usuário comum
- `ROLE_ADMIN` - Administrador do sistema

O controle de acesso no frontend é feito por:

- `authGuard`
- `guestGuard`
- `adminGuard`
- `authInterceptor`

---

## Arquitetura

A aplicação segue uma separação clara de responsabilidades dentro de `src/app`.

### Estrutura principal

- **core**
  - Serviços, modelos, guards, interceptors e utilitários compartilhados.

- **features**
  - Telas principais da aplicação, separadas por domínio ou fluxo de uso.

- **shared**
  - Componentes reutilizáveis, como cabeçalhos, cartões de leilão e ações do usuário.

### Camadas principais

- **Pages**
  - Componentes de tela responsáveis pela experiência de cada rota.

- **Services**
  - Camada de comunicação com a API.

- **Models**
  - Tipos TypeScript usados para representar dados recebidos e enviados.

- **Guards**
  - Proteção de rotas públicas, privadas e administrativas.

- **Interceptors**
  - Inclusão automática do token JWT nas requisições HTTP.

---

## Comunicação com a API

As chamadas HTTP são centralizadas principalmente em:

- `src/app/core/services/auth.service.ts`
- `src/app/core/services/auction.service.ts`
- `src/app/core/services/marketplace.service.ts`

Principais áreas integradas:

- Autenticação
- Usuários
- Categorias
- Itens
- Leilões
- Lances
- Comentários
- Endereços
- Pagamentos
- Entregas
- Notificações
- Administração

---

## Testes

Para executar os testes unitários:

```bash
npm test
```

O projeto utiliza **Vitest** como test runner.

---

## Build

Para gerar uma versão de produção:

```bash
npm run build
```

Os arquivos finais serão gerados na pasta:

```text
dist/
```

---

## Scripts Disponíveis

- `npm start` - Inicia o servidor de desenvolvimento
- `npm run build` - Gera o build de produção
- `npm run watch` - Executa o build em modo observação
- `npm test` - Executa os testes unitários

---

## Observações Importantes

- O frontend depende da API para autenticação, listagens e operações protegidas.
- A API deve estar ativa antes de usar fluxos como login, cadastro, lances, pagamentos e administração.
- O proxy de desenvolvimento só funciona ao executar a aplicação com `ng serve` ou `npm start`.
- Tokens expirados são removidos automaticamente da sessão local.

---

