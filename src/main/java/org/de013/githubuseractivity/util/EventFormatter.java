package org.de013.githubuseractivity.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses GitHub API events JSON and formats each event into a readable string.
 * Uses no external libraries – all parsing is done with simple string operations.
 */
public class EventFormatter {

    public static List<String> format(String json) {
        return format(json, null);
    }

    public static List<String> format(String json, String eventTypeFilter) {
        List<String> results = new ArrayList<>();
        List<String> eventObjects = splitTopLevelObjects(json);

        for (String obj : eventObjects) {
            String type = extractString(obj, "\"type\"");
            String repo = extractNestedString(obj, "\"repo\"", "\"name\"");
            if (repo == null) repo = "unknown";

            // Apply filter if specified
            if (eventTypeFilter != null) {
                String normalized = normalizeEventType(eventTypeFilter);
                if (!eventTypeFilter.equalsIgnoreCase(type) && !normalized.equalsIgnoreCase(type)) {
                    continue;
                }
            }

            String line = formatEvent(type, repo, obj);
            if (line != null) {
                results.add(line);
            }
        }

        return results;
    }

    /** Normalizes a user-supplied filter like "push" → "PushEvent". */
    private static String normalizeEventType(String input) {
        if (input == null) return null;
        String lower = input.toLowerCase().replace("event", "");
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1) + "Event";
    }

    private static String formatEvent(String type, String repo, String obj) {
        if (type == null) return Messages.get("event.unknown", repo);

        switch (type) {
            case "PushEvent": {
                String payload = extractBlock(obj, "\"payload\"");
                int commits = 0;
                if (payload != null) {
                    String commitsBlock = extractArrayBlock(payload, "\"commits\"");
                    if (commitsBlock != null) {
                        commits = countObjects(commitsBlock);
                    }
                    // fallback: size field
                    if (commits == 0) {
                        String sizeStr = extractString(payload, "\"size\"");
                        if (sizeStr != null) {
                            try { commits = Integer.parseInt(sizeStr.trim()); } catch (NumberFormatException ignored) {}
                        }
                    }
                }
                if (commits == 0) commits = 1;
                return Messages.get("event.push", commits, repo);
            }
            case "WatchEvent":
                return Messages.get("event.watch", repo);
            case "ForkEvent":
                return Messages.get("event.fork", repo);
            case "CreateEvent": {
                String payload = extractBlock(obj, "\"payload\"");
                String refType = payload != null ? extractString(payload, "\"ref_type\"") : null;
                if (refType == null) refType = "repository";
                return Messages.get("event.create", refType, repo);
            }
            case "DeleteEvent": {
                String payload = extractBlock(obj, "\"payload\"");
                String refType = payload != null ? extractString(payload, "\"ref_type\"") : null;
                if (refType == null) refType = "branch";
                return Messages.get("event.delete", refType, repo);
            }
            case "IssuesEvent": {
                String payload = extractBlock(obj, "\"payload\"");
                String action = payload != null ? extractString(payload, "\"action\"") : null;
                if ("opened".equals(action)) return Messages.get("event.issues.opened", repo);
                if ("closed".equals(action)) return Messages.get("event.issues.closed", repo);
                return Messages.get("event.issues.other", repo);
            }
            case "PullRequestEvent": {
                String payload = extractBlock(obj, "\"payload\"");
                String action = payload != null ? extractString(payload, "\"action\"") : null;
                if ("opened".equals(action)) return Messages.get("event.pull_request.opened", repo);
                if ("closed".equals(action)) return Messages.get("event.pull_request.closed", repo);
                return Messages.get("event.pull_request.other", repo);
            }
            case "ReleaseEvent":
                return Messages.get("event.release", repo);
            case "IssueCommentEvent":
                return Messages.get("event.issue_comment", repo);
            case "CommitCommentEvent":
                return Messages.get("event.commit_comment", repo);
            case "MemberEvent":
                return Messages.get("event.member", repo);
            case "PublicEvent":
                return Messages.get("event.public", repo);
            default:
                return Messages.get("event.unknown", repo);
        }
    }

    /** Splits the top-level JSON array into individual object strings. */
    private static List<String> splitTopLevelObjects(String json) {
        List<String> objects = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        int depth = 0;
        int start = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && start != -1) {
                    objects.add(json.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return objects;
    }

    /** Extracts a string value for a given key in a flat JSON object. */
    static String extractString(String json, String key) {
        int idx = json.indexOf(key);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx + key.length());
        if (colon < 0) return null;

        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) valueStart++;
        if (valueStart >= json.length()) return null;

        if (json.charAt(valueStart) == '"') {
            int end = json.indexOf('"', valueStart + 1);
            if (end < 0) return null;
            return json.substring(valueStart + 1, end);
        } else {
            // numeric or boolean
            int end = valueStart;
            while (end < json.length() && !",}\n".contains(String.valueOf(json.charAt(end)))) end++;
            return json.substring(valueStart, end).trim();
        }
    }

    /** Extracts a string value from a nested JSON block identified by blockKey, then innerKey. */
    private static String extractNestedString(String json, String blockKey, String innerKey) {
        String block = extractBlock(json, blockKey);
        if (block == null) return null;
        return extractString(block, innerKey);
    }

    /** Extracts the JSON object/block (including braces) immediately after blockKey. */
    static String extractBlock(String json, String blockKey) {
        int idx = json.indexOf(blockKey);
        if (idx < 0) return null;
        int colon = json.indexOf(':', idx + blockKey.length());
        if (colon < 0) return null;

        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
        if (start >= json.length()) return null;

        char open = json.charAt(start);
        char close = open == '{' ? '}' : (open == '[' ? ']' : 0);
        if (close == 0) return null;

        int depth = 0;
        for (int i = start; i < json.length(); i++) {
            if (json.charAt(i) == open) depth++;
            else if (json.charAt(i) == close) {
                depth--;
                if (depth == 0) return json.substring(start, i + 1);
            }
        }
        return null;
    }

    /** Extracts a JSON array block (including brackets) immediately after arrayKey. */
    private static String extractArrayBlock(String json, String arrayKey) {
        return extractBlock(json, arrayKey);
    }

    /** Counts the number of top-level objects in a JSON array string. */
    private static int countObjects(String arrayJson) {
        int count = 0;
        int depth = 0;
        for (char c : arrayJson.toCharArray()) {
            if (c == '{') { if (depth == 0) count++; depth++; }
            else if (c == '}') depth--;
        }
        return count;
    }
}
