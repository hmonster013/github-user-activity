package org.de013.githubuseractivity.cli.command;

import org.de013.githubuseractivity.service.UserActivityService;
import org.de013.githubuseractivity.util.Messages;

public class LanguageCommand implements Command {
    private final String langArg;

    public LanguageCommand(String langArg) {
        this.langArg = langArg;
    }

    @Override
    public void excute(UserActivityService userActivityService) {
        if (langArg == null) {
            System.err.println(Messages.get("error.language.missing_arg"));
            return;
        }
        switch (langArg.toLowerCase()) {
            case "en":
                Messages.setLanguage(Messages.Language.EN);
                System.out.println(Messages.get("language.switched"));
                break;
            case "vi":
                Messages.setLanguage(Messages.Language.VI);
                System.out.println(Messages.get("language.switched"));
                break;
            default:
                System.err.println(Messages.get("error.language.invalid", langArg));
        }
    }
}
