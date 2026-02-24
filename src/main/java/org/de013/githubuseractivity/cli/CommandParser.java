package org.de013.githubuseractivity.cli;

import org.de013.githubuseractivity.cli.command.Command;
import org.de013.githubuseractivity.cli.command.DefaultCommand;
import org.de013.githubuseractivity.cli.command.HelpCommand;
import org.de013.githubuseractivity.cli.command.LanguageCommand;
import org.de013.githubuseractivity.util.Messages;

public class CommandParser {
    private String[] args;

    public CommandParser() {}

    public CommandParser(String[] args) { this.args = args; }

    public Command parse() {
        if (args.length == 0) {
            System.err.println(Messages.get("error.default.missing_username"));
            return null;
        }

        String first = args[0];

        if ("--help".equals(first) || "-h".equals(first)) {
            return new HelpCommand();
        }

        if ("--language".equals(first) || "-l".equals(first)) {
            String lang = args.length > 1 ? args[1] : null;
            return new LanguageCommand(lang);
        }

        // github-activity <username> [--filter <type>]
        String username = first;
        String filter = null;
        for (int i = 1; i < args.length - 1; i++) {
            if ("--filter".equals(args[i]) || "-f".equals(args[i])) {
                filter = args[i + 1];
                break;
            }
        }

        return new DefaultCommand(username, filter);
    }
}
