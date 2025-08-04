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
   ./gradlew build
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

### Executando com JAR

```bash
./gradlew bootJar
java -jar build/libs/apipedidos-0.0.1-SNAPSHOT.jar
```

## Documentação da API + Collections

### Base URL
```
http://localhost:8080
```

### Endpoints

#### 1. Efetuar Pedido
- **POST** `/pedidos`
- **Descrição:** Cria um novo pedido

**Headers:**
```
Content-Type: application/json
transactionId: TXN-12345
```

**Request Body:**
```json
{
  "codigoIdentificacaoCliente": "CLI-001",
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
  --header 'transactionId: 61f7b74a-06a5-465c-a76f-41b94271c687' \
  --data '{
  "codigoIdentificacaoCliente": "7e1d3012-44cf-48f6-b037-82bcee5ca010",
  "produtos": [
		{
      "idProduto": 2,
      "nomeProduto": "Mouse Logitech",
      "quantidadeProduto": 2,
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
	"data": [
		{
			"idPedido": 1754239350400,
			"codigoIdentificacaoCliente": "8e18a3e1-8bd5-41bd-a95d-fc3fd2ee32ea",
			"statusPedido": "ATIVO",
			"descricaoProdutos": [
				{
					"idProduto": 2,
					"nomeProduto": "Mouse Logitech",
					"quantidadeProduto": 2,
					"precoUnitarioProduto": 75.0
				}
			],
			"dataHoraCriacaoPedido": "03-08-2025 13:42:29",
			"transactionId": "a1f10007-835c-4a14-b065-908e0d687f57"
		}
	]
}
```


**Exemplo com cURL:**
```bash
curl --request GET \
  --url http://localhost:8080/pedidos
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
	"data": {
		"codigoErro": "PNE",
		"motivoErro": "Pedido com ID 111 não encontrado"
	}
}
```

**Exemplo com cURL:**
```bash
curl --request POST \
  --url http://localhost:8080/pedidos/1754183832474/cancelamentos \
  --header 'Content-Type: application/json' \
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
	"data": {
		"idPedido": 1754239350400,
		"codigoIdentificacaoCliente": "8e18a3e1-8bd5-41bd-a95d-fc3fd2ee32ea",
		"statusPedido": "ATIVO",
		"descricaoProdutos": [
			{
				"idProduto": 2,
				"nomeProduto": "Mouse Logitech",
				"quantidadeProduto": 2,
				"precoUnitarioProduto": 75.0
			}
		],
		"dataHoraCriacaoPedido": "03-08-2025 13:42:29",
		"transactionId": "a1f10007-835c-4a14-b065-908e0d687f57"
	}
}
```

**Response (404 Not Found):**
```json
{
	"data": {
		"codigoErro": "PNE",
		"motivoErro": "Pedido com ID 111 não encontrado"
	}
}
```

**Exemplo com cURL:**
```bash
curl --request GET \
  --url http://localhost:8080/pedidos/1754239350400
```

## Arquitetura

O projeto segue os princípios de **Screaming Architecture** com a seguinte estrutura:

```
src/main/java/
├── adapters/
│   ├── dataprovider/repository/     # Repositórios e implementações
│   └── entrypoint/web/             # Controllers e DTOs
├── application/
│   ├── exception/                  # Exceções de negócio
│   └── usecase/                   # Casos de uso
└── domain/
    └── entity/                    # Entidades de domínio

src/main/resources/
├── application.properties          # Configurações
└── data/
    ├── pedidos.json                # Arquivo de dados dos pedidos
    └── produtos.json               # Arquivo de dados dos produtos
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
- Gera um `requestId` único para cada requisição
- Registra início, fim e duração das requisições
- Adiciona contexto ao MDC (Mapped Diagnostic Context) do SLF4J


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
