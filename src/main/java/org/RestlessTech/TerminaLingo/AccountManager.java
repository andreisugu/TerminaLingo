package org.RestlessTech.TerminaLingo;

// Handles various files containing various users data
// - Creates new users
// - Deletes users
// - Logs users in (for now just says if they exist)
// - Add logs to selected user (lesson and score)
// - Add words learned to user's learned word list

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.mindrot.jbcrypt.BCrypt;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AccountManager {
    private final Gson gson;
    private final List<User> users;
    // File for users named "Users"
    String usersFileName = "users.json";
    Path usersFile;

    public AccountManager(Path masterPath) {
        usersFile = masterPath.resolve(usersFileName);
        // Configure Gson for pretty printing and LocalDate serialization/deserialization
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                // Register a custom adapter for LocalDate if you have issues with default serialization
                // Or ensure your Gson version handles java.time types by default (newer versions often do)
                // If you face issues, you can uncomment and implement a custom type adapter:
                // .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
        this.users = loadUsers();
    }

    public AccountManager() {
        this(Paths.get(""));
    }

    /// File Handling
    private List<User> loadUsers() {
        // In case there's no file
        if (!Files.exists(usersFile)) {
            System.out.println("No users file found. Creating new one.");
            return new ArrayList<>();
        }

        // If there's a file, then try to read from it
        try (FileReader reader = new FileReader(usersFile.toFile())) {
            Type userListType = new TypeToken<ArrayList<User>>() {
            }.getType();
            return gson.fromJson(reader, userListType);
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error loading users from JSON file: " + e.getMessage());
            return new ArrayList<>(); // Start a new file then
        }
    }

    public void saveUsers() {
        try (FileWriter writer = new FileWriter(usersFile.toFile())) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error saving users to JSON file: " + e.getMessage());
        }
    }

    /// Account Management Functions
    public User registerNewUser(String username, String password) {
        // Empty check
        if (username == null || password == null) {
            System.out.println("Invalid username or password. Please try again.");
            return null;
        }
        // Already exists check
        if (getUserByUsername(username).isPresent()) {
            System.out.println("Username already exists. Please try another one.");
            return null;
        }

        // Create a new user after having passed all checks
        // Encrypt password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User newUser = new User(username, hashedPassword);
        updateLastLoginDate(newUser);
        users.add(newUser);
        saveUsers();
        System.out.println("User " + username + " registered successfully.");
        return newUser;
    }

    public User loginUser(String username, String password) {
        // Empty check
        if (username == null || password == null) {
            System.out.println("Invalid username or password. Please try again.");
            return null;
        }
        // Doesn't exist check
        User existingUser;
        if (getUserByUsername(username).isEmpty()) {
            // We won't tell the user that the username doesn't exist for security reasons
            System.out.println("Invalid username or password. Please try again.");
            return null;
        } else {
            existingUser = getUserByUsername(username).get();
            // Check password
            if (BCrypt.checkpw(password, existingUser.getPassword())) {
                System.out.println("User " + username + " successfully logged in.");
                updateLastLoginDate(existingUser);
                saveUsers();
                return existingUser;
            } else {
                System.out.println("Invalid username or password. Please try again.");
                return null;
            }
        }
    }

    ///  Helper Methods
    // Get user by username
    private Optional<User> getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    // Update daily login streak
    private void updateLastLoginDate(User user) {
        // Get the current date
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        // Format the current date into a String
        String currentDate = today.format(formatter);
        String previousDate = yesterday.format(formatter);

        // Compare with Users last login date
        if (!Objects.equals(user.getLastLoginDate(), currentDate)) {
            // If the date the user has is of the previous day, then increment the streak.
            // Otherwise, set it to 1.
            if (Objects.equals(user.getLastLoginDate(), previousDate)) {
                user.incrementDailyLoginStreak();
            } else {
                user.setDailyLoginStreak(1);
            }
            user.setLastLoginDate(currentDate);
        }
    }
}
