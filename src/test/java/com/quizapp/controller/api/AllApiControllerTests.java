package com.quizapp.controller.api;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All API Controller Tests Suite")
@SelectClasses({
        EmailApiControllerTest.class,
        QuizAttemptApiControllerTest.class,
        AuthApiControllerTest.class
        // Removed QuizApiControllerTest.class since it was deleted
})
public class AllApiControllerTests {
    // Test suite that runs all API controller tests
}