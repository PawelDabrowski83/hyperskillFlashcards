package org.hyperskill.flashcards;

import org.hyperskill.flashcards.configuration.ActionsEnum;
import org.hyperskill.flashcards.configuration.CommandLineConfigurator;
import org.hyperskill.flashcards.configuration.LocaleConfigurator;
import org.hyperskill.flashcards.utils.ScannerWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static org.hyperskill.flashcards.configuration.PatternConfiguration.CARD_DEFINITION_PATTERN;
import static org.hyperskill.flashcards.utils.PrintUtils.passInputAndLog;
import static org.hyperskill.flashcards.utils.PrintUtils.printMe;
import static org.hyperskill.flashcards.utils.SimpleLogger.log;
import static org.hyperskill.flashcards.utils.SimpleLogger.pathToSave;

public class App {

    protected static ResourceBundle messages;
    protected final LocaleConfigurator localeConfigurator = new LocaleConfigurator();
    protected final CommandLineConfigurator commandLineConfigurator;
    private final Map<String, ActionsEnum> translateActions;
    private final Map<String, String> cards = new HashMap<>();
    private final Map<String, Integer> mistakes = new HashMap<>();
    private final ScannerWrapper scannerWrapper;

    public App(String[] args){
        messages = localeConfigurator.setLocale(args);
        translateActions = localeConfigurator.getTranslatedMenuItems(messages);
        scannerWrapper = new ScannerWrapper(new Scanner(System.in));
        commandLineConfigurator = new CommandLineConfigurator();
    }

    public void play(String[] args) {
        commandLineConfigurator.configure(args, this);
        acceptCommands();
    }

    protected void acceptCommands(){
        printMe(messages.getString("commandLine"));

        try (scannerWrapper) {
            while (scannerWrapper.hasNextLine()) {
                ActionsEnum yourChoice = getNextUserAction();

                switch (yourChoice) {
                    case ADD -> addCard();
                    case REMOVE -> removeCard();
                    case IMPORT -> importCards();
                    case EXPORT -> exportCards();
                    case ASK -> askQuestion();
                    case EXIT -> {
                        exitAndPossiblySaveToFile();
                        return;
                    }
                    case LOG -> logCards();
                    case HARDEST_CARD -> hardestCard();
                    case RESET_STATS -> resetStats();
                }
                printMe(messages.getString("commandLine"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private ActionsEnum getNextUserAction() {
        String input;
        input = passInputAndLog(scannerWrapper).toLowerCase();
        ActionsEnum yourChoice = ActionsEnum.DEFAULT;
        if (translateActions.containsKey(input)) {
            yourChoice = translateActions.get(input);
        }
        return yourChoice;
    }

    public void addCard() {

        printMe(messages.getString("addCard"));
        String card = passInputAndLog(scannerWrapper).trim();

        while (card.isEmpty() || cards.containsKey(card)) {
            if (card.isEmpty()) {
                printMe(messages.getString("addCard"));
            } else {
                printMe(messages.getString("cardDuplicate"), card);
                return;
            }
            card = passInputAndLog(scannerWrapper).trim();
        }

        printMe(messages.getString("addDefinition"));
        String definition = passInputAndLog(scannerWrapper).trim();

        if (definition.isEmpty() || cards.containsValue(definition)) {
            if (definition.isEmpty()) {
                printMe(messages.getString("addDefinition"));
            } else {
                printMe(messages.getString("definitionDuplicate"), definition);
                return;
            }
            definition = passInputAndLog(scannerWrapper).trim();
        }
        cards.put(card, definition);
        printMe(messages.getString("cardAdded"), card, definition);
    }

    public void removeCard() {

        printMe(messages.getString("removeCard"));
        String card = passInputAndLog(scannerWrapper).trim();

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

    public void importCards() {

        printMe(messages.getString("importCard"));
        String fileName = passInputAndLog(scannerWrapper).trim();
        importCardsFromFile(fileName);
    }

    public void importCardsFromFile(String fileName) {
        File file = new File(fileName);
        int count = 0;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String newLine = fileScanner.nextLine();
                if (newLine.matches(CARD_DEFINITION_PATTERN.pattern())) {
                    Matcher matcher = CARD_DEFINITION_PATTERN.matcher(newLine);
                    if(matcher.matches()){
                        cards.put(matcher.group(1), matcher.group(2));
                        if (!"0".equalsIgnoreCase(matcher.group(3))) {
                            mistakes.put(matcher.group(1), Integer.parseInt(matcher.group(3)));
                        }
                    }
                    count++;
                }
            }
            printMe(messages.getString("importSuccess"), count);
        } catch (IOException e) {
            printMe(messages.getString("fileNotFound"));
        }

    }

    public void exportCards() {

        printMe(messages.getString("exportCard"));
        String fileName = passInputAndLog(scannerWrapper);
        exportToFile(fileName);
    }

    protected void exportToFile(String fileName) {
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

    public void askQuestion() {

        printMe(messages.getString("askHowMany"));

        String numberAsString = passInputAndLog(scannerWrapper);
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
            String answer = passInputAndLog(scannerWrapper).trim();

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

    public void hardestCard() {

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

    public void logCards() {

        printMe(messages.getString("log"));
        String fileName = passInputAndLog(scannerWrapper);
        logToFile(fileName);
    }

    protected void logToFile(String fileName) {
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

    public void exitAndPossiblySaveToFile() {
        printMe(messages.getString("exitMessage"));
        if (!pathToSave.isEmpty()) {
            exportToFile(pathToSave);
        }
    }

    public void resetStats() {
        mistakes.clear();
        printMe(messages.getString("resetStats"));
    }

}
