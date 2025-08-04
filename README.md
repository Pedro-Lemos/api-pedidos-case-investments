# API de Pedidos

Uma API REST para gerenciamento de pedidos, desenvolvida em Java com Spring Boot seguindo os princípios de Clean Architecture.

## Funcionalidades

- ✅ Efetuar pedidos
- ✅ Listar pedidos ativos
- ✅ Cancelar pedidos

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.x**
- **Gradle**
- **JUnit 5** (testes unitários)
- **Armazenamento em arquivo JSON** (simulando banco de dados)

## Informações do negócio

Para efetuar um pedido é necessário informar:
- Um `codigoIdentificacaoCliente`.
- E um `Produto` ou uma lista de `Produtos` validos.

Para listar pedidos ativos, é necessário informar:
- Um `pedidoId`.

Para cancelar um pedido, é necessário:
- Um `pedidoId` válido.
- Que o pedido esteja ativo.
- Um `motivoCancelamento`.


### PedidoId

O `pedidoId` é gerado automaticamente após a efetuação de um novo pedido.
Ele sempre seguirá o padrão `ANO + MES + DIA + SEQUENCIAL`

Exemplo: `2025080400001`


## Como Executar o Projeto

### Pré-requisitos

- Java 17 ou superior
- Gradle

### Swagger

- Encontra-se disponível no arquivo ``swagger-api-pedidos.yaml`` dentro da pasta raiz do projeto.

### Executando Localmente

1. **Clone o repositório:**
   ```bash
   git clone <url-do-repositorio>
   cd apipedidos
   ```

2. **Compile o projeto:**
   ```bash
   ./gradlew clean build
   ```

3. **Execute os testes:**
   ```bash
   ./gradlew test
   ```

4. **Inicie a aplicação:**
   ```bash
   ./gradlew bootRun
   ```

5. **A API estará disponível em:** `http://localhost:8080`

## Documentação da API + Collections

### Base URL
```
http://localhost:8080
```

### Endpoints

#### 1. Efetuar Pedido
- **POST** `/pedidos`
- **Descrição:** Efetua um novo pedido

**Headers:**
```
Content-Type: application/json
transactionId: randomUUID
correlationId: randomUUID
```

**Request Body:**
```json
{
  "codigoIdentificacaoCliente": "2cb91ec4-6031-45f1-b668-07af6440f5d9",
  "produtos": [
    {
      "idProduto": 1,
      "nomeProduto": "Notebook Dell",
      "quantidadeProduto": 2,
      "precoUnitario": 2500.00
    },
    {
      "idProduto": 2,
      "nomeProduto": "Mouse",
      "quantidadeProduto": 1,
      "precoUnitario": 50.00
    }
  ]
}
```

**Response (201 Created):**
```json
{
  "data": {
    "mensagem": "Pedido efetuado com sucesso!",
    "idPedido": 1640995200123
  }
}
```

**Response (422):**
```json
{
  "data": {
    "codigoErro": "PND",
    "motivoErro": "Não foi possível realizar o pedido. Quantidade do produto: Notebook Dell, maior que o disponível em estoque."
  }
}
```


**Exemplo com cURL:**
```bash
curl --request POST \
  --url http://localhost:8080/pedidos \
  --header 'Content-Type: application/json' \
  --header 'correlationId: abe6018e-0e8f-44b0-89b9-2cd4d4975c0f' \
  --header 'transactionId: cb49ea66-d374-4219-97a9-e562fb09dab1' \
  --data '{
	"codigoIdentificacaoCliente": "0ca5cc8d-20f8-4797-a64c-b24d6588beff",
	"produtos": [
		{
			"idProduto": 2,
			"nomeProduto": "Mouse Logitech",
			"quantidadeProduto": 1,
			"precoUnitario": 75.00
		}
	]
}'
```

#### 2. Listar Pedidos Ativos
- **GET** `/pedidos`
- **Descrição:** Retorna lista de pedidos com status ATIVO

**Response (200 OK):**
```json
{
   "data":[
      {
         "idPedido":1754239350400,
         "codigoIdentificacaoCliente":"8e18a3e1-8bd5-41bd-a95d-fc3fd2ee32ea",
         "statusPedido":"ATIVO",
         "descricaoProdutos":[
            {
               "idProduto":2,
               "nomeProduto":"Mouse Logitech",
               "quantidadeProduto":2,
               "precoUnitarioProduto":75.0
            }
         ],
         "dataHoraCriacaoPedido":"03-08-2025 13:42:29",
         "transactionId":"a1f10007-835c-4a14-b065-908e0d687f57"
      }
   ]
}
```


**Exemplo com cURL:**
```bash
curl --request GET \
  --url http://localhost:8080/pedidos \
  --header 'correlationId: 01aa043f-b3d3-492f-bb29-8dc2cdf6059a'
```

#### 3. Cancelar Pedido
- **POST** `/pedidos/{pedidoId}/cancelamentos`
- **Descrição:** Cancela um pedido específico

**Request Body:**
```json
{
  "motivoCancelamento": "Cliente desistiu da compra"
}
```

**Response (200 OK):**
```json
{
  "data": {
    "mensagem": "Pedido cancelado com sucesso"
  }
}
```

**Response (404 Not Found):**
```json
{
   "data":{
      "codigoErro":"PNE",
      "motivoErro":"Pedido com ID 111 não encontrado"
   }
}
```

**Exemplo com cURL:**
```bash
curl --request POST \
  --url http://localhost:8080/pedidos/1754239350400/cancelamentos \
  --header 'Content-Type: application/json' \
  --header 'correlationId: 582cf491-a51f-4464-a42d-a73a234e8feb' \
  --data '{
	"motivoCancelamento": "Pedido incorreto"
}'
```

#### 4. Buscar Pedido por ID
- **GET** `/pedidos/{pedidoId}`
- **Descrição:** Retorna um pedido específico pelo ID

**Response (200 OK):**
```json
{
   "data":{
      "idPedido":2025080400003,
      "codigoIdentificacaoCliente":"8e18a3e1-8bd5-41bd-a95d-fc3fd2ee32ea",
      "statusPedido":"ATIVO",
      "descricaoProdutos":[
         {
            "idProduto":2,
            "nomeProduto":"Mouse Logitech",
            "quantidadeProduto":2,
            "precoUnitarioProduto":75.0
         }
      ],
      "dataHoraCriacaoPedido":"03-08-2025 13:42:29",
      "transactionId":"a1f10007-835c-4a14-b065-908e0d687f57"
   }
}
```

**Response (404 Not Found):**
```json
{
   "data":{
      "codigoErro":"PNE",
      "motivoErro":"Pedido com ID 111 não encontrado"
   }
}
```

**Exemplo com cURL:**
```bash
curl --request GET \
  --url http://localhost:8080/pedidos/2025080400003 \
  --header 'correlationId: 989d1ae6-e3bd-44e7-81a4-355e72d5b261'
```

## Arquitetura

O projeto segue os princípios de **Screaming Architecture** com a seguinte estrutura:

```
/
└── data/
    ├── pedidos.json                # Arquivo de dados dos pedidos
    └── produtos.json               # Arquivo de dados dos produtos
src/main/java/
├── adapters/
│   ├── dataprovider/repository/     # Repositórios e implementações
│   └── entrypoint/web/             # Controllers e DTOs
├── application/
│   ├── exception/                  # Exceções personalizadas para a aplicação
│   └── service/                   # Services
│   └── usecase/                   # Casos de uso
└── domain/
    └── entity/                    # Entidades de domínio
    └── utils/                    # Utils de domínio do projeto
src/main/resources/
├── application.properties          # Configurações
```

## Executando Testes

### Todos os testes
```bash
./gradlew clean test
```

### Testes específicos
```bash
./gradlew clean test --tests "*BuscarPedidoControllerTest*"
```

### Relatório de cobertura
```bash
./gradlew clean test jacocoTestReport
```

### Executar aplicação em modo de desenvolvimento
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

##  Códigos de Erro

| Código | Descrição | Status HTTP |
|--------|-----------|-------------|
| PEM | Pedido em andamento | 422 |
| PND | Produto não disponível | 422 |
| PNE | Pedido/Produto não encontrado | 404 |
| PIE | Pedidos inativos | 204 |
| DPI | Dados do produto inconsistentes | 400 |
| SI | Serviço indisponível | 500 |
| VLD | Validação inválida | 400 |

## Modelos de Dados

### Pedido Request
```json
{
  "codigoIdentificacaoCliente": "string",
  "produtos": [
    {
      "idProduto": "long",
      "nomeProduto": "string",
      "quantidadeProduto": "integer (positivo)",
      "precoUnitario": "decimal (positivo)"
    }
  ]
}
```

### Pedido Response
```json
{
   "idPedido": "long",
   "codigoIdentificacaoCliente": "string",
   "statusPedido": "ATIVO | INATIVO",
   "descricaoProdutos": [
      {
         "idProduto": "long",
         "nomeProduto": "string",
         "quantidadeProduto": "integer",
         "precoUnitario": "decimal"
      }
   ],
   "dataHoraCriacaoPedido": "datetime (PT-BR)",
   "transactionId": "string"
}
```

## Banco de dados

Os dados são armazenados em arquivos JSON no diretório `data/`:
- **Pedidos:** `data/pedidos.json`
- **Produtos:** `data/produtos.json`

Os arquivos são criados automaticamente na primeira execução da aplicação.

## Logs

A aplicação possui um sistema de logging estruturado que registra todas as operações importantes:

### Interceptador de Requisições
- **LoggingInterceptor**: Intercepta todas as requisições para `/pedidos/**`
- Captura o `correlationId` informado no header e registra na composição do log;
- Registra início, fim e duração das requisições;
- Adiciona contexto ao MDC (Mapped Diagnostic Context) do SLF4J.


## Configurações

### Personalizar caminho dos arquivos
```properties
# application.properties
app.pedidos.file.path=data/pedidos.json
app.produtos.file.path=data/produtos.json
```

### Porta da aplicação
```properties
server.port=8080
```

## Tratamento de Erros

A API retorna códigos de status HTTP apropriados:

- **200**: Sucesso
- **201**: Criado com sucesso
- **204**: Sem conteúdo (lista vazia)
- **400**: Requisição inválida
- **404**: Recurso não encontrado
- **422**: Erro de regra de negócio
- **500**: Erro interno do servidor

Formato padrão de erro:
```json
{
  "codigo": "PNE",
  "mensagem": "Descrição do erro"
}
```

## 📄 Licença

Este projeto está sob a licença MIT.
