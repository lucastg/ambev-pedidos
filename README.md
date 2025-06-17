# Ambev Pedidos API

Esta API foi desenvolvida para o gerenciamento de pedidos e produtos, servindo como um backend robusto e escalável para de gestão de pedidos.

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

### Linguagem e Framework

- **Java 17 e Spring Boot:** Escolha baseada na robustez, escalabilidade e maturidade do Java e Spring Boot para APIs RESTful. O Spring Boot acelera o desenvolvimento e sua vasta comunidade garante fácil manutenção e suporte.

### Arquitetura do Sistema
- **Arquitetura de Ports and Adapters (Arquitetura Hexagonal):** Adotada para desacoplar a lógica de negócio da infraestrutura, proporcionando:
    - **Versatilidade:** Facilita a troca de componentes de infraestrutura (banco de dados, mensageria) sem impactar o domínio.
    - **Testabilidade:** Permite testes independentes da lógica de negócio.
    - **Manutenibilidade:** Melhora a organização e facilita modificações.

### Mensageria
- **RabbitMQ para Processamento de Pedidos:** Essencial para lidar com alta volumetria (100-200 mil pedidos/dia). O RabbitMQ garante:
    - **Desacoplamento:** Separa o Produto Externo A do consumidor (serviço order), evitando que picos de demanda sobrecarreguem diretamente o serviço.
    - **Resiliência:** As mensagens são persistidas nas filas, garantindo que os pedidos não se percam em caso de falha do serviço order.
    - **Escalabilidade:** Permite escalar horizontalmente os consumidores do serviço order para processar mensagens em paralelo, atendendo à demanda.
    - **Comunicação Eficiente:** Facilita integração assíncrona com serviços externos.
    - **Controle de Concorrência e Disponibilidade:** Contribui para a alta disponibilidade sob alta carga.

### Banco de Dados
- **PostgreSQL:** SGBD relacional escolhido por sua robustez, confiabilidade, escalabilidade e conformidade SQL, crucial para o volume de dados de pedidos.
- **Spring Data JPA e Hibernate:** Simplifica o acesso a dados e o mapeamento do objeto, reduzindo o código para operações CRUD.

### Containerização e Implantação
- **Docker e Docker Compose:** Garantem um ambiente consistente e portátil, simplificando o desenvolvimento e a implantação da aplicação e suas dependências (banco de dados, RabbitMQ).
- **Hospedagem no Render:** Utilizado para demonstração pela facilidade de deploy contínuo, com a observação sobre "cold start" para gerenciar expectativas de desempenho inicial.

### Qualidade de Código
- **Testes Unitários e de Integração:** Nescessarios para a validação da lógica de negócio e integração entre componentes.
  - **Testes de Repositório com H2:** Uso de banco de dados em memória para alta performance e isolamento nos testes de integração da camada de persistência.
- **Tratamento de Exceções:** Implementação centralizada para respostas de erro consistentes e claras da API.
- **Boas Práticas de Código:** Aderência a padrões como código limpo, convenções de nomenclatura e princípios SOLID para manutenibilidade e extensibilidade.

## Pontos de Desafio e Respostas
- **Verificação de Duplicação de Pedidos:** Mecanismos implementados, como o uso do idExterno para idempotência, evitam reprocessamento e inconsistências.
- **Disponibilidade do Serviço com Alta Volumetria:** A combinação de RabbitMQ, Spring Boot (escalabilidade horizontal) e PostgreSQL (robustez do BD) assegura alta disponibilidade.
- **Consistência dos Dados e Concorrência:** Garantida por transações ACID do PostgreSQL e pela distribuição de carga via RabbitMQ.
- **Engargalamento do Banco Escolhido:** PostgreSQL é uma escolha robusta; otimização de queries, índices e a distribuição de carga pela mensageria mitigam esse risco.
