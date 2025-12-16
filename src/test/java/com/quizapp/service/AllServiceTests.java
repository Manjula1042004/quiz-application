package com.quizapp.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All Service Tests Suite")
@SelectClasses({
        BulkImportServiceTest.class,
        CategoryServiceTest.class,
        CustomUserDetailsServiceTest.class,
        DebugServiceTest.class,
        EmailServiceTest.class,
        EmailVerificationServiceTest.class,
        PasswordResetServiceTest.class,
        QuestionServiceTest.class,
        QuizAttemptServiceTest.class,
        QuizServiceTest.class,
        TagServiceTest.class,
        TwoFactorAuthServiceTest.class,
        UserServiceTest.class
})
public class AllServiceTests {
    // Test suite that runs all service tests
}