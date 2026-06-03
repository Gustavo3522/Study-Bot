package com.studybot.service;

import com.studybot.entity.StudySession;
import com.studybot.repository.StudySessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudySessionService {

    private final StudySessionRepository repository;

    @Transactional
    public void registerEntry(String userId, String username, String guildId) {
        Optional<StudySession> activeSession =
                repository.findByUserIdAndGuildIdAndLeftAtIsNull(userId, guildId);

        if (activeSession.isPresent()) {
            log.warn("Usuário {} já possui sessão ativa. Ignorando entrada duplicada.", username);
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        StudySession session = StudySession.builder()
                .userId(userId)
                .username(username)
                .guildId(guildId)
                .enteredAt(now)
                .sessionDate(now.toLocalDate())
                .build();

        repository.save(session);
        log.info("📥 {} entrou na Sala de Estudos às {}", username, now);
    }

    @Transactional
    public Optional<SessionResult> registerExit(String userId, String username, String guildId) {
        Optional<StudySession> activeOpt =
                repository.findByUserIdAndGuildIdAndLeftAtIsNull(userId, guildId);

        if (activeOpt.isEmpty()) {
            log.warn("Nenhuma sessão ativa encontrada para {}. Saída ignorada.", username);
            return Optional.empty();
        }

        StudySession session = activeOpt.get();
        LocalDateTime now = LocalDateTime.now();
        long minutes = Duration.between(session.getEnteredAt(), now).toMinutes();

        if (minutes < 1) {
            repository.delete(session);
            log.info("⚡ Sessão de {} ignorada (duração < 1 min).", username);
            return Optional.empty();
        }

        session.setLeftAt(now);
        session.setDurationMinutes(minutes);
        repository.save(session);

        Long totalToday = repository.sumDurationByUserAndDate(userId, guildId, LocalDate.now());

        log.info("📤 {} saiu após {}min. Total hoje: {}min", username, minutes, totalToday);

        return Optional.of(new SessionResult(username, minutes, totalToday, LocalDate.now()));
    }

    public record SessionResult(
            String username,
            long sessionMinutes,
            long totalTodayMinutes,
            LocalDate date
    ) {
        public String formattedSession() {
            return formatMinutes(sessionMinutes);
        }

        public String formattedTotal() {
            return formatMinutes(totalTodayMinutes);
        }

        private String formatMinutes(long total) {
            long h = total / 60;
            long m = total % 60;
            if (h == 0) return m + "min";
            if (m == 0) return h + "h";
            return h + "h " + m + "min";
        }
    }
}
