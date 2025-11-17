# compareApis Gradle Task Documentation

## Overview

The `compareApis` Gradle task compares the Java API implementation with the Python CoDrone EDU API to ensure feature parity. It generates a comprehensive report showing which methods exist in both APIs, which are missing, and provides insights for maintaining compatibility.

## Features

- **Automated API Comparison**: Compares Java methods with Python API methods
- **PyPI Version Fetching**: Can automatically fetch and compare against the latest Python package version
- **Version-Specific Comparison**: Compare against any specific version of the Python API
- **Automated Package Management**: Installs and manages Python package versions in a virtual environment
- **Detailed Reporting**: Generates markdown reports with method mappings and gaps

## Requirements

- **Java 21+**: Required for running Gradle
- **Python 3.8+**: Required for Python helper scripts and package management
- **Internet Connection**: Needed for fetching PyPI information and installing packages

## Usage

### Basic Comparison

Compare against the default configured Python API version (currently 2.6.0):

```bash
./gradlew compareApis
```

This will:
1. Create a Python virtual environment if it doesn't exist
2. Install or verify the configured Python package version
3. Compare Java API methods with Python API methods
4. Generate `API_COMPARISON.md` in the project root

### Compare Against Latest PyPI Version

Fetch the latest version from PyPI and compare:

```bash
./gradlew compareApis -PcompareLatest=true
```

This will:
1. Query PyPI for the latest `codrone-edu` version
2. Install that version in the virtual environment
3. Generate a versioned report: `API_COMPARISON_vs_X.Y.md`

### Compare Against Specific Version

Compare against a specific Python API version:

```bash
./gradlew compareApis -PapiVersion=2.6.0
```

This will:
1. Install the specified version
2. Generate a versioned report: `API_COMPARISON_2.6.0.md`

### Combined Options

You can combine options for more control:

```bash
# Fetch latest and compare with explicit version output
./gradlew compareApis -PcompareLatest=true -PapiVersion=2.5.0
```

## Output Files

The task generates markdown reports in the project root:

- `API_COMPARISON.md` - Default comparison report
- `API_COMPARISON_vs_X.Y.md` - Versioned comparison when using `-PcompareLatest`
- `API_COMPARISON_X.Y.Z.md` - Versioned comparison when using `-PapiVersion`

### Report Contents

Each report includes:

1. **Summary Section**
   - Total Python methods
   - Total Java methods
   - Matched methods count
   - Methods in Python but not in Java
   - Methods in Java but not in Python

2. **Missing Methods Lists**
   - Python methods not implemented in Java
   - Java-specific methods not in Python

3. **Method Mappings**
   - Documented mappings (using `@pythonEquivalent` annotations)
   - Inferred mappings (by naming convention)

## Python Helper Scripts

The task uses two Python helper scripts in the `scripts/` directory:

### fetch_pypi_version.py

Queries the PyPI JSON API to get the latest version of the codrone-edu package.

```bash
# Usage
python3 scripts/fetch_pypi_version.py

# Output
2.6.0
```

### check_install_package.py

Checks if a specific version of a package is installed and optionally installs it.

```bash
# Check version (no installation)
python3 scripts/check_install_package.py reference/python-venv 2.6.0

# Check and install if needed
python3 scripts/check_install_package.py reference/python-venv 2.6.0 --install
```

## Virtual Environment Management

The task automatically manages a Python virtual environment in `reference/python-venv/`:

- **Creation**: Created automatically on first run
- **Package Installation**: Managed automatically based on requested version
- **Location**: `reference/python-venv/` (gitignored)

To manually reset the virtual environment:

```bash
rm -rf reference/python-venv
./gradlew compareApis
```

## Troubleshooting

### Python Not Found

**Error**: `Failed to create Python virtual environment`

**Solution**: Ensure Python 3 is installed and available in your PATH:

```bash
python3 --version  # Should show 3.8 or higher
which python3      # Should show path to Python
```

### PyPI Connection Failed

**Error**: `Failed to fetch version from PyPI`

**Solution**: 
1. Check your internet connection
2. Verify you can access https://pypi.org
3. If behind a proxy, configure Python to use it:

```bash
export HTTP_PROXY=http://proxy:port
export HTTPS_PROXY=http://proxy:port
./gradlew compareApis
```

### Package Installation Failed

**Error**: `Error installing package`

**Solution**:
1. Try manual installation:
   ```bash
   reference/python-venv/bin/pip install --upgrade codrone-edu==2.6.0
   ```
2. Check for pip upgrade:
   ```bash
   reference/python-venv/bin/pip install --upgrade pip
   ```
3. If all else fails, recreate the virtual environment

### Version Mismatch Warnings

**Warning**: `Version mismatch: have 2.6, want 2.6.0`

This is usually harmless - PyPI sometimes returns shortened versions (2.6 vs 2.6.0). The task will automatically reinstall to ensure exact version match.

### macOS-Specific Issues

#### Security Warnings

macOS may show security warnings for Python scripts. Allow them in System Preferences > Security & Privacy.

#### Python 2 vs Python 3

macOS includes Python 2 by default. Ensure you're using Python 3:

```bash
# Check version
python3 --version

# If Python 3 is not installed, use Homebrew
brew install python3
```

#### Virtual Environment Permissions

If you encounter permission errors:

```bash
# Fix ownership
sudo chown -R $USER:staff reference/python-venv

# Or recreate with correct permissions
rm -rf reference/python-venv
./gradlew compareApis
```

## Integration with CI/CD

The `compareApis` task can be integrated into CI/CD pipelines:

```yaml
# Example GitHub Actions workflow
- name: Compare APIs
  run: ./gradlew compareApis -PcompareLatest=true
  
- name: Upload Comparison Report
  uses: actions/upload-artifact@v3
  with:
    name: api-comparison
    path: API_COMPARISON_*.md
```

## Development Notes

### How It Works

1. **Version Resolution**: Determines target Python API version (from property, PyPI, or default)
2. **Environment Setup**: Creates Python virtual environment if needed
3. **Package Management**: Verifies/installs correct Python package version
4. **Method Extraction**: 
   - Parses `Drone.java` for public methods and `@pythonEquivalent` annotations
   - Uses hardcoded list of documented Python methods from official docs
5. **Comparison**: Matches methods using annotations and naming conventions
6. **Report Generation**: Creates markdown report with findings

### Method Mapping Strategy

The task uses two strategies to map Java methods to Python equivalents:

1. **Documented Mappings**: Uses `@pythonEquivalent` Javadoc annotations
   ```java
   /**
    * @pythonEquivalent set_drone_LED
    */
   public void setDroneLED(...)
   ```

2. **Inferred Mappings**: Converts Java camelCase to Python snake_case
   - `getTrim()` → `get_trim()`
   - `setRoll()` → `set_roll()`

### Future Enhancements

Potential improvements for future versions:

- [ ] Parse Python source code directly instead of using hardcoded list
- [ ] Support for comparing method signatures and parameters
- [ ] Interactive HTML report generation
- [ ] Automated GitHub issue creation for missing methods
- [ ] Support for comparing multiple versions simultaneously

## Related Documentation

- [README.md](README.md) - Project overview and setup
- [CHANGELOG.md](CHANGELOG.md) - Version history
- [API_COMPARISON_SUMMARY.md](API_COMPARISON_SUMMARY.md) - Historical comparison summaries
- [Python API Documentation](https://docs.robolink.com/docs/CoDroneEDU/Python/Function-Documentation)

## Contributing

When adding new methods to the Java API:

1. Add the `@pythonEquivalent` annotation to the Javadoc
2. Run `./gradlew compareApis` to verify mapping
3. Update the report in your PR

When Python API is updated:

1. Run `./gradlew compareApis -PcompareLatest=true`
2. Review new methods in the report
3. Consider implementing missing methods or documenting why they're not needed

## Support

For issues or questions:

1. Check [Troubleshooting](#troubleshooting) section above
2. Review existing GitHub issues
3. Create a new issue with:
   - Gradle version (`./gradlew --version`)
   - Python version (`python3 --version`)
   - Operating system
   - Full error output
   - Steps to reproduce
