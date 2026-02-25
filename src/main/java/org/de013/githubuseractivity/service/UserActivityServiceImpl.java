package org.de013.githubuseractivity.service;

import org.de013.githubuseractivity.util.EventFormatter;
import org.de013.githubuseractivity.util.Messages;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class UserActivityServiceImpl implements UserActivityService {
    private static final String BASE_URL = "https://api.github.com/users/{0}/events";
    private final HttpClient httpClient;

    public UserActivityServiceImpl() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // Package-private constructor for testing
    UserActivityServiceImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void fetchUserActivity(String username) {
        fetchUserActivity(username, null);
    }

    @Override
    public void fetchUserActivity(String username, String eventTypeFilter) {
        String url = BASE_URL.replace("{0}", username);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "github-user-activity-cli")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        try {
            HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int status = response.statusCode();
            if (status == 200) {
                List<String> events = EventFormatter.format(response.body(), eventTypeFilter);
                if (events.isEmpty()) {
                    System.out.println(Messages.get("info.no_activity", username));
                } else {
                    for (String event : events) {
                        System.out.println("- " + event);
                    }
                }
            } else if (status == 404) {
                System.err.println(Messages.get("error.http.not_found", username));
            } else if (status == 403 || status == 429) {
                System.err.println(Messages.get("error.http.rate_limit"));
            } else {
                System.err.println(Messages.get("error.http.generic", status));
            }
        } catch (Exception e) {
            System.err.println(Messages.get("error.http.connection"));
        }
    }
}
