package org.RestlessTech.TerminaLingo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// This will handle:
// - logging the user in => initiate loop of following until the end
// - selecting a language to learn -> select a chapter -> select a lesson ->
// send the user into lesson via LessonParser -> return results to account manager and show results
// -> return to selecting a chapter
// -- for those above, we can go back on those selections


public class InterfaceManager {
    Scanner scanner = new Scanner(System.in);
    LessonParser lessonParser;
    Integer currentState;
    // Account Management
    AccountManager accountManager;
    User loggedUser;
    // File structure related:
    String masterFolderName = "MasterLessons";
    Path currPath;
    String LessonLang, LessonChap, LessonSel;
    Path masterPath;
    List<String> languages;

    public InterfaceManager() {
        /// Preparations
        handleMasterFolder();
        loggedUser = null;
        accountManager = new AccountManager(masterPath);
        currentState = 1;
        currPath = masterPath;
    }

    private void userAuth() {
        System.out.println("1) Register Account\n2) Login\n3) Exit Program");
        String choice = scanner.nextLine();
        String username, password;
        switch (choice) {
            case "1":
                System.out.println("Welcome to TerminaLingo!\nUsername:");
                username = scanner.next();
                System.out.println("Password:");
                password = scanner.next();

                loggedUser = accountManager.registerNewUser(username, password);
                break;
            case "2":
                System.out.println("Please log in.\nUsername:");
                username = scanner.next();
                System.out.println("Password:");
                password = scanner.next();

                loggedUser = accountManager.loginUser(username, password);
                break;
            case "3":
                System.out.println("Thank you for using TerminaLingo!\nGoodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Unrecognized choice. Rebooting.");
                loggedUser = null;
                break;
        }
    }

    private String getChoiceAbout(String whatAbout, List<String> choices) {
        while (true) {
            System.out.println("Please select a " + whatAbout + " from the following list:");
            int count = 1;
            for (String choice : choices) {
                System.out.println(count + ": " + choice);
                count++;
            }
            System.out.println("-------------------");
            System.out.println("B: Back | S: Stats | L: Logout | E: Exit");
            String choiceStr = scanner.nextLine();
            int choiceNr = -1;
            try {
                choiceNr = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                // Then, if the input is a single letter and is either B S L or L, we continue
                if (choiceStr.length() != 1) {
                    System.out.println("Invalid choice. Please try again.");
                    return "-2";
                }
                choiceStr = choiceStr.toUpperCase(Locale.ROOT);
                switch (choiceStr) {
                    case "E" ->
                        // Exit Program
                            System.exit(0);
                    case "L" -> {
                        // Logout
                        loggedUser = null;
                        accountManager = new AccountManager(masterPath);
                        return "-1";
                    }
                    case "S" -> {
                        // Stats
                        System.out.println(loggedUser.toString());
                        System.out.println("Input anything to continue.");
                        scanner.nextLine();
                        return "-2";
                    }
                    case "B" -> {
                        // Back
                        return "0";
                    }
                }
                System.out.println("Invalid choice. Please try again.");
            }
            // If it's a number, then proceed with selection as usual
            if (choiceNr >= 1 && choiceNr <= count) {
                // Then we choose that language
                return choices.get(choiceNr - 1);
            }
        }
    }

    private List<String> getSubdirectories(Path dirPath) {
        List<String> subdirectories = new ArrayList<>();
        // Get the list of subdirectories in dirPath. That means every single folder and adds to subdirectories
        File masterDir = new File(dirPath.toUri());
        File[] files = masterDir.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                subdirectories.add(file.getName());
            }
        }
        return subdirectories;
    }

    private List<String> getFiles(Path dirPath) {
        List<String> files = new ArrayList<>();
        File masterDir = new File(dirPath.toUri());
        File[] filesList = masterDir.listFiles();
        assert filesList != null;
        for (File file : filesList) {
            if (file.isFile()) {
                files.add(file.getName());
            }
        }
        return files;
    }

    public void stateSelect() {
        switch (currentState) {
            case 1:
                /// Auth Screen
                while (loggedUser == null) {
                    userAuth();
                }
                currentState = 2;
                break;
            case 2:
                /// Lesson Language selection
                currPath = masterPath;
                languages = getSubdirectories(currPath);
                Collections.sort(languages);
                LessonLang = getChoiceAbout("Language", languages);
                if (LessonLang.equals("0")) {
                    loggedUser = null;
                    currentState = 1;
                    return;
                }
                currentState = nextChoice(currentState, LessonLang);
                break;
            case 3:
                /// Lesson Chapter Selection
                currPath = currPath.resolve(LessonLang);
                List<String> chapters = getSubdirectories(currPath);
                Collections.sort(chapters);
                System.out.println("Language: " + LessonLang);
                LessonChap = getChoiceAbout("Chapter", chapters);
                currentState = nextChoice(currentState, LessonChap);
                if (currentState == 1) { // Logout
                    loggedUser = null;
                }
                if (currentState == 2) { // Back
                    currPath = currPath.getParent().getParent();
                }
                break;
            case 4:
                /// Lesson Selection
                currPath = currPath.resolve(LessonChap);
                List<String> lessons = getFiles(currPath);
                Collections.sort(lessons);
                System.out.println("Language: " + LessonLang);
                System.out.println("Chapter: " + LessonChap);
                LessonSel = getChoiceAbout("Lesson", lessons);
                currentState = nextChoice(currentState, LessonSel);
                if (currentState == 1) { // Logout
                    loggedUser = null;
                }
                if (currentState == 3) { // Back
                    currPath = currPath.getParent().getParent();
                }
                break;
            case 5:
                /// Send user into Lesson Parser with LessonSelection
                // Give the path to Parser
                currPath = currPath.resolve(LessonSel);
                lessonParser.sellectedLessonPath = currPath;

                // Start Lesson
                int lessonResult = lessonParser.parseLesson();
                if (lessonResult > 0) {
                    loggedUser.addLessonCompleted(lessonParser.sellectedLessonPath.toString(), lessonResult);
                    for (String word : lessonParser.lesson.getCuvinteInvatate()) {
                        loggedUser.addLearnedWord(word);
                    }
                    loggedUser.incrementLessonsTotal();
                    accountManager.saveUsers();
                    System.out.println("Lesson \"" + lessonParser.lesson.getNumeLectie() + "\" learned successfully.");
                    System.out.println("Score: " + lessonResult + "/100");
                } else {
                    System.out.println("Lesson " + LessonSel + " encountered an error.");
                }
                // Now we continue with the Language Select section
                currentState = 2;
                break;
            default:
                System.out.println("Unrecognized choice. Rebooting.");
                loggedUser = null;
                currentState = 1;
                break;
        }
    }

    private int nextChoice(int currentState, String givenChoice) {
        if (Objects.equals(givenChoice, "0")) { // Back 1 state
            return currentState - 1;
        }
        if (Objects.equals(givenChoice, "-1")) { // Logout => state 1
            return 1;
        }
        if (Objects.equals(givenChoice, "-2")) { // Stats => same state
            return currentState;
        }
        return currentState + 1;
    }

    private void handleMasterFolder() {
        // Get the current Path and put it in masterPath
        masterPath = Paths.get("");
        masterPath = masterPath.resolve(masterFolderName);
        // Check if the path actually exists
        if (!Files.exists(masterPath)) {
            // No folder found, exit program.
            System.out.println("Folder doesn't exist at location: " + masterPath.toAbsolutePath());
            System.exit(0);
        }
        // Otherwise great stuff.
    }


}
