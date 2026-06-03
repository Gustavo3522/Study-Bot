-- ============================================================
-- StudyBot - Schema do banco de dados
-- Execute no PostgreSQL antes de iniciar o bot
-- ============================================================

CREATE DATABASE studybot;

\c studybot;

CREATE TABLE IF NOT EXISTS study_sessions (
    id               BIGSERIAL PRIMARY KEY,
    user_id          VARCHAR(20)  NOT NULL,
    username         VARCHAR(100) NOT NULL,
    guild_id         VARCHAR(20)  NOT NULL,
    entered_at       TIMESTAMP    NOT NULL,
    left_at          TIMESTAMP,
    duration_minutes BIGINT,
    session_date     DATE         NOT NULL
);

-- Índices para queries frequentes
CREATE INDEX IF NOT EXISTS idx_sessions_user_guild
    ON study_sessions (user_id, guild_id);

CREATE INDEX IF NOT EXISTS idx_sessions_date
    ON study_sessions (session_date);

CREATE INDEX IF NOT EXISTS idx_sessions_active
    ON study_sessions (user_id, guild_id, left_at)
    WHERE left_at IS NULL;

-- ============================================================
-- Queries úteis para consulta manual
-- ============================================================

-- Ver sessões ativas no momento:
-- SELECT username, entered_at FROM study_sessions WHERE left_at IS NULL;

-- Top estudantes do dia:
-- SELECT username, SUM(duration_minutes) as total_min
-- FROM study_sessions
-- WHERE session_date = CURRENT_DATE AND left_at IS NOT NULL
-- GROUP BY username
-- ORDER BY total_min DESC;

-- Histórico de um usuário (últimos 7 dias):
-- SELECT session_date, SUM(duration_minutes) as total_min
-- FROM study_sessions
-- WHERE user_id = '123456789' AND session_date >= CURRENT_DATE - 7
-- GROUP BY session_date
-- ORDER BY session_date DESC;
