package org.hyperskill.flashcards.utils;

import static org.hyperskill.flashcards.utils.SimpleLogger.log;

public class ScannerLogger {
    public static String passInputAndLog(ScannerWrapper scannerWrapper) {
        String buffer = scannerWrapper.getString();
        log.add("> " + buffer);
        return buffer;
    }
}
