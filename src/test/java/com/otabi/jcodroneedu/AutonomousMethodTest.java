package com.otabi.jcodroneedu;

import com.otabi.jcodroneedu.autonomous.AutonomousMethod;
import com.otabi.jcodroneedu.autonomous.AutonomousMethodRegistry;
import com.otabi.jcodroneedu.autonomous.examples.AvoidWallAutonomousMethod;
import com.otabi.jcodroneedu.autonomous.examples.KeepDistanceAutonomousMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for autonomous methods: AvoidWall and KeepDistance.
 * These tests verify that the autonomous methods can be executed and interact with the drone as expected.
 */
@DisplayName("Autonomous Method Tests")
class AutonomousMethodTest {

    private AutonomousMethod methodUnderTest;
    private Map<String, Integer> params;
    private TestDrone testDrone;

    @Nested
    @DisplayName("AvoidWallAutonomousMethod")
    class AvoidWallTests {
        @Test
        @DisplayName("Should execute avoidWall and use front range sensor")
        void shouldExecuteAvoidWall() {
            methodUnderTest = new AvoidWallAutonomousMethod();
            params = new HashMap<>();
            params.put("timeout", 2);
            params.put("distance", 50);
            testDrone = new TestDrone();
            methodUnderTest.execute(testDrone, params);

            List<String> commands = testDrone.getCommandHistory();
            assertFalse(commands.isEmpty(), "Command history should not be empty. Actual: " + commands);
            boolean controlOrHover = commands.stream().anyMatch(cmd -> cmd.contains("sendControl")) ||
                                     commands.stream().anyMatch(cmd -> cmd.contains("hover"));
            if (!controlOrHover) {
                System.out.println("Command history: " + commands);
            }
            assertTrue(controlOrHover, "Should send control or hover commands. Actual: " + commands);
        }
    }

    @Nested
    @DisplayName("KeepDistanceAutonomousMethod")
    class KeepDistanceTests {
        @Test
        @DisplayName("Should execute keepDistance and use front range sensor")
        void shouldExecuteKeepDistance() {
            methodUnderTest = new KeepDistanceAutonomousMethod();
            params = new HashMap<>();
            params.put("timeout", 2);
            params.put("distance", 60);
            testDrone = new TestDrone();
            methodUnderTest.execute(testDrone, params);

            List<String> commands = testDrone.getCommandHistory();
            assertFalse(commands.isEmpty(), "Command history should not be empty. Actual: " + commands);
            boolean controlOrHover = commands.stream().anyMatch(cmd -> cmd.contains("sendControl")) ||
                                     commands.stream().anyMatch(cmd -> cmd.contains("hover"));
            if (!controlOrHover) {
                System.out.println("Command history: " + commands);
            }
            assertTrue(controlOrHover, "Should send control or hover commands. Actual: " + commands);
        }
    }

    @Test
    @DisplayName("Registry should list only autonomous methods")
    void registryShouldListAutonomousMethods() {
        AutonomousMethodRegistry registry = AutonomousMethodRegistry.getInstance();
        List<String> methods = registry.listMethods();
        assertTrue(methods.contains("avoidWall"), "Registry should contain avoidWall");
        assertTrue(methods.contains("keepDistance"), "Registry should contain keepDistance");
        assertEquals(2, methods.size(), "Registry should only contain the two autonomous methods");
    }
}
