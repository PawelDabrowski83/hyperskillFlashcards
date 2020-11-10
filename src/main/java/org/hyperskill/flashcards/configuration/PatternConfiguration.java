package org.hyperskill.flashcards.configuration;

import java.util.regex.Pattern;

public class PatternConfiguration {
    protected static final Pattern CARD_DEFINITION_PATTERN = Pattern.compile("\"(.+)\":\"(.+)\":\"(\\d+)\"");
    protected static final Pattern IMPORT_COMMAND_PATTERN = Pattern.compile("-import");
    protected static final Pattern EXPORT_COMMAND_PATTERN = Pattern.compile("-export");
    protected static final Pattern FILE_PATTERN = Pattern.compile("\\S+\\.txt");

}
