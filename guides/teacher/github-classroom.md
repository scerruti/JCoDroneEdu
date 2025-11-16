---
layout: guide
title: GitHub Classroom Integration
category: Teacher Guide
permalink: /guides/teacher/github-classroom.html
---

## Setting Up GitHub Classroom

GitHub Classroom automates assignment distribution, submission, and grading. Perfect for programming classes.

### Step 1: Create GitHub Organization

1. Go to [github.com/organizations/new](https://github.com/organizations/new)
2. Choose organization name (e.g., `YourSchool-CompSci-2024`)
3. Set billing email and organization email
4. Verify you own it
5. Write down: **Organization URL** (needed for classroom setup)

### Step 2: Create GitHub Classroom

1. Go to [classroom.github.com](https://classroom.github.com)
2. Click "Create a classroom"
3. Select your organization
4. Name it (e.g., "Intro to Drone Programming")
5. Accept terms
6. **Note the Classroom URL** for sharing with students

### Step 3: Create Your First Assignment

**From GitHub Classroom:**

1. Click "Create an assignment"
2. **Assignment name:** "Hello Drone"
3. **Slug:** `hello-drone` (appears in repository names)
4. **Repository visibility:** Public or Private
5. **Add a template repository:**
   - Create/use a repo with starter code
   - Suggested: `JCoDroneEdu-Template` with:
     - Starter Java project
     - Sample `HelloDrone.java`
     - Build configuration
6. **Save assignment**

### Step 4: Share with Students

**Each student needs to:**

1. Create GitHub account (free)
2. Visit classroom URL
3. Click assignment
4. Accept invitation
5. GitHub creates personal repo: `hello-drone-username`
6. Clone to local machine:
   ```bash
   git clone https://github.com/YourOrg/hello-drone-username.git
   ```

---

## Assignment Workflow

### Template Repository Setup

**Create `JCoDroneEdu-Template` repo with:**

```
JCoDroneEdu-Template/
├── README.md (assignment instructions)
├── src/
│   └── main/java/
│       └── Solution.java (starter code)
├── build.gradle (Gradle configuration)
├── .gitignore
└── docs/
    └── API_REFERENCE.md (link to Javadoc)
```

**Example `README.md` for assignment:**
```markdown
# Hello Drone Assignment

## Objective
Write a program that makes the drone:
1. Take off
2. Fly in a square
3. Land

## Starter Code
See `Solution.java`

## Submission
Modify `Solution.java` and push to GitHub.

## Testing
```bash
./gradlew build
java -cp build/classes/java/main Solution
```

## Resources
- [Student Guide](link-to-guide)
- [API Reference](link-to-javadoc)
```

### Assignment Ideas (Progression)

**Week 1: Hello Drone**
- Takeoff and land only
- Tests basic connection and command

**Week 2: Flight Patterns**
- Fly a square using loops
- Tests for loops and control flow

**Week 3: Sensor Reading**
- Display battery and height
- Tests variables and sensor APIs

**Week 4: Autonomous Flight**
- Maintain height using sensors
- Tests conditionals and loops with feedback

**Week 5-6: Project**
- Student-designed autonomous behavior
- Tests all learned concepts

---

## Grading Setup

### Rubric Template

Create in GitHub Classroom settings:

```
Functionality (40%):
  ✓ Compiles without errors (10%)
  ✓ Program runs successfully (10%)
  ✓ Core behavior correct (20%)

Code Quality (30%):
  ✓ Clear variable names (10%)
  ✓ Appropriate comments (10%)
  ✓ Well-structured functions (10%)

Experimentation (20%):
  ✓ Added custom elements (20%)

Reflection (10%):
  ✓ Brief write-up of learning (10%)
```

### Grading Workflow

1. **Pull Requests:** Students submit via GitHub (automatic with classroom)
2. **Code Review:**
   - Clone their repo locally
   - Review code in IDE
   - Add comments directly in GitHub PR
3. **Testing:**
   - Run their code on actual drone
   - Note any runtime issues
4. **Feedback:**
   - Comment on specific lines: "Good use of variable names here"
   - Suggest improvements: "Consider extracting this to a function"
   - Document grade

**Example GitHub Comment:**
```
Great job on the square pattern! 

One suggestion: your turn angle could be a 
constant at the top of the program rather than 
hardcoded in the loop. This makes it easier to 
change later.

See line 15 for example.
```

---

## Classroom Management

### Student Access Control

**Private vs Public Repos:**
- **Private:** Only student and instructor see code
- **Public:** Encourages sharing and collaboration

**Recommended:** Private repos (students comfortable experimenting)

### Preventing Plagiarism

**Use GitHub tools:**
1. GitHub's built-in plagiarism detection
2. Review commit history (shows who wrote what when)
3. Compare student repos for similar code patterns

**Teaching approach:**
- Discuss academic integrity in first class
- Normalize peer learning AND individual work
- Require reflection: "What did you learn from this assignment?"

### Backup and Assessment

**Archive assignments:**
```bash
# End of semester
git clone --mirror https://github.com/YourOrg/hello-drone-* backups/
```

Preserve student work and track progress over time.

---

## Common Classroom Scenarios

### Scenario 1: Student can't compile

**Solution:**
1. Check Java version: `java -version`
2. Verify IDE setup: Run template program
3. Add diagnostic assignment: "Print system info"

### Scenario 2: Different computers, different results

**Solution:**
- Standardize: Provide `.gradle` wrapper
- Document: IDE setup guide for all platforms
- Test on student machines before assignment

### Scenario 3: Student wants to resubmit

**Process:**
1. They push new commits (automatic)
2. You grade again (pull latest)
3. GitHub shows commit history

No special process needed—Git handles it!

---

## Integration with Student Guide

Link assignments to relevant guide pages:

```
Assignment: Flight Patterns
Learning:   guides/student/flight-patterns.html
API:        javadoc/index.html
Debugging:  guides/student/debugging.html
```

Students know where to find help resources.

---

## Repository Structure Recommendation

Share with students (in template):

```
your-assignment/
├── src/
│   └── main/java/
│       └── YourName.java (EDIT THIS)
├── build.gradle
├── README.md (READ FIRST)
├── REFLECTION.md (WRITE HERE at end)
└── .gitignore
```

Clear what to edit, what to read, where to document learning.

---

## Scaling Tips (Large Classes)

**With 100+ students:**
- Use teaching assistants to help with grading
- Assign peer reviews (students review 2 classmates)
- Use code coverage tools to identify incomplete work
- Batch similar feedback as GitHub templates

---

Next: [Student Accounts]({{ '/guides/teacher/student-accounts.html' | relative_url }})
