package org.hyperskill.flashcards;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LocaleConfigurator {
    protected static final Pattern COVERED_LANGUAGES = Pattern.compile("^pl$|^en$");
    protected static final Pattern COVERED_COUNTRIES = Pattern.compile("^PL$|^US$");

    protected ResourceBundle setLocale(String[] args){
        String language = "pl";
        String country = "PL";

        if (args.length > 0 && args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                if (COVERED_LANGUAGES.matcher(args[i]).matches() && COVERED_COUNTRIES.matcher(args[i + 1]).matches()) {
                    language = args[i];
                    country = args[i + 1];
                }
            }
        }

        Locale currentLocale = new Locale(language, country);

        return ResourceBundle.getBundle("MessagesBundle", currentLocale);
    }
}
