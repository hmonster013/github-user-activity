package org.de013.githubuseractivity.cli.command;

import org.de013.githubuseractivity.service.UserActivityService;

public class DefaultCommand implements Command {
    private final String username;
    private final String filter;

    public DefaultCommand(String username) { this(username, null); }

    public DefaultCommand(String username, String filter) {
        this.username = username;
        this.filter = filter;
    }

    @Override
    public void excute(UserActivityService userActivityService) {
        userActivityService.fetchUserActivity(username, filter);
    }
}
