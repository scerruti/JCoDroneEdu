#!/usr/bin/env python3
"""
Enhanced API comparison that generates a comprehensive two-tier report.

This script:
1. Parses Python API (Tier 1 documented + Tier 2 undocumented)
2. Parses Java API with signatures
3. Maps Java methods to Python equivalents
4. Generates enhanced markdown report with design rationale

Usage:
    python3 generate_enhanced_comparison.py <java_version> <python_version> <output_file>
"""

import sys
import os
import json
from parse_python_api import parse_drone_file
from parse_java_api import parse_drone_java


def generate_enhanced_report(java_version, python_version, output_file,
                            drone_py_path, drone_java_path, documented_methods_file):
    """Generate the enhanced two-tier comparison report."""
    
    report = []
    
    # Header
    report.append("# Enhanced API Comparison Report")
    report.append("")
    report.append(f"**Java Version:** {java_version}")
    report.append(f"**Python API Version:** {python_version}")
    report.append("")
    
    # Overview
    report.append("## Overview")
    report.append("")
    report.append("This report provides a comprehensive two-tier API comparison:")
    report.append("")
    report.append("- **Tier 1 (Must-Have)**: Methods from the official Python documentation that should be implemented in Java")
    report.append("- **Tier 2 (Awareness)**: Non-deprecated public Python methods not in official docs")
    report.append("- **Design Decisions**: Intentional differences and rationale")
    report.append("")
    
    # Load documented methods
    documented_methods = set()
    with open(documented_methods_file, 'r') as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith('#'):
                documented_methods.add(line)
    
    # Parse Python API
    python_data = parse_drone_file(drone_py_path, documented_methods)
    if not python_data:
        print("Error: Could not parse Python API", file=sys.stderr)
        return False
    
    tier1_methods = {m['name']: m for m in python_data['tier1_documented']}
    tier2_methods = {m['name']: m for m in python_data['tier2_undocumented']}
    
    # Parse Java API
    java_data = parse_drone_java(drone_java_path)
    if not java_data:
        print("Error: Could not parse Java API", file=sys.stderr)
        return False
    
    # Build mappings
    python_to_java = {}  # python_method -> [java_methods]
    java_signatures = {}  # java_method -> [signatures]
    
    for method_info in java_data['methods']:
        java_method = method_info['method_name']
        signature = method_info['signature']
        python_equiv = method_info.get('python_equivalent')
        
        # Store signature
        if java_method not in java_signatures:
            java_signatures[java_method] = []
        java_signatures[java_method].append(signature)
        
        # Map to Python
        if python_equiv:
            if python_equiv not in python_to_java:
                python_to_java[python_equiv] = []
            python_to_java[python_equiv].append(java_method)
    
    # Also check by naming convention
    def to_camel_case(snake_str):
        components = snake_str.split('_')
        return components[0] + ''.join(x.title() for x in components[1:])
    
    for py_method in list(tier1_methods.keys()) + list(tier2_methods.keys()):
        if py_method not in python_to_java:
            camel = to_camel_case(py_method)
            if camel in java_signatures:
                python_to_java[py_method] = [camel]
    
    # Calculate statistics
    tier1_matched = {pm: python_to_java[pm] for pm in tier1_methods if pm in python_to_java}
    tier1_missing = [pm for pm in tier1_methods if pm not in python_to_java]
    tier2_present = [pm for pm in tier2_methods if pm in python_to_java]
    
    all_mapped_java = set()
    for java_methods in python_to_java.values():
        all_mapped_java.update(java_methods)
    java_only = [jm for jm in java_signatures.keys() if jm not in all_mapped_java]
    
    # Summary Statistics
    report.append("## Summary Statistics")
    report.append("")
    report.append("| Category | Count |")
    report.append("|----------|-------|")
    report.append(f"| **Python Tier 1 (Documented)** | {len(tier1_methods)} |")
    report.append(f"| **Python Tier 2 (Undocumented Public)** | {len(tier2_methods)} |")
    report.append(f"| **Java Public Methods** | {len(java_signatures)} |")
    report.append(f"| **Tier 1 Matched** | {len(tier1_matched)} |")
    report.append(f"| **Tier 1 Missing** | {len(tier1_missing)} |")
    report.append(f"| **Tier 2 with Java Equivalent** | {len(tier2_present)} |")
    report.append(f"| **Java-Only Methods** | {len(java_only)} |")
    report.append("")
    
    # Tier 1 Mapping Table
    report.append("## Tier 1: Official Python API → Java Implementation")
    report.append("")
    report.append(f"### ✅ Implemented Methods ({len(tier1_matched)}/{len(tier1_methods)})")
    report.append("")
    report.append("| Python Method | Java Method(s) | Signature(s) |")
    report.append("|---------------|----------------|--------------|")
    
    for py_method in sorted(tier1_matched.keys()):
        java_methods = tier1_matched[py_method]
        java_list = "<br>".join(f"`{jm}`" for jm in java_methods)
        sigs = []
        for jm in java_methods:
            sigs.extend(java_signatures.get(jm, []))
        sig_list = "<br>".join(sig.replace("|", "\\|") for sig in sigs[:3])  # Limit to 3
        report.append(f"| `{py_method}` | {java_list} | {sig_list} |")
    
    report.append("")
    
    if tier1_missing:
        report.append(f"### ⚠️ Missing from Java ({len(tier1_missing)})")
        report.append("")
        report.append("These methods are documented in the official Python API but not yet implemented in Java:")
        report.append("")
        report.append("| Python Method | Recommendation |")
        report.append("|---------------|----------------|")
        for method in sorted(tier1_missing):
            report.append(f"| `{method}` | **SHOULD IMPLEMENT** |")
        report.append("")
    
    # Tier 2
    if tier2_methods:
        report.append("## Tier 2: Non-Documented Public Python Methods")
        report.append("")
        report.append("These are public methods in `drone.py` that are:")
        report.append("- Not in the official documentation")
        report.append("- Not deprecated")
        report.append("- Not internal (no leading underscore)")
        report.append("")
        report.append("**Note**: Implementation is optional. Track for awareness of Python API surface.")
        report.append("")
        report.append("| Python Method | Status in Java |")
        report.append("|---------------|----------------|")
        
        for method in sorted(tier2_methods.keys()):
            status = "✓ Has equivalent" if method in python_to_java else "○ Not implemented"
            report.append(f"| `{method}` | {status} |")
        report.append("")
    
    # Design Decisions
    report.append("## Design Decisions & Rationale")
    report.append("")
    
    # Method overloading
    report.append("### Method Overloading")
    report.append("")
    report.append("Java provides method overloading for improved usability:")
    report.append("")
    
    overloaded = {m: sigs for m, sigs in java_signatures.items() if len(sigs) > 1}
    if overloaded:
        report.append("| Java Method | Overloads | Rationale |")
        report.append("|-------------|-----------|-----------|")
        for method in sorted(overloaded.keys())[:20]:
            count = len(overloaded[method])
            report.append(f"| `{method}` | {count} | Provides convenience methods with default parameters |")
        if len(overloaded) > 20:
            report.append(f"| ... | | *({len(overloaded) - 20} more overloaded methods)* |")
    else:
        report.append("*No significant overloading patterns detected.*")
    report.append("")
    
    # Java-specific methods
    report.append("### Java-Specific Methods")
    report.append("")
    report.append("These methods exist only in Java for platform-specific needs:")
    report.append("")
    if java_only:
        report.append("| Java Method | Rationale |")
        report.append("|-------------|-----------|")
        for method in sorted(java_only)[:20]:
            rationale = "Java-specific convenience or internal method"
            if "Exception" in method:
                rationale = "Java exception handling pattern"
            elif method.startswith("get") and "Object" in method:
                rationale = "Returns strongly-typed object"
            elif method.startswith("auto"):
                rationale = "Convenience method for Java developers"
            report.append(f"| `{method}` | {rationale} |")
        if len(java_only) > 20:
            report.append(f"| ... | *({len(java_only) - 20} more methods)* |")
    else:
        report.append("*All Java methods have Python equivalents.*")
    report.append("")
    
    # Naming conventions
    report.append("### Naming Conventions")
    report.append("")
    report.append("- **Python**: snake_case (e.g., `get_battery`, `set_drone_LED`)")
    report.append("- **Java**: camelCase (e.g., `getBattery`, `setDroneLED`)")
    report.append("- Mapping is automatic via `@pythonEquivalent` annotations")
    report.append("")
    
    # Type system
    report.append("### Type System Differences")
    report.append("")
    report.append("- **Python**: Dynamic typing, flexible parameter types")
    report.append("- **Java**: Static typing, compile-time type checking")
    report.append("- Java provides additional type safety and IDE support")
    report.append("")
    
    # Compliance summary
    report.append("## Compliance Summary")
    report.append("")
    compliance = int((len(tier1_matched) / len(tier1_methods) * 100)) if tier1_methods else 100
    report.append(f"**Tier 1 Compliance**: {compliance}% ({len(tier1_matched)}/{len(tier1_methods)})")
    report.append("")
    
    if not tier1_missing:
        report.append("✅ **Full compliance** with official Python documentation!")
    else:
        report.append(f"⚠️ **{len(tier1_missing)} methods** from official docs not yet implemented")
    report.append("")
    
    report.append("---")
    report.append("*Generated by enhanced compareApis task*")
    report.append("")
    
    # Write report
    with open(output_file, 'w') as f:
        f.write('\n'.join(report))
    
    print(f"✅ Enhanced report generated: {output_file}")
    print(f"   Tier 1 compliance: {compliance}% ({len(tier1_matched)}/{len(tier1_methods)})")
    print(f"   Tier 2 tracked: {len(tier2_methods)} undocumented methods")
    print(f"   Java methods: {len(java_signatures)}")
    
    return True


def main():
    if len(sys.argv) != 4:
        print("Usage: generate_enhanced_comparison.py <java_version> <python_version> <output_file>", file=sys.stderr)
        return 1
    
    java_version = sys.argv[1]
    python_version = sys.argv[2]
    output_file = sys.argv[3]
    
    # Hardcoded paths - adjust as needed
    drone_py_path = "reference/python-venv/lib/python3.12/site-packages/codrone_edu/drone.py"
    drone_java_path = "src/main/java/com/otabi/jcodroneedu/Drone.java"
    documented_methods_file = "build/documented_methods.txt"
    
    if not os.path.exists(drone_py_path):
        print(f"Error: {drone_py_path} not found", file=sys.stderr)
        return 1
    
    if not os.path.exists(drone_java_path):
        print(f"Error: {drone_java_path} not found", file=sys.stderr)
        return 1
    
    if not os.path.exists(documented_methods_file):
        print(f"Error: {documented_methods_file} not found", file=sys.stderr)
        return 1
    
    success = generate_enhanced_report(java_version, python_version, output_file,
                                      drone_py_path, drone_java_path, documented_methods_file)
    
    return 0 if success else 1


if __name__ == "__main__":
    sys.exit(main())
