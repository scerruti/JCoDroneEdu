#!/usr/bin/env python3
"""
Parse the Python codrone_edu drone.py file to extract all public methods.

This script identifies:
1. All public methods (not starting with _)
2. Whether they're documented (from the official list)
3. Whether they're deprecated
4. Their function signatures

Usage:
    python3 scripts/parse_python_api.py <path_to_drone.py> <path_to_documented_methods.txt>

Output:
    JSON with categorized methods and signatures
"""

import sys
import ast
import json
import re


def is_deprecated(node):
    """Check if a function has @deprecated in its docstring."""
    if not ast.get_docstring(node):
        return False
    docstring = ast.get_docstring(node)
    # Look for various forms of deprecation markers
    deprecation_markers = [
        'deprecated',
        '@deprecated',
        '.. deprecated::',
        'DEPRECATED'
    ]
    docstring_lower = docstring.lower()
    return any(marker.lower() in docstring_lower for marker in deprecation_markers)


def get_function_signature(node):
    """Extract a readable function signature."""
    args = []
    
    # Regular arguments
    for arg in node.args.args:
        if arg.arg != 'self':  # Skip self parameter
            args.append(arg.arg)
    
    # Handle *args
    if node.args.vararg:
        args.append(f"*{node.args.vararg.arg}")
    
    # Handle **kwargs
    if node.args.kwarg:
        args.append(f"**{node.args.kwarg.arg}")
    
    return f"({', '.join(args)})"


def parse_drone_file(drone_py_path, documented_methods):
    """Parse drone.py and categorize all public methods."""
    
    with open(drone_py_path, 'r', encoding='utf-8') as f:
        source = f.read()
    
    try:
        tree = ast.parse(source)
    except SyntaxError as e:
        print(f"Error parsing {drone_py_path}: {e}", file=sys.stderr)
        return None
    
    # Find the main Drone class
    drone_class = None
    for node in ast.walk(tree):
        if isinstance(node, ast.ClassDef) and node.name == 'Drone':
            drone_class = node
            break
    
    if not drone_class:
        print("Error: Could not find Drone class", file=sys.stderr)
        return None
    
    results = {
        'tier1_documented': [],      # In official docs
        'tier2_undocumented': [],     # Public, not deprecated, not documented
        'deprecated_methods': [],     # Marked as deprecated
        'internal_methods': []        # Starting with _ (for reference)
    }
    
    # Process all methods in the Drone class
    for item in drone_class.body:
        if not isinstance(item, ast.FunctionDef):
            continue
        
        method_name = item.name
        signature = get_function_signature(item)
        docstring = ast.get_docstring(item) or ""
        deprecated = is_deprecated(item)
        
        method_info = {
            'name': method_name,
            'signature': signature,
            'docstring_preview': docstring[:100].replace('\n', ' ') if docstring else ""
        }
        
        # Categorize
        if method_name.startswith('_'):
            # Internal/private method
            results['internal_methods'].append(method_info)
        elif deprecated:
            # Deprecated method
            results['deprecated_methods'].append(method_info)
        elif method_name in documented_methods:
            # Tier 1: Documented in official docs
            results['tier1_documented'].append(method_info)
        else:
            # Tier 2: Public, not deprecated, not documented
            results['tier2_undocumented'].append(method_info)
    
    return results


def main():
    if len(sys.argv) != 3:
        print("Usage: parse_python_api.py <drone.py> <documented_methods.txt>", file=sys.stderr)
        return 1
    
    drone_py_path = sys.argv[1]
    documented_methods_file = sys.argv[2]
    
    # Load documented methods
    documented_methods = set()
    try:
        with open(documented_methods_file, 'r') as f:
            for line in f:
                line = line.strip()
                if line and not line.startswith('#'):
                    documented_methods.add(line)
    except FileNotFoundError:
        print(f"Error: {documented_methods_file} not found", file=sys.stderr)
        return 1
    
    # Parse drone.py
    results = parse_drone_file(drone_py_path, documented_methods)
    
    if results is None:
        return 1
    
    # Output as JSON
    print(json.dumps(results, indent=2))
    return 0


if __name__ == "__main__":
    sys.exit(main())
