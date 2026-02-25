package org.de013.githubuseractivity.service;

import org.de013.githubuseractivity.util.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

class UserActivityServiceImplTest {

    private HttpClient mockClient;
    private UserActivityServiceImpl service;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        Messages.currentLanguage = Messages.Language.EN;
        mockClient = Mockito.mock(HttpClient.class);
        service = new UserActivityServiceImpl(mockClient);
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @SuppressWarnings("unchecked")
    private HttpResponse<String> mockResponse(int status, String body) {
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        doReturn(status).when(response).statusCode();
        doReturn(body).when(response).body();
        return response;
    }

    @Test
    void fetchUserActivity_200_printsFormattedEvents() throws Exception {
        // Arrange
        String json = "[{\"type\":\"WatchEvent\",\"repo\":{\"name\":\"user/repo\"},\"payload\":{}}]";
        doReturn(mockResponse(200, json)).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("octocat");

        // Assert
        assertTrue(outContent.toString().contains("Starred user/repo"));
    }

    @Test
    void fetchUserActivity_200_withFilter_printsOnlyMatchingEvents() throws Exception {
        // Arrange
        String json = "[" +
                "{\"type\":\"WatchEvent\",\"repo\":{\"name\":\"a/b\"},\"payload\":{}}," +
                "{\"type\":\"ForkEvent\",\"repo\":{\"name\":\"c/d\"},\"payload\":{}}" +
                "]";
        doReturn(mockResponse(200, json)).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("octocat", "ForkEvent");

        // Assert
        String output = outContent.toString();
        assertTrue(output.contains("Forked c/d"));
        assertFalse(output.contains("Starred a/b"));
    }

    @Test
    void fetchUserActivity_200_emptyEvents_printsNoActivity() throws Exception {
        // Arrange
        doReturn(mockResponse(200, "[]")).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("octocat");

        // Assert
        assertTrue(outContent.toString().contains("No recent activity found for 'octocat'"));
    }

    @Test
    void fetchUserActivity_404_printsNotFoundError() throws Exception {
        // Arrange
        doReturn(mockResponse(404, "")).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("ghost");

        // Assert
        assertTrue(errContent.toString().contains("User 'ghost' not found"));
    }

    @Test
    void fetchUserActivity_403_printsRateLimitError() throws Exception {
        // Arrange
        doReturn(mockResponse(403, "")).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("octocat");

        // Assert
        assertTrue(errContent.toString().contains("rate limit"));
    }

    @Test
    void fetchUserActivity_429_printsRateLimitError() throws Exception {
        // Arrange
        doReturn(mockResponse(429, "")).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("octocat");

        // Assert
        assertTrue(errContent.toString().contains("rate limit"));
    }

    @Test
    void fetchUserActivity_500_printsGenericError() throws Exception {
        // Arrange
        doReturn(mockResponse(500, "")).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("octocat");

        // Assert
        assertTrue(errContent.toString().contains("500"));
    }

    @Test
    void fetchUserActivity_networkException_printsConnectionError() throws Exception {
        // Arrange
        doThrow(new RuntimeException("timeout")).when(mockClient).send(any(HttpRequest.class), any());

        // Act
        service.fetchUserActivity("octocat");

        // Assert
        assertTrue(errContent.toString().contains("Could not connect"));
    }
}
