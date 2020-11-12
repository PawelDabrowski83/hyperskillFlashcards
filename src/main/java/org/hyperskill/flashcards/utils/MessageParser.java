package org.hyperskill.flashcards.utils;

import static org.hyperskill.flashcards.utils.SimpleLogger.lineCounter;


public class MessageParser {
    private final MessagePrinter messagePrinter;

    public MessageParser(MessagePrinter messagePrinter) {
        this.messagePrinter = messagePrinter;
    }

    public void printMe(String str) {
        String toPrint;
        if (str == null) {
            str = "";
        }
        toPrint = lineCounter + " | " + str;
        messagePrinter.printFinalize(toPrint);
    }

    public void printMe(String str1, String str2) {
        String toPrint = lineCounter + " | " + String.format(str1, str2);
        messagePrinter.printFinalize(toPrint);
    }

    public void printMe(String str, int n) {
        String toPrint = lineCounter + " | " + String.format(str, n);
        messagePrinter.printFinalize(toPrint);
    }

    public void printMe(String str1, String str2, String str3) {
        String toPrint = lineCounter + " | " + String.format(str1, str2, str3);
        messagePrinter.printFinalize(toPrint);
    }

    public void printMe(String str1, String str2, int n) {
        String toPrint = lineCounter + " | " + String.format(str1, str2, n);
        messagePrinter.printFinalize(toPrint);
    }
}
