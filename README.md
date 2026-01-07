# TerminaLingo: Interactive Language Learning CLI

## 🌐 Try it Online!

**[Launch TerminaLingo Web App](https://andreisugu.github.io/TerminaLingo/)** - No installation required! Try the web-based version directly in your browser.

## 📚 About TerminaLingo

TerminaLingo is an interactive language learning application available in two versions:
- **Web Version**: A terminal-style web application that runs directly in your browser (deployed on GitHub Pages)
- **CLI Version**: A console-based Java application for local use

Both versions provide a structured environment for users to practice vocabulary and translation skills, track their progress, and manage their learning journey.

## ✨ Key Features

* **User Management**: Secure registration and login system with password hashing (jBCrypt).

* **Progress Tracking**: Records completed lessons, scores, unique learned words, and daily login streaks.

* **Dynamic Lessons**: Loads multi-part lessons from customizable JSON files.

* **Varied Exercises**: Supports different test types, including:

  * Translation with provided word banks.

  * Free-form translation.

  * Word matching exercises.

  * Informative text displays.

* **Intuitive CLI**: Menu-driven navigation for selecting languages, chapters, and lessons.

* **Data Persistence**: All user data and progress are saved locally in JSON files (Gson).

* **Text Normalization**: Robust handling of accents and punctuation for accurate answer comparison.

## 🚀 Getting Started

### Option 1: Web Version (Recommended)

**No installation required!** Simply visit [https://andreisugu.github.io/TerminaLingo/](https://andreisugu.github.io/TerminaLingo/) in your web browser.

The web version features:
- Terminal-style interface that mimics the CLI experience
- Runs entirely in your browser
- Progress saved in browser local storage
- Works on desktop and mobile devices
- Includes sample lessons to get you started

### Option 2: Java CLI Version

To run TerminaLingo locally as a Java CLI application, follow these steps:

#### Prerequisites

* **Java Development Kit (JDK) 17 or newer**: Ensure JDK 17+ is installed and configured in your system's PATH. You can download it from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium (OpenJDK)](https://adoptium.net/).

* **Git** (optional, for cloning): If you prefer to clone the repository.

#### Installation & Setup

1. **Clone the repository (or download the source code):**

   ```
   git clone https://github.com/andreisugu/TerminaLingo/
   cd TerminaLingo
   ```

2. **Configure Lesson Content:**

   * Create a folder named `MasterLessons` in the root directory of the project (next to the `src` folder).

   * Inside `MasterLessons`, create subfolders for each language (e.g., `English`, `Romanian`).

   * Within each language folder, create subfolders for chapters (e.g., `Basics`, `Verbs`).

   * Place your lesson JSON files (e.g., `Lesson1.json`, `Lecția1.json`) inside these chapter folders. Refer to the Data Structure section below for JSON format examples.

   ```
   TerminaLingo/
   ├── src/
   │   └── ... (Java files)
   ├── MasterLessons/
   │   ├── English/
   │   │   ├── Basics/
   │   │   │   └── Lesson1.json
   │   │   └── Advanced/
   │   │       └── Lesson2.json
   │   └── Romanian/
   │       ├── Basics/
   │       │   └── Lecția1.json
   │       └── Verbs/
   │           └── Lecția2.json
   └── users.json (generated on first run)
   
   ```

3. **Dependencies:**
   TerminaLingo uses Google Gson for JSON processing and jBCrypt for password hashing. These are already configured in `pom.xml`.

#### Running the Java CLI Application

1. **Compile:**
   Open your terminal in the project's root directory (`TerminaLingo/`) and compile the Java files.

   * **With Maven:**

     ```
     mvn clean install
     
     ```

2. **Execute:**

   * **With Maven:**

     ```
     mvn exec:java -Dexec.mainClass="org.RestlessTech.TerminaLingo.Main"
     
     ```


## 🎮 Usage

Once the application is running, follow the on-screen prompts in your console:

### Authentication:

* Choose to **Register Account (1)** or **Login (2)**.

* Follow the prompts to enter your username and password.

### Navigation:

* After logging in, you'll enter the language selection menu.

* Enter the number corresponding to your desired language, chapter, or lesson.

### Special Commands (available in most selection menus):

* **B** (or **0**): Go back to the previous menu.

* **S**: Display your personal statistics (login streak, completed lessons, learned words). Press Enter to continue.

* **L**: Log out and return to the authentication screen.

* **E**: Exit the program.

### Lesson Progression:

* Each lesson presents various test types. Read the instructions carefully.

* Enter your answers in the console. The application will provide immediate feedback (correct/incorrect) and, if needed, the correct answer.

* Press Enter after each response to proceed.

## 📂 Project Structure

The project is organized into a single main Java package, `org.RestlessTech.TerminaLingo`, containing all core classes:

* `Main.java`: Application entry point.

* `InterfaceManager.java`: Handles the command-line interface, user navigation, and overall application flow.

* `AccountManager.java`: Manages user accounts, authentication, and user data persistence.

* `LessonParser.java`: Parses lesson JSON files and orchestrates the interactive testing logic.

* `User.java`: Data model for a user, including progress and learned words.

* `UserLessonProgress.java`: Records a user's score for a specific lesson.

* `Lesson.java`: Data model for a lesson, containing its name, difficulty, and a list of tests.

* `Test.java`: Data model for an individual test within a lesson, defining its type and content.

## 📝 Data Structure (JSON Examples)

### `users.json`

This file stores a list of `User` objects, including their progress.

```
[
  {
    "lessonsCompleted": [
      {
        "lessonPath": "MasterLessons\\Romanian\\Basics\\Lesson1.json",
        "score": 95
      }
    ],
    "learnedWords": [
      "salut",
      "bună"
    ],
    "userName": "testuser",
    "password": "$2a$10$abcdefghijklmnopqrstuvw.xyz1234567890", // Hashed password
    "dailyLoginStreak": 2,
    "lastLoginDate": "09072025",
    "lessonsTotal": 1
  }
]

```

### Lesson JSON File (e.g., `Lesson1.json`)

Lesson files are located in `MasterLessons/{Language}/{Chapter}/{LessonName}.json`. Each file represents a `Lesson` object and contains a list of `Test` objects.

```
{
  "numeLectie": "Introduction to Romanian",
  "dificultate": "Easy",
  "cuvinteInvatate": [
    "hello",
    "good",
    "day"
  ],
  "teste": [
    {
      "tip": 1,
      "propozitie": "Hello, how are you?",
      "raspunsCorect": "Salut, ce mai faci?",
      "leftWords": [],
      "rightWords": []
    },
    {
      "tip": 3,
      "propozitie": null,
      "raspunsCorect": null,
      "leftWords": ["dog", "cat"],
      "rightWords": ["câine", "pisică"]
    }
  ]
}

```

## 🌐 Web Version Details

### Architecture

The web version is a complete reimplementation of the CLI application using HTML, CSS, and JavaScript:

- **HTML/CSS**: Terminal-style interface with a dark theme and green text
- **JavaScript**: All application logic including user authentication, lesson parsing, and progress tracking
- **Local Storage**: User data and progress are stored in the browser's localStorage
- **GitHub Pages**: Automatically deployed using GitHub Actions

### Key Differences from Java Version

- **No Server Required**: Runs entirely in the browser
- **Client-side Storage**: Data persists in browser localStorage instead of JSON files
- **Sample Lessons**: Includes embedded sample lessons (does not load from MasterLessons folder)
- **Cross-platform**: Works on any device with a modern web browser

### Deployment

The web version is automatically deployed to GitHub Pages via GitHub Actions when changes are pushed to the main branch. The workflow file is located at `.github/workflows/deploy-pages.yml`.

To deploy your own version:
1. Fork this repository
2. Enable GitHub Pages in repository settings (Settings → Pages → Source: GitHub Actions)
3. Push changes to the main branch
4. Your site will be available at `https://yourusername.github.io/TerminaLingo/`

## 🔮 Future Enhancements

* ~~**Web Version**: Create a browser-based version deployed on GitHub Pages~~ ✅ **Completed**

* **Graphical User Interface (GUI)**: Transition from CLI to a more intuitive GUI (e.g., JavaFX, Swing).

* **Relational Database**: Replace JSON persistence with a robust database solution (e.g., MySQL, PostgreSQL) and ORM (e.g., Hibernate, JPA).

* **More Test Types**: Introduce new exercise formats (e.g., sentence completion, multiple choice, listening comprehension).

* **Lesson Customization**: Allow users to create or modify lessons.

* **Advanced Statistics**: Implement detailed progress tracking with visualizations.

* **Gamification**: Add game-like elements (badges, leaderboards) to enhance engagement.

* **Internationalization**: Support multiple interface languages.

* **Unit Tests**: Implement comprehensive automated unit tests for core logic.

* **Online Synchronization**: Enable user progress synchronization across devices.

## 🤝 Contributing

Contributions are welcome! If you have suggestions for improvements or new features, please open an [issue](https://www.google.com/search?q=https://github.com/YourUsername/TerminaLingo/issues) or submit a [pull request](https://www.google.com/search?q=https://github.com/YourUsername/TerminaLingo/pulls).

## 📄 License

This project is licensed under the GNUV3.0 - see the `LICENSE` file for details.
