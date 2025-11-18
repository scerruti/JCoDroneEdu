/*
 * CoDrone EDU Java Library
 * Copyright (c) 2024-2025 Stephen P. Cerruti
 * Licensed under the MIT License
 * See LICENSE file in project root
 */

package com.otabi.jcodroneedu;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Drone color classifier wrapper methods.
 * Tests appendColorData, loadClassifier, loadColorData, newColorData, and predictColors.
 */
public class DroneColorClassifierTest {
    private static Path tempDir;
    private Drone drone;

    @BeforeAll
    static void setupTempDirectory() throws IOException {
        tempDir = Files.createTempDirectory("test_color_dataset_");
    }

    @AfterAll
    static void cleanupTempDirectory() throws IOException {
        if (tempDir != null && Files.exists(tempDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
                for (Path entry : stream) {
                    Files.deleteIfExists(entry);
                }
            }
            Files.deleteIfExists(tempDir);
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        drone = new Drone();
        
        // Create sample dataset files for each test
        createSampleDataset();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (drone != null) {
            drone.close();
        }
        
        // Clean up dataset files after each test
        cleanupDatasetFiles();
    }

    private void createSampleDataset() throws IOException {
        // Create red.txt with sample data
        try (BufferedWriter writer = Files.newBufferedWriter(tempDir.resolve("red.txt"))) {
            writer.write("5 200 180 50\n");
            writer.write("7 205 175 52\n");
        }
        
        // Create blue.txt with sample data
        try (BufferedWriter writer = Files.newBufferedWriter(tempDir.resolve("blue.txt"))) {
            writer.write("240 100 100 50\n");
            writer.write("235 95 105 48\n");
        }
    }

    private void cleanupDatasetFiles() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(tempDir)) {
            for (Path entry : stream) {
                Files.deleteIfExists(entry);
            }
        }
    }

    // --- appendColorData tests ---

    @Test
    @DisplayName("appendColorData throws IllegalStateException when colorClassifier is null")
    void appendColorData_throwsWhenNotInitialized() {
        double[][] samples = {{10, 210, 185, 55}};
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> drone.appendColorData("red", samples, tempDir.toString()),
            "Expected IllegalStateException when classifier not initialized"
        );
        
        assertTrue(exception.getMessage().contains("not initialized"),
            "Exception message should indicate classifier not initialized");
    }

    @Test
    @DisplayName("appendColorData appends samples to existing label file")
    void appendColorData_appendsToExistingLabelFile() throws IOException {
        // Load the classifier first
        drone.loadClassifier(tempDir.toString());
        
        // Count lines before append
        long linesBefore = Files.lines(tempDir.resolve("red.txt")).count();
        
        // Append new samples
        double[][] newSamples = {
            {10, 210, 185, 55},
            {8, 198, 182, 51}
        };
        drone.appendColorData("red", newSamples, tempDir.toString());
        
        // Count lines after append
        long linesAfter = Files.lines(tempDir.resolve("red.txt")).count();
        
        assertEquals(linesBefore + 2, linesAfter,
            "Should have 2 more lines after appending 2 samples");
        
        // Verify the content contains the new samples
        String content = Files.readString(tempDir.resolve("red.txt"));
        assertTrue(content.contains("10") && content.contains("210"),
            "File should contain the appended samples");
    }

    // --- newColorData tests ---

    @Test
    @DisplayName("newColorData throws IllegalStateException when colorClassifier is null")
    void newColorData_throwsWhenNotInitialized() {
        double[][] samples = {{120, 100, 100, 50}};
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> drone.newColorData("green", samples, tempDir.toString()),
            "Expected IllegalStateException when classifier not initialized"
        );
        
        assertTrue(exception.getMessage().contains("not initialized"),
            "Exception message should indicate classifier not initialized");
    }

    @Test
    @DisplayName("newColorData creates new file with samples")
    void newColorData_createsFileWithSamples() throws IOException {
        // Load the classifier first
        drone.loadClassifier(tempDir.toString());
        
        // Verify green.txt doesn't exist yet
        Path greenFile = tempDir.resolve("green.txt");
        assertFalse(Files.exists(greenFile), "green.txt should not exist before test");
        
        // Create new color data
        double[][] samples = {
            {120, 100, 100, 50},
            {122, 98, 98, 48},
            {118, 102, 102, 52}
        };
        drone.newColorData("green", samples, tempDir.toString());
        
        // Verify file was created
        assertTrue(Files.exists(greenFile), "green.txt should be created");
        
        // Verify content
        long lineCount = Files.lines(greenFile).count();
        assertEquals(3, lineCount, "File should contain 3 lines for 3 samples");
        
        String content = Files.readString(greenFile);
        assertTrue(content.contains("120") && content.contains("100"),
            "File should contain the sample data");
    }

    @Test
    @DisplayName("newColorData throws IOException when file already exists")
    void newColorData_throwsWhenFileExists() throws IOException {
        // Load the classifier first
        drone.loadClassifier(tempDir.toString());
        
        // Try to create a file that already exists (red.txt)
        double[][] samples = {{5, 200, 180, 50}};
        
        assertThrows(
            IOException.class,
            () -> drone.newColorData("red", samples, tempDir.toString()),
            "Should throw IOException when label file already exists"
        );
    }

    // --- loadClassifier tests ---

    @Test
    @DisplayName("loadClassifier loads dataset and enables predict")
    void loadClassifier_loadsDatasetAndEnablesPredict() throws IOException {
        // Load the classifier
        drone.loadClassifier(tempDir.toString());
        
        // Verify we can predict colors (would throw if not loaded)
        assertDoesNotThrow(
            () -> {
                String result = drone.predictColors(new double[]{5, 200, 180, 50});
                assertNotNull(result, "Prediction should return a label");
            },
            "Should be able to predict after loading classifier"
        );
    }

    @Test
    @DisplayName("loadClassifier throws IOException with invalid path")
    void loadClassifier_throwsWithInvalidPath() {
        assertThrows(
            IOException.class,
            () -> drone.loadClassifier("/nonexistent/path/to/dataset"),
            "Should throw IOException for nonexistent dataset path"
        );
    }

    // --- loadColorData tests ---

    @Test
    @DisplayName("loadColorData loads dataset and enables predict")
    void loadColorData_loadsDatasetAndEnablesPredict() throws IOException {
        // Load the classifier with graph disabled
        drone.loadColorData(tempDir.toString(), false);
        
        // Verify we can predict colors
        assertDoesNotThrow(
            () -> {
                String result = drone.predictColors(new double[]{5, 200, 180, 50});
                assertNotNull(result, "Prediction should return a label");
            },
            "Should be able to predict after loading color data"
        );
    }

    @Test
    @DisplayName("loadColorData throws IOException with invalid path")
    void loadColorData_throwsWithInvalidPath() {
        assertThrows(
            IOException.class,
            () -> drone.loadColorData("/nonexistent/path/to/dataset", false),
            "Should throw IOException for nonexistent dataset path"
        );
    }

    @Test
    @DisplayName("loadColorData with showGraph=false does not require display")
    void loadColorData_withGraphDisabled() throws IOException {
        // This test verifies that we can load without display issues
        assertDoesNotThrow(
            () -> drone.loadColorData(tempDir.toString(), false),
            "Loading with showGraph=false should not require display"
        );
    }

    // --- predictColors tests ---

    @Test
    @DisplayName("predictColors throws IllegalStateException when not loaded")
    void predictColors_throwsWhenNotLoaded() {
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> drone.predictColors(new double[]{5, 200, 180, 50}),
            "Expected IllegalStateException when classifier not loaded"
        );
        
        assertTrue(exception.getMessage().contains("not loaded"),
            "Exception message should indicate classifier not loaded");
    }

    @Test
    @DisplayName("predictColors returns expected label after load")
    void predictColors_returnsExpectedLabelAfterLoad() throws IOException {
        // Load the classifier
        drone.loadClassifier(tempDir.toString());
        
        // Predict red (should be close to training data)
        String redPrediction = drone.predictColors(new double[]{5, 200, 180, 50});
        assertEquals("red", redPrediction, "Should predict 'red' for red sample");
        
        // Predict blue (should be close to training data)
        String bluePrediction = drone.predictColors(new double[]{240, 100, 100, 50});
        assertEquals("blue", bluePrediction, "Should predict 'blue' for blue sample");
    }

    @Test
    @DisplayName("predictColors works with variations in input")
    void predictColors_worksWithVariations() throws IOException {
        // Load the classifier
        drone.loadClassifier(tempDir.toString());
        
        // Test with slight variations (should still predict correctly with KNN)
        String prediction1 = drone.predictColors(new double[]{6, 202, 178, 51});
        assertEquals("red", prediction1, "Should predict 'red' for similar values");
        
        String prediction2 = drone.predictColors(new double[]{238, 98, 102, 49});
        assertEquals("blue", prediction2, "Should predict 'blue' for similar values");
    }

    // --- Integration tests ---

    @Test
    @DisplayName("Complete workflow: load, append, predict")
    void completeWorkflow_loadAppendPredict() throws IOException {
        // 1. Load classifier
        drone.loadClassifier(tempDir.toString());
        
        // 2. Verify initial prediction
        String initialPrediction = drone.predictColors(new double[]{5, 200, 180, 50});
        assertEquals("red", initialPrediction);
        
        // 3. Append new samples
        double[][] newSamples = {{10, 210, 185, 55}};
        drone.appendColorData("red", newSamples, tempDir.toString());
        
        // 4. Reload and verify prediction still works
        drone.loadClassifier(tempDir.toString());
        String afterAppendPrediction = drone.predictColors(new double[]{10, 210, 185, 55});
        assertEquals("red", afterAppendPrediction);
    }

    @Test
    @DisplayName("Complete workflow: load, create new color, predict")
    void completeWorkflow_loadCreatePredict() throws IOException {
        // 1. Load classifier with initial colors
        drone.loadClassifier(tempDir.toString());
        
        // 2. Create new color
        double[][] greenSamples = {
            {120, 100, 100, 50},
            {122, 98, 98, 48}
        };
        drone.newColorData("green", greenSamples, tempDir.toString());
        
        // 3. Reload with new color
        drone.loadClassifier(tempDir.toString());
        
        // 4. Verify all colors can be predicted
        assertEquals("red", drone.predictColors(new double[]{5, 200, 180, 50}));
        assertEquals("blue", drone.predictColors(new double[]{240, 100, 100, 50}));
        assertEquals("green", drone.predictColors(new double[]{120, 100, 100, 50}));
    }
}
