package src.tests;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class ServerTestsRunner {
    public static void main(String[] args) {
        System.out.println("STARTED TESTS");
        Result result = JUnitCore.runClasses(ServerTests.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }
        if (result.wasSuccessful())
            System.out.println("ALL TEST PASSED");
        else
            System.out.println("SOME TEST FAILED");
    }
}