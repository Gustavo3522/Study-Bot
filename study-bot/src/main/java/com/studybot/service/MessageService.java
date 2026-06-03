package com.studybot.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.format.DateTimeFormatter;

@Service
public class MessageService {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MessageEmbed buildSessionEmbed(StudySessionService.SessionResult result) {
        return new EmbedBuilder()
                .setTitle("📚 " + result.username() + " terminou uma sessão de estudos")
                .setColor(new Color(88, 101, 242)) // cor roxa estilo Discord
                .addField("⏱️ Tempo", result.formattedSession(), true)
                .addField("📅 Data", result.date().format(DATE_FORMATTER), true)
                .addField("📊 Total hoje", result.formattedTotal(), false)
                .setFooter("StudyBot • Bons estudos! 🎯")
                .build();
    }

    public String buildSessionText(StudySessionService.SessionResult result) {
        return """
                📚 **%s** terminou uma sessão de estudos
                
                ⏱️ Tempo: **%s**
                📅 Data: **%s**
                
                Total hoje: **%s**
                """.formatted(
                result.username(),
                result.formattedSession(),
                result.date().format(DATE_FORMATTER),
                result.formattedTotal()
        );
    }
}
