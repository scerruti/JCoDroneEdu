# CoDrone EDU Teacher Guide

**A Comprehensive Guide for Educators Using CoDrone EDU in Java**

This guide is designed to help you successfully integrate CoDrone EDU into your computer science classroom, whether you're teaching AP Computer Science A, an introductory programming course, or a robotics elective. We've written this specifically for teachers who may not have an extensive computer science background, providing clear explanations and step-by-step guidance.

---

## Table of Contents

1. [Educational Philosophy](#educational-philosophy)
2. [Getting Started - Setup & Administration](#getting-started---setup--administration)
3. [GitHub Classroom Integration](#github-classroom-integration)
4. [Managing Student GitHub Accounts](#managing-student-github-accounts)
5. [IDE & Development Environment](#ide--development-environment)
6. [Curriculum Design Framework](#curriculum-design-framework)
7. [Standards Alignment](#standards-alignment)
8. [CoDrone EDU Hardware Management](#codrone-edu-hardware-management)
9. [Common Lesson Plans](#common-lesson-plans)
10. [Glossary of Terms](#glossary-of-terms)

---

## Educational Philosophy

### Why Drones for Computer Science Education?

Programming a drone provides immediate, tangible feedback that makes abstract programming concepts concrete. When students write `drone.turnRight(90)`, they see the physical result immediately - the drone actually turns. This connection between code and real-world behavior helps students:

**Build Mental Models:**
- **Cause and Effect**: Code execution has visible consequences
- **Debugging**: Physical behavior reveals logical errors immediately
- **Sequence Matters**: Order of operations is obvious when the drone follows instructions

**Engage Different Learning Styles:**
- **Visual Learners**: See the drone's movement patterns
- **Kinesthetic Learners**: Manipulate physical hardware
- **Analytical Learners**: Problem-solve with sensor data
- **Creative Learners**: Design custom flight patterns

**Connect to Real-World Applications:**
- Delivery drones (Amazon, UPS)
- Agricultural monitoring
- Search and rescue operations
- Photography and cinematography
- Infrastructure inspection

### Core Teaching Principles

**1. Safety First, Always**
Every lesson must emphasize safety:
- Clear flight zones marked in classroom
- Emergency stop procedures reviewed daily
- Battery monitoring as a programming requirement
- Student pairs: one flies, one monitors

**2. Incremental Complexity**
Start simple and build systematically:
- Week 1: Connect and takeoff/land
- Week 2: Basic movement (forward, backward)
- Week 3: Turns and patterns
- Week 4: Sensors and decision-making
- Week 5+: Complex projects

**3. Fail Forward Philosophy**
Crashes are learning opportunities:
- "Why did the drone hit the wall?" leads to discussions about:
  - Loop conditions
  - Sensor reading frequency
  - Distance calculations
- Encourage experimentation within safety bounds
- Celebrate creative solutions, even if they fail initially

**4. Collaborative Problem-Solving**
Programming drones works best in pairs:
- **Driver**: Types the code
- **Navigator**: Reads instructions, spots errors, tracks drone
- Roles switch regularly (every 15-20 minutes)
- Both students responsible for understanding all code

**5. Cross-Curricular Connections**
CoDrone programming naturally integrates:
- **Physics**: Forces, acceleration, velocity, air pressure
- **Mathematics**: Angles, distance, geometry, coordinate systems
- **Engineering**: Design constraints, optimization, testing
- **Problem-Solving**: Decomposition, pattern recognition, debugging

---

## Getting Started - Setup & Administration

### Classroom Requirements

**Physical Space:**
- Minimum 3m x 3m clear area for flight zone
- Marked boundaries (tape on floor works well)
- Away from windows, fans, and fragile objects
- Good lighting (helps with student monitoring)

**Computer Setup:**
- One computer per student pair
- Windows, macOS, or Linux
- USB ports for controller connection
- Java Development Kit (JDK) 11 or newer installed
- IDE installed (VS Code, IntelliJ IDEA, or BlueJ recommended)

**Network Considerations:**
- Bluetooth can be unreliable in crowded networks
- USB connection to controller is most reliable
- Some school firewalls may block necessary ports
- Test connections before first class session

### Initial Hardware Setup

**Before First Class:**

1. **Charge All Batteries**
   - Drone batteries: 2-3 hours for full charge
   - Controller batteries: Rechargeable or use fresh AAs
   - Have spares available for rotation
   - Label batteries with numbers/colors for tracking

2. **Pair Controllers with Drones**
   - Each controller permanently paired with one drone
   - Label matching sets (Drone 1 ↔ Controller 1)
   - Test all pairs before class
   - Document any problematic units

3. **Install Software**
   - JDK on all computers
   - IDE of choice
   - CoDrone EDU library (JAR file or Maven dependency)
   - Test program on each computer

4. **Prepare Safety Materials**
   - Emergency stop procedures posted
   - Battery safety information
   - Flight zone boundaries marked
   - Safety checklist for each station

### Student Account Setup

**Recommended Approach:**

**Option A: School Google Workspace**
- Students use school Google accounts
- Enable GitHub via Google SSO (Single Sign-On)
- Easier for IT departments to manage
- Parents already signed consent forms

**Option B: GitHub Accounts**
- Students create GitHub accounts with school emails
- Parent/guardian consent required (under 13)
- Useful skill for college/career portfolios
- See "Managing Student GitHub Accounts" section below

**Option C: Lab Computers Only**
- Students work only on school machines
- No GitHub integration needed
- Simpler but less exposure to version control
- Code doesn't follow students home

### Classroom Management Tips

**Station Setup:**
- Pairs assigned to specific computers/drone sets
- Rotation schedule if more students than drones
- Clear procedures for:
  - Getting hardware from storage
  - Starting up computers
  - Connecting drones
  - Ending class (land, disconnect, store)

**Time Management:**
- First 5 min: Setup and battery check
- Next 5-10 min: Instruction/demonstration
- Main 30-35 min: Coding and testing
- Last 10 min: Cleanup, reflection, next steps

**Behavior Expectations:**
- Drone flies only in designated zone
- One program running per drone
- Emergency stop if anything unexpected
- Report crashes immediately
- No flying near people or faces

---

## GitHub Classroom Integration

GitHub Classroom is a free tool that helps you distribute assignments, collect student work, and provide feedback. It integrates with GitHub to create private repositories for each student.

### Why Use GitHub Classroom?

**Benefits:**
- Automatic repository creation for each student
- Built-in deadline tracking
- Easy code review and feedback
- Students learn professional workflows
- Version history shows student progress
- Works with autograding (optional)

**When NOT to Use:**
- Students under 13 (COPPA restrictions without parent consent)
- School blocks GitHub
- First few weeks (add complexity gradually)
- Limited tech support available

### Setting Up GitHub Classroom

**Step 1: Create Organization**
1. Go to [GitHub](https://github.com) and sign in
2. Click "+" → "New organization"
3. Choose "Free" plan
4. Name it: "YourSchool-CSA-2025" (or similar)
5. Add your school email

**Step 2: Create Classroom**
1. Go to [classroom.github.com](https://classroom.github.com)
2. Click "New classroom"
3. Select your organization
4. Name it: "AP CSA Period 3" (or similar)
5. Add student roster (optional, can do later)

**Step 3: Create First Assignment**
1. Click "New assignment"
2. Choose "Individual assignment"
3. Title: "L0101 First Flight"
4. Set deadline (optional)
5. Choose starter code repository (optional)
6. Click "Create assignment"
7. Copy the assignment link to share with students

**Step 4: Students Accept Assignment**
1. Students click your assignment link
2. They authorize GitHub Classroom
3. GitHub creates private repo for them
4. They clone to their computer
5. They code, commit, push solutions
6. You review their work on GitHub

### Assignment Workflow Example

**Teacher Preparation:**
```
1. Create assignment: "L0104 Variables - Square Flight"
2. Add starter code (optional):
   - SquareFlight.java with TODO comments
   - README.md with instructions
   - build.gradle for dependencies
3. Set deadline: Friday 11:59 PM
4. Post assignment link on Canvas/Google Classroom
```

**Student Workflow:**
```
1. Click assignment link
2. Accept assignment (creates their repo)
3. Clone repo to computer:
   git clone https://github.com/YourSchool-CSA-2025/l0104-variables-studentname.git
4. Open in IDE
5. Write code
6. Test with drone
7. Commit and push:
   git add .
   git commit -m "Completed square flight with variables"
   git push
8. Submit link on Canvas (optional)
```

**Teacher Feedback:**
```
1. Go to classroom dashboard
2. Click on assignment
3. See all student repos
4. Click on student repo
5. Review code
6. Leave comments on specific lines
7. Close issue if complete, or request changes
```

### Cursory Overview of Autograding

GitHub Classroom supports autograding, but we recommend starting without it. Manual code review provides richer learning feedback. If you want to explore autograding later:

- **What it does**: Runs automated tests on student code
- **When it runs**: On every push to GitHub
- **What it checks**: Syntax, specific method calls, output format
- **Limitation**: Cannot test actual drone flight behavior
- **Resources**: [GitHub Classroom Autograding Docs](https://docs.github.com/en/education/manage-coursework-with-github-classroom/teach-with-github-classroom/use-autograding)

For most drone projects, manual review of flight videos or in-class demonstrations works better than autograding.

---

## Managing Student GitHub Accounts

### Privacy and Consent Considerations

**For Students Under 13:**
- GitHub requires parent/guardian consent (COPPA compliance)
- Send home consent forms before creating accounts
- Consider using school-managed accounts instead
- Alternative: Students work on shared lab computers without personal GitHub

**For Students 13 and Over:**
- Can create own accounts with school email
- Still recommend parent notification
- Include in course syllabus/expectations

### Account Creation Options

**Option 1: School Email Domain**
```
Pros:
+ Easy for IT to manage
+ School controls access
+ Automatic verification
+ Accounts tied to enrollment

Cons:
- Account may disappear when student graduates
- Student loses portfolio
- Less authentic to professional practice

Recommendation: Good for middle school, early high school
```

**Option 2: Personal GitHub Accounts**
```
Pros:
+ Student keeps account after graduation
+ Builds professional portfolio
+ Learn authentic workflows
+ Can contribute to open source

Cons:
- Requires monitoring of appropriate use
- Parent consent needed for under 13
- Students responsible for password

Recommendation: Best for older high school, especially seniors
```

### Student Account Setup Guide

Share these instructions with students:

**Creating a GitHub Account (13+):**
1. Go to [github.com](https://github.com)
2. Click "Sign up"
3. Use school email if possible: firstname.lastname@schooldomain.edu
4. Choose username (professional: firstlast or similar)
5. Verify email address
6. Complete profile:
   - Real name (optional but recommended)
   - Profile picture (optional)
   - Bio: "Computer Science Student at [School Name]"
7. Enable two-factor authentication (recommended)

**Privacy Settings to Review:**
- Set default repository visibility (private)
- Control who can see email address (hide it)
- Disable GitHub Copilot suggestions if school policy requires

### Managing Multiple Classes

If you teach multiple sections:

**Separate Organizations:**
```
YourSchool-CSA-Period1-2025
YourSchool-CSA-Period3-2025
YourSchool-CSA-Period5-2025
```
Or use one organization with multiple classrooms within it.

**Pros of Separate Orgs:**
- Clear separation between classes
- Different permission structures possible
- Easier to archive after semester

**Pros of One Org:**
- Fewer organizations to manage
- Can share resources across classes
- Simpler for students in multiple classes

---

## IDE & Development Environment

### Choosing an IDE

We recommend three IDEs based on your students' experience level:

**BlueJ - Best for Complete Beginners**
```
Pros:
+ Visual object interaction
+ Simple interface
+ Interactive testing
+ Shows object relationships graphically
+ Designed for education

Cons:
- Limited features for advanced students
- Not used professionally
- Harder to integrate with GitHub

Best for: First programming course, young students, visual learners
Download: https://www.bluej.org/
```

**VS Code - Best for Most Classrooms**
```
Pros:
+ Free and lightweight
+ Excellent Java Extension Pack
+ GitHub integration built-in
+ Used professionally
+ Runs on any OS
+ Extensions for everything

Cons:
- Can be overwhelming at first
- Requires some initial setup
- Many options to configure

Best for: AP CSA, students with some experience
Download: https://code.visualstudio.com/
Setup Guide: Install Java Extension Pack
```

**IntelliJ IDEA Community - Best for Advanced Students**
```
Pros:
+ Professional-grade IDE
+ Excellent code completion
+ Powerful refactoring
+ Great debugging tools
+ Industry standard

Cons:
- Heavier resource usage
- Complex interface
- Steeper learning curve
- May be overkill for beginners

Best for: Advanced students, seniors planning CS major
Download: https://www.jetbrains.com/idea/download/
```

### Setting Up VS Code (Recommended Path)

**Initial Setup for Teacher:**
1. Install VS Code
2. Install Java Extension Pack:
   - Open Extensions (Ctrl+Shift+X)
   - Search "Java Extension Pack"
   - Install (includes Language Support, Debugger, Maven, etc.)
3. Install CoDrone EDU library:
   - Download latest JAR from [GitHub Releases](https://github.com/scerruti/JCoDroneEdu/releases)
   - Save to known location: `C:\CoDroneLibrary\` or `/usr/local/lib/codrone/`

**Student Setup - Day 1 Lesson:**
1. **Install Java JDK** (if not already installed)
   - Version 11 or newer
   - Show how to verify: `java --version` in terminal

2. **Install VS Code**
   - Download from code.visualstudio.com
   - Run installer
   - Accept defaults

3. **Install Java Extensions**
   - Walk through Extensions panel
   - Search "Java Extension Pack"
   - Click Install
   - Restart VS Code

4. **Create First Project**
   - File → New File
   - Save as `FirstConnection.java`
   - Type or paste test program:
   ```java
   import com.otabi.jcodroneedu.Drone;
   
   public class FirstConnection {
       public static void main(String[] args) {
           Drone drone = new Drone();
           drone.pair();
           System.out.println("Connected to drone!");
           drone.close();
       }
   }
   ```

5. **Add CoDrone Library**
   - Create `lib` folder in project
   - Copy JAR file to `lib` folder
   - Right-click JAR → "Add to Java Source Path"

6. **Run the Program**
   - Click "Run" button above `main` method
   - Or use Run menu
   - See console output

### Troubleshooting Common IDE Issues

**"Cannot find Drone class"**
```
Problem: Library not in classpath
Solution: 
1. Check JAR file in lib folder
2. Right-click JAR → "Add to Java Source Path"
3. Restart VS Code if needed
```

**"No controller found"**
```
Problem: Controller not connected or recognized
Solution:
1. Check USB cable connection
2. Turn controller on
3. Check Device Manager (Windows) or ls /dev/tty* (Mac/Linux)
4. Try different USB port
5. Install USB-Serial drivers if needed
```

**Program runs but nothing happens**
```
Problem: Multiple causes
Solution checklist:
- Is drone powered on?
- Is controller paired with drone?
- Did pair() complete successfully?
- Check battery levels
- Look for error messages in console
```

### Lab Computer Management

**Creating a Standard Image:**
1. Set up one computer perfectly
2. Install all software
3. Test thoroughly
4. Create disk image (use school IT tools)
5. Deploy to all lab computers

**Keep in one place:**
- CoDrone library JAR files
- Example projects
- Documentation PDFs
- Student workspace folders

**Student Data Management:**
- Each student has folder: `C:\Users\Student\CoDroneProjects\`
- Or use network drives if available
- Regular backups of student work
- Clear cleanup procedures at year end

---

## Curriculum Design Framework

### Learning Intentions & Success Criteria

Each lesson should explicitly state what students will learn and how they'll demonstrate understanding. Here's the framework:

**Lesson L0101: First Flight**

*Learning Intentions:*
- I am learning to connect to a drone programmatically
- I am learning the basic structure of a drone control program
- I am learning safety procedures for drone flight

*Success Criteria:*
- I can write code that connects to a drone
- I can make the drone take off and land safely
- I can explain what each line of my program does
- My program includes proper connection and disconnection

**Lesson L0104: Variables in Flight**

*Learning Intentions:*
- I am learning to use variables to control drone behavior
- I am learning to plan a flight path before coding
- I am learning to modify values without rewriting code

*Success Criteria:*
- I can declare variables for flight parameters
- I can use variables in drone movement commands
- I can modify my flight pattern by changing only variable values
- I can explain why variables make code more flexible

### Standards Alignment

#### AP Computer Science A Standards

The CoDrone EDU Java API aligns with these AP CSA learning objectives:

**Unit 1: Primitive Types**
- **VAR-1**: Types (int, double, boolean, String)
- **VAR-2**: Variables and assignment
- Application: Setting flight speeds, distances, durations

**Unit 2: Using Objects**
- **MOD-1**: Creating objects with constructors
- **MOD-2**: Calling methods on objects
- Application: `Drone drone = new Drone(); drone.takeoff();`

**Unit 3: Boolean Expressions and if Statements**
- **CON-1**: Conditional statements
- **CON-2**: Boolean expressions
- Application: `if (drone.getBattery() < 20) { drone.land(); }`

**Unit 4: Iteration**
- **CON-2**: for loops, while loops
- Application: Flying patterns, repeating movements

**Unit 5: Writing Classes** (Advanced)
- **MOD-2**: Defining classes and methods
- **MOD-3**: Method overloading
- Application: Creating custom drone behaviors, extending Drone class

#### CSTA K-12 Standards

**Level 3A (Grades 9-10):**
- **3A-AP-13**: Create prototypes that use algorithms to solve computational problems
  - *Application*: Autonomous obstacle avoidance, path planning
- **3A-AP-16**: Design and iteratively develop computational artifacts for practical intent
  - *Application*: Flight pattern projects, sensor-based behaviors
- **3A-AP-17**: Use version control systems
  - *Application*: Git/GitHub for project management

**Level 3B (Grades 11-12):**
- **3B-AP-08**: Describe how artificial intelligence drives many software systems
  - *Application*: Autonomous drone behaviors, computer vision integration
- **3B-AP-14**: Construct solutions to problems using student-created components
  - *Application*: Custom flight functions, reusable behaviors

#### State-Specific Standards

*Note: Your state may have additional standards. Common mappings:*

**NGSS (Science):**
- HS-PS2: Motion and Forces
- HS-ETS1: Engineering Design

**Common Core Math:**
- HSG-SRT: Trigonometry (angles, rotations)
- HSG-GPE: Expressing geometric properties with equations

### Lesson Progression Framework

**Week-by-Week Outline (14-Week Semester):**

**Weeks 1-2: Foundations**
- Introduction to drones and safety
- Development environment setup
- First connection and flight
- Sequential commands

**Weeks 3-4: Control Structures**
- Variables for flight parameters
- Conditional statements with sensors
- For loops for patterns
- While loops for continuous monitoring

**Weeks 5-6: Functions and Methods**
- Creating reusable flight functions
- Parameters and return values
- Decomposing complex behaviors
- Testing and debugging

**Weeks 7-8: Sensors and Decision Making**
- Reading sensor data (battery, range, IMU)
- Sensor-based decision trees
- Safety monitoring systems
- Data-driven flight adjustments

**Weeks 9-10: Arrays and Data Structures**
- Storing sensor readings in arrays
- Flight path planning with arrays
- Processing multiple measurements
- Statistical analysis of flight data

**Weeks 11-12: Object-Oriented Design**
- Understanding the Drone class
- Creating custom behavior classes
- Inheritance and extension
- Designing autonomous systems

**Weeks 13-14: Final Projects**
- Student-designed flight challenges
- Peer code review
- Presentations and demonstrations
- Reflection and assessment

---

## CoDrone EDU Hardware Management

### Inventory System

**Essential Tracking:**
- Number each drone and controller
- Create inventory spreadsheet
- Track:
  - Drone serial number
  - Controller serial number
  - Battery serial numbers
  - Condition notes
  - Last flight date
  - Issues/repairs

**Example Spreadsheet:**
```
| Unit # | Drone S/N | Controller S/N | Status | Last Used | Issues |
|--------|-----------|----------------|--------|-----------|--------|
| 01     | DRN-001   | CTL-001        | Good   | 11/15/25  | None   |
| 02     | DRN-002   | CTL-002        | Repair | 11/10/25  | Motor  |
```

### Battery Management

**Best Practices:**
- Charge all batteries before each class
- Rotate batteries to distribute wear
- Store at 50% charge during breaks
- Replace when runtime drops below 6 minutes
- Never leave charging unattended

**Safety Rules:**
- Charge only with provided chargers
- Don't charge damaged batteries
- Charge in fire-safe location
- Follow manufacturer voltage limits
- Store in cool, dry place

**Typical Battery Life:**
- LiPo batteries: 300-500 charge cycles
- Expect 1-2 year replacement cycle with daily use
- Cost: ~$15-20 per battery
- Budget: 2 batteries per drone recommended

### Maintenance Schedule

**Daily (After Each Class):**
- Visual inspection for damage
- Check propellers for cracks
- Clean sensors gently
- Check battery contacts
- Verify controller connection

**Weekly:**
- Test all units with standard program
- Clean propeller guards
- Check for loose screws
- Update inventory sheet
- Charge all spare batteries

**Monthly:**
- Deep clean all units
- Update firmware if available
- Replace worn propellers
- Calibrate sensors if needed
- Review damage/repair log

**Semester:**
- Full inventory audit
- Replace consumables (propellers, pads)
- Professional repairs if needed
- Update documentation
- Plan budget for replacements

### Common Repairs

**Broken Propeller (Most Common):**
```
Symptoms: Wobbling flight, difficulty gaining altitude
Cause: Impact with wall/ceiling/floor
Repair: Replace propeller (~5 minutes)
Parts: Propeller set ($5-10)
```

**Motor Issues:**
```
Symptoms: Won't take off, one corner droops
Cause: Debris, overheating, or mechanical failure
Repair: Clean first, replace if necessary
Parts: Motor ($15-25 each)
Note: May require soldering skills
```

**Controller Not Pairing:**
```
Symptoms: No connection to drone
Troubleshooting:
1. Fresh batteries in controller
2. Re-pair using manufacturer instructions
3. Check antenna connection
4. Reset controller settings
5. Contact manufacturer if persists
```

### Storage Solutions

**Short-term (Daily):**
- Bins with foam inserts
- Each unit in labeled slot
- Controllers and drones together
- Batteries stored separately
- Charging station nearby

**Long-term (Summer):**
- Original packaging if available
- Remove batteries (store at 50% charge)
- Cool, dry location
- Silica gel packets for moisture
- Check monthly for issues

### Budget Planning

**Initial Purchase (Per Classroom Set of 12 Units):**
- 12 Drones: $2,400-3,600 ($200-300 each)
- 24 Batteries: $360-480 (2 per drone)
- Spare propellers: $60-100
- Carrying case: $100-200
- **Total**: ~$3,000-4,500

**Annual Replacement:**
- Propellers: $100-150
- Batteries: $150-200 (1/3 replacement)
- Misc repairs: $100-200
- **Total**: ~$350-550/year

**Grant Opportunities:**
- DonorsChoose.org
- Local education foundations
- STEM-focused grants
- Robotics competition sponsors
- Technology company donations

---

## Common Lesson Plans

### Lesson L0101: Your First Flight

**Duration**: 50 minutes

**Learning Intentions:**
- Students can connect to a drone programmatically
- Students understand the basic structure of a drone program
- Students can execute safe takeoff and landing procedures

**Materials:**
- Computer with IDE
- CoDrone EDU set (one per pair)
- First Flight handout
- Safety checklist

**Lesson Flow:**

*Introduction (10 min):*
1. Demonstrate first flight with your drone
2. Show code on projector
3. Discuss safety procedures
4. Assign student pairs and drone units

*Guided Practice (15 min):*
1. Walk through creating new file
2. Type program together (or provide starter code)
3. Explain each line's purpose:
   - `Drone drone = new Drone()` - Creates drone object
   - `drone.pair()` - Connects to hardware
   - `drone.takeoff()` - Motors start, ascends
   - `drone.hover(5)` - Stays in place
   - `drone.land()` - Descends safely
   - `drone.close()` - Disconnects

*Independent Practice (20 min):*
1. Student pairs run program
2. Observe flight behavior
3. Modify hover duration
4. Try adding `System.out.println()` statements
5. Troubleshoot any issues

*Closure (5 min):*
1. Share observations
2. What surprised you?
3. Preview next lesson (adding movement)
4. Proper cleanup procedure

**Assessment:**
- ✓ Program compiles without errors
- ✓ Drone takes off and lands safely
- ✓ Students can explain what each line does
- ✓ Safety procedures followed

**Common Issues:**
- "Drone won't connect" → Check USB, batteries, power
- "Program crashes" → Missing `close()` statement
- "Drone drifts" → Normal, will address with trim later

---

### Lesson L0106: Using Conditionals

**Duration**: 50 minutes

**Learning Intentions:**
- Students can write if statements to make flight decisions
- Students can read sensor data to inform decisions
- Students understand how to use comparison operators

**Prerequisites:**
- Basic flight commands
- Variables
- Reading sensor values

**Challenge**: Create a program that checks battery level and adjusts flight behavior accordingly.

**Lesson Flow:**

*Hook (5 min):*
"What should your drone do if the battery gets low during flight? Just keep flying? Or land safely?"

*Instruction (10 min):*
1. Review if statement syntax
2. Demonstrate `drone.getBattery()`
3. Show comparison: `if (battery < 20)`
4. Discuss logical operators (&&, ||)

*Guided Example (10 min):*
```java
int battery = drone.getBattery();

if (battery > 50) {
    System.out.println("Battery good - flying high!");
    drone.moveUp(100, "cm");
    drone.hover(3);
} else if (battery > 30) {
    System.out.println("Battery medium - flying low");
    drone.moveUp(50, "cm");
    drone.hover(2);
} else {
    System.out.println("Battery low - landing immediately");
    drone.land();
}
```

*Independent Practice (20 min):*
Challenge variations:
1. **Easy**: Check battery once, decide flight or no flight
2. **Medium**: Check multiple times during flight, adjust behavior
3. **Hard**: Check both battery AND front sensor, avoid obstacles while monitoring power

*Closure (5 min):*
- Share interesting solutions
- Discuss real-world applications
- How do delivery drones handle low battery?

**Assessment:**
- ✓ if/else syntax correct
- ✓ Appropriate comparison operators
- ✓ Logical decision-making
- ✓ Safe behavior with low battery

---

### Lesson L0107: Drawing Shapes with Loops

**Duration**: 50 minutes

**Learning Intentions:**
- Students can use for loops to repeat flight commands
- Students understand iteration in practical context
- Students can calculate loop parameters for desired patterns

**Challenge**: Fly a perfect square using only 5 lines of flight code (one loop, one turn).

**Lesson Flow:**

*Introduction (5 min):*
Show two programs side-by-side:
```java
// Without loop (12 lines)
drone.moveForward(50, "cm", 1.0);
drone.turnRight(90);
drone.moveForward(50, "cm", 1.0);
drone.turnRight(90);
drone.moveForward(50, "cm", 1.0);
drone.turnRight(90);
drone.moveForward(50, "cm", 1.0);

// With loop (4 lines)
for (int i = 0; i < 4; i++) {
    drone.moveForward(50, "cm", 1.0);
    drone.turnRight(90);
}
```

*Instruction (10 min):*
1. for loop syntax breakdown
2. How many iterations for a square? (4)
3. What angle for square corners? (90°)
4. How about a triangle? (3 sides, 120° turns)

*Practice (25 min):*
Progressive challenges:
1. Fly a square (given)
2. Fly a triangle (calculate angle)
3. Fly a hexagon (calculate sides and angle)
4. Fly a circle (many small segments)

*Extension:*
Nested loops for 3x3 grid pattern

*Closure (10 min):*
- Gallery walk: Look at each drone's final position
- Discuss precision: Why aren't they all perfect?
- Intro to calibration and accumulated error

**Assessment:**
- ✓ for loop syntax correct
- ✓ Correct iteration count
- ✓ Appropriate angle calculations
- ✓ Shape recognizable (even if not perfect)

---

## Glossary of Terms

### Hardware Terms

**Accelerometer**: Sensor that measures acceleration forces (how fast drone speeds up/slows down). Used for stability control.

**Barometer**: Sensor that measures air pressure. Used to calculate altitude since air pressure decreases with height.

**Battery (LiPo)**: Lithium Polymer rechargeable battery. High energy density but requires careful charging. Never fully discharge.

**Controller**: The handheld remote that communicates with the drone. In our setup, connects to computer via USB.

**Drone**: The flying quadcopter. Has four motors, sensors, and onboard computer.

**IMU**: Inertial Measurement Unit - combines accelerometer and gyroscope to track drone's motion and orientation.

**Motor**: Electric motors that spin propellers. Four motors allow precise control of movement and rotation.

**Propeller**: Spinning blade that creates thrust to lift and move the drone. Each drone has four propellers.

**Range Sensor (ToF)**: Time-of-Flight sensor that measures distance using light. Front and bottom sensors detect obstacles and ground.

**USB-Serial**: Connection type used between controller and computer. Uses USB port but communicates like old serial port.

### Software Terms

**API**: Application Programming Interface - the set of methods (commands) you can use to control the drone. Example: `drone.takeoff()` is part of the API.

**Bluetooth**: Wireless communication method. Drones can use Bluetooth but USB connection is more reliable in classroom.

**Camel Case**: Naming style where first word lowercase, subsequent words capitalized. Example: `moveForward`, `getFrontRange`

**Class**: Blueprint for creating objects. `Drone` is a class; `drone` is an object created from that class.

**Compile**: Converting Java source code into bytecode that computer can run. Happens when you click "Run".

**IDE**: Integrated Development Environment - software for writing and testing code (VS Code, IntelliJ, BlueJ).

**JAR File**: Java ARchive - compressed file containing compiled Java code. CoDrone library comes as a JAR.

**Method**: Function that belongs to a class. Examples: `takeoff()`, `land()`, `moveForward()` are all methods.

**Object**: Instance of a class. When you write `Drone drone = new Drone()`, you create a drone object.

**Parameter**: Value you provide to a method. In `moveForward(50, "cm", 1.0)`, the parameters are 50, "cm", and 1.0.

**Repository (Repo)**: Storage location for code, typically on GitHub. Contains all files and version history.

### Flight Terms

**Altitude**: Height above sea level (measured by barometer).

**Elevation**: Same as altitude - vertical distance from reference point (sea level).

**Height**: Distance above ground (measured by bottom range sensor).

**Hover**: Stay in one place in the air. Motors adjust constantly to maintain position.

**Pitch**: Tilting forward or backward. Controls forward/backward movement.

**Roll**: Tilting left or right. Controls left/right movement.

**Throttle**: Power to motors. Higher throttle = more lift = higher flight.

**Yaw**: Rotation left or right. Like turning your head.

**Trim**: Small adjustments to compensate for imbalances. Helps drone fly straight.

### Programming Terms

**Boolean**: True/false value. Example: `boolean isFlying = true;`

**Conditional**: if/else statement that makes decisions based on conditions.

**For Loop**: Repeats code a specific number of times. Example: `for (int i = 0; i < 4; i++)`

**Integer (int)**: Whole number. Example: `int power = 50;`

**Iteration**: One execution of loop body. In `for (int i = 0; i < 4; i++)`, loop has 4 iterations.

**String**: Text data. Always in quotes. Example: `String direction = "forward";`

**Variable**: Named storage for a value. Example: `int speed = 50;` creates variable named speed.

**While Loop**: Repeats code as long as condition is true. Example: `while (battery > 20)`

### Educational Terms

**AP CSA**: Advanced Placement Computer Science A - college-level Java programming course.

**Learning Intention**: Statement of what students will learn in a lesson. Starts with "I am learning to..."

**Success Criteria**: Specific, measurable ways students demonstrate understanding. "I can..."

**Scaffolding**: Temporary support structures (like starter code) that help students learn, then are removed.

**Formative Assessment**: Checking understanding during learning (not graded). Examples: observation, questions.

**Summative Assessment**: Evaluating learning at end of unit (graded). Examples: projects, tests.

---

## Summary

This guide provides the foundation for successfully teaching computer science with CoDrone EDU. Remember:

**Start Simple:**
- Week 1: Just connect and takeoff/land
- Build complexity gradually
- Students need time to develop physical intuition

**Safety Always:**
- Clear procedures reviewed daily
- Flight zones marked and enforced
- Emergency stop practiced regularly
- Battery monitoring built into every program

**Learn Together:**
- You don't need to be a drone expert
- Students will discover things you didn't know
- Troubleshooting together is valuable learning
- Document solutions for future classes

**Seek Support:**
- GitHub repository: [JCoDroneEdu](https://github.com/scerruti/JCoDroneEdu)
- Robolink documentation: [docs.robolink.com](https://docs.robolink.com/docs/CoDroneEDU/Python/getting-started)
- Teacher community forums
- Your students are resourceful too!

**Make It Meaningful:**
- Connect to real-world applications
- Let students be creative
- Celebrate interesting failures
- Share student work beyond classroom

Most importantly: Have fun! The joy of seeing code make something physical happen is what makes teaching with drones so rewarding.

---

**Additional Resources:**

- Student Guide (student-guide.md) - Comprehensive guide for students
- Design Guide (forthcoming) - Technical details for advanced users
- Development History (forthcoming) - Evolution of the library
- GitHub Issues - Report problems or request features
- Example Programs - See src/main/java/com/otabi/jcodroneedu/examples/

---

*This guide was created for educators teaching computer science with CoDrone EDU. For updates and corrections, please visit the GitHub repository.*

**Document Info:**
- **Version**: 1.0
- **Target Audience**: K-12 Teachers (no CS background required)
- **Last Updated**: November 2025
- **Word Count**: ~4,500 words
