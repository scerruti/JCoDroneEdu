#!/usr/bin/env python3
"""
Fetch the latest version of codrone-edu package from PyPI.

This script queries the PyPI JSON API to get the latest stable version
of the codrone-edu package.

Usage:
    python3 fetch_pypi_version.py

Output:
    Prints the version string to stdout (e.g., "2.6.0")
    Returns exit code 0 on success, non-zero on failure.
"""

import sys
import json
import urllib.request
import urllib.error


def fetch_latest_version(package_name="codrone-edu", timeout=10):
    """
    Fetch the latest version of a package from PyPI.
    
    Args:
        package_name: Name of the package on PyPI
        timeout: Request timeout in seconds
    
    Returns:
        Version string or None if fetch failed
    """
    url = f"https://pypi.org/pypi/{package_name}/json"
    
    try:
        with urllib.request.urlopen(url, timeout=timeout) as response:
            data = json.loads(response.read().decode('utf-8'))
            version = data['info']['version']
            return version
    except urllib.error.URLError as e:
        print(f"Error: Failed to connect to PyPI: {e}", file=sys.stderr)
        return None
    except KeyError as e:
        print(f"Error: Unexpected PyPI response format: {e}", file=sys.stderr)
        return None
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        return None


def main():
    """Main entry point."""
    version = fetch_latest_version()
    
    if version:
        # Print just the version to stdout for easy parsing
        print(version)
        return 0
    else:
        # Error message already printed to stderr
        return 1


if __name__ == "__main__":
    sys.exit(main())
