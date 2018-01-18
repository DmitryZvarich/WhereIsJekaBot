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

import java.sql.SQLException;
import java.util.Arrays;
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

                    SendMessage message = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText("Jeka is here");

                    SendLocation location = location = getLocation().setChatId(chat_id);

                    execute(message); // Sending our message object to user
                    execute(location);
                } catch (TelegramApiException | SQLException e) {
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

    private SendLocation getLocation() throws SQLException {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:57.0) Gecko/20100101 Firefox/57.0");

        HttpEntity<String> httpEntity = new HttpEntity<>("", headers);
        ResponseEntity<String> response = restTemplate.exchange("https://www.marinetraffic.com/en/ais/get_info_window_json?asset_type=ship&id=3171412", HttpMethod.GET, httpEntity, String.class);

        JSONObject jsonObject = new JSONObject(response.getBody());
        JSONObject values = jsonObject.getJSONObject("values");

        return new SendLocation(Float.valueOf(values.get("ship_lat").toString()), Float.valueOf(values.get("ship_lon").toString()));
    }
}
