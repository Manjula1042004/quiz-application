package com.quizapp.controller;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All Controller Tests Suite")
@SelectClasses({
        AttemptControllerTest.class,
        ProfileControllerTest.class,
        CategoryControllerTest.class,
        QuizControllerTest.class,
        AuthControllerTest.class,
        PasswordResetControllerTest.class,
        TestControllerTest.class,
        DashboardControllerTest.class,
        DebugControllerTest.class
})
public class AllControllerTests {
    // Test suite that runs all entity tests
}