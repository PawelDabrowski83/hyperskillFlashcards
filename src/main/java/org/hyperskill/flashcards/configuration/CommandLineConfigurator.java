package org.hyperskill.flashcards.configuration;

import org.hyperskill.flashcards.App;

import static org.hyperskill.flashcards.configuration.PatternConfiguration.*;
import static org.hyperskill.flashcards.configuration.PatternConfiguration.FILE_PATTERN;
import static org.hyperskill.flashcards.utils.SimpleLogger.pathToSave;

public class CommandLineConfigurator {

    public void configure(String[] args, App app){
        if (args.length > 0 && args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                if (IMPORT_COMMAND_PATTERN.matcher(args[i]).matches() && FILE_PATTERN.matcher(args[i + 1]).matches()) {
                    app.importCardsFromFile(args[i + 1]);
                }
                if (EXPORT_COMMAND_PATTERN.matcher(args[i]).matches() && FILE_PATTERN.matcher(args[i + 1]).matches()) {
                    pathToSave = args[i + 1];
                }
            }
        }
    }
}
