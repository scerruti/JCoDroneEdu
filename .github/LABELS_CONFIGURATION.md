# GitHub Labels Configuration

This document defines all labels to be created for the Test Coverage Improvement Plan project.

## How to Create Labels

You can create these labels through:
1. **GitHub UI**: Repository → Issues → Labels → New label
2. **GitHub CLI**: `gh label create "label-name" --color "color-code" --description "description"`
3. **GitHub API**: Use the Labels API endpoint

---

## Label Definitions

### Priority Labels

| Name | Color | Description |
|------|-------|-------------|
| `priority-critical` | `#B60205` (red) | Must complete immediately - blocks other work |
| `priority-high` | `#D93F0B` (orange) | Complete in current sprint |
| `priority-medium` | `#FBCA04` (yellow) | Schedule for upcoming sprint |
| `priority-low` | `#0E8A16` (blue) | Nice to have, schedule when capacity allows |

**GitHub CLI Commands:**
```bash
gh label create "priority-critical" --color "B60205" --description "Must complete immediately - blocks other work"
gh label create "priority-high" --color "D93F0B" --description "Complete in current sprint"
gh label create "priority-medium" --color "FBCA04" --description "Schedule for upcoming sprint"
gh label create "priority-low" --color "0E8A16" --description "Nice to have, schedule when capacity allows"
```

---

### Sprint Labels

| Name | Color | Description |
|------|-------|-------------|
| `sprint-1` | `#5319E7` (purple) | Sprint 1: Critical API & Core Logic |
| `sprint-2` | `#D876E3` (pink) | Sprint 2: ML & Data Processing Logic |
| `sprint-3` | `#1D76DB` (teal) | Sprint 3: Protocols, Communication, and Utilities |
| `sprint-4` | `#0E8A16` (green) | Sprint 4: System, Storage, and Integration |
| `sprint-5` | `#BFD4F2` (light-blue) | Sprint 5: Examples, Demos, and Edge Packages |

**GitHub CLI Commands:**
```bash
gh label create "sprint-1" --color "5319E7" --description "Sprint 1: Critical API & Core Logic"
gh label create "sprint-2" --color "D876E3" --description "Sprint 2: ML & Data Processing Logic"
gh label create "sprint-3" --color "1D76DB" --description "Sprint 3: Protocols, Communication, and Utilities"
gh label create "sprint-4" --color "0E8A16" --description "Sprint 4: System, Storage, and Integration"
gh label create "sprint-5" --color "BFD4F2" --description "Sprint 5: Examples, Demos, and Edge Packages"
```

---

### Type Labels

| Name | Color | Description |
|------|-------|-------------|
| `test` | `#7FE9A3` (light-green) | Test-related work |
| `coverage` | `#C5DEF5` (blue-gray) | Coverage improvement specific |
| `unit-test` | `#BFDB38` (lime) | Unit test development |
| `integration-test` | `#5E7D4F` (olive) | Integration test development |
| `documentation` | `#0075CA` (blue) | Test documentation |

**GitHub CLI Commands:**
```bash
gh label create "test" --color "7FE9A3" --description "Test-related work"
gh label create "coverage" --color "C5DEF5" --description "Coverage improvement specific"
gh label create "unit-test" --color "BFDB38" --description "Unit test development"
gh label create "integration-test" --color "5E7D4F" --description "Integration test development"
gh label create "documentation" --color "0075CA" --description "Test documentation"
```

---

### Status Labels

| Name | Color | Description |
|------|-------|-------------|
| `ready` | `#0E8A16` (green) | Ready to start - all prerequisites met |
| `in-progress` | `#FBCA04` (yellow) | Currently being worked on |
| `blocked` | `#D93F0B` (red) | Blocked by dependencies or issues |
| `review` | `#5319E7` (purple) | Ready for code review |
| `done` | `#0B4F30` (dark-green) | Completed and merged |

**GitHub CLI Commands:**
```bash
gh label create "ready" --color "0E8A16" --description "Ready to start - all prerequisites met"
gh label create "in-progress" --color "FBCA04" --description "Currently being worked on"
gh label create "blocked" --color "D93F0B" --description "Blocked by dependencies or issues"
gh label create "review" --color "5319E7" --description "Ready for code review"
gh label create "done" --color "0B4F30" --description "Completed and merged"
```

---

### Component Labels

| Name | Color | Description |
|------|-------|-------------|
| `component-api` | `#1D76DB` (blue) | Core API classes (Drone, Controllers) |
| `component-protocol` | `#006B75` (teal) | Protocol implementation |
| `component-system` | `#D876E3` (magenta) | System-level functionality |
| `component-storage` | `#FEF2C0` (cream) | Storage operations |
| `component-ml` | `#F9D0C4` (peach) | Machine learning features |
| `component-autonomous` | `#C2E0C6` (mint) | Autonomous flight features |
| `component-tools` | `#BFDADC` (cyan) | Developer tools and utilities |
| `component-examples` | `#FFC0CB` (pink) | Example code and demos |
| `component-buzzer` | `#FFD700` (gold) | Buzzer/audio functionality |
| `component-display` | `#E99695` (salmon) | Display/screen functionality |

**GitHub CLI Commands:**
```bash
gh label create "component-api" --color "1D76DB" --description "Core API classes (Drone, Controllers)"
gh label create "component-protocol" --color "006B75" --description "Protocol implementation"
gh label create "component-system" --color "D876E3" --description "System-level functionality"
gh label create "component-storage" --color "FEF2C0" --description "Storage operations"
gh label create "component-ml" --color "F9D0C4" --description "Machine learning features"
gh label create "component-autonomous" --color "C2E0C6" --description "Autonomous flight features"
gh label create "component-tools" --color "BFDADC" --description "Developer tools and utilities"
gh label create "component-examples" --color "FFC0CB" --description "Example code and demos"
gh label create "component-buzzer" --color "FFD700" --description "Buzzer/audio functionality"
gh label create "component-display" --color "E99695" --description "Display/screen functionality"
```

---

## Bulk Label Creation Script

Save this as `create-labels.sh` and run with `bash create-labels.sh`:

```bash
#!/bin/bash

# Priority Labels
gh label create "priority-critical" --color "B60205" --description "Must complete immediately - blocks other work" --force
gh label create "priority-high" --color "D93F0B" --description "Complete in current sprint" --force
gh label create "priority-medium" --color "FBCA04" --description "Schedule for upcoming sprint" --force
gh label create "priority-low" --color "0E8A16" --description "Nice to have, schedule when capacity allows" --force

# Sprint Labels
gh label create "sprint-1" --color "5319E7" --description "Sprint 1: Critical API & Core Logic" --force
gh label create "sprint-2" --color "D876E3" --description "Sprint 2: ML & Data Processing Logic" --force
gh label create "sprint-3" --color "1D76DB" --description "Sprint 3: Protocols, Communication, and Utilities" --force
gh label create "sprint-4" --color "0E8A16" --description "Sprint 4: System, Storage, and Integration" --force
gh label create "sprint-5" --color "BFD4F2" --description "Sprint 5: Examples, Demos, and Edge Packages" --force

# Type Labels
gh label create "test" --color "7FE9A3" --description "Test-related work" --force
gh label create "coverage" --color "C5DEF5" --description "Coverage improvement specific" --force
gh label create "unit-test" --color "BFDB38" --description "Unit test development" --force
gh label create "integration-test" --color "5E7D4F" --description "Integration test development" --force
gh label create "documentation" --color "0075CA" --description "Test documentation" --force

# Status Labels
gh label create "ready" --color "0E8A16" --description "Ready to start - all prerequisites met" --force
gh label create "in-progress" --color "FBCA04" --description "Currently being worked on" --force
gh label create "blocked" --color "D93F0B" --description "Blocked by dependencies or issues" --force
gh label create "review" --color "5319E7" --description "Ready for code review" --force
gh label create "done" --color "0B4F30" --description "Completed and merged" --force

# Component Labels
gh label create "component-api" --color "1D76DB" --description "Core API classes (Drone, Controllers)" --force
gh label create "component-protocol" --color "006B75" --description "Protocol implementation" --force
gh label create "component-system" --color "D876E3" --description "System-level functionality" --force
gh label create "component-storage" --color "FEF2C0" --description "Storage operations" --force
gh label create "component-ml" --color "F9D0C4" --description "Machine learning features" --force
gh label create "component-autonomous" --color "C2E0C6" --description "Autonomous flight features" --force
gh label create "component-tools" --color "BFDADC" --description "Developer tools and utilities" --force
gh label create "component-examples" --color "FFC0CB" --description "Example code and demos" --force
gh label create "component-buzzer" --color "FFD700" --description "Buzzer/audio functionality" --force
gh label create "component-display" --color "E99695" --description "Display/screen functionality" --force

echo "✅ All labels created successfully!"
```

---

## Label Usage Guidelines

### How to Apply Labels

1. **When Creating an Issue:**
   - Apply one priority label (critical, high, medium, or low)
   - Apply one sprint label (sprint-1 through sprint-5)
   - Apply at least one type label (test, unit-test, integration-test)
   - Apply relevant component labels
   - Add `ready` status label if ready to start

2. **During Development:**
   - Change status from `ready` to `in-progress` when you start work
   - Add `blocked` if you encounter blockers
   - Change to `review` when ready for code review

3. **After Completion:**
   - Change status to `done` when merged
   - Keep all other labels for tracking and reporting

### Example Label Combination

For "Add unit tests for Drone color LED methods":
```
priority-critical
sprint-1
test
coverage
unit-test
component-api
ready
```

---

## Filtering and Searching

### Useful Label Queries

**View all Sprint 1 tasks:**
```
is:issue label:sprint-1
```

**View critical priorities in progress:**
```
is:issue label:priority-critical label:in-progress
```

**View ready tasks for current sprint:**
```
is:issue label:sprint-1 label:ready
```

**View all API component tests:**
```
is:issue label:component-api label:test
```

**View blocked issues:**
```
is:issue label:blocked is:open
```

---

## Maintenance

- Review labels quarterly for relevance
- Archive completed sprint labels after sprint completion
- Add new component labels as needed
- Keep descriptions up to date
- Remove unused labels to reduce clutter

---

**Created:** 2025-11-17  
**Last Updated:** 2025-11-17  
**Maintained By:** Test Coverage Project Team
