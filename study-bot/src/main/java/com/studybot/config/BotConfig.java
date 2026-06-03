package com.studybot.config;

import com.studybot.listener.VoiceChannelListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Value("${discord.token}")
    private String token;

    @Autowired
    private VoiceChannelListener voiceChannelListener;

    @Bean
    public JDA jda() throws InterruptedException {
        JDA jda = JDABuilder.createDefault(token)
                .enableIntents(
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES
                )
                .addEventListeners(voiceChannelListener)
                .build();

        jda.awaitReady();
        System.out.println("✅ StudyBot online! Monitorando canais de voz...");
        return jda;
    }
}
