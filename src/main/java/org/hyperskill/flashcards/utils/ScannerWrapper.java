package org.hyperskill.flashcards.utils;

import java.util.Scanner;

public class ScannerWrapper implements AutoCloseable {

    private final Scanner scanner;

    public ScannerWrapper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String getString(){
        return scanner.nextLine().trim();
    }

    public boolean hasNextLine(){
        return scanner.hasNextLine();
    }

    @Override
    public void close() throws Exception {
        scanner.close();
    }
}
