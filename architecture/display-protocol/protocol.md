---
layout: guide
category: Architecture
title: Protocol Specification
---

# 0x88 Display Protocol Specification

Complete technical specification for the 0x88 batch image protocol.

## Overview

The 0x88 protocol enables efficient batch transmission of display commands, achieving 100× performance improvement over pixel-by-pixel rendering.

## Packet Structure

```
[Header: 0x88] [Length: 2 bytes] [Commands: N bytes]
```

## Command Types

- **0x00**: Clear display
- **0x01**: Draw point (x, y, color)
- **0x02**: Draw line (x1, y1, x2, y2, color)
- **0x03**: Draw circle (x, y, radius, color)
- **0x04**: Draw rectangle (x, y, width, height, color)
- **0x05**: Draw string (x, y, text)
- **0x06**: Fill display (color)

## Color Encoding

Colors are encoded as 24-bit RGB values: `0xRRGGBB`

## Example

Drawing a line from (0,0) to (100,100) in white:
```
0x88 0x00 0x08 0x02 0x00 0x00 0x64 0x64 0xFF 0xFF 0xFF
```

## See Also

- [Echo Discovery](/architecture/display-protocol/discovery.html)
- [Interleaving Strategy](/architecture/display-protocol/interleaving.html)
