package com.quizapp.config;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All Configuration Tests Suite")
@SelectClasses({
        StringToListConverterTest.class,
        WebConfigTest.class,
        SwaggerConfigTest.class,
        SchedulerConfigTest.class,
        SessionTimeoutConfigTest.class,
        SecurityConfigTest.class
})
public class AllConfigTests {
    // Test suite that runs all configuration tests
}