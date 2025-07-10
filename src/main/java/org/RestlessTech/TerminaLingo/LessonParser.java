package org.RestlessTech.TerminaLingo;

// Gets a file, and starts a command line test
// Parses file until it ends
// Then exit the lesson and return result to Interface Manager

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.*;

public class LessonParser {
    private final Gson gson = new Gson();
    private final String blackout = "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
    public Lesson lesson = null;
    public Path sellectedLessonPath;
    Scanner scanner = new Scanner(System.in);

    public void fetchLesson() {
        try (FileReader reader = new FileReader(sellectedLessonPath.toFile())) {
            lesson = gson.fromJson(reader, Lesson.class);
        } catch (IOException e) {
            System.err.println("Error reading or parsing lesson file at " + sellectedLessonPath + ": " + e.getMessage());
        } catch (JsonSyntaxException e) {
            // This catches errors where the file content is not valid JSON or doesn't match the object structure
            System.err.println("Error parsing JSON syntax in lesson file at " + sellectedLessonPath + ": " + e.getMessage());
            System.err.println("Please check the JSON format of the file.");
        }
    }

    public int parseLesson() {
        lesson = null;
        List<Test> tests;
        int correctStreak = 0, totalScore, testNr;

        // Get lesson from file
        fetchLesson();
        // Add all tests from "lesson" to "tests"
        if (lesson == null) {
            return -1;
        }
        tests = new ArrayList<>(lesson.getTeste());
        testNr = tests.size();
        // Blank out clutter
        System.out.println(blackout);

        while (!tests.isEmpty()) {
            Test currTest = tests.removeFirst();
            if (queryTest(currTest)) {
                // Test succeded, add 1 to streak
                correctStreak++;
            } else {
                // Test failed, add the end of tests and go to next
                tests.add(currTest);
                correctStreak--;
            }
        }
        // If the user made many mistakes, the streak can go negative
        if (correctStreak < 0) correctStreak = 0;
        totalScore = (int) (50 + (double) correctStreak / testNr * 50);
        return totalScore;
    }

    private boolean queryTest(Test currTest) {
        boolean result = false;
        List<String> words, wordsAnswers, guesses;
        String question, correctAnswer, guess;

        switch (currTest.getTip()) {
            case 1:
                /// Preparing test
                words = new ArrayList<>();
                question = currTest.getPropozitie();
                correctAnswer = currTest.getRaspunsCorect();
                // Take all the words from correct answer
                // add to "words", randomize order
                // Split correctAnswer by spaces, ',', '.' with regex
                words = normalizeAndSplit(correctAnswer);
                // Randomize words
                Collections.shuffle(words);

                /// Querying user
                System.out.println("Translate the following sentence:\n" + question);
                System.out.println("Possible words:\n" + words);
                System.out.println("Enter your guess below!");
                guess = scanner.nextLine();
                // Check if words are in the right order
                result = answerCompare(guess, correctAnswer);

                break;
            case 2:
                /// Preparing test
                question = currTest.getPropozitie();
                correctAnswer = currTest.getRaspunsCorect();
                /// Querying user
                System.out.println("Translate the following sentence:\n" + question);
                System.out.println("Enter your guess below!");
                guess = scanner.nextLine();
                // Check if words are in the right order
                result = answerCompare(guess, correctAnswer);
                break;
            case 3:
                int attemptsRemaining = 3;

                // 1. Create a Map to store and manage the correct pairs.
                // We use LinkedHashMap to maintain original order for initial population,
                // but we'll shuffle the keys/values when displaying to the user.
                Map<String, String> remainingPairs = new LinkedHashMap<>();
                List<String> initialLeftWords = currTest.getLeftWords();
                List<String> initialRightWords = currTest.getRightWords();

                // Populate the map with original pairs, converting to lowercase for case-insensitive matching
                if (initialLeftWords.size() != initialRightWords.size()) {
                    System.err.println("Error: Left and Right word lists for combine test are not of equal size!");
                    return false; // Cannot proceed with a malformed test
                }
                for (int i = 0; i < initialLeftWords.size(); i++) {
                    // Use normalizeStringForComparison for both key and value
                    remainingPairs.put(normalizeString(initialLeftWords.get(i)),
                            normalizeString(initialRightWords.get(i)));
                }

                // 2. Game loop: Continue as long as there are pairs to match AND attempts remaining
                while (!remainingPairs.isEmpty() && attemptsRemaining > 0) {
                    System.out.println("\n--- Combine the words ---");

                    // Prepare words for display: Shuffle the current left and right words independently
                    List<String> currentDisplayLeft = new ArrayList<>(remainingPairs.keySet());
                    List<String> currentDisplayRight = new ArrayList<>(remainingPairs.values());
                    Collections.shuffle(currentDisplayLeft);
                    Collections.shuffle(currentDisplayRight);

                    System.out.println("Match the following pairs:");
                    for (int i = 0; i < currentDisplayLeft.size(); i++) {
                        System.out.println(currentDisplayLeft.get(i) + " - " + currentDisplayRight.get(i));
                    }

                    System.out.println("\nExample: leftWord rightWord");
                    System.out.println("Attempts remaining: " + attemptsRemaining);
                    System.out.print("Enter your guess: ");

                    guess = scanner.nextLine();

                    List<String> parsedGuesses = normalizeAndSplit(guess);

                    // 3. Check user's guess
                    if (parsedGuesses.size() != 2) {
                        System.out.println("Incorrect format. Please enter exactly two words separated by space (e.g., 'apple apfel').");
                        attemptsRemaining--;
                    } else {
                        String guess1 = parsedGuesses.get(0); // This is the left word
                        String guess2 = parsedGuesses.get(1); // This is the right word

                        // Check if guess1 is a key in our map AND if its corresponding value is guess2
                        if (remainingPairs.containsKey(guess1) && remainingPairs.get(guess1).equals(guess2)) {
                            System.out.println("CORRECT PAIR!");
                            remainingPairs.remove(guess1); // Remove the matched pair from the map
                            if (attemptsRemaining < 3) { // Reward correct guesses
                                attemptsRemaining++;
                            }
                        } else if (remainingPairs.containsKey(guess2) && remainingPairs.get(guess2).equals(guess1)) {
                            System.out.println("CORRECT PAIR!");
                            remainingPairs.remove(guess2);
                            if (attemptsRemaining < 3) {
                                attemptsRemaining++;
                            }
                        } else {
                            System.out.println("INCORRECT PAIR or words not found! Try again.");
                            attemptsRemaining--;
                        }
                    }
                }

                // 4. Determine the final result of the test
                if (remainingPairs.isEmpty()) {
                    result = true;
                    System.out.println("Good Job! You've matched all pairs.");
                } else {
                    System.out.println("You ran out of attempts or couldn't match all pairs.");
                    System.out.println("Remaining pairs: " + remainingPairs); // Show what was left
                }
                break;
            case 4:
                result = true;
                question = currTest.getPropozitie();
                System.out.println(question);
                break;
            default:
                System.out.println("Unknown test type. Please contact our support!");
                result = true;
                break;
        }
        // Hide previous answer sufficiently to prevent cheating and cluttering
        System.out.println("Input anything to continue.");
        scanner.nextLine();
        // Blank out terminal
        System.out.println(blackout);

        return result;
    }

    private String normalizeString(String text) {
        // 1. Normalizează la forma NFC (Canonical Composition) pentru a gestiona reprezentările Unicode
        String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFC);

        // 2. Converteste la litere mici, independent de locație
        normalizedText = normalizedText.toLowerCase(Locale.ROOT);

        // 3. Înlocuiește caracterele românești cu echivalentele lor simple
        normalizedText = normalizedText.replace('ă', 'a');
        normalizedText = normalizedText.replace('î', 'i');
        normalizedText = normalizedText.replace('ș', 's');
        normalizedText = normalizedText.replace('ț', 't');
        normalizedText = normalizedText.replace('â', 'a'); // Nu uita de 'â'

        return normalizedText;
    }

    private List<String> normalizeAndSplit(String text) {
        String normalizedText = normalizeString(text);
        String[] rawWords = normalizedText.toLowerCase().split("[\\s,.?!]+");
        List<String> words = new ArrayList<>();
        for (String word : rawWords) {
            String trimmedWord = word.trim();
            if (!trimmedWord.isEmpty()) {
                words.add(trimmedWord);
            }
        }
        return words;
    }

    private boolean answerCompare(String answer, String correctAnswer) {
        // Get a list of raw words for both question and correctAnswer
        // Afterward, compare size. If size matches, make sure each word is where it should be.
        // Otherwise, print the correct answer, "INCORRECT => correctAnswer" and return false
        boolean result = true;
        List<String> wordsCorrAnswer = normalizeAndSplit(correctAnswer);
        List<String> wordsAnswer = normalizeAndSplit(answer);
        if (!wordsCorrAnswer.equals(wordsAnswer)) {
            result = false;
        }
        if (!result) {
            System.out.println("INCORRECT!\nCorrect answer: " + correctAnswer);
        } else {
            System.out.println("CORRECT!");
        }

        return result;
    }
}
