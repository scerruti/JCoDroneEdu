package com.otabi.jcodroneedu.buzzer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe singleton registry for managing buzzer sequences.
 * 
 * <p>This registry maintains a collection of named {@link BuzzerSequence} objects
 * that can be played on the drone or controller. The registry comes pre-loaded with
 * three built-in sequences ("success", "warning", "error") and supports registration
 * of custom sequences at runtime.</p>
 * 
 * <h3>🎯 Design Pattern: Registry + Singleton</h3>
 * <ul>
 *   <li><strong>Singleton</strong> - Only one registry exists in the application</li>
 *   <li><strong>Registry</strong> - Centralized storage and lookup of sequences</li>
 *   <li><strong>Thread-Safe</strong> - Uses ConcurrentHashMap for multi-threaded access</li>
 * </ul>
 * 
 * <h3>💡 Educational Use (L0303):</h3>
 * <pre>{@code
 * // Get the registry instance
 * BuzzerSequenceRegistry registry = BuzzerSequenceRegistry.getInstance();
 * 
 * // Use a built-in sequence
 * BuzzerSequence success = registry.get("success");
 * 
 * // Create and register a custom sequence
 * BuzzerSequence myMelody = new BuzzerSequence.Builder()
 *     .addNote(440, 200)  // A
 *     .addNote(494, 200)  // B
 *     .addNote(523, 400)  // C
 *     .build("my-melody");
 * 
 * registry.register("my-melody", myMelody);
 * 
 * // List all available sequences
 * System.out.println("Available sequences: " + registry.list());
 * }</pre>
 * 
 * <h3>🔒 Thread Safety:</h3>
 * This class is safe to use from multiple threads. The internal map is a
 * {@link ConcurrentHashMap}, which allows concurrent reads and writes without
 * external synchronization.
 * 
 * @author Stephen Cerruti
 * @version 1.0.0
 * @see BuzzerSequence
 * @see com.otabi.jcodroneedu.Drone#controllerBuzzerSequence(String)
 * @see com.otabi.jcodroneedu.Drone#droneBuzzerSequence(String)
 * @educational
 */
public class BuzzerSequenceRegistry {
    
    /** Singleton instance */
    private static final BuzzerSequenceRegistry INSTANCE = new BuzzerSequenceRegistry();
    
    /** Thread-safe map of sequence name → BuzzerSequence */
    private final Map<String, BuzzerSequence> sequences;
    
    /**
     * Private constructor - use {@link #getInstance()} to access.
     * Registers built-in sequences on construction.
     */
    private BuzzerSequenceRegistry() {
        this.sequences = new ConcurrentHashMap<>();
        registerBuiltInSequences();
    }
    
    /**
     * Gets the singleton instance of the registry.
     * 
     * @return The global BuzzerSequenceRegistry instance
     */
    public static BuzzerSequenceRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Registers the three built-in sequences.
     * Called automatically during initialization.
     */
    private void registerBuiltInSequences() {
        register("success", BuzzerSequence.createSuccessSequence());
        register("warning", BuzzerSequence.createWarningSequence());
        register("error", BuzzerSequence.createErrorSequence());
    }
    
    /**
     * Registers a buzzer sequence with the given name.
     * 
     * <p>If a sequence with this name already exists, it will be replaced.
     * This allows students to override built-in sequences if desired.</p>
     * 
     * <p><strong>⚠️ Note:</strong> Sequence names are case-sensitive.
     * "Success" and "success" are different keys.</p>
     * 
     * @param name The name to register the sequence under
     * @param sequence The buzzer sequence to register
     * @throws IllegalArgumentException if name is null/empty or sequence is null
     */
    public void register(String name, BuzzerSequence sequence) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Sequence name cannot be null or empty");
        }
        if (sequence == null) {
            throw new IllegalArgumentException("Sequence cannot be null");
        }
        
        sequences.put(name, sequence);
    }
    
    /**
     * Retrieves a buzzer sequence by name.
     * 
     * @param name The name of the sequence to retrieve
     * @return The buzzer sequence, or null if not found
     */
    public BuzzerSequence get(String name) {
        return sequences.get(name);
    }
    
    /**
     * Checks if a sequence with the given name exists.
     * 
     * @param name The sequence name to check
     * @return true if the sequence exists, false otherwise
     */
    public boolean has(String name) {
        return sequences.containsKey(name);
    }
    
    /**
     * Unregisters (removes) a sequence by name.
     * 
     * <p>Built-in sequences can be unregistered, though this is not recommended
     * for educational use as it may break example code that relies on them.</p>
     * 
     * @param name The name of the sequence to remove
     * @return true if a sequence was removed, false if it didn't exist
     */
    public boolean unregister(String name) {
        return sequences.remove(name) != null;
    }
    
    /**
     * Lists all registered sequence names in alphabetical order.
     * 
     * @return Sorted list of sequence names
     */
    public List<String> list() {
        List<String> names = new ArrayList<>(sequences.keySet());
        Collections.sort(names);
        return names;
    }
    
    /**
     * Gets the number of registered sequences.
     * 
     * @return The count of sequences in the registry
     */
    public int count() {
        return sequences.size();
    }
    
    /**
     * Clears all sequences from the registry, including built-ins.
     * 
     * <p><strong>⚠️ Caution:</strong> This removes all sequences, including
     * the built-in "success", "warning", and "error" sequences. Call
     * {@link #registerBuiltInSequences()} to restore them.</p>
     */
    public void clear() {
        sequences.clear();
    }
    
    /**
     * Restores the built-in sequences to the registry.
     * Useful after calling {@link #clear()}.
     */
    public void restoreBuiltIns() {
        registerBuiltInSequences();
    }
    
    /**
     * Generates markdown documentation for all registered sequences.
     * 
     * <p>This is useful for creating reference documentation for students
     * showing all available sequences and their properties.</p>
     * 
     * @return Markdown-formatted documentation string
     */
    public String generateDocumentation() {
        StringBuilder doc = new StringBuilder();
        doc.append("# Buzzer Sequence Registry\n\n");
        doc.append(String.format("**Total Sequences:** %d\n\n", count()));
        
        doc.append("## Registered Sequences\n\n");
        doc.append("| Name | Notes | Duration | Description |\n");
        doc.append("|------|-------|----------|-------------|\n");
        
        for (String name : list()) {
            BuzzerSequence seq = sequences.get(name);
            doc.append(String.format("| `%s` | %d | %dms | %s |\n",
                name,
                seq.getNoteCount(),
                seq.getTotalDurationMs(),
                getSequenceDescription(name)
            ));
        }
        
        return doc.toString();
    }
    
    /**
     * Gets a human-readable description for built-in sequences.
     * 
     * @param name The sequence name
     * @return Description string, or "Custom sequence" for non-built-ins
     */
    private String getSequenceDescription(String name) {
        switch (name) {
            case "success":
                return "Two ascending tones (positive feedback)";
            case "warning":
                return "Three medium beeps (caution alert)";
            case "error":
                return "Three low beeps (error indication)";
            default:
                return "Custom sequence";
        }
    }
    
    @Override
    public String toString() {
        return String.format("BuzzerSequenceRegistry[sequences=%d, names=%s]", 
            count(), list());
    }
}
