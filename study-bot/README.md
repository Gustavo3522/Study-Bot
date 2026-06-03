# 📚 StudyBot

Bot Discord para rastrear automaticamente as horas de estudo dos membros. Quando alguém entra na **Sala de Estudos** (canal de voz), o bot registra o horário. Ao sair, calcula o tempo e publica um relatório no canal **⏰-horas-de-estudos**.

---

## 🛠️ Stack

| Tecnologia    | Versão  | Função                        |
|---------------|---------|-------------------------------|
| Java          | 17+     | Linguagem principal           |
| Spring Boot   | 3.2.5   | Framework / IoC / JPA         |
| JDA           | 5.0.0   | Discord API client            |
| PostgreSQL    | 14+     | Banco de dados                |
| Hibernate     | 6.x     | ORM (via Spring Data JPA)     |
| Lombok        | latest  | Redução de boilerplate        |

---

## 📂 Estrutura do projeto

```
study-bot/
├── src/main/java/com/studybot/
│   ├── StudyBotApplication.java       # Entry point Spring Boot
│   ├── config/
│   │   └── BotConfig.java             # Configuração do JDA
│   ├── entity/
│   │   └── StudySession.java          # Entidade JPA (tabela)
│   ├── repository/
│   │   └── StudySessionRepository.java # Queries JPA
│   ├── service/
│   │   ├── StudySessionService.java    # Lógica de entrada/saída
│   │   └── MessageService.java        # Monta as mensagens Discord
│   └── listener/
│       └── VoiceChannelListener.java  # Escuta eventos de voz
├── src/main/resources/
│   ├── application.properties         # Configurações
│   └── schema.sql                     # Script do banco
└── pom.xml
```

---

## ⚙️ Configuração

### 1. Criar o Bot no Discord

1. Acesse [discord.com/developers/applications](https://discord.com/developers/applications)
2. Clique em **New Application** → dê um nome
3. Vá em **Bot** → clique em **Add Bot**
4. Copie o **Token**
5. Em **Privileged Gateway Intents**, ative:
   - `SERVER MEMBERS INTENT`
   - `PRESENCE INTENT` (opcional)
6. Vá em **OAuth2 → URL Generator**:
   - Scopes: `bot`
   - Bot Permissions: `View Channels`, `Send Messages`, `Embed Links`, `Connect`
7. Cole a URL gerada no navegador e adicione o bot ao servidor

### 2. Criar o banco PostgreSQL

```bash
psql -U postgres -f src/main/resources/schema.sql
```

Ou manualmente:
```sql
CREATE DATABASE studybot;
```
O Hibernate criará a tabela automaticamente na primeira execução (`ddl-auto=update`).

### 3. Configurar `application.properties`

```properties
# Token do bot (obrigatório)
discord.token=SEU_TOKEN_AQUI

# Nomes dos canais (devem ser iguais aos do servidor)
discord.study-channel-name=Sala de Estudos
discord.log-channel-name=⏰-horas-de-estudos

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/studybot
spring.datasource.username=postgres
spring.datasource.password=sua_senha_aqui
```

### 4. Executar

```bash
# Compilar e rodar
mvn spring-boot:run

# Ou gerar o JAR
mvn clean package
java -jar target/study-bot-1.0.0.jar
```

---

## 📊 Exemplo de mensagem gerada

```
╔══════════════════════════════════════════╗
║  📚 Gustavo terminou uma sessão de estudos
╠══════════════════════════════════════════╣
║  ⏱️ Tempo       │  1h 42min
║  📅 Data        │  02/06/2026
║  📊 Total hoje  │  3h 18min
╚══════════════════════════════════════════╝
  StudyBot • Bons estudos! 🎯
```

---

## 🔧 Personalizações comuns

| O que mudar                          | Onde mudar                          |
|--------------------------------------|-------------------------------------|
| Nome da sala de estudos              | `application.properties`           |
| Nome do canal de relatório           | `application.properties`           |
| Cor do embed                         | `MessageService.java`              |
| Duração mínima para registrar sessão | `StudySessionService.java` (1 min) |
| Formato da data/hora                 | `MessageService.java`              |

---

## 🗃️ Queries úteis (PostgreSQL)

```sql
-- Sessões ativas agora
SELECT username, entered_at FROM study_sessions WHERE left_at IS NULL;

-- Ranking do dia
SELECT username, SUM(duration_minutes) AS total_min
FROM study_sessions
WHERE session_date = CURRENT_DATE AND left_at IS NOT NULL
GROUP BY username ORDER BY total_min DESC;

-- Histórico semanal de um usuário
SELECT session_date, SUM(duration_minutes) AS total_min
FROM study_sessions
WHERE user_id = 'SEU_USER_ID'
  AND session_date >= CURRENT_DATE - 7
GROUP BY session_date ORDER BY session_date DESC;
```

---

## ⚠️ Observações

- **Bot reiniciado com alguém na sala:** a saída será ignorada (sem sessão ativa salva). Isso é intencional para evitar registros incorretos.
- **Sessões < 1 minuto:** automaticamente descartadas.
- **Múltiplos servidores:** suportado — cada servidor usa seu próprio `guild_id`.
