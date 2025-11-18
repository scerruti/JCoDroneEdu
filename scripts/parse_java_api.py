#!/usr/bin/env python3
"""
Parse Java Drone class to extract methods, signatures, and @pythonEquivalent mappings.

This script extracts:
1. All public methods and their full signatures
2. @pythonEquivalent annotations
3. Design rationale from Javadoc comments
4. Method overloads

Usage:
    python3 scripts/parse_java_api.py <path_to_Drone.java>

Output:
    JSON with Java method details and Python mappings
"""

import sys
import re
import json


def parse_javadoc(lines, start_idx):
    """Parse Javadoc comment before a method."""
    javadoc = []
    python_equivalent = None
    python_reference = None
    since = None
    educational = None
    deprecated = False
    
    i = start_idx
    while i >= 0:
        line = lines[i].strip()
        
        if line.startswith('/**'):
            # Start of javadoc found
            break
        
        if line.startswith('*/'):
            # Inside javadoc
            i -= 1
            continue
        
        # Extract tags
        if '@pythonEquivalent' in line:
            python_equivalent = line.split('@pythonEquivalent')[1].strip()
        elif '@pythonReference' in line:
            python_reference = line.split('@pythonReference')[1].strip()
        elif '@since' in line:
            since = line.split('@since')[1].strip()
        elif '@educational' in line:
            educational = line.split('@educational')[1].strip()
        elif '@deprecated' in line or '@Deprecated' in line:
            deprecated = True
        elif line.startswith('*') and not line.startswith('*/'):
            # Regular javadoc text (for design rationale)
            javadoc.insert(0, line.lstrip('* '))
        
        i -= 1
        if i < 0 or (line.startswith('/**') and i < start_idx - 100):
            break
    
    return {
        'python_equivalent': python_equivalent,
        'python_reference': python_reference,
        'since': since,
        'educational': educational,
        'deprecated': deprecated,
        'javadoc_text': ' '.join(javadoc[:5]) if javadoc else None  # First few lines for context
    }


def extract_method_signature(line):
    """Extract method signature from a Java method declaration."""
    # Remove leading whitespace and access modifiers
    line = line.strip()
    
    # Extract the method name and parameters
    match = re.search(r'(\w+)\s+(\w+)\s*\((.*?)\)\s*(?:throws\s+[\w\s,.<>]+)?(?:\{|;)?', line)
    if match:
        return_type = match.group(1)
        method_name = match.group(2)
        params = match.group(3).strip()
        
        # Parse parameters
        param_list = []
        if params:
            for param in params.split(','):
                param = param.strip()
                if param:
                    # Extract type and name
                    parts = param.rsplit(None, 1)
                    if len(parts) == 2:
                        param_list.append({'type': parts[0], 'name': parts[1]})
        
        return {
            'return_type': return_type,
            'method_name': method_name,
            'parameters': param_list,
            'signature': f"{return_type} {method_name}({params})"
        }
    
    return None


def parse_drone_java(java_path):
    """Parse Drone.java and extract all public methods."""
    
    with open(java_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    results = {
        'methods': [],
        'method_groups': {}  # Group overloaded methods
    }
    
    i = 0
    while i < len(lines):
        line = lines[i]
        
        # Look for public method declarations
        if re.match(r'\s*public\s+\w+\s+\w+\s*\(', line):
            # Found a method declaration
            signature = extract_method_signature(line)
            
            if signature:
                # Parse javadoc
                javadoc = parse_javadoc(lines, i - 1)
                
                method_info = {
                    **signature,
                    **javadoc
                }
                
                results['methods'].append(method_info)
                
                # Group overloaded methods
                method_name = signature['method_name']
                if method_name not in results['method_groups']:
                    results['method_groups'][method_name] = []
                results['method_groups'][method_name].append(method_info)
        
        i += 1
    
    return results


def main():
    if len(sys.argv) != 2:
        print("Usage: parse_java_api.py <Drone.java>", file=sys.stderr)
        return 1
    
    java_path = sys.argv[1]
    
    try:
        results = parse_drone_java(java_path)
        print(json.dumps(results, indent=2))
        return 0
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        import traceback
        traceback.print_exc()
        return 1


if __name__ == "__main__":
    sys.exit(main())
