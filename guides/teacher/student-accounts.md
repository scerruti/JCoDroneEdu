---
layout: guide
title: Student Accounts
category: Teacher Guide
permalink: /guides/teacher/student-accounts.html
---

## Managing Student Accounts

### Account Requirements

**Each student needs:**
- GitHub account (free)
- Java JDK installed (free)
- IntelliJ IDEA Community Edition (free)
- USB cable to connect to drone

All free! No licensing costs for students.

---

## GitHub Account Setup

### Before First Class

**Option 1: Students Create Own Accounts**
- Faster, teaches Git skills
- Ensure privacy settings are appropriate
- Provide instructions sheet

**Option 2: Bulk Account Creation**
- Use GitHub's education tools
- Save time in large classes
- Still requires individual activation

### Privacy and Safety

**GitHub Privacy Settings (teach students):**

1. Profile → Settings → Visibility
   - Set "Private" for student profiles
   - Hide activity timeline

2. Repository Settings
   - Keep school repos Private
   - Only share what's needed

3. Email Privacy
   - Use school email for academic work
   - Add noreply email (GitHub → Settings → Emails)
   - Enable "Block command line pushes that expose my email"

### GitHub Education Benefits

**Apply for GitHub Education:**
1. Go to [education.github.com](https://education.github.com)
2. Click "Get benefits"
3. Verify as educator
4. Gives students unlimited private repos

---

## Java Setup

### Supported Platforms

**Windows:**
1. Download Java JDK from oracle.com or adoptopenjdk.com
2. Run installer
3. Add to PATH (installer handles this)
4. Verify: `java -version`

**macOS:**
1. Install via Homebrew: `brew install openjdk`
2. Or download from oracle.com
3. Verify: `java -version`

**Linux:**
1. Ubuntu: `sudo apt-get install openjdk-11-jdk`
2. Fedora: `sudo dnf install java-11-openjdk`
3. Verify: `java -version`

### Support Resources

Create handout for students:

```
=== JAVA INSTALLATION TROUBLESHOOTING ===

Q: I installed Java but `java -version` says "not found"
A: Java isn't in your PATH. 
   On Windows: Computer → Properties → Environment Variables
   On Mac/Linux: Add to ~/.bash_profile: 
   export PATH="/path/to/java:$PATH"

Q: It says "unrecognized" or "command not found"
A: Restart your terminal after installing!

Q: I have Java 8 but need Java 11+
A: Download newer version from adoptopenjdk.com
```

---

## IDE Setup

### IntelliJ IDEA Community Edition

**Installation:**
1. Download from jetbrains.com/idea/download
2. Choose Community Edition (it's free)
3. Run installer
4. Launch IDE

**First-Time Setup:**
- Choose light or dark theme (preference)
- Check for JDK configuration
- Install JCoDroneEdu library

### Setting Up JCoDroneEdu

**In IntelliJ:**
1. File → Project Structure
2. Libraries → + → Download JCoDroneEdu
3. Point to your project

**Or manually:**
1. Download JAR from repository
2. Project Structure → Libraries → +
3. Select JAR file
4. Apply changes

### Create Student Project Template

**Share as starting point:**

```
JavaDroneProject/
├── src/
│   └── Main.java (empty main method)
├── lib/
│   └── jcodroneedu.jar
├── README.md
└── .gitignore
```

Students copy this for each assignment.

---

## Classroom Roles and Permissions

### GitHub Organization Roles

**Owner (You):**
- Create classroom
- Create assignments
- Access all student repos

**Member (TAs/Graders):**
- Can review submissions
- Cannot delete repos
- Cannot modify grades (if using LMS)

**Invitation workflow:**
1. Organization → Members → Invite
2. Send to TA email
3. They accept invitation
4. Ready to help grade

### What Students See

Students only see:
- Their own repository
- Classroom assignment list
- No other student work (private repos)

---

## Account Maintenance

### During Semester

**First week:**
- Verify all students have GitHub accounts
- Verify all students can compile Java
- Test one assignment early

**Ongoing:**
- Monitor for unused accounts (inactive 2+ weeks = check in)
- Help with password resets
- Track who's struggling early

### End of Semester

**Archive student work:**
```bash
# Create mirror backup of all student repos
for repo in $(gh repo list YourOrg --limit 1000 -q); do
  git clone --mirror $repo backup/$repo
done
```

**Preserve for records:**
- School data retention policy
- Typical: 2-3 years for portfolios
- Secure backup location

---

## Troubleshooting Common Issues

### Issue: Student forgot password

**Solution:**
- GitHub password reset: github.com/password_reset
- Student provides confirmation code

### Issue: Student can't connect to drone

**Check in order:**
1. USB cable plugged in?
2. Drone turned on?
3. Java installed? (`java -version`)
4. IDE recognizes library?
5. Restart IDE, then drone

**Test program:**
```java
public class DiagnosticTest {
    public static void main(String[] args) {
        System.out.println("Java: OK");
        System.out.println("IDE: OK");
        try {
            Drone drone = new Drone(true);
            System.out.println("Drone: OK");
            System.out.println("Battery: " + drone.getBattery() + "%");
        } catch (Exception e) {
            System.out.println("Drone: FAILED - " + e.getMessage());
        }
    }
}
```

### Issue: Two students on same machine

**Solution:**
- Create separate user accounts on computer
- Each logs in with own GitHub
- IntelliJ can have multiple projects open

---

## Accessibility Considerations

### For Students with Disabilities

**Hearing impaired:**
- Drone has LED status indicators (visual feedback)
- Ensure code comments explain all behaviors
- No audio-only feedback

**Vision impaired:**
- Provide code in text format
- Pair with sighted partner
- Focus on algorithm, not visual flying

**Motor limitations:**
- Voice dictation software: Dragon NaturallySpeaking
- IDE supports accessibility features
- Text-to-speech for code reading

---

## FERPA and Data Privacy

### Student Data Protection

**GitHub:**
- Student repos are private by default
- Only student + teacher + GH admins see
- Complies with FERPA

**Best practices:**
- Never post student names publicly
- Use GitHub usernames in comments
- Back up locally, not to public cloud

**Grading in LMS:**
- Keep grades in school LMS (Canvas, Google, etc)
- Don't share raw assignment scores publicly
- Link to private GitHub repos only

---

## Useful Resources

- **Java troubleshooting:** [oracle.com/java/technologies](https://www.oracle.com/java/technologies)
- **IntelliJ help:** [jetbrains.com/help/idea](https://www.jetbrains.com/help/idea)
- **GitHub docs:** [docs.github.com](https://docs.github.com)
- **Student Guide:** [Local link]({{ '/guides/student/index.html' | relative_url }})

---

Next: [IDE Setup Details]({{ '/guides/teacher/ide-setup.html' | relative_url }})
