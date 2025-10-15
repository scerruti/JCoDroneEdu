package com.otabi.jcodroneedu.receiver;

import com.otabi.jcodroneedu.Drone;
import com.otabi.jcodroneedu.DroneStatus;
import com.otabi.jcodroneedu.LinkManager;
import com.otabi.jcodroneedu.InventoryManager;
import com.otabi.jcodroneedu.protocol.DataType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiverAckTest {

    // Simple smoke tests for ACK handling
    @Test
    public void onAckReceived_nullDoesNotThrow() {
        Drone dummyDrone = null;
        DroneStatus droneStatus = new DroneStatus();
        LinkManager linkManager = new LinkManager();
        InventoryManager inventoryManager = new InventoryManager();

        Receiver receiver = new Receiver(dummyDrone, droneStatus, linkManager, inventoryManager);

        // Should not throw and should be handled gracefully
        assertDoesNotThrow(() -> receiver.onAckReceived(null));
    }

    @Test
    public void onAckReceived_completesPendingFuture() throws Exception {
        Drone dummyDrone = null;
        DroneStatus droneStatus = new DroneStatus();
        LinkManager linkManager = new LinkManager();
        InventoryManager inventoryManager = new InventoryManager();

        Receiver receiver = new Receiver(dummyDrone, droneStatus, linkManager, inventoryManager);

        // Arrange: expect an ACK for DataType.State
        receiver.expectAck(DataType.State);
        CompletableFuture<Void> fut = receiver.getAckFuture(DataType.State);
        assertNotNull(fut, "Future should be registered for DataType.State");

        // Act: simulate receiving an ACK for State
        receiver.onAckReceived(DataType.State);

        // Assert: future completes
        fut.get(1, TimeUnit.SECONDS); // will throw if not completed
        assertTrue(fut.isDone());
        assertFalse(fut.isCompletedExceptionally());
    }
}
