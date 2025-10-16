package com.otabi.jcodroneedu.buzzer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Unit tests for BuzzerSequenceRegistry class.
 * Tests singleton pattern, registration, retrieval, and thread safety.
 */
@DisplayName("BuzzerSequenceRegistry Unit Tests")
class BuzzerSequenceRegistryTest {
    
    private BuzzerSequenceRegistry registry;
    
    @BeforeEach
    void setUp() {
        registry = BuzzerSequenceRegistry.getInstance();
        // Clear and restore built-ins before each test for isolation
        registry.clear();
        registry.restoreBuiltIns();
    }
    
    @Test
    @DisplayName("getInstance returns singleton instance")
    void testSingleton() {
        BuzzerSequenceRegistry instance1 = BuzzerSequenceRegistry.getInstance();
        BuzzerSequenceRegistry instance2 = BuzzerSequenceRegistry.getInstance();
        
        assertSame(instance1, instance2, "Should return same singleton instance");
    }
    
    @Test
    @DisplayName("Built-in sequences are registered on initialization")
    void testBuiltInSequences() {
        assertTrue(registry.has("success"), "Should have 'success' sequence");
        assertTrue(registry.has("warning"), "Should have 'warning' sequence");
        assertTrue(registry.has("error"), "Should have 'error' sequence");
        
        assertEquals(3, registry.count(), "Should have exactly 3 built-in sequences");
    }
    
    @Test
    @DisplayName("Register adds new sequence")
    void testRegister() {
        BuzzerSequence custom = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("custom");
        
        registry.register("custom", custom);
        
        assertTrue(registry.has("custom"), "Should have registered sequence");
        assertEquals(4, registry.count(), "Should have 4 sequences (3 built-in + 1 custom)");
    }
    
    @Test
    @DisplayName("Register replaces existing sequence")
    void testRegisterReplacement() {
        BuzzerSequence original = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("test");
        
        BuzzerSequence replacement = new BuzzerSequence.Builder()
            .addNote(880, 300)
            .build("test");
        
        registry.register("test", original);
        int countBefore = registry.count();
        
        registry.register("test", replacement);
        int countAfter = registry.count();
        
        assertEquals(countBefore, countAfter, "Count should not change when replacing");
        
        BuzzerSequence retrieved = registry.get("test");
        assertEquals(1, retrieved.getNotes().size(), "Should retrieve replacement sequence");
        assertEquals(880, retrieved.getNotes().get(0).frequency, "Should have replacement frequency");
    }
    
    @Test
    @DisplayName("Register rejects null name")
    void testRegisterRejectsNullName() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("test");
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register(null, sequence);
        }, "Should reject null name");
    }
    
    @Test
    @DisplayName("Register rejects blank name")
    void testRegisterRejectsBlankName() {
        BuzzerSequence sequence = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("test");
        
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register("   ", sequence);
        }, "Should reject blank name");
    }
    
    @Test
    @DisplayName("Register rejects null sequence")
    void testRegisterRejectsNullSequence() {
        assertThrows(IllegalArgumentException.class, () -> {
            registry.register("test", null);
        }, "Should reject null sequence");
    }
    
    @Test
    @DisplayName("Get retrieves registered sequence")
    void testGet() {
        BuzzerSequence custom = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("custom");
        
        registry.register("custom", custom);
        BuzzerSequence retrieved = registry.get("custom");
        
        assertNotNull(retrieved, "Should retrieve sequence");
        assertSame(custom, retrieved, "Should retrieve same sequence instance");
    }
    
    @Test
    @DisplayName("Get returns null for missing sequence")
    void testGetMissing() {
        BuzzerSequence result = registry.get("nonexistent");
        
        assertNull(result, "Should return null for missing sequence");
    }
    
    @Test
    @DisplayName("Has returns true for existing sequence")
    void testHasExisting() {
        assertTrue(registry.has("success"), "Should find 'success' sequence");
    }
    
    @Test
    @DisplayName("Has returns false for missing sequence")
    void testHasMissing() {
        assertFalse(registry.has("nonexistent"), "Should not find nonexistent sequence");
    }
    
    @Test
    @DisplayName("List returns all sequence names sorted")
    void testList() {
        BuzzerSequence custom1 = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("zebra");
        
        BuzzerSequence custom2 = new BuzzerSequence.Builder()
            .addNote(880, 300)
            .build("apple");
        
        registry.register("zebra", custom1);
        registry.register("apple", custom2);
        
        List<String> names = registry.list();
        
        assertEquals(5, names.size(), "Should have 5 sequences (3 built-in + 2 custom)");
        
        // Check alphabetical ordering
        assertEquals("apple", names.get(0), "First should be 'apple'");
        assertEquals("error", names.get(1), "Second should be 'error'");
        assertEquals("success", names.get(2), "Third should be 'success'");
        assertEquals("warning", names.get(3), "Fourth should be 'warning'");
        assertEquals("zebra", names.get(4), "Fifth should be 'zebra'");
    }
    
    @Test
    @DisplayName("Count returns correct number of sequences")
    void testCount() {
        assertEquals(3, registry.count(), "Should start with 3 built-in sequences");
        
        BuzzerSequence custom = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("custom");
        
        registry.register("custom", custom);
        assertEquals(4, registry.count(), "Should have 4 after adding one");
    }
    
    @Test
    @DisplayName("Unregister removes sequence")
    void testUnregister() {
        BuzzerSequence custom = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .build("custom");
        
        registry.register("custom", custom);
        assertTrue(registry.has("custom"), "Should have custom sequence");
        
        boolean removed = registry.unregister("custom");
        assertTrue(removed, "Should return true when removing existing sequence");
        assertFalse(registry.has("custom"), "Should no longer have custom sequence");
    }
    
    @Test
    @DisplayName("Unregister returns false for missing sequence")
    void testUnregisterMissing() {
        boolean removed = registry.unregister("nonexistent");
        
        assertFalse(removed, "Should return false when removing nonexistent sequence");
    }
    
    @Test
    @DisplayName("Unregister can remove built-in sequences")
    void testUnregisterBuiltIn() {
        assertTrue(registry.has("success"), "Should have 'success' initially");
        
        boolean removed = registry.unregister("success");
        assertTrue(removed, "Should successfully remove built-in");
        assertFalse(registry.has("success"), "Should no longer have 'success'");
        assertEquals(2, registry.count(), "Should have 2 sequences remaining");
    }
    
    @Test
    @DisplayName("Clear removes all sequences")
    void testClear() {
        assertEquals(3, registry.count(), "Should start with 3 sequences");
        
        registry.clear();
        
        assertEquals(0, registry.count(), "Should have 0 sequences after clear");
        assertFalse(registry.has("success"), "Should not have 'success' after clear");
        assertFalse(registry.has("warning"), "Should not have 'warning' after clear");
        assertFalse(registry.has("error"), "Should not have 'error' after clear");
    }
    
    @Test
    @DisplayName("RestoreBuiltIns re-adds default sequences")
    void testRestoreBuiltIns() {
        registry.clear();
        assertEquals(0, registry.count(), "Should have 0 sequences after clear");
        
        registry.restoreBuiltIns();
        
        assertEquals(3, registry.count(), "Should have 3 sequences after restore");
        assertTrue(registry.has("success"), "Should have 'success' after restore");
        assertTrue(registry.has("warning"), "Should have 'warning' after restore");
        assertTrue(registry.has("error"), "Should have 'error' after restore");
    }
    
    @Test
    @DisplayName("RestoreBuiltIns is idempotent")
    void testRestoreBuiltInsIdempotent() {
        registry.restoreBuiltIns();
        int count1 = registry.count();
        
        registry.restoreBuiltIns();
        int count2 = registry.count();
        
        assertEquals(count1, count2, "Calling restoreBuiltIns twice should not duplicate sequences");
        assertEquals(3, count2, "Should still have exactly 3 built-in sequences");
    }
    
    @Test
    @DisplayName("Thread safety: concurrent registrations")
    void testConcurrentRegistrations() throws InterruptedException {
        int threadCount = 10;
        int sequencesPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < sequencesPerThread; i++) {
                        BuzzerSequence seq = new BuzzerSequence.Builder()
                            .addNote(440 + i, 100)
                            .build("thread" + threadId + "-seq" + i);
                        
                        registry.register("thread" + threadId + "-seq" + i, seq);
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        assertEquals(threadCount * sequencesPerThread, successCount.get(), 
            "All registrations should succeed");
        
        // Account for built-in sequences (3) + concurrent registrations
        int expectedCount = 3 + (threadCount * sequencesPerThread);
        assertEquals(expectedCount, registry.count(), 
            "Registry should have all sequences");
    }
    
    @Test
    @DisplayName("Thread safety: concurrent reads and writes")
    void testConcurrentReadsAndWrites() throws InterruptedException {
        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger readSuccesses = new AtomicInteger(0);
        AtomicInteger writeSuccesses = new AtomicInteger(0);
        
        // Pre-register some sequences
        for (int i = 0; i < 50; i++) {
            BuzzerSequence seq = new BuzzerSequence.Builder()
                .addNote(440 + i, 100)
                .build("seq" + i);
            registry.register("seq" + i, seq);
        }
        
        // Half threads read, half write
        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            final boolean isReader = (t % 2 == 0);
            
            executor.submit(() -> {
                try {
                    if (isReader) {
                        // Read operations
                        for (int i = 0; i < 100; i++) {
                            String name = "seq" + (i % 50);
                            BuzzerSequence seq = registry.get(name);
                            if (seq != null) {
                                readSuccesses.incrementAndGet();
                            }
                        }
                    } else {
                        // Write operations
                        for (int i = 0; i < 50; i++) {
                            BuzzerSequence seq = new BuzzerSequence.Builder()
                                .addNote(880 + i, 150)
                                .build("writer-thread" + threadId + "-seq" + i);
                            
                            registry.register("writer-thread" + threadId + "-seq" + i, seq);
                            writeSuccesses.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        assertTrue(readSuccesses.get() > 0, "Should have successful reads");
        assertTrue(writeSuccesses.get() > 0, "Should have successful writes");
        
        // Verify registry integrity
        assertTrue(registry.count() > 50, "Should have base sequences plus new writes");
    }
    
    @Test
    @DisplayName("GenerateDocumentation creates markdown output")
    void testGenerateDocumentation() {
        String doc = registry.generateDocumentation();
        
        assertNotNull(doc, "Documentation should not be null");
        assertFalse(doc.isEmpty(), "Documentation should not be empty");
        
        // Check for expected content
        assertTrue(doc.contains("# Buzzer Sequence Registry"), "Should have title");
        assertTrue(doc.contains("success"), "Should document 'success' sequence");
        assertTrue(doc.contains("warning"), "Should document 'warning' sequence");
        assertTrue(doc.contains("error"), "Should document 'error' sequence");
    }
    
    @Test
    @DisplayName("GenerateDocumentation includes custom sequences")
    void testGenerateDocumentationWithCustom() {
        BuzzerSequence custom = new BuzzerSequence.Builder()
            .addNote(440, 200)
            .addNote(880, 300)
            .build("my-melody");
        
        registry.register("my-melody", custom);
        
        String doc = registry.generateDocumentation();
        
        assertTrue(doc.contains("my-melody"), "Should document custom sequence");
        assertTrue(doc.contains("| 2 |") || doc.contains("| `my-melody` | 2 |"), 
            "Should show note count of 2");
    }
}
