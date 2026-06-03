package com.studybot.repository;

import com.studybot.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    Optional<StudySession> findByUserIdAndGuildIdAndLeftAtIsNull(String userId, String guildId);

    List<StudySession> findByUserIdAndGuildIdAndSessionDateAndLeftAtIsNotNull(
            String userId, String guildId, LocalDate sessionDate);

    @Query("""
            SELECT COALESCE(SUM(s.durationMinutes), 0)
            FROM StudySession s
            WHERE s.userId = :userId
              AND s.guildId = :guildId
              AND s.sessionDate = :date
              AND s.leftAt IS NOT NULL
            """)
    Long sumDurationByUserAndDate(
            @Param("userId") String userId,
            @Param("guildId") String guildId,
            @Param("date") LocalDate date);
}
