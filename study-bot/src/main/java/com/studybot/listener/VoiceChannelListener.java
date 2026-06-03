package com.studybot.listener;

import com.studybot.service.MessageService;
import com.studybot.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceChannelListener extends ListenerAdapter {

    @Value("${discord.study-channel-name}")
    private String studyChannelName;

    @Value("${discord.log-channel-name}")
    private String logChannelName;

    private final StudySessionService sessionService;
    private final MessageService messageService;

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        Member member = event.getMember();

        if (member.getUser().isBot()) return;

        AudioChannelUnion channelLeft   = event.getChannelLeft();
        AudioChannelUnion channelJoined = event.getChannelJoined();

        String userId   = member.getId();
        String username = member.getEffectiveName();
        String guildId  = event.getGuild().getId();

        boolean leftStudyRoom   = channelLeft   != null && isStudyChannel(channelLeft.getName());
        boolean joinedStudyRoom = channelJoined != null && isStudyChannel(channelJoined.getName());

        if (joinedStudyRoom && !leftStudyRoom) {
            sessionService.registerEntry(userId, username, guildId);
        }

        if (leftStudyRoom && !joinedStudyRoom) {
            Optional<StudySessionService.SessionResult> result =
                    sessionService.registerExit(userId, username, guildId);

            result.ifPresent(r -> sendReport(event.getGuild(), r));
        }
    }

    private boolean isStudyChannel(String channelName) {
        return studyChannelName.equalsIgnoreCase(channelName);
    }

    private void sendReport(Guild guild, StudySessionService.SessionResult result) {
        TextChannel logChannel = findLogChannel(guild);

        if (logChannel == null) {
            log.error("Canal '{}' não encontrado no servidor '{}'.", logChannelName, guild.getName());
            return;
        }

        logChannel.sendMessageEmbeds(messageService.buildSessionEmbed(result)).queue(
                success -> log.info("✅ Relatório enviado para #{}", logChannelName),
                error   -> {
                    log.error("❌ Falha ao enviar embed, usando texto puro.", error);
                    // Fallback: envia como texto simples
                    logChannel.sendMessage(messageService.buildSessionText(result)).queue();
                }
        );
    }

    private TextChannel findLogChannel(Guild guild) {
        return guild.getTextChannelsByName(logChannelName, true)
                .stream()
                .findFirst()
                .orElse(null);
    }
}
