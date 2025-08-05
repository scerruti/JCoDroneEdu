package com.otabi.jcodroneedu.ml;

import smile.classification.KNN;
import smile.data.formula.Formula;
import smile.data.vector.BaseVector;
import smile.data.DataFrame;
import smile.data.vector.IntVector;
import smile.plot.swing.ScatterPlot;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * ColorClassifier provides KNN-based color classification using HSVL data.
 * <p>
 * Data is loaded from a directory of text files, one per color label, each row a sample.
 * Supports training, prediction, appending, and creating new color data files.
 * Optionally displays a 3D scatter plot of the data.
 * <p>
 * This class is designed for educational use and closely mimics the Python reference API.
 */
public class ColorClassifier {
    private KNN<double[]> knn;
    private List<String> labels = new ArrayList<>();
    private List<double[]> data = new ArrayList<>();
    private List<String> labelNames = new ArrayList<>();

    // Package-private getter for test/debug use only
    List<String> getLabelNames() {
        return labelNames;
    }
    private int k = 3;

    /**
     * Loads color data from a dataset directory and trains the KNN classifier.
     * Each file in the directory should be named <label>.txt and contain rows of HSVL values.
     *
     * @param datasetPath Path to the dataset directory
     * @param showGraph   If true, displays a 3D scatter plot of the data
     * @throws IOException if files cannot be read
     */
    public void loadColorData(String datasetPath, boolean showGraph) throws IOException {
        File dir = new File(datasetPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new FileNotFoundException("Dataset directory not found: " + datasetPath);
        }
        data.clear();
        labels.clear();
        labelNames.clear();
        for (File file : Objects.requireNonNull(dir.listFiles((d, name) -> name.endsWith(".txt")))) {
            String label = file.getName().replace(".txt", "");
            labelNames.add(label);
            List<double[]> samples = readSamples(file);
            for (double[] sample : samples) {
                data.add(sample);
                labels.add(label);
            }
        }
        if (labelNames.size() < 2) {
            throw new IllegalArgumentException("Dataset must have at least 2 color labels.");
        }
        // Prepare data for Smile
        double[][] x = data.toArray(new double[0][]);
        int[] y = new int[labels.size()];
        for (int i = 0; i < labels.size(); i++) {
            y[i] = labelNames.indexOf(labels.get(i));
        }
        knn = KNN.fit(x, y, k);
        if (showGraph) {
            plot3D(x, y);
        }
    }

    /**
     * Predicts the color label for the given HSVL data.
     * @param colorData double[] of HSVL values
     * @return Predicted label name
     */
    public String predictColor(double[] colorData) {
        if (knn == null) throw new IllegalStateException("Classifier not trained. Call loadColorData() first.");
        int labelIdx = knn.predict(colorData);
        return labelNames.get(labelIdx);
    }

    /**
     * Appends new samples to an existing label file.
     * @param label Color label
     * @param samples 2D array of HSVL samples
     * @param datasetPath Path to dataset directory
     * @throws IOException if file cannot be written
     */
    public void appendColorData(String label, double[][] samples, String datasetPath) throws IOException {
        File file = new File(datasetPath, label + ".txt");
        if (!file.exists()) throw new FileNotFoundException("Label file not found: " + file.getPath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            for (double[] sample : samples) {
                writer.write(arrayToLine(sample));
                writer.newLine();
            }
        }
    }

    /**
     * Creates a new label file with the given samples.
     * @param label Color label
     * @param samples 2D array of HSVL samples
     * @param datasetPath Path to dataset directory
     * @throws IOException if file cannot be written
     */
    public void newColorData(String label, double[][] samples, String datasetPath) throws IOException {
        File file = new File(datasetPath, label + ".txt");
        if (file.exists()) throw new IOException("Label file already exists: " + file.getPath());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (double[] sample : samples) {
                writer.write(arrayToLine(sample));
                writer.newLine();
            }
        }
    }

    /**
     * Resets the classifier and all loaded data.
     */
    public void resetClassifier() {
        knn = null;
        data.clear();
        labels.clear();
        labelNames.clear();
    }

    // Helper: Read samples from a text file
    private List<double[]> readSamples(File file) throws IOException {
        List<double[]> samples = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("[ ,\t]+");
                double[] sample = new double[tokens.length];
                for (int i = 0; i < tokens.length; i++) {
                    sample[i] = Double.parseDouble(tokens[i]);
                }
                samples.add(sample);
            }
        }
        return samples;
    }

    // Helper: Convert array to space-separated string
    private String arrayToLine(double[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(' ');
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    // Optional: 3D plot using Smile's plotting (Swing)
    private void plot3D(double[][] x, int[] y) {
        // Only plot first 3 dimensions (HSV)
        double[][] xyz = new double[x.length][3];
        for (int i = 0; i < x.length; i++) {
            System.arraycopy(x[i], 0, xyz[i], 0, 3);
        }
        try {
            ScatterPlot.of(xyz, y, '.').canvas().window();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Plotting was interrupted", e);
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException("Plotting failed due to invocation error", e);
        }
    }
}
