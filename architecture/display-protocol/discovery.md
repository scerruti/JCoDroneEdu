---
layout: guide
category: Architecture
title: Echo Discovery
---

# Echo Discovery Mechanism

The echo discovery mechanism is a clever technique used to identify which protocol variants are supported by the drone.

## How Echo Works

Before using the 0x88 batch protocol, the controller performs an echo test:

1. Send a small echo command (0x88 with minimal payload)
2. If the drone responds with an echo, the protocol is supported
3. If no response, fall back to pixel-by-pixel rendering

## Echo Protocol Specification

- **Command:** 0x88
- **Payload Size:** 1 byte (version indicator)
- **Response:** Echo of the same command
- **Timeout:** 100ms

## Performance Impact

The echo test adds minimal overhead:
- One-time discovery cost: ~100ms
- Subsequent renders benefit from 100× speedup

## See Also

- [Protocol Specification](/architecture/display-protocol/protocol.html)
- [Interleaving Strategy](/architecture/display-protocol/interleaving.html)
