package org.hyperskill.flashcards;

import java.util.InputMismatchException;
import java.util.Scanner;

public class ScannerWrapper implements AutoCloseable {

    private final Scanner scanner;

    public ScannerWrapper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String getString(){
        return scanner.nextLine().trim();
    }

    public int getInt() throws InputMismatchException {
        return scanner.nextInt();
    }

    public double getDouble() throws InputMismatchException{
        return scanner.nextDouble();
    }

    public boolean hasNextLine(){
        return scanner.hasNextLine();
    }

    @Override
    public void close() throws Exception {
        scanner.close();
    }
}
