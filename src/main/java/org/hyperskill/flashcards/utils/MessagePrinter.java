package org.hyperskill.flashcards.utils;

import static org.hyperskill.flashcards.utils.SimpleLogger.lineCounter;
import static org.hyperskill.flashcards.utils.SimpleLogger.log;

public class MessagePrinter {

    public static void printFinalize (String str) {
        log.add(str);
        System.out.println(str);
        lineCounter++;
    }
}
