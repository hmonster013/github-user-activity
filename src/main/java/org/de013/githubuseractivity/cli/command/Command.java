package org.de013.githubuseractivity.cli.command;

import org.de013.githubuseractivity.service.UserActivityService;
import org.de013.githubuseractivity.service.UserActivityServiceImpl;

public interface Command {
    void excute(UserActivityService userActivityService);
}
