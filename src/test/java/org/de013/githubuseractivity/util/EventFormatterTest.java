package org.de013.githubuseractivity.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventFormatterTest {

    @BeforeEach
    void setUp() {
        Messages.currentLanguage = Messages.Language.EN;
    }

    private String event(String type, String repo, String payloadJson) {
        return "[{\"type\":\"" + type + "\",\"repo\":{\"name\":\"" + repo + "\"},"
                + "\"payload\":" + payloadJson + "}]";
    }

    // ---- PushEvent ----

    @Test
    void format_pushEvent_countsCommitsFromArray() {
        // Arrange
        String json = event("PushEvent", "user/repo",
                "{\"commits\":[{\"sha\":\"a\"},{\"sha\":\"b\"},{\"sha\":\"c\"}]}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Pushed 3 commit(s) to user/repo", result.get(0));
    }

    @Test
    void format_pushEvent_fallsBackToSizeField() {
        // Arrange
        String json = event("PushEvent", "user/repo", "{\"size\":5}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals("Pushed 5 commit(s) to user/repo", result.get(0));
    }

    // ---- WatchEvent / ForkEvent ----

    @Test
    void format_watchEvent_returnsStarredMessage() {
        // Arrange
        String json = event("WatchEvent", "octocat/hello", "{}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Starred octocat/hello"), result);
    }

    @Test
    void format_forkEvent_returnsForkedMessage() {
        // Arrange
        String json = event("ForkEvent", "octocat/hello", "{}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Forked octocat/hello"), result);
    }

    // ---- CreateEvent / DeleteEvent ----

    @Test
    void format_createEvent_includesRefType() {
        // Arrange
        String json = event("CreateEvent", "user/repo", "{\"ref_type\":\"branch\"}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Created branch in user/repo"), result);
    }

    @Test
    void format_deleteEvent_includesRefType() {
        // Arrange
        String json = event("DeleteEvent", "user/repo", "{\"ref_type\":\"tag\"}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Deleted tag in user/repo"), result);
    }

    // ---- IssuesEvent ----

    @Test
    void format_issuesEvent_opened() {
        // Arrange
        String json = event("IssuesEvent", "user/repo", "{\"action\":\"opened\"}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Opened an issue in user/repo"), result);
    }

    @Test
    void format_issuesEvent_closed() {
        // Arrange
        String json = event("IssuesEvent", "user/repo", "{\"action\":\"closed\"}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Closed an issue in user/repo"), result);
    }

    // ---- PullRequestEvent ----

    @Test
    void format_pullRequestEvent_opened() {
        // Arrange
        String json = event("PullRequestEvent", "user/repo", "{\"action\":\"opened\"}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Opened a pull request in user/repo"), result);
    }

    @Test
    void format_pullRequestEvent_closed() {
        // Arrange
        String json = event("PullRequestEvent", "user/repo", "{\"action\":\"closed\"}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Closed a pull request in user/repo"), result);
    }

    // ---- Other events ----

    @Test
    void format_releaseEvent_returnsPublished() {
        // Arrange
        String json = event("ReleaseEvent", "user/repo", "{}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Published a release in user/repo"), result);
    }

    @Test
    void format_unknownEvent_returnsDidSomething() {
        // Arrange
        String json = event("SomeRandomEvent", "user/repo", "{}");

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(List.of("Did something in user/repo"), result);
    }

    // ---- Multiple events ----

    @Test
    void format_multipleEvents_returnsAllFormatted() {
        // Arrange
        String json = "[" +
                "{\"type\":\"WatchEvent\",\"repo\":{\"name\":\"a/b\"},\"payload\":{}}," +
                "{\"type\":\"ForkEvent\",\"repo\":{\"name\":\"c/d\"},\"payload\":{}}" +
                "]";

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Starred a/b", result.get(0));
        assertEquals("Forked c/d", result.get(1));
    }

    @Test
    void format_emptyArray_returnsEmptyList() {
        // Arrange
        String json = "[]";

        // Act
        List<String> result = EventFormatter.format(json);

        // Assert
        assertTrue(result.isEmpty());
    }

    // ---- Filter ----

    @Test
    void format_withExactFilter_onlyReturnsMatchingType() {
        // Arrange
        String json = "[" +
                "{\"type\":\"PushEvent\",\"repo\":{\"name\":\"a/b\"},\"payload\":{\"commits\":[{}]}}," +
                "{\"type\":\"WatchEvent\",\"repo\":{\"name\":\"c/d\"},\"payload\":{}}" +
                "]";

        // Act
        List<String> result = EventFormatter.format(json, "PushEvent");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).startsWith("Pushed"));
    }

    @Test
    void format_withAliasFilter_normalizesAndMatches() {
        // Arrange – user passes "push" instead of "PushEvent"
        String json = event("PushEvent", "user/repo", "{\"commits\":[{}]}");

        // Act
        List<String> result = EventFormatter.format(json, "push");

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).startsWith("Pushed"));
    }

    @Test
    void format_withFilter_noMatch_returnsEmpty() {
        // Arrange
        String json = event("WatchEvent", "user/repo", "{}");

        // Act
        List<String> result = EventFormatter.format(json, "PushEvent");

        // Assert
        assertTrue(result.isEmpty());
    }
}
