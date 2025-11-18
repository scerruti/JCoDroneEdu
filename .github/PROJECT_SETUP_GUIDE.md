# Test Coverage Improvement Project - Setup Guide

This guide walks you through setting up the complete Test Coverage Improvement Project structure in GitHub.

---

## 📋 Prerequisites

- Repository admin access to scerruti/JCoDroneEdu
- GitHub CLI installed (optional, but recommended): https://cli.github.com/
- Basic familiarity with GitHub Issues, Labels, and Milestones

---

## 🚀 Quick Start (5 Minutes)

### Option A: Automated Setup with GitHub CLI

If you have GitHub CLI installed, run these commands from your local repository:

```bash
# Navigate to repository
cd /path/to/JCoDroneEdu

# Create all labels
bash .github/scripts/create-labels.sh

# Create all milestones
bash .github/scripts/create-milestones.sh

# Create all issues from templates
bash .github/scripts/create-issues.sh
```

### Option B: Manual Setup via GitHub UI

Follow the detailed steps below to set up everything manually through the GitHub web interface.

---

## 📖 Detailed Setup Instructions

### Step 1: Review the Epic Issue Document

1. Open `.github/TEST_COVERAGE_EPIC.md`
2. Review the overall structure and goals
3. Familiarize yourself with the 5 sprints
4. Note the 60 planned issues across all sprints

### Step 2: Create Labels

**Via GitHub UI:**

1. Go to https://github.com/scerruti/JCoDroneEdu/labels
2. Click "New label" for each label in `.github/LABELS_CONFIGURATION.md`
3. Enter name, color (without #), and description
4. Click "Create label"
5. Repeat for all 24 labels

**Via GitHub CLI (faster):**

```bash
# From repository root
cd .github/scripts
bash create-labels.sh
```

**Labels to create:**
- 4 Priority labels (critical, high, medium, low)
- 5 Sprint labels (sprint-1 through sprint-5)
- 5 Type labels (test, coverage, unit-test, integration-test, documentation)
- 5 Status labels (ready, in-progress, blocked, review, done)
- 10 Component labels (api, protocol, system, storage, ml, autonomous, tools, examples, buzzer, display)

### Step 3: Create Milestones

**Via GitHub UI:**

1. Go to https://github.com/scerruti/JCoDroneEdu/milestones
2. Click "New milestone"
3. For each sprint in `.github/MILESTONES_CONFIGURATION.md`:
   - Enter title (e.g., "Sprint 1 - Critical API & Core Logic")
   - Enter description
   - Set due date (2 weeks intervals)
   - Click "Create milestone"

**Via GitHub CLI:**

```bash
# From repository root
cd .github/scripts
bash create-milestones.sh
```

**Milestones to create:**
1. Sprint 1 - Critical API & Core Logic (Due: 2 weeks)
2. Sprint 2 - ML & Data Processing Logic (Due: 4 weeks)
3. Sprint 3 - Protocols, Communication, and Utilities (Due: 6 weeks)
4. Sprint 4 - System, Storage, and Integration (Due: 8 weeks)
5. Sprint 5 - Examples, Demos, and Edge Packages (Due: 10 weeks)

### Step 4: Create Epic Issue

1. Go to https://github.com/scerruti/JCoDroneEdu/issues/new
2. Title: "Test Coverage Improvement Plan - Epic"
3. Body: Copy content from `.github/TEST_COVERAGE_EPIC.md`
4. Labels: `test`, `coverage`, `documentation`, `priority-critical`
5. Pin this issue to the top
6. Click "Create issue"

### Step 5: Create Sprint Issues

For each sprint, create issues based on the sprint documents:

**Sprint 1 Issues (10 issues):**
Use `.github/SPRINT_1_CRITICAL_API.md` as reference

1. Create issue for each of the 10 tasks in Sprint 1
2. Title: "[TEST] Add unit tests for <component>"
3. Body: Include methods to cover, test scenarios, and acceptance criteria
4. Labels: `sprint-1`, `priority-[level]`, `test`, `unit-test`, `component-[name]`, `ready`
5. Milestone: "Sprint 1 - Critical API & Core Logic"
6. Assignee: (leave unassigned or assign to sprint owner)

**Sprint 2 Issues (10 issues):**
Use `.github/SPRINT_2_ML_DATA.md` as reference

**Sprint 3 Issues (14 issues):**
Use `.github/SPRINT_3_PROTOCOLS.md` as reference

**Sprint 4 Issues (13 issues):**
Use `.github/SPRINT_4_SYSTEM_INTEGRATION.md` as reference

**Sprint 5 Issues (13 issues):**
Use `.github/SPRINT_5_EXAMPLES_EDGE.md` as reference

**Total: 60 issues**

### Step 6: Create Issue Template

The issue template is already created at:
`.github/ISSUE_TEMPLATE/test_coverage.yml`

Verify it appears when creating new issues.

### Step 7: Set Up Project Board (Optional)

**Create a GitHub Project Board:**

1. Go to https://github.com/scerruti/JCoDroneEdu/projects
2. Click "New project"
3. Choose "Board" view
4. Name: "Test Coverage Improvement"
5. Add columns:
   - 📋 Backlog (filter: `label:ready`)
   - 🏃 In Progress (filter: `label:in-progress`)
   - 👀 Review (filter: `label:review`)
   - 🚫 Blocked (filter: `label:blocked`)
   - ✅ Done (filter: `label:done`)
6. Add all 60 issues to the project
7. Configure automation rules for label changes

---

## 📊 Project Structure Overview

```
Test Coverage Improvement Plan
│
├── Epic Issue (pinned)
│   └── Links to all 5 sprints
│
├── Sprint 1: Critical API & Core Logic
│   ├── Milestone: Sprint 1 (10 issues)
│   └── Priority: Critical
│
├── Sprint 2: ML & Data Processing Logic
│   ├── Milestone: Sprint 2 (10 issues)
│   └── Priority: High
│
├── Sprint 3: Protocols, Communication, and Utilities
│   ├── Milestone: Sprint 3 (14 issues)
│   └── Priority: Medium-High
│
├── Sprint 4: System, Storage, and Integration
│   ├── Milestone: Sprint 4 (13 issues)
│   └── Priority: Medium
│
└── Sprint 5: Examples, Demos, and Edge Packages
    ├── Milestone: Sprint 5 (13 issues)
    └── Priority: Low-Medium
```

---

## 🔧 Helper Scripts

Create these scripts in `.github/scripts/` directory:

### create-labels.sh

```bash
#!/bin/bash
# See .github/LABELS_CONFIGURATION.md for the full script
```

### create-milestones.sh

```bash
#!/bin/bash
# See .github/MILESTONES_CONFIGURATION.md for the full script
```

### create-issues.sh

```bash
#!/bin/bash
# This would create all 60 issues programmatically
# Template provided below
```

---

## 👥 Team Assignments

### Assign Sprint Owners

1. Go to each milestone
2. Assign a sprint owner in the description
3. Sprint owner responsibilities:
   - Lead sprint planning
   - Review all PRs in their sprint
   - Track sprint progress
   - Conduct sprint retrospective

### Assign Team Members to Issues

1. During sprint planning, team members pick issues
2. Self-assign by clicking "Assignees" on the issue
3. Add `in-progress` label when starting work
4. Move issue to "In Progress" column on project board

---

## 📈 Tracking Progress

### Daily Standups

Each team member shares:
- What I completed yesterday
- What I'm working on today
- Any blockers

### Weekly Sprint Check-ins

- Review milestone progress
- Update issue statuses
- Identify and resolve blockers
- Adjust priorities if needed

### End-of-Sprint Reviews

- Demo completed test coverage
- Review coverage metrics
- Conduct retrospective
- Plan next sprint

---

## 🎯 Success Metrics

Track these metrics for each sprint:

- **Issue Completion Rate:** # closed / # total
- **Code Coverage:** % coverage achieved vs target
- **Tests Added:** # new tests written
- **Test Pass Rate:** # passing / # total tests
- **Sprint Velocity:** Issues completed per sprint
- **Defect Rate:** # bugs found in new tests

---

## 📝 Documentation

Key documents in `.github/`:

| Document | Purpose |
|----------|---------|
| `TEST_COVERAGE_EPIC.md` | Overall project tracking and structure |
| `SPRINT_1_CRITICAL_API.md` | Sprint 1 detailed plan |
| `SPRINT_2_ML_DATA.md` | Sprint 2 detailed plan |
| `SPRINT_3_PROTOCOLS.md` | Sprint 3 detailed plan |
| `SPRINT_4_SYSTEM_INTEGRATION.md` | Sprint 4 detailed plan |
| `SPRINT_5_EXAMPLES_EDGE.md` | Sprint 5 detailed plan |
| `LABELS_CONFIGURATION.md` | Label definitions and setup |
| `MILESTONES_CONFIGURATION.md` | Milestone definitions and setup |
| `PROJECT_SETUP_GUIDE.md` | This document |

---

## 🔄 Workflow

### For Developers

1. **Pick an issue** from current sprint with `ready` label
2. **Assign yourself** to the issue
3. **Change label** from `ready` to `in-progress`
4. **Create a branch**: `git checkout -b test/issue-#-short-description`
5. **Write tests** following patterns in TESTING_GUIDE.md
6. **Run tests locally**: `./gradlew test`
7. **Commit changes**: `git commit -m "Add tests for <component> (#issue-number)"`
8. **Push branch**: `git push origin test/issue-#-short-description`
9. **Create PR** with title "[TEST] Add tests for <component> (#issue-number)"
10. **Link PR to issue** by adding "Closes #issue-number" in PR description
11. **Change label** to `review`
12. **Request review** from sprint owner
13. **Address feedback** if needed
14. **Merge PR** after approval
15. **Change label** to `done`
16. **Close issue** (auto-closed by PR merge)

### For Reviewers

1. **Check** tests follow repository conventions
2. **Verify** coverage meaningfully improves
3. **Ensure** tests are clear and maintainable
4. **Validate** tests align with educational use cases
5. **Run tests** locally to confirm they pass
6. **Provide** constructive feedback
7. **Approve** or request changes
8. **Merge** when approved

---

## 🛠️ Troubleshooting

### Issue: Labels not showing in filters
**Solution:** Refresh page or clear browser cache

### Issue: Milestones show wrong due dates
**Solution:** Edit milestone and update due date

### Issue: Project board not updating
**Solution:** Check automation rules are configured correctly

### Issue: Can't create issues
**Solution:** Verify you have write access to repository

---

## 📞 Support

- **Questions:** Create discussion in repository
- **Issues:** File issue with `question` label
- **Documentation:** Update relevant `.md` file
- **Process improvements:** Discuss in sprint retrospective

---

## ✅ Setup Checklist

Before starting Sprint 1, ensure:

- [ ] All 24 labels created
- [ ] All 5 milestones created
- [ ] Epic issue created and pinned
- [ ] All 60 sprint issues created
- [ ] Issues properly labeled and assigned to milestones
- [ ] Project board set up (optional)
- [ ] Team members have repository access
- [ ] Sprint owners assigned
- [ ] Kickoff meeting scheduled
- [ ] Documentation reviewed by team
- [ ] CI/CD pipeline ready for test runs

---

**Ready to start?** Begin with Sprint 1!

See `.github/SPRINT_1_CRITICAL_API.md` for detailed sprint plan.

---

**Created:** 2025-11-17  
**Last Updated:** 2025-11-17  
**Maintained By:** Test Coverage Project Team
