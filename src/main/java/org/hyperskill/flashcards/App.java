package org.hyperskill.flashcards;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static org.hyperskill.flashcards.PatternConfiguration.*;
import static org.hyperskill.flashcards.PatternConfiguration.CARD_DEFINITION_PATTERN;
import static org.hyperskill.flashcards.PrintUtils.passInputAndLog;
import static org.hyperskill.flashcards.PrintUtils.printMe;
import static org.hyperskill.flashcards.SimpleLogger.log;
import static org.hyperskill.flashcards.SimpleLogger.pathToSave;

public class App {

    protected static ResourceBundle messages = null;

    public void play(String[] args) {
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

        Locale currentLocale;
        currentLocale = new Locale(language, country);

        messages = ResourceBundle.getBundle("MessagesBundle", currentLocale);

        // map ActionMenu to proper language
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

        Map<String, String> cards = new HashMap<>();
        Map<String, Integer> mistakes = new HashMap<>();

        if (args.length > 0 && args.length % 2 == 0) {
            for (int i = 0; i < args.length; i += 2) {
                if (IMPORT_COMMAND_PATTERN.matcher(args[i]).matches() && FILE_PATTERN.matcher(args[i + 1]).matches()) {
                    importCardsFromFile(args[i + 1], cards, mistakes);
                }
                if (EXPORT_COMMAND_PATTERN.matcher(args[i]).matches() && FILE_PATTERN.matcher(args[i + 1]).matches()) {
                    pathToSave = args[i + 1];
                }
            }
        }

        printMe(messages.getString("commandLine"));

        try (Scanner scanner = new Scanner(System.in)) {
            String input;
            while (scanner.hasNextLine()) {
                input = passInputAndLog(scanner).trim().toLowerCase();
                ActionsEnum yourChoice = ActionsEnum.DEFAULT;
                if (translateAction.containsKey(input)) {
                    yourChoice = translateAction.get(input);
                }

                switch (yourChoice) {
                    case ADD:
                        addCard(scanner, cards);
                        break;
                    case REMOVE:
                        removeCard(scanner, cards, mistakes);
                        break;
                    case IMPORT:
                        importCards(scanner, cards, mistakes);
                        break;
                    case EXPORT:
                        exportCards(scanner, cards, mistakes);
                        break;
                    case ASK:
                        askQuestion(scanner, cards, mistakes);
                        break;
                    case EXIT:
                        exitAndPossiblySaveToFile(cards, mistakes);
                        return;
                    case LOG:
                        logCards(scanner);
                        break;
                    case HARDEST_CARD:
                        hardestCard(mistakes);
                        break;
                    case RESET_STATS:
                        resetStats(mistakes);
                        break;
                    default:
                }
                printMe(messages.getString("commandLine"));
            }
        }
    }

    public static void addCard(Scanner scanner, Map<String, String> cards) {

        printMe(messages.getString("addCard"));
        String card = passInputAndLog(scanner).trim();

        while (card.isEmpty() || cards.containsKey(card)) {
            if (card.isEmpty()) {
                printMe(messages.getString("addCard"));
            } else {
                printMe(messages.getString("cardDuplicate"), card);
                return;
            }
            card = passInputAndLog(scanner).trim();
        }

        printMe(messages.getString("addDefinition"));
        String definition = passInputAndLog(scanner).trim();

        if (definition.isEmpty() || cards.containsValue(definition)) {
            if (definition.isEmpty()) {
                printMe(messages.getString("addDefinition"));
            } else {
                printMe(messages.getString("definitionDuplicate"), definition);
                return;
            }
            definition = passInputAndLog(scanner).trim();
        }
        cards.put(card, definition);
        printMe(messages.getString("cardAdded"), card, definition);
    }

    public static void removeCard(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(messages.getString("removeCard"));
        String card = passInputAndLog(scanner).trim();

        if (card.isEmpty() || !cards.containsKey(card)) {
            if (card.isEmpty()) {
                printMe(messages.getString("removeCard"));
            } else {
                printMe(messages.getString("cardNotExisting"), card);
                return;
            }
        }
        cards.remove(card);
        mistakes.remove(card);
        printMe(messages.getString("cardRemoved"));
    }

    public static void importCards(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(messages.getString("importCard"));
        String fileName = passInputAndLog(scanner).trim();
        importCardsFromFile(fileName, cards, mistakes);
    }

    protected static void importCardsFromFile(String fileName, Map<String, String> cards, Map<String, Integer> mistakes) {
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
            printMe(messages.getString("importSuccess"), count);
        } catch (IOException e) {
            printMe(messages.getString("fileNotFound"));
        }

    }

    public static void exportCards(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(messages.getString("exportCard"));
        String fileName = passInputAndLog(scanner).trim();
        exportToFile(fileName, cards, mistakes);
    }

    protected static void exportToFile(String fileName, Map<String, String> cards, Map<String, Integer> mistakes) {
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
            printMe(messages.getString("fileNotFound"));
            return;
        }
        printMe(messages.getString("exportSuccess"), count);
    }

    public static void askQuestion(Scanner scanner, Map<String, String> cards, Map<String, Integer> mistakes) {

        printMe(messages.getString("askHowMany"));

        String numberAsString = passInputAndLog(scanner).trim();
        int number = 0;
        try {
            number = Integer.parseInt(numberAsString);
        } catch (NumberFormatException e) {
            printMe(messages.getString("numberFormatException"), numberAsString);
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

            printMe(messages.getString("askQuestion"), cardEntry);
            String answer = passInputAndLog(scanner).trim();

            if (definitionEntry.equalsIgnoreCase(answer)) {
                printMe(messages.getString("askQuestionCorrect"));
            } else {
                mistakes.put(cardEntry, (mistakes.getOrDefault(cardEntry, 0) + 1));
                if (cards.containsValue(answer)) {
                    String cardFoundByDefinition = cards.entrySet().stream()
                            .filter(n -> n.getValue().equalsIgnoreCase(answer))
                            .map(Map.Entry::getKey)
                            .collect(Collectors.joining());
                    printMe(messages.getString("askQuestionMistakenWithAnother"), definitionEntry, cardFoundByDefinition);
                } else {
                    printMe(messages.getString("askQuestionWrong"), definitionEntry);
                }
            }
            number--;

        }
    }

    public static void hardestCard(Map<String, Integer> mistakes) {

        int maxMistakes = mistakes.values().stream().max(Comparator.naturalOrder()).orElse(0);

        if (maxMistakes == 0) {
            printMe(messages.getString("hardestNone"));
        } else {
            Map<String, Integer> hardestCards = mistakes.entrySet().stream()
                    .filter(e -> e.getValue().equals(maxMistakes))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            if (hardestCards.size() > 1) {
                String hardestCardNames = String.join("\", \"", hardestCards.keySet());
                printMe(messages.getString("hardestCardManyOfThem"), hardestCardNames, maxMistakes);
            } else {
                printMe(messages.getString("hardestCard"), hardestCards.keySet().stream().findFirst().orElse(""), maxMistakes);
            }
        }
    }

    public static void logCards(Scanner scanner) {

        printMe(messages.getString("log"));
        String fileName = passInputAndLog(scanner).trim();
        logToFile(fileName);
    }

    protected static void logToFile(String fileName) {
        File file = new File(fileName);

        try (FileWriter fileWriter = new FileWriter(file)) {
            for (String logEntry : log) {
                fileWriter.write(logEntry + System.lineSeparator());
                fileWriter.flush();
            }
        } catch (IOException e) {
            printMe(messages.getString("fileNotFound"));
            return;
        }
        printMe(messages.getString("logSaved"));
    }

    public static void exitAndPossiblySaveToFile(Map<String, String> cards, Map<String, Integer> mistakes) {
        printMe(messages.getString("exitMessage"));
        if (!pathToSave.isEmpty()) {
            exportToFile(pathToSave, cards, mistakes);
        }
    }

    public static void resetStats(Map<String, Integer> mistakes) {
        mistakes.clear();
        printMe(messages.getString("resetStats"));
    }

}
