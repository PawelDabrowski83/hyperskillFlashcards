package org.hyperskill.flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static org.hyperskill.flashcards.PrintUtils.printMe;
import static org.hyperskill.flashcards.PrintUtils.passInputAndLog;

public class Main {

    private static final String COMMAND_LINE = "Input the action (add, remove, import, export, ask, exit," +
            " log, hardest card, reset stats):";
    private static final String EXIT_MESSAGE = "Bye bye!";
    private static final String ADD_CARD = "The card:";
    private static final String CARD_DUPLICATE = "The card \"%s\" already exists." + System.lineSeparator();
    private static final String ADD_DEFINITION = "The definition of the card:";
    private static final String DEFINITION_DUPLICATE = "The definition \"%s\" already exists." + System.lineSeparator();
    private static final String CARD_ADDED = "The pair (\"%s\":\"%s\") has been added." + System.lineSeparator();
    private static final String REMOVE_CARD = "The card:";
    private static final String CARD_REMOVED = "The card has been removed.";
    private static final String CARD_NOT_EXISTING = "Can't remove \"%s\": there is no such card." + System.lineSeparator();
    private static final String IMPORT_CARD = "File name: action";
    private static final String FILE_NOT_FOUND = "File not found.";
    private static final String IMPORT_SUCCESS = "%d cards have been loaded." + System.lineSeparator();
    private static final Pattern CARD_DEFINITION_PATTERN = Pattern.compile("\"(.+)\":\"(.+)\":\"(\\d+)\"");
    private static final String EXPORT_CARD = "File name: action";
    private static final String EXPORT_SUCCESS = "%d cards have been saved." + System.lineSeparator();
    private static final String ASK_HOW_MANY = "How many times to ask?";
    private static final String ASK_QUESTION = "Print the definition of \"%s\":" + System.lineSeparator();
    private static final String ASK_QUESTION_CORRECT = "Correct answer.";
    private static final String ASK_QUESTION_FAIL = "Wrong answer. The correct one is \"%s\", you've just written the definition of \"%s\"." + System.lineSeparator();
    private static final String ASK_QUESTION_WRONG = "Wrong answer. The correct one is \"%s\"." + System.lineSeparator();
    private static final String HARDEST_NONE = "There are no cards with errors.";
    private static final String HARDEST_CARD = "The hardest card is \"%s\". You have %d errors answering it." + System.lineSeparator();
    private static final String HARDEST_CARD_MULTIPLE = "The hardest cards are \"%s\". You have %d errors answering them." + System.lineSeparator();
    private static final String RESET_STATS = "Card statistics has been reset.";
    private static final String LOG = "File name:";
    private static final String LOG_SAVED = "The log has been saved.";
    protected static int lineCounter = 0;
    protected static List<String> log = new ArrayList<>();

    public static void main(String[] args) {

        Map<String, String> cards = new HashMap<>();
        Map<String, Integer> mistakes = new HashMap<>();

        printMe(COMMAND_LINE);

        try (Scanner scanner = new Scanner(System.in)) {
            String input = "";
            while (scanner.hasNextLine() && !"exit".equalsIgnoreCase(input)) {
                input = passInputAndLog(scanner).trim().toLowerCase();

                switch (input) {
                    case "add":
                        addCard(scanner, cards);
                        break;
                    case "remove":
                        removeCard(scanner, cards, mistakes);
                        break;
                    case "import":
                        importCards(scanner, cards, mistakes);
                        break;
                    case "export":
                        exportCards(scanner, cards, mistakes);
                        break;
                    case "ask":
                        askQuestion(scanner, cards, mistakes);
                        break;
                    case "exit":
                        System.out.println(EXIT_MESSAGE);
                        return;
                    case "log":
                        logToFile(scanner);
                        break;
                    case "hardest card":
                        hardestCard(mistakes);
                        break;
                    case "reset stats":
                        resetStats(mistakes);
                        break;
                    default:
                        printMe(COMMAND_LINE);
                }
            }
        }
    }

    public static void addCard(Scanner scanner, Map<String, String> cards) {

        printMe(ADD_CARD);
        String card = passInputAndLog(scanner).trim();

        while (card.isEmpty() || cards.containsKey(card)) {
            if (card.isEmpty()) {
                printMe(ADD_CARD);
            } else {
                printMe(CARD_DUPLICATE, card);
                printMe(COMMAND_LINE);
                return;
            }
            card = passInputAndLog(scanner).trim();
        }

        printMe(ADD_DEFINITION);
        String definition = passInputAndLog(scanner).trim();

        if (definition.isEmpty() || cards.containsValue(definition)) {
            if (definition.isEmpty()) {
                printMe(ADD_DEFINITION);
            } else {
                printMe(DEFINITION_DUPLICATE, definition);
                printMe(COMMAND_LINE);
                return;
            }
            definition = passInputAndLog(scanner).trim();
        }
        cards.put(card, definition);
        printMe(CARD_ADDED, card, definition);
        printMe(COMMAND_LINE);
    }

    public static void removeCard(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(REMOVE_CARD);
        String card = passInputAndLog(scanner).trim();

        if (card.isEmpty() || !cards.containsKey(card)) {
            if (card.isEmpty()) {
                printMe(REMOVE_CARD);
            } else {
                printMe(CARD_NOT_EXISTING, card);
                printMe(COMMAND_LINE);
                return;
            }

        }
        cards.remove(card);
        mistakes.remove(card);
        printMe(CARD_REMOVED);
        printMe(COMMAND_LINE);
    }

    public static void importCards(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(IMPORT_CARD);
        String fileName = passInputAndLog(scanner).trim();

        File file = new File(fileName);
        int count = 0;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String newLine = fileScanner.nextLine();
                if (newLine.matches(CARD_DEFINITION_PATTERN.pattern())) {
                    Matcher matcher = CARD_DEFINITION_PATTERN.matcher(newLine);
                    matcher.matches(); // necessary to check matcher.group
                    cards.put(matcher.group(1), matcher.group(2));
                    if (!"0".equalsIgnoreCase(matcher.group(3))) {
                        mistakes.put(matcher.group(1), Integer.parseInt(matcher.group(3)));
                    }
                    count++;
                }
            }
            printMe(IMPORT_SUCCESS, count);
        } catch (IOException e) {
            printMe(FILE_NOT_FOUND);
        }
        printMe(COMMAND_LINE);
    }

    public static void exportCards(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(EXPORT_CARD);
        String fileName = passInputAndLog(scanner).trim();

        File file = new File(fileName);
        int count = 0;

        try (FileWriter fileWriter = new FileWriter(file)) {
            for (Map.Entry<String, String> record : cards.entrySet()) {
                fileWriter.write("\"" + record.getKey() + "\":\"" + record.getValue() + "\":\"" +
                        mistakes.getOrDefault(record.getKey(), 0) + "\"" + System.lineSeparator());
                fileWriter.flush();
                count++;
            }
        } catch (IOException e) {
            printMe(FILE_NOT_FOUND);
            return;
        }
        printMe(EXPORT_SUCCESS, count);
        printMe(COMMAND_LINE);
    }

    public static void askQuestion(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(ASK_HOW_MANY);

        String numberAsString = passInputAndLog(scanner).trim();
        int number = 0;
        try {
            number = Integer.parseInt(numberAsString);
        } catch (NumberFormatException e) {
            printMe("Cannot parse %s", numberAsString);
        }

        Map<Integer, String> auxilaryMap = new HashMap<>();

        int counter = 0;
        for (String key : cards.keySet()) {
            auxilaryMap.put(counter, key);
            counter++;
        }

        Random random = new Random();

        while (number > 0) {
            int randomized = random.nextInt(counter);
            String cardEntry = auxilaryMap.get(randomized);
            String definitionEntry = cards.get(cardEntry);

            printMe(ASK_QUESTION, cardEntry);
            String answer = passInputAndLog(scanner).trim();

            if (definitionEntry.equalsIgnoreCase(answer)) {
                printMe(ASK_QUESTION_CORRECT);
            } else {
                mistakes.put(cardEntry, (mistakes.getOrDefault(cardEntry, 0) + 1));
                if (cards.containsValue(answer)) {
                    String cardFoundByDefinition = cards.entrySet().stream()
                            .filter(n -> n.getValue().equalsIgnoreCase(answer))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.joining());
                    printMe(ASK_QUESTION_FAIL, definitionEntry, cardFoundByDefinition);
                } else {
                    printMe(ASK_QUESTION_WRONG, definitionEntry);
                }
            }
            number--;

        }
        printMe(COMMAND_LINE);
    }

    public static void hardestCard(Map<String, Integer> mistakes) {

        int maxMistakes = mistakes.values().stream().max(Comparator.naturalOrder()).orElse(0);

        if (maxMistakes == 0) {
            printMe(HARDEST_NONE);
        } else {
            Map<String, Integer> hardestCards = mistakes.entrySet().stream()
                    .filter(e -> e.getValue().equals(maxMistakes))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (hardestCards.size() > 1) {
                String hardestCardNames = String.join("\", \"", hardestCards.keySet());
                printMe(HARDEST_CARD_MULTIPLE, hardestCardNames, maxMistakes);
            } else {
                printMe(HARDEST_CARD, hardestCards.keySet().stream().findFirst().orElse(""), maxMistakes);
            }
        }
        printMe(COMMAND_LINE);
    }

    public static void logToFile(Scanner scanner) {

        printMe(LOG);
        String fileName = passInputAndLog(scanner).trim();
        File file = new File(fileName);

        try (FileWriter fileWriter = new FileWriter(file)) {
            for (String logEntry : log) {
                fileWriter.write(logEntry + System.lineSeparator());
                fileWriter.flush();
            }
        } catch (IOException e) {
            printMe(FILE_NOT_FOUND);
            printMe(COMMAND_LINE);
            return;
        }
        printMe(LOG_SAVED);
        printMe(COMMAND_LINE);
    }

    public static void resetStats(Map<String, Integer> mistakes) {
        mistakes.clear();
        printMe(RESET_STATS);
        printMe(COMMAND_LINE);
    }


}
