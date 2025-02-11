package com.example.absensireact.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class TelegramNotificationService {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.chat.id}")
    private String chatId;

    private final RestTemplate restTemplate;

    @Autowired
    public TelegramNotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendErrorNotificationLogin(String errorMessage) {
        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                botToken,
                chatId,
                errorMessage
        );

        restTemplate.getForObject(url, String.class);
    }

    public void sendErrorNotification(String apiEndpoint, String payload, String jwt, Exception e) {
        String errorMessage = "🚨 *Error API Presensi* 🚨\n"
                + "🔗 *Endpoint*: `" + apiEndpoint + "`\n"
                + "📌 *JWT*: `" + jwt + "`\n"
                + "📄 *Payload*: `" + payload + "`\n"
                + "❌ *Error*: `" + e.getMessage() + "`";

        try {
            String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString());
            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&parse_mode=Markdown",
                    botToken,
                    chatId,
                    encodedMessage
            );
            restTemplate.getForObject(url, String.class);
        } catch (Exception ex) {
            System.err.println("Gagal mengirim notifikasi ke Telegram: " + ex.getMessage());
        }
    }


    public void sendErrorNotificationForException(String className, String methodName, Object[] args, Throwable ex) {
        // Escape karakter spesial MarkdownV2
        String errorMessage = String.format(
                "🚨 *API ERROR DETECTED* 🚨\n" +
                        "📌 *Class*: `%s`\n" +
                        "🔗 *Method*: `%s`\n" +
                        "📝 *Arguments*: `%s`\n" +
                        "❌ *Exception*: `%s`\n" +
                        "📄 *Message*: `%s`",
                escapeMarkdownV2(className),
                escapeMarkdownV2(methodName),
                escapeMarkdownV2(Arrays.toString(args)),
                escapeMarkdownV2(ex.getClass().getSimpleName()),
                escapeMarkdownV2(ex.getMessage())
        );

        // Buat URL request dengan parse_mode=MarkdownV2 agar pesan tetap rapi
        String url = UriComponentsBuilder.fromHttpUrl("https://api.telegram.org/bot" + botToken + "/sendMessage")
                .queryParam("chat_id", chatId)
                .queryParam("text", errorMessage)
                .queryParam("parse_mode", "MarkdownV2") // Memastikan format MarkdownV2
                .toUriString();

        // Kirim pesan ke Telegram
        restTemplate.getForObject(url, String.class);
    }

    // Fungsi untuk escape karakter spesial di MarkdownV2
    private String escapeMarkdownV2(String text) {
        if (text == null) return "";
        return text.replace("_", "\\_")
                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("]", "\\]")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("~", "\\~")
                .replace("`", "\\`")
                .replace(">", "\\>")
                .replace("#", "\\#")
                .replace("+", "\\+")
                .replace("-", "\\-")
                .replace("=", "\\=")
                .replace("|", "\\|")
                .replace("{", "\\{")
                .replace("}", "\\}")
                .replace(".", "\\.")
                .replace("!", "\\!");
    }

}
