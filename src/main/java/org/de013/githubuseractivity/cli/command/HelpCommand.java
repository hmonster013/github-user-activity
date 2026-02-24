package org.de013.githubuseractivity.cli.command;

import org.de013.githubuseractivity.service.UserActivityService;
import org.de013.githubuseractivity.util.Messages;

public class HelpCommand implements Command {
    @Override
    public void excute(UserActivityService userActivityService) {
        System.out.println(Messages.get("help.text"));
    }
}
