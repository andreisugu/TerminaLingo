// TerminaLingo Web Application
// Main application state
const app = {
    currentState: 'AUTH',
    loggedUser: null,
    currentLanguage: null,
    currentChapter: null,
    currentLesson: null,
    lessonProgress: {},
    awaitingInput: false,
    inputCallback: null,
    lessons: {},
};

// Storage keys
const STORAGE_KEYS = {
    USERS: 'terminalingo_users',
    CURRENT_USER: 'terminalingo_current_user',
};

// DOM elements
let terminalOutput;
let terminalInput;

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
    terminalOutput = document.getElementById('terminal-output');
    terminalInput = document.getElementById('terminal-input');
    
    terminalInput.addEventListener('keypress', handleInput);
    
    // Clear initial loading message
    terminalOutput.innerHTML = '';
    
    // Load lessons data
    loadLessons();
    
    // Start the application
    showAuthMenu();
});

// Utility functions
function clearTerminal() {
    terminalOutput.innerHTML = '';
}

function printLine(text, className = '') {
    const line = document.createElement('div');
    line.className = `output-line ${className}`;
    line.textContent = text;
    terminalOutput.appendChild(line);
    terminalOutput.scrollTop = terminalOutput.scrollHeight;
}

function printBlankLine() {
    printLine('');
}

function getUserInput(callback) {
    app.awaitingInput = true;
    app.inputCallback = callback;
    terminalInput.disabled = false;
    terminalInput.focus();
}

function handleInput(e) {
    if (e.key === 'Enter' && app.awaitingInput) {
        e.preventDefault();
        const input = terminalInput.value.trim();
        
        // Echo user input
        printLine(`$ ${input}`, 'user-input');
        
        terminalInput.value = '';
        app.awaitingInput = false;
        
        if (app.inputCallback) {
            const callback = app.inputCallback;
            app.inputCallback = null;
            callback(input);
        }
    }
}

// User Authentication
function showAuthMenu() {
    app.currentState = 'AUTH';
    printLine('Welcome to TerminaLingo!', 'info');
    printLine('=====================================');
    printBlankLine();
    printLine('1) Register Account');
    printLine('2) Login');
    printLine('3) Exit Program');
    printBlankLine();
    getUserInput(handleAuthChoice);
}

function handleAuthChoice(choice) {
    switch (choice) {
        case '1':
            registerAccount();
            break;
        case '2':
            loginAccount();
            break;
        case '3':
            printLine('Thank you for using TerminaLingo. Goodbye!', 'info');
            terminalInput.disabled = true;
            break;
        default:
            printLine('Invalid choice. Please try again.', 'error');
            showAuthMenu();
            break;
    }
}

function registerAccount() {
    printLine('Register New Account', 'info');
    printLine('-------------------');
    printLine('Username:');
    getUserInput((username) => {
        if (!username) {
            printLine('Username cannot be empty.', 'error');
            showAuthMenu();
            return;
        }
        
        const users = getUsers();
        if (users[username]) {
            printLine('Username already exists. Please choose another.', 'error');
            showAuthMenu();
            return;
        }
        
        printLine('Password:');
        getUserInput((password) => {
            if (!password) {
                printLine('Password cannot be empty.', 'error');
                showAuthMenu();
                return;
            }
            
            // Create new user
            users[username] = {
                username: username,
                password: password, // In production, this should be hashed
                lessonsCompleted: [],
                learnedWords: [],
                dailyLoginStreak: 0,
                lastLoginDate: getCurrentDate(),
                lessonsTotal: 0
            };
            
            saveUsers(users);
            printLine('Account created successfully!', 'success');
            printBlankLine();
            showAuthMenu();
        });
    });
}

function loginAccount() {
    printLine('Login', 'info');
    printLine('-----');
    printLine('Username:');
    getUserInput((username) => {
        if (!username) {
            printLine('Username cannot be empty.', 'error');
            showAuthMenu();
            return;
        }
        
        const users = getUsers();
        if (!users[username]) {
            printLine('User not found.', 'error');
            showAuthMenu();
            return;
        }
        
        printLine('Password:');
        getUserInput((password) => {
            if (users[username].password !== password) {
                printLine('Incorrect password.', 'error');
                showAuthMenu();
                return;
            }
            
            // Update login streak
            users[username] = updateLoginStreak(users[username]);
            saveUsers(users);
            
            app.loggedUser = users[username];
            localStorage.setItem(STORAGE_KEYS.CURRENT_USER, username);
            
            printLine(`Welcome back, ${username}!`, 'success');
            printLine(`Login streak: ${app.loggedUser.dailyLoginStreak} days`, 'info');
            printBlankLine();
            
            showLanguageMenu();
        });
    });
}

// Language and Lesson Selection
function showLanguageMenu() {
    app.currentState = 'LANGUAGE_SELECT';
    printLine('Select a Language:', 'info');
    printLine('=================');
    printBlankLine();
    
    const languages = Object.keys(app.lessons);
    languages.forEach((lang, index) => {
        printLine(`${index + 1}) ${lang}`);
    });
    
    printBlankLine();
    printLine('Special Commands:');
    printLine('S - Show Statistics');
    printLine('L - Logout');
    printLine('E - Exit');
    printBlankLine();
    
    getUserInput((choice) => {
        if (choice.toUpperCase() === 'S') {
            showStatistics();
        } else if (choice.toUpperCase() === 'L') {
            logout();
        } else if (choice.toUpperCase() === 'E') {
            printLine('Thank you for using TerminaLingo. Goodbye!', 'info');
            terminalInput.disabled = true;
        } else {
            const langIndex = parseInt(choice) - 1;
            if (langIndex >= 0 && langIndex < languages.length) {
                app.currentLanguage = languages[langIndex];
                showChapterMenu();
            } else {
                printLine('Invalid choice. Please try again.', 'error');
                showLanguageMenu();
            }
        }
    });
}

function showChapterMenu() {
    printLine(`Language: ${app.currentLanguage}`, 'info');
    printLine('Select a Chapter:', 'info');
    printLine('================');
    printBlankLine();
    
    const chapters = Object.keys(app.lessons[app.currentLanguage]);
    chapters.forEach((chapter, index) => {
        printLine(`${index + 1}) ${chapter}`);
    });
    
    printBlankLine();
    printLine('B/0 - Go Back | S - Statistics | L - Logout | E - Exit');
    printBlankLine();
    
    getUserInput((choice) => {
        if (choice.toUpperCase() === 'B' || choice === '0') {
            showLanguageMenu();
        } else if (choice.toUpperCase() === 'S') {
            showStatistics();
        } else if (choice.toUpperCase() === 'L') {
            logout();
        } else if (choice.toUpperCase() === 'E') {
            printLine('Thank you for using TerminaLingo. Goodbye!', 'info');
            terminalInput.disabled = true;
        } else {
            const chapterIndex = parseInt(choice) - 1;
            if (chapterIndex >= 0 && chapterIndex < chapters.length) {
                app.currentChapter = chapters[chapterIndex];
                showLessonMenu();
            } else {
                printLine('Invalid choice. Please try again.', 'error');
                showChapterMenu();
            }
        }
    });
}

function showLessonMenu() {
    printLine(`Language: ${app.currentLanguage} > ${app.currentChapter}`, 'info');
    printLine('Select a Lesson:', 'info');
    printLine('===============');
    printBlankLine();
    
    const lessons = app.lessons[app.currentLanguage][app.currentChapter];
    lessons.forEach((lesson, index) => {
        const completed = isLessonCompleted(lesson.path);
        const status = completed ? '✓' : ' ';
        printLine(`${index + 1}) [${status}] ${lesson.name} (${lesson.difficulty})`);
    });
    
    printBlankLine();
    printLine('B/0 - Go Back | S - Statistics | L - Logout | E - Exit');
    printBlankLine();
    
    getUserInput((choice) => {
        if (choice.toUpperCase() === 'B' || choice === '0') {
            showChapterMenu();
        } else if (choice.toUpperCase() === 'S') {
            showStatistics();
        } else if (choice.toUpperCase() === 'L') {
            logout();
        } else if (choice.toUpperCase() === 'E') {
            printLine('Thank you for using TerminaLingo. Goodbye!', 'info');
            terminalInput.disabled = true;
        } else {
            const lessonIndex = parseInt(choice) - 1;
            if (lessonIndex >= 0 && lessonIndex < lessons.length) {
                startLesson(lessons[lessonIndex]);
            } else {
                printLine('Invalid choice. Please try again.', 'error');
                showLessonMenu();
            }
        }
    });
}

// Lesson execution
function startLesson(lesson) {
    app.currentLesson = lesson;
    app.lessonProgress = {
        tests: [...lesson.tests],
        correctStreak: 0,
        totalTests: lesson.tests.length,
        correctAnswers: 0
    };
    
    printBlankLine();
    printLine('='.repeat(50), 'info');
    printLine(`Starting Lesson: ${lesson.name}`, 'info');
    printLine(`Difficulty: ${lesson.difficulty}`, 'info');
    printLine('='.repeat(50), 'info');
    printBlankLine();
    
    runNextTest();
}

function runNextTest() {
    if (app.lessonProgress.tests.length === 0 || app.lessonProgress.correctStreak >= 5) {
        finishLesson();
        return;
    }
    
    const test = app.lessonProgress.tests.shift();
    executeTest(test);
}

function executeTest(test) {
    printLine('-'.repeat(50));
    
    switch (test.tip) {
        case 1: // Translation with correct answer provided
        case 2: // Translation
            printLine(`Translate: ${test.propozitie}`);
            printLine('Your answer:');
            getUserInput((answer) => checkAnswer(answer, test.raspunsCorect, test));
            break;
            
        case 3: // Word matching
            printLine('Match the words:');
            printBlankLine();
            test.leftWords.forEach((word, idx) => {
                printLine(`${idx + 1}. ${word}`);
            });
            printBlankLine();
            printLine('With:');
            printBlankLine();
            const shuffled = [...test.rightWords].sort(() => Math.random() - 0.5);
            shuffled.forEach((word, idx) => {
                printLine(`${String.fromCharCode(65 + idx)}. ${word}`);
            });
            printBlankLine();
            printLine('Enter your answers (e.g., 1A 2B 3C):');
            getUserInput((answer) => checkMatchingAnswer(answer, test.leftWords, test.rightWords, test));
            break;
            
        case 4: // Information display
            printLine(test.propozitie, 'info');
            printBlankLine();
            printLine('Press Enter to continue...');
            getUserInput(() => {
                app.lessonProgress.correctStreak++;
                runNextTest();
            });
            break;
            
        default:
            printLine('Unknown test type. Skipping...', 'warning');
            runNextTest();
            break;
    }
}

function normalizeText(text) {
    return text.toLowerCase()
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .replace(/[.,\/#!$%\^&\*;:{}=\-_`~()?]/g, '')
        .trim();
}

function checkAnswer(userAnswer, correctAnswer, test) {
    const normalized = normalizeText(userAnswer);
    const normalizedCorrect = normalizeText(correctAnswer);
    
    if (normalized === normalizedCorrect) {
        printLine('✓ Correct!', 'success');
        app.lessonProgress.correctStreak++;
        app.lessonProgress.correctAnswers++;
        
        // Add learned words
        if (app.currentLesson.cuvinteInvatate) {
            app.currentLesson.cuvinteInvatate.forEach(word => {
                if (!app.loggedUser.learnedWords.includes(word)) {
                    app.loggedUser.learnedWords.push(word);
                }
            });
        }
    } else {
        printLine('✗ Incorrect', 'error');
        printLine(`Correct answer: ${correctAnswer}`, 'warning');
        app.lessonProgress.correctStreak = Math.max(0, app.lessonProgress.correctStreak - 1);
        
        // Add test back to the queue
        app.lessonProgress.tests.push(test);
    }
    
    printBlankLine();
    setTimeout(runNextTest, 500);
}

function checkMatchingAnswer(userAnswer, leftWords, rightWords, test) {
    const pairs = userAnswer.toUpperCase().split(' ');
    let allCorrect = true;
    
    for (let i = 0; i < leftWords.length; i++) {
        const expectedPair = `${i + 1}${String.fromCharCode(65 + i)}`;
        if (!pairs.includes(expectedPair)) {
            allCorrect = false;
            break;
        }
    }
    
    if (allCorrect && pairs.length === leftWords.length) {
        printLine('✓ All matches correct!', 'success');
        app.lessonProgress.correctStreak++;
        app.lessonProgress.correctAnswers++;
    } else {
        printLine('✗ Some matches are incorrect', 'error');
        printLine('Correct matches:', 'warning');
        leftWords.forEach((left, idx) => {
            printLine(`  ${left} = ${rightWords[idx]}`);
        });
        app.lessonProgress.correctStreak = Math.max(0, app.lessonProgress.correctStreak - 1);
        
        // Add test back to the queue
        app.lessonProgress.tests.push(test);
    }
    
    printBlankLine();
    setTimeout(runNextTest, 500);
}

function finishLesson() {
    const score = Math.round((app.lessonProgress.correctAnswers / app.lessonProgress.totalTests) * 100);
    
    printLine('='.repeat(50), 'info');
    printLine('Lesson Complete!', 'success');
    printLine('='.repeat(50), 'info');
    printBlankLine();
    printLine(`Score: ${score}%`, 'info');
    printLine(`Correct answers: ${app.lessonProgress.correctAnswers}/${app.lessonProgress.totalTests}`, 'info');
    printBlankLine();
    
    // Update user progress
    const lessonPath = app.currentLesson.path;
    const existingProgress = app.loggedUser.lessonsCompleted.find(p => p.lessonPath === lessonPath);
    
    if (existingProgress) {
        existingProgress.score = Math.max(existingProgress.score, score);
    } else {
        app.loggedUser.lessonsCompleted.push({
            lessonPath: lessonPath,
            score: score
        });
        app.loggedUser.lessonsTotal++;
    }
    
    // Save progress
    const users = getUsers();
    users[app.loggedUser.username] = app.loggedUser;
    saveUsers(users);
    
    printLine('Progress saved!', 'success');
    printBlankLine();
    printLine('Press Enter to continue...');
    getUserInput(() => showLessonMenu());
}

// Statistics
function showStatistics() {
    printBlankLine();
    printLine('='.repeat(50), 'info');
    printLine('Your Statistics', 'info');
    printLine('='.repeat(50), 'info');
    printBlankLine();
    printLine(`Username: ${app.loggedUser.username}`);
    printLine(`Login Streak: ${app.loggedUser.dailyLoginStreak} days`);
    printLine(`Lessons Completed: ${app.loggedUser.lessonsTotal}`);
    printLine(`Unique Words Learned: ${app.loggedUser.learnedWords.length}`);
    printBlankLine();
    printLine('Press Enter to continue...');
    getUserInput(() => {
        if (app.currentState === 'LANGUAGE_SELECT') {
            showLanguageMenu();
        } else {
            showLanguageMenu();
        }
    });
}

function logout() {
    printLine('Logging out...', 'info');
    app.loggedUser = null;
    localStorage.removeItem(STORAGE_KEYS.CURRENT_USER);
    printBlankLine();
    showAuthMenu();
}

// Helper functions
function getUsers() {
    const usersJson = localStorage.getItem(STORAGE_KEYS.USERS);
    return usersJson ? JSON.parse(usersJson) : {};
}

function saveUsers(users) {
    localStorage.setItem(STORAGE_KEYS.USERS, JSON.stringify(users));
}

function getCurrentDate() {
    const now = new Date();
    return `${now.getDate().toString().padStart(2, '0')}${(now.getMonth() + 1).toString().padStart(2, '0')}${now.getFullYear()}`;
}

function updateLoginStreak(user) {
    const today = getCurrentDate();
    
    if (user.lastLoginDate === today) {
        return user;
    }
    
    const lastDate = new Date(
        user.lastLoginDate.substring(4),
        user.lastLoginDate.substring(2, 4) - 1,
        user.lastLoginDate.substring(0, 2)
    );
    
    const todayDate = new Date();
    const diffTime = Math.abs(todayDate - lastDate);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 1) {
        user.dailyLoginStreak++;
    } else if (diffDays > 1) {
        user.dailyLoginStreak = 1;
    }
    
    user.lastLoginDate = today;
    return user;
}

function isLessonCompleted(lessonPath) {
    if (!app.loggedUser) return false;
    return app.loggedUser.lessonsCompleted.some(p => p.lessonPath === lessonPath);
}

// Load lessons data
function loadLessons() {
    // Sample lessons data structure
    app.lessons = {
        'English (Romanian)': {
            'Basic': [
                {
                    name: 'Lecția 1: Fraze Comune',
                    difficulty: 'Ușor',
                    path: 'English (Romanian)/Basic/Lectie1',
                    cuvinteInvatate: ['bună dimineața', 'bună seara', 'noapte bună', 'cum ești?', 'sunt bine', 'mulțumesc', 'cu plăcere', 'scuze', 'da', 'nu'],
                    tests: [
                        {
                            tip: 1,
                            propozitie: 'Bună dimineața, cum te simți?',
                            raspunsCorect: 'Good morning, how do you feel?'
                        },
                        {
                            tip: 1,
                            propozitie: 'Mulțumesc mult pentru ajutor.',
                            raspunsCorect: 'Thank you very much for your help.'
                        },
                        {
                            tip: 2,
                            propozitie: 'How are you today?',
                            raspunsCorect: 'Cum ești azi?'
                        },
                        {
                            tip: 2,
                            propozitie: 'You are welcome.',
                            raspunsCorect: 'Cu plăcere.'
                        },
                        {
                            tip: 3,
                            leftWords: ['masă', 'scaun', 'carte', 'apă', 'pâine'],
                            rightWords: ['table', 'chair', 'book', 'water', 'bread']
                        },
                        {
                            tip: 4,
                            propozitie: "Example usage of 'hello': 'Hello, how are you?' (Bună, cum ești?)"
                        }
                    ]
                }
            ]
        },
        'Dutch': {
            'Advanced': [
                {
                    name: 'Introducere in Saluturi',
                    difficulty: 'Usor',
                    path: 'Dutch/Advanced/Ez',
                    cuvinteInvatate: ['hello', 'goodbye', 'please', 'thank you'],
                    tests: [
                        {
                            tip: 1,
                            propozitie: 'Salut, cum te simti azi?',
                            raspunsCorect: 'Hello, how do you feel today?'
                        },
                        {
                            tip: 2,
                            propozitie: 'The food is delicious.',
                            raspunsCorect: 'Mancarea este delicioasa.'
                        },
                        {
                            tip: 3,
                            leftWords: ['apple', 'house', 'car'],
                            rightWords: ['Apfel', 'Haus', 'Auto']
                        },
                        {
                            tip: 4,
                            propozitie: "Example usage of 'thank you': 'Mulțumesc pentru ajutor!'"
                        }
                    ]
                }
            ]
        }
    };
}
