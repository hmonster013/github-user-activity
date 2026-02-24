package org.de013.githubuseractivity.service;

public interface UserActivityService {
    void fetchUserActivity(String username);
    void fetchUserActivity(String username, String eventTypeFilter);
}
