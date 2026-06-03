📚 StudyBot

Bot para Discord que rastreia automaticamente as horas de estudo dos membros do servidor.

## Como funciona

1. Membro entra no canal de voz Sala de Estudos
2. Bot registra o horário de entrada no banco de dados
3. Ao sair, o bot calcula a duração e envia um relatório

## Stack

Java 21 · Spring Boot 3.2.5 · JDA 5.0.0 · PostgreSQL · Maven

## Como rodar

# 1. Configure as variáveis de ambiente
DISCORD_TOKEN=seu_token
DB_PASSWORD=sua_senha

# 2. Execute
mvn spring-boot:run


## Funcionalidades

- Registro automático de entrada e saída por canal de voz
- Cálculo de duração por sessão
- Total acumulado por dia por usuário
- Suporte a múltiplos servidores
- Token e senha protegidos via variáveis de ambiente
