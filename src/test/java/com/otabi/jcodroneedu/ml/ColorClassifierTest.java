package com.otabi.jcodroneedu.ml;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ColorClassifier.
 */
public class ColorClassifierTest {
    private static final String TEST_DATASET = "test_color_dataset";
    private ColorClassifier classifier;


    @BeforeEach
    void setupDataset() throws IOException {
        Files.createDirectories(Paths.get(TEST_DATASET));
        // Always create two label files for every test, ensure each ends with a newline
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(TEST_DATASET, "red.txt"))) {
            w.write("0 100 100 50\n1 99 99 49\n");
        }
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(TEST_DATASET, "blue.txt"))) {
            w.write("240 100 100 50\n241 99 99 49\n");
        }
    }

    @AfterEach
    void cleanupDataset() throws IOException {
        // Remove all files in the test dataset directory
        Path dir = Paths.get(TEST_DATASET);
        if (Files.exists(dir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path entry : stream) {
                    Files.deleteIfExists(entry);
                }
            }
            Files.deleteIfExists(dir);
        }
    }

    @BeforeEach
    void setUp() {
        classifier = new ColorClassifier();
    }

    @Test
    void testLoadColorDataAndPredict() throws Exception {
        classifier.loadColorData(TEST_DATASET, false);
        assertEquals("red", classifier.predictColor(new double[]{0, 100, 100, 50}));
        assertEquals("blue", classifier.predictColor(new double[]{240, 100, 100, 50}));
    }

    @Test
    void testAppendColorData() throws Exception {
        // Assert the new sample is present
        double[][] newSamples = {{2, 98, 98, 48}};
        assertTrue(Files.exists(Paths.get(TEST_DATASET, "red.txt")), "red.txt should exist before appending");
        classifier.appendColorData("red", newSamples, TEST_DATASET);
        // Print contents of red.txt after appending for debugging
        Path redPath = Paths.get(TEST_DATASET, "red.txt");
        System.out.println("Contents of red.txt after append:");
        Files.lines(redPath).forEach(System.out::println);
        // Assert the new sample is present (accept both int and double formatting)
        String fileContents = String.join("\n", Files.readAllLines(redPath));
        boolean foundInt = fileContents.contains("2 98 98 48");
        boolean foundDouble = fileContents.contains("2.0 98.0 98.0 48.0");
        assertTrue(foundInt || foundDouble, "Appended sample not found in red.txt. Contents: " + fileContents);
        // Now reload classifier and check predictions
        classifier.loadColorData(TEST_DATASET, false);
        assertEquals("red", classifier.predictColor(new double[]{2, 98, 98, 48}));
        assertEquals("red", classifier.predictColor(new double[]{0, 100, 100, 50}));
        assertEquals("blue", classifier.predictColor(new double[]{240, 100, 100, 50}));
    }

    @Test
    void testNewColorData() throws Exception {
        String newLabel = "green";
        double[][] samples = {
            {120, 100, 100, 50},
            {121, 99, 99, 49},
            {122, 98, 98, 48}
        };
        classifier.newColorData(newLabel, samples, TEST_DATASET);
        classifier.loadColorData(TEST_DATASET, false);
        // Debug: print label names and predictions
        System.out.println("Labels: " + classifier.getLabelNames());
        System.out.println("Predict (120,100,100,50): " + classifier.predictColor(new double[]{120, 100, 100, 50}));
        System.out.println("Predict (0,100,100,50): " + classifier.predictColor(new double[]{0, 100, 100, 50}));
        System.out.println("Predict (240,100,100,50): " + classifier.predictColor(new double[]{240, 100, 100, 50}));
        // Check all label predictions
        assertEquals("green", classifier.predictColor(new double[]{120, 100, 100, 50}));
        assertEquals("red", classifier.predictColor(new double[]{0, 100, 100, 50}));
        assertEquals("blue", classifier.predictColor(new double[]{240, 100, 100, 50}));
    }

    @Test
    void testResetClassifier() throws Exception {
        classifier.loadColorData(TEST_DATASET, false);
        classifier.resetClassifier();
        assertThrows(IllegalStateException.class, () -> classifier.predictColor(new double[]{0, 100, 100, 50}));
    }
}
