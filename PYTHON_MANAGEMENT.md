# CoDrone EDU Python Library Management

This project now includes automated Python library management through Gradle tasks. This ensures we always have the latest CoDrone EDU library for reference and can track changes automatically.

## Available Tasks

### Core Library Management

```bash
# Check if updates are available
./gradlew checkCodroneVersion

# Create Python virtual environment
./gradlew createPythonVenv

# Update CoDrone EDU library to latest version
./gradlew updateCodroneEdu

# Copy latest library code to reference directory
./gradlew updateReferenceCode

# Fetch and update changelog from Robolink docs
./gradlew updateChangelog

# Complete update workflow (recommended)
./gradlew updateCodroneDocs
```

### Development Tasks

```bash
# Validate Python environment setup
./gradlew validatePythonSetup

# View all Python-related tasks
./gradlew tasks --group python
```

## Workflow

### Initial Setup
```bash
# Set up everything for the first time
./gradlew updateCodroneDocs
```

### Regular Updates
```bash
# Check for updates (run weekly/monthly)
./gradlew checkCodroneVersion

# If updates available, run full update
./gradlew updateCodroneDocs
```

### Development Workflow
```bash
# After updating reference code
./gradlew updateReferenceCode

# Review changes
git diff reference/

# Update MockDrone if needed
# Run tests
./gradlew test

# Commit changes
git add reference/ CODRONE_EDU_METHOD_TRACKING.md
git commit -m "Update CoDrone EDU reference to version X.X"
```

## What Gets Updated

### `reference/codrone_edu/`
- **Source**: Latest from PyPI via pip install
- **Content**: Complete CoDrone EDU Python library source code
- **Excludes**: Python cache files (`__pycache__`, `.pyc`, `.pyo`)

### `reference/version.txt`
- **Auto-generated**: Package information and version details
- **Source**: `pip show codrone-edu` output
- **Contains**: Version, dependencies, update timestamp

### `CODRONE_EDU_METHOD_TRACKING.md`
- **Appends**: Latest changelog from Robolink documentation
- **Source**: https://docs.robolink.com/docs/CoDroneEDU/Python/Python-Changelog
- **Format**: Markdown with timestamps

## Python Environment

### Location
- **Path**: `python-venv/` (git-ignored)
- **Type**: Python 3 virtual environment
- **Packages**: codrone-edu, requests, beautifulsoup4

### Management
- **Created**: Automatically by Gradle tasks
- **Updated**: When running update tasks
- **Isolated**: Separate from system Python

## Automation Benefits

### Version Tracking
- ✅ Always know exact version in use
- ✅ Automatic update checking
- ✅ Build warnings when outdated

### Documentation
- ✅ Automatic changelog integration
- ✅ Version history tracking
- ✅ Change impact assessment

### Development
- ✅ Consistent reference code
- ✅ Easy MockDrone updates
- ✅ Reliable CI/CD integration

## CI/CD Integration

### Version Check (Daily)
```yaml
# Example GitHub Actions
- name: Check CoDrone EDU Version
  run: ./gradlew checkCodroneVersion
```

### Automated Updates (Weekly)
```yaml
# Example automated update workflow
- name: Update CoDrone EDU Reference
  run: ./gradlew updateCodroneDocs
- name: Create PR if changes
  # ... create pull request with changes
```

## Troubleshooting

### Python Environment Issues
```bash
# Recreate environment
rm -rf python-venv/
./gradlew createPythonVenv
```

### Network/PyPI Issues
```bash
# Manual fallback
cd python-venv
./bin/pip install --upgrade codrone-edu
```

### Version Conflicts
```bash
# Force update
./gradlew updateCodroneEdu --rerun-tasks
```

## Manual Override

If automated updates fail, you can still manually copy from an existing installation:

```bash
# From existing venv
cp -r /path/to/your/venv/lib/python3.x/site-packages/codrone_edu reference/

# Update version info manually
echo "VERSION: X.X (manual)" > reference/version.txt
```

---

**Best Practice**: Run `./gradlew checkCodroneVersion` before major development sessions to ensure you're working with current reference code.
