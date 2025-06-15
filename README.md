# Ambev Pedidos API

Esta API foi desenvolvida para o gerenciamento de pedidos e produtos, servindo como um backend robusto e escalável para aplicações de e-commerce ou sistemas de gestão de pedidos.

## 🚀 Tecnologias Utilizadas

*   **Java 17**
*   **Spring Boot 3.5.0**
*   **Docker**
*   **PostgreSQL**

## 🌐 Aplicação e Banco de Dados Hospedados no Render
A versão de demonstração desta API, juntamente com seu banco de dados PostgreSQL, está hospedada na plataforma Render para fácil acesso e teste.

## Você pode explorar e interagir com todos os endpoints da API através da interface do [Swagger UI](https://ambev-pedidos.onrender.com/swagger-ui/index.html#/).

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
