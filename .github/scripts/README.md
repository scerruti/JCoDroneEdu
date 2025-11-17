# Test Coverage Improvement Project - Scripts

This directory contains helper scripts to automate the setup of the Test Coverage Improvement Project.

## Available Scripts

### create-labels.sh

Creates all 29 labels needed for the project:
- 4 Priority labels (critical, high, medium, low)
- 5 Sprint labels (sprint-1 through sprint-5)
- 5 Type labels (test, coverage, unit-test, integration-test, documentation)
- 5 Status labels (ready, in-progress, blocked, review, done)
- 10 Component labels (api, protocol, system, storage, ml, autonomous, tools, examples, buzzer, display)

**Usage:**
```bash
cd .github/scripts
bash create-labels.sh
```

**Requirements:**
- GitHub CLI (`gh`) installed and authenticated
- Repository write access

### create-milestones.sh

Creates all 5 milestones for the project sprints:
- Sprint 1: Critical API & Core Logic (2 weeks)
- Sprint 2: ML & Data Processing Logic (4 weeks)
- Sprint 3: Protocols, Communication, and Utilities (6 weeks)
- Sprint 4: System, Storage, and Integration (8 weeks)
- Sprint 5: Examples, Demos, and Edge Packages (10 weeks)

**Usage:**
```bash
cd .github/scripts
bash create-milestones.sh
```

**Requirements:**
- GitHub CLI (`gh`) installed and authenticated
- Repository write access
- Python 3 (optional, for cross-platform date calculation)

## Quick Setup

To set up the entire project structure:

```bash
cd /path/to/JCoDroneEdu/.github/scripts

# Create all labels
bash create-labels.sh

# Create all milestones
bash create-milestones.sh

# Issues should be created manually or with a custom script
# See ../PROJECT_SETUP_GUIDE.md for details
```

## Manual Setup

If you prefer manual setup or don't have GitHub CLI installed, follow the instructions in:
- `../ LABELS_CONFIGURATION.md` - for labels
- `../MILESTONES_CONFIGURATION.md` - for milestones
- `../PROJECT_SETUP_GUIDE.md` - for complete setup guide

## Troubleshooting

### "gh: command not found"

Install GitHub CLI from: https://cli.github.com/

### "gh: authentication required"

Authenticate with:
```bash
gh auth login
```

### "permission denied"

Ensure scripts are executable:
```bash
chmod +x *.sh
```

### "milestone already exists"

This is normal if running scripts multiple times. Existing milestones are skipped.

### "label already exists"

This is normal if running scripts multiple times. The `--force` flag updates existing labels.

## Documentation

For detailed information, see:
- `../TEST_COVERAGE_EPIC.md` - Epic issue content
- `../SPRINT_1_CRITICAL_API.md` - Sprint 1 details
- `../SPRINT_2_ML_DATA.md` - Sprint 2 details
- `../SPRINT_3_PROTOCOLS.md` - Sprint 3 details
- `../SPRINT_4_SYSTEM_INTEGRATION.md` - Sprint 4 details
- `../SPRINT_5_EXAMPLES_EDGE.md` - Sprint 5 details
- `../LABELS_CONFIGURATION.md` - Label definitions
- `../MILESTONES_CONFIGURATION.md` - Milestone definitions
- `../PROJECT_SETUP_GUIDE.md` - Complete setup guide

## Support

If you encounter issues with these scripts, please:
1. Check the troubleshooting section above
2. Review the documentation
3. Create an issue with the `question` label
4. Contact the project maintainers

---

**Created:** 2025-11-17  
**Last Updated:** 2025-11-17
