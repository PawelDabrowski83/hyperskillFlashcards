package org.hyperskill.flashcards;

import java.util.Scanner;
import static org.hyperskill.flashcards.Main.log;

public class PrintUtils {

    public static void printMe(String str) {
        String toPrint = Main.lineCounter + " | " + str;
        printFinalize(toPrint);
    }

    public static void printMe(String str1, String str2) {
        String toPrint = String.format(str1, str2);
        printFinalize(toPrint);
    }

    public static void printMe(String str, int n) {
        String toPrint = String.format(str, n);
        printFinalize(toPrint);
    }

    public static void printMe(String str1, String str2, String str3) {
        String toPrint = String.format(str1, str2, str3);
        printFinalize(toPrint);
    }

    public static void printMe(String str1, String str2, int n) {
        String toPrint = String.format(str1, str2, n);
        printFinalize(toPrint);
    }

    public static void printFinalize (String str) {
        log.add(str);
        System.out.println(str);
        Main.lineCounter++;
    }

    public static String passInputAndLog(Scanner scanner) {
        String buffer = scanner.nextLine();
        log.add("> " + buffer);
        return buffer;
    }
}
