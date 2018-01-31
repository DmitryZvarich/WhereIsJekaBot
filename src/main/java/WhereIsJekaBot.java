import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.api.methods.send.SendLocation;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WhereIsJekaBot extends TelegramLongPollingBot {
    public void onUpdateReceived(Update update) {
        Set<String> keywords = new HashSet<>(Arrays.asList("/Jeka", "/ship"));

        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {

            if (keywords.contains(update.getMessage().getText()))
            {
                try
                {
                    long chat_id = update.getMessage().getChatId();

                    SendMessage message = new SendMessage(chat_id, "Jeka is here");

                    JSONObject jsonObject = getJSON();
                    SendLocation location = new SendLocation((float)jsonObject.getDouble("ship_lat"), (float)jsonObject.getDouble("ship_lon")).setChatId(chat_id);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Date date = new Date(jsonObject.getLong("last_pos")*1000);

                    String info = "From: " + jsonObject.get("last_port_name")
                            + "\nTo: " + jsonObject.get("next_port_name")
                            + "\nPosition received: " + simpleDateFormat.format(date);
                    SendMessage infoMessage = new SendMessage(chat_id, info);

                    execute(message);
                    execute(location);
                    execute(infoMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            else
            {
                long chat_id = update.getMessage().getChatId();
                SendMessage message = new SendMessage() // Create a message object object
                        .setChatId(chat_id)
                        .setText("Use commands: " + keywords);
                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getBotUsername()
    {
        return "WhereIsJekaBot";
    }

    public String getBotToken()
    {
        return "487489796:AAFY_RSxN43QJkAmsSSPxLCF47lA9HPEUUQ";
    }

    private JSONObject getJSON()
    {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");

        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange("https://www.marinetraffic.com/en/ais/get_info_window_json?asset_type=ship&id=3171412", HttpMethod.GET, httpEntity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        return jsonObject.getJSONObject("values");
    }
}
