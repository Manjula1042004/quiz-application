// File: src/main/java/com/quizapp/config/DataInitializer.java
package com.quizapp.config;

import com.quizapp.entity.*;
import com.quizapp.repository.QuizRepository;
import com.quizapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;


    /**
     * Creates all 4 quizzes with 30 questions each if they don't exist
     */

    private void createFullStackQuiz(User admin) {
        Quiz quiz = new Quiz();
        quiz.setTitle("Full Stack Development");
        quiz.setDescription("Comprehensive quiz covering frontend, backend, databases, and DevOps concepts with 30 questions");
        quiz.setTimeLimit(60);
        quiz.setCreatedBy(admin);
        quiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        quiz.setIsPublic(true); // ← MUST BE TRUE
        quiz.setEnabled(true);  // ← MUST BE TRUE
        quiz.setIsTemplate(false); // ← MUST BE FALSE
        quiz.setCreatedAt(LocalDateTime.now());

        quiz.setIsPublic(true);      // ← MUST be true
        quiz.setEnabled(true);       // ← MUST be true
        quiz.setIsTemplate(false);


        List<Question> questions = new ArrayList<>();

        // HTML/CSS Questions (10 questions)
        questions.add(createQuestion("Which HTML5 element is used for navigation links?",
                Arrays.asList("<nav>", "<navigation>", "<menu>", "<links>"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What does CSS stand for?",
                Arrays.asList("Cascading Style Sheets", "Creative Style System", "Computer Style Sheets", "Colorful Style Sheets"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which HTML tag is used for the largest heading?",
                Arrays.asList("<h6>", "<heading>", "<h1>", "<head>"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which CSS property is used to change text color?",
                Arrays.asList("text-color", "font-color", "color", "text-style"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which HTML attribute specifies an alternate text for an image?",
                Arrays.asList("src", "title", "alt", "href"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("How do you select an element with id 'demo' in CSS?",
                Arrays.asList(".demo", "#demo", "demo", "*demo"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which HTML element is used to define important text?",
                Arrays.asList("<strong>", "<b>", "<i>", "<important>"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is the default display value for a <div> element?",
                Arrays.asList("inline", "block", "flex", "grid"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which CSS property controls the text size?",
                Arrays.asList("text-style", "font-size", "text-size", "font-style"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which HTML element is used to create a hyperlink?",
                Arrays.asList("<link>", "<a>", "<href>", "<hyperlink>"), 1, DifficultyLevel.EASY, quiz));

        // JavaScript Questions (10 questions)
        questions.add(createQuestion("What is the output of: console.log(typeof null)?",
                Arrays.asList("null", "undefined", "object", "number"), 2, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which method creates a new array with results of calling a function?",
                Arrays.asList("forEach()", "map()", "filter()", "reduce()"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What does '===' operator do in JavaScript?",
                Arrays.asList("Assigns value", "Compares value and type", "Compares value only", "Checks inequality"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which method adds new elements to the end of an array?",
                Arrays.asList("push()", "pop()", "shift()", "unshift()"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What keyword is used to declare a variable in modern JavaScript?",
                Arrays.asList("var", "let", "const", "variable"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which built-in method returns the length of a string?",
                Arrays.asList("length()", "size()", "index()", "getLength()"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What does JSON stand for?",
                Arrays.asList("JavaScript Object Notation", "JavaScript Online Network", "Java Source Object Notation", "JavaScript Oriented Notation"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which method removes the last element from an array?",
                Arrays.asList("push()", "pop()", "shift()", "removeLast()"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is the purpose of 'use strict' in JavaScript?",
                Arrays.asList("Makes code run faster", "Enforces stricter parsing and error handling", "Allows using newer syntax", "Makes variables global"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which method converts JSON string to JavaScript object?",
                Arrays.asList("JSON.stringify()", "JSON.parse()", "JSON.convert()", "JSON.toObject()"), 1, DifficultyLevel.MEDIUM, quiz));

        // React Questions (5 questions)
        questions.add(createQuestion("In React, what is used to pass data to a component?",
                Arrays.asList("state", "props", "context", "hooks"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which hook is used to manage state in functional components?",
                Arrays.asList("useEffect", "useState", "useContext", "useReducer"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is the correct syntax to write a React component?",
                Arrays.asList("function MyComponent() {}", "class MyComponent extends Component {}", "const MyComponent = () => {}", "All of the above"), 3, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which method is called when a component is first rendered?",
                Arrays.asList("componentDidMount", "componentWillMount", "render", "constructor"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What does JSX stand for?",
                Arrays.asList("JavaScript XML", "Java Syntax Extension", "JavaScript Extension", "Java XML"), 0, DifficultyLevel.MEDIUM, quiz));

        // Backend/API Questions (5 questions)
        questions.add(createQuestion("What is the purpose of CORS in web development?",
                Arrays.asList("To cache responses", "To secure database connections", "To allow cross-origin requests", "To compress data"), 2, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("Which HTTP method is used to update data?",
                Arrays.asList("GET", "POST", "PUT", "DELETE"), 2, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is REST API?",
                Arrays.asList("A programming language", "An architectural style for APIs", "A database system", "A testing framework"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which status code indicates success?",
                Arrays.asList("200", "404", "500", "301"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What does JWT stand for in authentication?",
                Arrays.asList("Java Web Token", "JSON Web Token", "JavaScript Web Token", "Java Web Tool"), 1, DifficultyLevel.MEDIUM, quiz));

        quiz.setQuestions(questions);
        quizRepository.save(quiz);
        System.out.println("✓ Created 'Full Stack Development' quiz with " + questions.size() + " questions");
    }

    @Transactional
    private void createJavaProgrammingQuiz(User admin) {
        Quiz quiz = new Quiz();
        quiz.setTitle("Java Programming");
        quiz.setDescription("Comprehensive Java programming quiz with 30 questions covering fundamentals to advanced concepts");
        quiz.setTimeLimit(60);
        quiz.setCreatedBy(admin);
        quiz.setDifficultyLevel(DifficultyLevel.MEDIUM);
        quiz.setIsPublic(true);
        quiz.setEnabled(true);
        quiz.setCreatedAt(LocalDateTime.now());

        List<Question> questions = new ArrayList<>();

        // Java Basics (10 questions)
        questions.add(createQuestion("Which keyword is used to define a constant in Java?",
                Arrays.asList("const", "final", "static", "constant"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is the default value of a boolean variable in Java?",
                Arrays.asList("true", "false", "null", "0"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which is NOT a primitive data type in Java?",
                Arrays.asList("int", "String", "boolean", "char"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is the size of int in Java?",
                Arrays.asList("8 bits", "16 bits", "32 bits", "64 bits"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which keyword is used to create an instance of a class?",
                Arrays.asList("this", "new", "instance", "class"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is the parent class of all Java classes?",
                Arrays.asList("Object", "Main", "Class", "Root"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which operator is used to compare two values?",
                Arrays.asList("=", "==", "!=", "&&"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is the output of: System.out.println(5 + '5')?",
                Arrays.asList("55", "10", "58", "Error"), 2, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which loop is guaranteed to execute at least once?",
                Arrays.asList("for loop", "while loop", "do-while loop", "for-each loop"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What does JVM stand for?",
                Arrays.asList("Java Virtual Machine", "Java Variable Method", "Java Visual Manager", "Java Verified Module"), 0, DifficultyLevel.EASY, quiz));

        // OOP Concepts (10 questions)
        questions.add(createQuestion("Which concept allows a class to have multiple methods with same name?",
                Arrays.asList("Encapsulation", "Inheritance", "Polymorphism", "Abstraction"), 2, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which keyword is used for inheritance?",
                Arrays.asList("inherits", "extends", "implements", "super"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is abstraction?",
                Arrays.asList("Hiding implementation details", "Reusing code", "Multiple forms", "Data hiding"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which access modifier provides the widest accessibility?",
                Arrays.asList("private", "protected", "default", "public"), 3, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Can we override static methods in Java?",
                Arrays.asList("Yes", "No", "Only in subclasses", "Only with same parameters"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which keyword refers to current class instance?",
                Arrays.asList("this", "super", "self", "current"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is constructor chaining?",
                Arrays.asList("Calling one constructor from another", "Creating multiple constructors", "Destroying constructors", "Inheriting constructors"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is method overloading?",
                Arrays.asList("Same method name, different parameters", "Different method name, same parameters", "Same method in parent and child", "Method with no parameters"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which keyword prevents method overriding?",
                Arrays.asList("static", "final", "private", "abstract"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is an interface?",
                Arrays.asList("A blueprint of a class", "A collection of abstract methods", "A type of class", "A method modifier"), 1, DifficultyLevel.MEDIUM, quiz));

        // Collections (5 questions)
        questions.add(createQuestion("Which collection class allows duplicate elements?",
                Arrays.asList("Set", "List", "Map", "Queue"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which interface does ArrayList implement?",
                Arrays.asList("Set", "List", "Map", "Collection"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which collection doesn't allow duplicates?",
                Arrays.asList("ArrayList", "LinkedList", "HashSet", "Vector"), 2, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is the default capacity of ArrayList?",
                Arrays.asList("5", "10", "15", "20"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which map implementation maintains insertion order?",
                Arrays.asList("HashMap", "TreeMap", "LinkedHashMap", "HashTable"), 2, DifficultyLevel.MEDIUM, quiz));

        // Exception Handling (5 questions)
        questions.add(createQuestion("What is the parent class of all exceptions in Java?",
                Arrays.asList("Error", "RuntimeException", "Throwable", "Exception"), 2, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which keyword is used to handle exceptions?",
                Arrays.asList("throw", "throws", "try-catch", "exception"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What is finally block used for?",
                Arrays.asList("To handle exceptions", "To clean up resources", "To throw exceptions", "To declare exceptions"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is the difference between throw and throws?",
                Arrays.asList("No difference", "throw is for checked exceptions, throws for unchecked", "throw is used to throw exception, throws declares exception", "throw is keyword, throws is method"), 2, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("Which exception occurs when dividing by zero?",
                Arrays.asList("NullPointerException", "ArithmeticException", "ArrayIndexOutOfBounds", "NumberFormatException"), 1, DifficultyLevel.EASY, quiz));

        quiz.setQuestions(questions);
        quizRepository.save(quiz);
        System.out.println("✓ Created 'Java Programming' quiz with " + questions.size() + " questions");
    }

    @Transactional
    private void createWebDevelopmentQuiz(User admin) {
        Quiz quiz = new Quiz();
        quiz.setTitle("Web Development Basics");
        quiz.setDescription("Comprehensive web development fundamentals with 30 questions");
        quiz.setTimeLimit(60);
        quiz.setCreatedBy(admin);
        quiz.setDifficultyLevel(DifficultyLevel.EASY);
        quiz.setIsPublic(true);
        quiz.setEnabled(true);
        quiz.setCreatedAt(LocalDateTime.now());

        List<Question> questions = new ArrayList<>();

        // HTML Questions (15 questions)
        questions.add(createQuestion("What does HTML stand for?",
                Arrays.asList("Hyper Text Markup Language", "High Tech Modern Language",
                        "Hyper Transfer Markup Language", "Home Tool Markup Language"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag is used to create a hyperlink in HTML?",
                Arrays.asList("<link>", "<a>", "<href>", "<hyperlink>"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag is used for the largest heading?",
                Arrays.asList("<h1>", "<h6>", "<heading>", "<head>"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag inserts a line break?",
                Arrays.asList("<br>", "<lb>", "<break>", "<line>"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which attribute is used to provide alternative text for images?",
                Arrays.asList("title", "src", "alt", "href"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag defines a table row?",
                Arrays.asList("<td>", "<tr>", "<th>", "<table>"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag is used for an unordered list?",
                Arrays.asList("<ol>", "<ul>", "<li>", "<list>"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag is used to define an image?",
                Arrays.asList("<img>", "<image>", "<picture>", "<photo>"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag defines important text?",
                Arrays.asList("<strong>", "<b>", "<i>", "<em>"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag is used for a paragraph?",
                Arrays.asList("<para>", "<p>", "<paragraph>", "<text>"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag defines a form?",
                Arrays.asList("<form>", "<input>", "<submit>", "<button>"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which input type creates a checkbox?",
                Arrays.asList("checkbox", "check", "radio", "button"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag creates a drop-down list?",
                Arrays.asList("<list>", "<dropdown>", "<select>", "<option>"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag is used for a table header cell?",
                Arrays.asList("<td>", "<th>", "<thead>", "<header>"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which tag defines document metadata?",
                Arrays.asList("<head>", "<meta>", "<body>", "<title>"), 0, DifficultyLevel.EASY, quiz));

        // CSS Questions (15 questions)
        questions.add(createQuestion("Which CSS property is used to change the text color?",
                Arrays.asList("text-color", "font-color", "color", "text-style"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which property controls background color?",
                Arrays.asList("color", "bgcolor", "background-color", "bg-color"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("How do you center text using CSS?",
                Arrays.asList("text-align: center;", "align: center;", "center: true;", "text-center: true;"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which property adds space between elements?",
                Arrays.asList("spacing", "margin", "padding", "space"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which property adds space inside an element?",
                Arrays.asList("margin", "padding", "border", "spacing"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("How do you make text bold?",
                Arrays.asList("font-weight: bold;", "text-style: bold;", "bold: true;", "font: bold;"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which property changes font size?",
                Arrays.asList("text-size", "font-size", "size", "font-style"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("How do you remove underline from links?",
                Arrays.asList("text-decoration: none;", "underline: none;", "link-style: none;", "decoration: none;"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which property adds rounded corners?",
                Arrays.asList("border-radius", "corner-radius", "rounded", "border-style"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which value makes element invisible but occupies space?",
                Arrays.asList("display: none;", "visibility: hidden;", "opacity: 0;", "hidden: true;"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("How do you select element with class 'myClass'?",
                Arrays.asList("#myClass", ".myClass", "myClass", "*myClass"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which property controls element width?",
                Arrays.asList("width", "size", "length", "dimension"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("What does CSS box model include?",
                Arrays.asList("Margin, Border, Padding, Content", "Width, Height, Depth", "Top, Right, Bottom, Left", "Color, Size, Position"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which unit is relative to font size?",
                Arrays.asList("px", "em", "cm", "pt"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which property positions elements?",
                Arrays.asList("display", "position", "float", "layout"), 1, DifficultyLevel.MEDIUM, quiz));

        quiz.setQuestions(questions);
        quizRepository.save(quiz);
        System.out.println("✓ Created 'Web Development Basics' quiz with " + questions.size() + " questions");
    }

    @Transactional
    private void createDatabaseQuiz(User admin) {
        Quiz quiz = new Quiz();
        quiz.setTitle("Database Fundamentals");
        quiz.setDescription("Comprehensive database concepts and SQL queries with 30 questions");
        quiz.setTimeLimit(60);
        quiz.setCreatedBy(admin);
        quiz.setDifficultyLevel(DifficultyLevel.HARD);
        quiz.setIsPublic(true);
        quiz.setEnabled(true);
        quiz.setCreatedAt(LocalDateTime.now());

        List<Question> questions = new ArrayList<>();

        // SQL Basics (10 questions)
        questions.add(createQuestion("Which SQL command is used to retrieve data from a database?",
                Arrays.asList("GET", "SELECT", "RETRIEVE", "FETCH"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which SQL command is used to insert data?",
                Arrays.asList("ADD", "INSERT", "CREATE", "UPDATE"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which SQL command is used to update data?",
                Arrays.asList("MODIFY", "CHANGE", "UPDATE", "ALTER"), 2, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which SQL command is used to delete data?",
                Arrays.asList("REMOVE", "DELETE", "DROP", "ERASE"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which SQL command is used to create a table?",
                Arrays.asList("MAKE", "CREATE", "BUILD", "DEFINE"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which clause is used to filter records?",
                Arrays.asList("FILTER", "WHERE", "HAVING", "CONDITION"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which function returns the number of rows?",
                Arrays.asList("COUNT()", "SUM()", "AVG()", "MAX()"), 0, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which keyword sorts results in ascending order?",
                Arrays.asList("SORT", "ORDER BY", "ARRANGE", "GROUP BY"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which keyword eliminates duplicate rows?",
                Arrays.asList("UNIQUE", "DISTINCT", "DIFFERENT", "ONLY"), 1, DifficultyLevel.EASY, quiz));

        questions.add(createQuestion("Which operator searches for a pattern?",
                Arrays.asList("LIKE", "MATCH", "SIMILAR", "FIND"), 0, DifficultyLevel.EASY, quiz));

        // Database Concepts (10 questions)
        questions.add(createQuestion("What is the purpose of a primary key?",
                Arrays.asList("To improve performance", "To uniquely identify each record",
                        "To create relationships", "To sort data"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is a foreign key?",
                Arrays.asList("A primary key from another table", "A unique identifier", "An index", "A constraint"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is normalization?",
                Arrays.asList("Process of organizing data", "Making database faster", "Backing up data", "Securing database"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is denormalization?",
                Arrays.asList("Adding redundant data", "Removing tables", "Optimizing queries", "Creating indexes"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is an index?",
                Arrays.asList("A data structure that improves speed", "A table relationship", "A data type", "A constraint"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is a transaction?",
                Arrays.asList("A single operation", "A sequence of operations", "A table", "A query"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is a view?",
                Arrays.asList("A stored query", "A table copy", "An index", "A constraint"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is a stored procedure?",
                Arrays.asList("Precompiled SQL code", "A table", "A view", "An index"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is a trigger?",
                Arrays.asList("Automated response to events", "A query", "A table", "A constraint"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What is data integrity?",
                Arrays.asList("Accuracy and consistency of data", "Data security", "Data backup", "Data recovery"), 0, DifficultyLevel.MEDIUM, quiz));

        // Joins and Relationships (10 questions)
        questions.add(createQuestion("Which join returns all records when there is a match in either table?",
                Arrays.asList("INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL OUTER JOIN"), 3, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("Which join returns matching records from both tables?",
                Arrays.asList("INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL JOIN"), 0, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("Which join returns all records from left table?",
                Arrays.asList("INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL JOIN"), 1, DifficultyLevel.MEDIUM, quiz));

        questions.add(createQuestion("What does ACID stand for in database transactions?",
                Arrays.asList("Atomicity, Consistency, Isolation, Durability",
                        "Accuracy, Consistency, Integrity, Durability",
                        "Atomicity, Consistency, Integrity, Durability",
                        "Accuracy, Concurrency, Isolation, Durability"), 0, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("What is atomicity?",
                Arrays.asList("All or nothing", "Data consistency", "Transaction isolation", "Permanent changes"), 0, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("What is consistency?",
                Arrays.asList("Data validity", "Transaction speed", "Database size", "Backup frequency"), 0, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("What is isolation?",
                Arrays.asList("Transactions don't interfere", "Data security", "Network separation", "User access"), 0, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("What is durability?",
                Arrays.asList("Permanent changes", "Fast transactions", "Data backup", "System availability"), 0, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("What is deadlock?",
                Arrays.asList("Two transactions waiting for each other", "Database crash", "Network failure", "Data corruption"), 0, DifficultyLevel.HARD, quiz));

        questions.add(createQuestion("What is a clustered index?",
                Arrays.asList("Determines physical order of data", "Improves query speed", "Creates relationships", "Ensures uniqueness"), 0, DifficultyLevel.HARD, quiz));

        quiz.setQuestions(questions);
        quizRepository.save(quiz);
        System.out.println("✓ Created 'Database Fundamentals' quiz with " + questions.size() + " questions");
    }

    // Helper method to create questions
    private Question createQuestion(String text, List<String> options, int correctIndex,
                                    DifficultyLevel difficulty, Quiz quiz) {
        Question question = new Question();
        question.setQuestionText(text);
        question.setOptions(options);
        question.setCorrectAnswerIndex(correctIndex);
        question.setDifficultyLevel(difficulty);
        question.setQuiz(quiz);
        question.setPoints(1);
        return question;
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("\n🚀 =========================================");
        System.out.println("🚀 STARTING QUIZ INITIALIZATION");
        System.out.println("🚀 =========================================");

        // Count users
        long userCount = userRepository.count();
        System.out.println("👥 Total users in database: " + userCount);

        // Count quizzes
        long quizCount = quizRepository.count();
        System.out.println("📚 Total quizzes in database: " + quizCount);

        // Check if any user exists (any admin or participant)
        List<User> allUsers = userRepository.findAll();

        if (allUsers.isEmpty()) {
            System.out.println("\n⚠️ No users found in database.");
            System.out.println("📝 Please register at: http://localhost:8080/register");
            System.out.println("💡 First user to register will automatically become ADMIN");
        } else {
            System.out.println("\n✅ Found " + allUsers.size() + " user(s) in database");

            // Find first admin user to assign quizzes to
            Optional<User> adminOpt = allUsers.stream()
                    .filter(user -> user.getRole() == Role.ADMIN)
                    .findFirst();

            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();
                System.out.println("👑 Found admin: " + admin.getUsername() + " (ID: " + admin.getId() + ")");

                // Create quizzes if they don't exist
                createQuizzesIfNotExist(admin);
            } else {
                System.out.println("⚠️ No ADMIN user found.");
                System.out.println("💡 The first registered user automatically becomes ADMIN");
                System.out.println("📝 Please ask an admin to register first");

                // Use the first user anyway (they might become admin later)
                User firstUser = allUsers.get(0);
                System.out.println("👤 Using first user: " + firstUser.getUsername() + " (Role: " + firstUser.getRole() + ")");
                createQuizzesIfNotExist(firstUser);
            }
        }

        System.out.println("\n🎉 =========================================");
        System.out.println("🎉 APPLICATION READY!");
        System.out.println("🎉 =========================================");
        System.out.println("🔗 Application URL: http://localhost:8080");
        System.out.println("📝 Registration: http://localhost:8080/register");
        System.out.println("🔑 Login: http://localhost:8080/login");
        System.out.println("==========================================\n");
    }

    private void createQuizzesIfNotExist(User admin) {
        long quizCount = quizRepository.count();
        System.out.println("\n📚 Current quiz count in database: " + quizCount);

        if (quizCount == 0) {
            System.out.println("\n📚 Creating 4 quizzes with 30 questions each...");

            try {
                createFullStackQuiz(admin);
                createJavaProgrammingQuiz(admin);
                createWebDevelopmentQuiz(admin);
                createDatabaseQuiz(admin);

                System.out.println("✅ Successfully created 4 quizzes with 30 questions each");
                System.out.println("📊 Total: 4 quizzes × 30 questions = 120 questions");
            } catch (Exception e) {
                System.err.println("❌ Error creating quizzes: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("\n✅ Found " + quizCount + " existing quizzes");
            System.out.println("📝 Skipping quiz creation");
        }
    }

}