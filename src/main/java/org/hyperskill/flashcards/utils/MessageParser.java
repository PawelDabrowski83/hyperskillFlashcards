package org.hyperskill.flashcards.utils;

import static org.hyperskill.flashcards.utils.SimpleLogger.log;
import static org.hyperskill.flashcards.utils.SimpleLogger.lineCounter;
import static org.hyperskill.flashcards.utils.MessagePrinter.printFinalize;

public class MessageParser {

    public static void printMe(String str) {
        String toPrint;
        if (str == null) {
            str = "";
        }
        toPrint = lineCounter + " | " + str;
        printFinalize(toPrint);
    }

    public static void printMe(String str1, String str2) {
        String toPrint = lineCounter + " | " + String.format(str1, str2);
        printFinalize(toPrint);
    }

    public static void printMe(String str, int n) {
        String toPrint = lineCounter + " | " + String.format(str, n);
        printFinalize(toPrint);
    }

    public static void printMe(String str1, String str2, String str3) {
        String toPrint = lineCounter + " | " + String.format(str1, str2, str3);
        printFinalize(toPrint);
    }

    public static void printMe(String str1, String str2, int n) {
        String toPrint = lineCounter + " | " + String.format(str1, str2, n);
        printFinalize(toPrint);
    }
}
