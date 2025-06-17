# Ambev Pedidos API

Esta API foi desenvolvida para o gerenciamento de pedidos e produtos, servindo como um backend robusto e escalável para aplicações de e-commerce ou sistemas de gestão de pedidos.

## 🚀 Tecnologias Utilizadas

*   **Java 17**
*   **Spring Boot 3.5.0**
*   **Docker**
*   **PostgreSQL**
*   **RabbitMQ**

## 🌐 Aplicação e Banco de Dados Hospedados no Render
A versão de demonstração desta API, juntamente com seu banco de dados PostgreSQL, está hospedada na plataforma Render para fácil acesso e teste.

### Você pode explorar e interagir com todos os endpoints da API através da interface do [Swagger UI](https://ambev-pedidos.onrender.com/swagger-ui/index.html#/).

Acesse o link acima para visualizar a documentação completa, testar requisições e entender os modelos de dados da API.

**_Obs:_**  ⚠️ Nota Importante sobre a Aplicação Hospedada:
A aplicação no Render pode entrar em estado de inatividade após alguns minutos sem uso. Se isso acontecer, a primeira requisição pode demorar um pouco mais que o normal para processar, pois a aplicação estará sendo "acordada" (cold start).

# **Executar com Docker Compose**
Você pode executar todo o ambiente (API, Banco de Dados e RabbitMQ) localmente utilizando Docker Compose.

### **Pré-requisitos**
- Certifique-se de ter o Docker e o Docker Compose instalados em sua máquina.

### **Configure suas credenciais de API:**
- Copie o arquivo **.env.exemplo** com o nome **.env**

## **Execute o Docker Compose**
```bash
docker compose up --build -d
```

> Você pode explorar e interagir com todos os endpoints da API através da interface do [Swagger UI](http://localhost:8080/swagger-ui/index.html).

---
# Decisões Técnicas

Esta seção detalha as principais decisões técnicas e arquiteturais que guiaram o desenvolvimento do projeto, visando garantir robustez, escalabilidade, manutenibilidade e eficiência.

### Linguagem e Framework

- Java 17 e Spring Boot:

    A escolha por Java em conjunto com o framework Spring Boot, alem de ser um requisito do teste baseia-se na sua comprovada robustez, escalabilidade e maturidade no mercado. O Spring Boot, em particular, acelera o desenvolvimento com sua convenção sobre configuração e ecossistema abrangente de módulos, facilitando a criação de aplicações API RESTful.

### Arquitetura do Sistema
- Arquitetura de Ports and Adapters (Arquitetura Hexagonal): A arquitetura de Ports and Adapters para garantir que a lógica de negócio (dominio) permaneça desacoplada de detalhes de infraestrutura (banco de dados, mensageria, APIs externas), isto oferece:

    - **Versatilidade e Volatilidade:** Facilita a substituição de componentes de infraestrutura (ex: trocar de banco de dados, de broker de mensagens) sem impactar o domínio principal.
    - **Testabilidade:** Permite que a lógica de negócio seja testada independentemente, sem a necessidade de levantar todo o ambiente de infraestrutura.
    - **Manutenibilidade:** Melhora a organização do código, tornando-o mais fácil de entender e modificar.

### Mensageria Assíncrona
- **RabbitMQ para Processamento Assíncrono de Pedidos:** A decisão de utilizar o RabbitMQ foi estratégica para lidar com a alta volumetria de pedidos (100 a 200 mil pedidos/dia, conforme o desafio). O RabbitMQ, como um Message Broker robusto e maduro, permite:

    - Desacoplamento: Separa o produtor de pedidos (Produto Externo A) do consumidor (serviço order), evitando que picos de demanda sobrecarreguem diretamente o serviço.
    - Resiliência: As mensagens são persistidas nas filas, garantindo que os pedidos não se percam em caso de falha do serviço order.
    - Escalabilidade: Permite escalar horizontalmente os consumidores do serviço order para processar mensagens em paralelo, atendendo à demanda.
    - Comunicação Eficiente: Facilita a comunicação assíncrona e confiável com outros serviços externos (Produto Externo B), fundamental para a integração proposta no desafio.
    - Controle de Concorrência e Disponibilidade: Ajuda a gerenciar a concorrência no processamento e garante a disponibilidade do serviço, mesmo sob alta carga.

### Banco de Dados
- **PostgreSQL:** Escolhido como o sistema de gerenciamento de banco de dados relacional (SGBD) devido à sua:
    - Robustez e Confiabilidade: Conhecido por sua estabilidade e integridade de dados.
    - Escalabilidade: Capaz de lidar com grandes volumes de dados e alta concorrência, o que é crucial para o gerenciamento de pedidos.
    - Conformidade SQL: Oferece um conjunto rico de recursos e conformidade com padrões SQL.
    - Licença Permissiva: É um SGBD open-source com uma licença liberal.
  

- **Spring Data JPA e Hibernate:** Utilizados para a camada de persistência, simplificando o acesso a dados e o mapeamento objeto-relacional (ORM). O Spring Data JPA reduz significativamente o boilerplate code para operações CRUD, permitindo focar na lógica de negócio.

### Containerização e Implantação
- **Docker e Docker Compose:** A aplicação é totalmente conteinerizada usando Docker, o que garante:
    - Ambiente Consistente: Elimina problemas de "funciona na minha máquina", empacotando a aplicação e suas dependências em um ambiente isolado e portátil.
    - Facilidade de Implantação: Simplifica o processo de implantação em qualquer ambiente que suporte Docker (local, desenvolvimento, produção).
    - Orquestração Local: O Docker Compose permite orquestrar múltiplos serviços (aplicação, banco de dados, RabbitMQ) para um ambiente de desenvolvimento e teste local rápido e consistente.


- **Hospedagem no Render:** A escolha do Render para demonstração se deu pela sua facilidade de uso para deploy contínuo de aplicações Spring Boot e PostgreSQL, permitindo uma rápida visualização e teste do projeto. A consideração do "cold start" foi documentada para gerenciar expectativas do usuário.

### Qualidade de Código
- **Testes Unitários e de Integração:** Priorizamos a escrita de testes abrangentes (com JUnit 5, Mockito e AssertJ) para garantir a corretude da lógica de negócio e a integração entre os componentes.
    - Testes de Repositório com H2: Para os testes de integração da camada de persistência, utilizamos o H2 (banco de dados em memória) devido à sua alta performance e facilidade de setup, proporcionando testes rápidos e isolados.


- **Tratamento de Exceções:** Implementação de um tratamento de exceções centralizado para garantir respostas de erro consistentes e claras da API (utilizando @ControllerAdvice ou abordagens similares no Spring Boot), diferenciando 404 Not Found, 400 Bad Request, 409 Conflict, etc.
- **Boas Práticas de Código:** Aderência a padrões de código limpo, convenções de nomenclatura e princípios SOLID para garantir um código legível, manutenível e extensível.

## Pontos de Desafio e Respostas
- **Verificação de Duplicação de Pedidos:** A implementação inclui mecanismos para verificar e lidar com a duplicação de pedidos, provavelmente utilizando o idExterno como um identificador único para idempotência, evitando reprocessamento e inconsistências.


- **Disponibilidade do Serviço com Alta Volumetria:** A combinação de RabbitMQ (para desacoplamento e filas), Spring Boot (para escalabilidade horizontal e resiliência) e PostgreSQL (para robustez do BD) é a estratégia para garantir alta disponibilidade e gerenciamento eficaz da volumetria.


- **Consistência dos Dados e Concorrência:** A utilização de um SGBD relacional como PostgreSQL oferece transações ACID, que são cruciais para a consistência dos dados. O controle de concorrência é gerenciado tanto pelas transações do banco quanto pela natureza assíncrona do RabbitMQ, que distribui a carga de processamento.


- **Engargalamento do Banco Escolhido:** Com a volumetria citada, o PostgreSQL é uma escolha sólida. Estratégias como índices adequados, otimização de queries, e a distribuição de carga via RabbitMQ minimizam o risco de gargalo no banco. A configuração padrão otimizada do PostgreSQL é robusta.