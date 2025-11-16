---
layout: guide
category: Architecture
title: Interleaving Strategy
---

# Display Command Interleaving Strategy

How flight commands and display updates are efficiently interleaved to maintain responsive control.

## Problem

When displaying graphics, the communication channel gets saturated with display commands, causing flight commands to be delayed.

## Solution: Interleaved Transmission

Display commands are split into smaller batches and interleaved with flight control messages:

```
Flight Command -> Small Display Batch -> Flight Response -> 
Small Display Batch -> Flight Command -> ...
```

## Algorithm

1. Queue all display commands
2. Split into chunks of ~50 bytes
3. Send flight command
4. Wait for acknowledgment
5. Send one display batch
6. Repeat steps 3-5 until all display commands sent

## Performance Impact

- **Flight latency:** < 50ms (maintained)
- **Display throughput:** ~20ms per batch
- **Total rendering time:** Scales linearly with complexity

## Implementation Details

The `DisplayController` class implements this strategy transparently, so applications don't need to manage interleaving manually.

## See Also

- [Protocol Specification](/architecture/display-protocol/protocol.html)
- [Implementation Details](/architecture/display-protocol/implementation.html)
