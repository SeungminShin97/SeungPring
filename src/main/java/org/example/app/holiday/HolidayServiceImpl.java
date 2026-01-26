package org.example.app.holiday;

import org.example.framework.annotation.Component;
import org.example.framework.annotation.LazyProxy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@LazyProxy
public class HolidayServiceImpl implements HolidayService {

    private static final String URL = "https://date.nager.at/api/v3/publicholidays/2026/AT";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String getHoliday() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Failed to call holiday API", e);
        }
    }
}
