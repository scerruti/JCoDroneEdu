#!/usr/bin/env python3
"""
Check and optionally install a specific version of codrone-edu package.

This script checks if a specific version of codrone-edu is installed in the
virtual environment, and optionally installs/upgrades it if needed.

Usage:
    python3 check_install_package.py <venv_path> <version> [--install]

Arguments:
    venv_path: Path to Python virtual environment
    version: Desired version (e.g., "2.6.0")
    --install: If provided, install/upgrade to the specified version

Output:
    Prints status messages to stdout
    Returns exit code 0 if correct version is installed (or was installed)
    Returns exit code 1 on error
"""

import sys
import os
import subprocess
import re


def get_installed_version(pip_path, package_name="codrone-edu"):
    """
    Get the currently installed version of a package.
    
    Args:
        pip_path: Path to pip executable
        package_name: Name of the package
    
    Returns:
        Version string or None if not installed
    """
    try:
        result = subprocess.run(
            [pip_path, "show", package_name],
            capture_output=True,
            text=True,
            check=False,
            timeout=10
        )
        
        if result.returncode != 0:
            return None
        
        # Parse output for Version: line
        for line in result.stdout.split('\n'):
            if line.startswith('Version:'):
                version = line.split(':', 1)[1].strip()
                return version
        
        return None
    except Exception as e:
        print(f"Error checking installed version: {e}", file=sys.stderr)
        return None


def install_package(pip_path, package_name="codrone-edu", version=None):
    """
    Install or upgrade a package to a specific version.
    
    Args:
        pip_path: Path to pip executable
        package_name: Name of the package
        version: Desired version string
    
    Returns:
        True if installation succeeded, False otherwise
    """
    try:
        package_spec = f"{package_name}=={version}" if version else package_name
        
        print(f"Installing {package_spec}...")
        result = subprocess.run(
            [pip_path, "install", "--upgrade", package_spec],
            capture_output=True,
            text=True,
            check=False,
            timeout=120
        )
        
        if result.returncode != 0:
            print(f"Error installing package: {result.stderr}", file=sys.stderr)
            return False
        
        print(f"Successfully installed {package_spec}")
        return True
    except Exception as e:
        print(f"Error during installation: {e}", file=sys.stderr)
        return False


def main():
    """Main entry point."""
    if len(sys.argv) < 3:
        print("Usage: check_install_package.py <venv_path> <version> [--install]", file=sys.stderr)
        return 1
    
    venv_path = sys.argv[1]
    desired_version = sys.argv[2]
    should_install = "--install" in sys.argv
    
    # Determine pip executable path
    if sys.platform == "win32":
        pip_path = os.path.join(venv_path, "Scripts", "pip")
    else:
        pip_path = os.path.join(venv_path, "bin", "pip")
    
    if not os.path.exists(pip_path):
        print(f"Error: pip not found at {pip_path}", file=sys.stderr)
        print("Please create the virtual environment first", file=sys.stderr)
        return 1
    
    # Check current version
    current_version = get_installed_version(pip_path)
    
    if current_version:
        print(f"Current version: {current_version}")
    else:
        print("Package not installed")
    
    # Normalize versions for comparison (remove any suffixes)
    def normalize_version(v):
        # Extract just the numeric version part (e.g., "2.6.0" from "2.6.0rc1")
        match = re.match(r'^(\d+\.\d+(?:\.\d+)?)', v)
        return match.group(1) if match else v
    
    if current_version:
        current_normalized = normalize_version(current_version)
        desired_normalized = normalize_version(desired_version)
        
        if current_normalized == desired_normalized:
            print(f"✓ Correct version installed: {current_version}")
            return 0
        else:
            print(f"Version mismatch: have {current_version}, want {desired_version}")
    
    # Install if requested
    if should_install:
        if install_package(pip_path, version=desired_version):
            # Verify installation
            new_version = get_installed_version(pip_path)
            if new_version:
                print(f"✓ Successfully installed version: {new_version}")
                return 0
            else:
                print("Error: Installation appeared to succeed but package not found", file=sys.stderr)
                return 1
        else:
            return 1
    else:
        print("Use --install flag to install the correct version")
        return 1


if __name__ == "__main__":
    sys.exit(main())
