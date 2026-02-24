package org.de013.githubuseractivity;

import org.de013.githubuseractivity.cli.CommandParser;
import org.de013.githubuseractivity.cli.command.Command;
import org.de013.githubuseractivity.service.UserActivityServiceImpl;
import org.de013.githubuseractivity.util.Messages;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {}

        CommandParser commandParser = new CommandParser(args);
        UserActivityServiceImpl userActivityServiceImpl = new UserActivityServiceImpl();

        Command command = commandParser.parse();

        if (command == null) {
            System.err.println(Messages.get("error.parsing_failed"));
            return;
        }

        command.excute(userActivityServiceImpl);
    }
}
