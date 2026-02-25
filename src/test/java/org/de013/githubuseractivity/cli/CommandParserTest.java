package org.de013.githubuseractivity.cli;

import org.de013.githubuseractivity.cli.command.Command;
import org.de013.githubuseractivity.cli.command.DefaultCommand;
import org.de013.githubuseractivity.cli.command.HelpCommand;
import org.de013.githubuseractivity.cli.command.LanguageCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandParserTest {

    @Test
    void parse_noArgs_returnsNull() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{});

        // Act
        Command result = parser.parse();

        // Assert
        assertNull(result);
    }

    @Test
    void parse_helpLongFlag_returnsHelpCommand() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{"--help"});

        // Act
        Command result = parser.parse();

        // Assert
        assertInstanceOf(HelpCommand.class, result);
    }

    @Test
    void parse_helpShortFlag_returnsHelpCommand() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{"-h"});

        // Act
        Command result = parser.parse();

        // Assert
        assertInstanceOf(HelpCommand.class, result);
    }

    @Test
    void parse_languageLongFlag_returnsLanguageCommand() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{"--language", "en"});

        // Act
        Command result = parser.parse();

        // Assert
        assertInstanceOf(LanguageCommand.class, result);
    }

    @Test
    void parse_languageShortFlag_returnsLanguageCommand() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{"-l", "vi"});

        // Act
        Command result = parser.parse();

        // Assert
        assertInstanceOf(LanguageCommand.class, result);
    }

    @Test
    void parse_usernameOnly_returnsDefaultCommand() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{"octocat"});

        // Act
        Command result = parser.parse();

        // Assert
        assertInstanceOf(DefaultCommand.class, result);
    }

    @Test
    void parse_usernameWithFilterLongFlag_returnsDefaultCommand() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{"octocat", "--filter", "push"});

        // Act
        Command result = parser.parse();

        // Assert
        assertInstanceOf(DefaultCommand.class, result);
    }

    @Test
    void parse_usernameWithFilterShortFlag_returnsDefaultCommand() {
        // Arrange
        CommandParser parser = new CommandParser(new String[]{"octocat", "-f", "WatchEvent"});

        // Act
        Command result = parser.parse();

        // Assert
        assertInstanceOf(DefaultCommand.class, result);
    }
}
