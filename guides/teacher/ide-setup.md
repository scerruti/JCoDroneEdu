---
layout: guide
title: IDE Setup
category: Teacher Guide
permalink: /guides/teacher/ide-setup.html
---

## IntelliJ IDEA Configuration

This guide helps you set up and configure IntelliJ IDEA for your classroom.

### Installation & Project Setup

#### Getting Started

1. **Download Community Edition:**
   - Visit jetbrains.com/idea/download
   - Choose Community Edition (free)
   - Install on all student computers

2. **Create First Project:**
   - File → New → Project
   - Choose "Java" template
   - Set JDK to Java 11+ (may need to download)
   - Name: `DroneProject`

3. **Add JCoDroneEdu Library:**
   - Download JAR file
   - File → Project Structure → Libraries → +
   - Select JCoDroneEdu JAR
   - Apply and OK

#### Project Structure

```
DroneProject/
├── src/
│   ├── Main.java (entry point)
│   ├── SquarePattern.java
│   ├── FlightController.java
│   └── ...
├── lib/
│   └── jcodroneedu.jar
└── out/
    └── (compiled classes, auto-generated)
```

### Configuration for Students

#### First-Time Settings

After IntelliJ opens:

1. **Theme:**
   - File → Settings → Appearance
   - Choose "Light" or "Darcula"
   - Dark is easier on eyes during long sessions

2. **Font Size:**
   - File → Settings → Editor → Font
   - Recommendation: 14pt for easier reading
   - Line spacing: 1.2

3. **Code Style:**
   - File → Settings → Editor → Code Style
   - Choose "Google Java Style" or "Project"
   - Consistent formatting helps learning

4. **Run Configuration:**
   - Run → Edit Configurations
   - Add new Application config
   - Main class: Select your Main.java
   - Click Run (shortcut: Shift+F10)

#### Keyboard Shortcuts

**Essential shortcuts for drone programming:**

| Action | Windows/Linux | Mac |
|--------|---------------|-----|
| Run | Shift+F10 | Ctrl+R |
| Debug | Shift+F9 | Ctrl+D |
| Stop | Ctrl+F2 | Cmd+F2 |
| Format code | Ctrl+Alt+L | Cmd+Alt+L |
| Find | Ctrl+F | Cmd+F |
| Find/Replace | Ctrl+H | Cmd+H |
| Autocomplete | Ctrl+Space | Ctrl+Space |

Create cheat sheet poster for classroom!

### Code Templates

#### Template 1: Basic Drone Program

```java
import com.otabi.jcodroneedu.*;

public class MyDroneProgram {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            System.out.println("✓ Connected");
            System.out.println("Battery: " + drone.getBattery() + "%");
            
            // Your code here
            drone.takeoff();
            
            drone.land();
            System.out.println("✓ Complete");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
}
```

**To create this template in IDE:**
1. File → Settings → Editor → Live Templates
2. Click + → Live Template
3. Abbreviation: `droneprog`
4. Paste code above
5. Context: Java → Statement
6. Save

Now students type `droneprog` and hit Tab to auto-generate!

#### Template 2: Flight Pattern

```java
public static void flySquare(Drone drone) throws Exception {
    for (int side = 0; side < 4; side++) {
        drone.moveForward(0.5);
        Thread.sleep(2000);  // Fly for 2 seconds
        drone.turnLeft(0.5);
        Thread.sleep(1000);  // Turn for 1 second
    }
}
```

### Debugging Setup

#### Enable Debugging

1. **Set Breakpoint:**
   - Click line number in editor
   - Red dot appears

2. **Run in Debug Mode:**
   - Run → Debug (or Shift+F9)
   - Program pauses at breakpoint

3. **Step Through Code:**
   - F8: Step over (next line)
   - F7: Step into (enter function)
   - Shift+F8: Step out (exit function)

4. **Inspect Variables:**
   - Hover over variable name
   - See current value
   - Right-click → Evaluate Expression for complex values

#### Example Debugging Session

```java
public class DebugExample {
    public static void main(String[] args) throws Exception {
        try (Drone drone = new Drone(true)) {
            int battery = drone.getBattery();  // Breakpoint here
            System.out.println("Battery: " + battery);
            
            if (battery < 50) {  // Step through here
                System.out.println("Low battery!");
            }
        }
    }
}
```

### Run Configurations

#### For Different Assignment Types

**Assignment Type: Basic Flight**
- Main class: your.Main
- VM options: (leave empty)
- Program arguments: (leave empty)
- Click Run

**Assignment Type: With File Input**
- Program arguments: `data/input.txt`
- IDE passes as args[0]

**Assignment Type: Multiple Drones**
- VM options: `-XX:+UseG1GC -Xmx2g`
- Allocates more memory for multiple connections

---

## Classroom IDE Management

### Shared Computer Setup

**For shared lab computers:**

1. **User Account per Student:**
   - Each student logs into computer with own account
   - IntelliJ settings saved per user
   - GitHub credentials saved separately

2. **Shared Library:**
   - Install JCoDroneEdu library to global path
   - All users can access
   - Update once, benefits all students

3. **Project Templates:**
   - Store on shared drive or USB
   - Students copy template, not modify original
   - Ensures everyone starts clean

### Bulk Configuration

**For IT teams managing multiple machines:**

```bash
# On Windows (as Admin)
# Copy settings to all user profiles
xcopy "%APPDATA%\JetBrains\IntelliJIdea*\config\options" \
      "\\shared\jb_settings\" /S /Y
```

### IDE Settings Export

**Share your setup:**
1. File → Manage IDE Settings → Export Settings
2. Creates `.zip` file with all preferences
3. Students can import: File → Manage IDE Settings → Import Settings

### Educational License

**For institution accounts:**
1. Go to jetbrains.com/community/education
2. Apply for free license (for schools)
3. Provide proof of .edu email
4. Can supply license keys to students

---

## Troubleshooting

### Issue: "Cannot find Drone class"

**Solution:**
1. Verify JAR added to Libraries (see above)
2. Check Project Structure → Modules → Dependencies
3. Rebuild project: Build → Rebuild Project

### Issue: "Main class not found"

**Solution:**
1. Verify `public static void main(String[] args)` exists
2. Check class is in `src/` folder (not `out/`)
3. Rebuild project

### Issue: Code won't compile

**Checklist:**
- [ ] File saved (Ctrl+S)
- [ ] JDK configured (File → Project Structure)
- [ ] All imports correct
- [ ] No syntax errors (check red squiggles)
- [ ] Library added

### Issue: Program runs but drone doesn't respond

**Test connectivity:**
```java
try (Drone drone = new Drone(true)) {
    System.out.println("Connected!");
} catch (Exception e) {
    System.out.println("Cannot connect: " + e);
}
```

- USB cable connected?
- Drone powered on?
- Restart IDE?

### Issue: IDE freezes during run

**Solutions:**
1. Increase memory: Help → Edit Custom VM Options
   - Change: `-Xmx1024m` to `-Xmx2048m`
2. Run only one drone at a time
3. Close other applications

---

## Advanced IDE Features

### Version Control Integration

**Git from within IDE:**
1. VCS → Get from Version Control
2. Paste GitHub URL
3. Clone button
4. Now shows git status in files

**Commit from IDE:**
- Right-click file → Git → Commit
- Or: Ctrl+K
- Creates commit in GitHub

### Code Inspection

**Automatic code quality checks:**
1. Analyze → Run Inspection by Name
2. Search: "unused variable"
3. IDE highlights unused variables
4. Quick-fix: Alt+Enter → Delete

### Plugin Ecosystem

**Recommended plugins for drone programming:**
1. GitHub Copilot (AI code suggestions)
2. Sonarlint (code quality)
3. GitToolbox (Git improvements)

Install: File → Settings → Plugins → Marketplace

---

## Student Quick-Start Checklist

**Give students this checklist:**

- [ ] IntelliJ installed and opened
- [ ] JDK configured (File → Project Structure)
- [ ] Created `DroneProject` in File system
- [ ] Added JCoDroneEdu library
- [ ] Created `Main.java` with main() method
- [ ] Basic drone connection test passes
- [ ] Compiled and ran first program

**When all checked:** ✓ Ready to fly!

---

Next: [Curriculum Planning]({{ '/guides/teacher/curriculum.html' | relative_url }})
