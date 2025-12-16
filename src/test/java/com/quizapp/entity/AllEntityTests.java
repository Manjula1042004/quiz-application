package com.quizapp.entity;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All Entity Tests Suite")
@SelectClasses({
        UserEntityTest.class,
        QuizEntityTest.class,
        QuestionEntityTest.class,
        QuizAttemptEntityTest.class,
        CategoryEntityTest.class,
        TagEntityTest.class,
        DifficultyLevelTest.class,
        RoleTest.class,
        EmailVerificationTokenTest.class,
        PasswordResetTokenTest.class,
        TwoFactorAuthTest.class
})
public class AllEntityTests {
    // Test suite that runs all entity tests
}