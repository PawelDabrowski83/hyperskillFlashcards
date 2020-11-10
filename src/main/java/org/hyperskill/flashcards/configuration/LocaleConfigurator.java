package org.hyperskill.flashcards.configuration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LocaleConfigurator {
    protected static final Pattern COVERED_LANGUAGES = Pattern.compile("^pl$|^en$");
    protected static final Pattern COVERED_COUNTRIES = Pattern.compile("^PL$|^US$");

    public ResourceBundle setLocale(String[] args){
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

    public Map<String, ActionsEnum> getTranslatedMenuItems(ResourceBundle messages){
        Map<String, ActionsEnum> translateAction = new HashMap<>();
        translateAction.put(messages.getString("actionAdd"), ActionsEnum.ADD);
        translateAction.put(messages.getString("actionRemove"), ActionsEnum.REMOVE);
        translateAction.put(messages.getString("actionAsk"), ActionsEnum.ASK);
        translateAction.put(messages.getString("actionExit"), ActionsEnum.EXIT);
        translateAction.put(messages.getString("actionImport"), ActionsEnum.IMPORT);
        translateAction.put(messages.getString("actionExport"), ActionsEnum.EXPORT);
        translateAction.put(messages.getString("actionHardestCard"), ActionsEnum.HARDEST_CARD);
        translateAction.put(messages.getString("actionLog"), ActionsEnum.LOG);
        translateAction.put(messages.getString("actionResetStats"), ActionsEnum.RESET_STATS);
        return translateAction;
    }
}
