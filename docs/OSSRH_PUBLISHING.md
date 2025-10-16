# OSSRH / Maven Central Publishing Guide

This document collects the long-lead and actionable steps to get JCoDroneEdu published to Maven Central (OSSRH). Long lead-time items are listed first so you can work them while preparing CI and secrets.

### GitHub Personal Access Token (PAT) creation (for RELEASE_PAT)

1. Create a PAT that Actions will use to create and upload Release assets (recommended for reliable release uploads):
  - Go to: https://github.com/settings/tokens → Developer settings → Personal access tokens → Tokens (classic) → Generate new token (or fine-grained tokens if you prefer).
  - Scopes recommended (classic token): `repo` (for private repos) or at least `repo:public_repo` and `workflow` if needed. If using packages, include `read:packages`/`write:packages` as needed.
  - Description: `JCoDroneEdu release automation token`
  - Generate token and copy it immediately (you won't be able to view it later).

2. Add the token to the GitHub repository secrets (Settings → Secrets → Actions) as `RELEASE_PAT`.

3. Test by running the release workflow; the `Prepare release token` step in the workflow prefers `RELEASE_PAT` over `GITHUB_TOKEN` and records `pat_present=true` when present.

---

### Sonatype Central — Portal user token (required for portal operations)

Sonatype provides a portal user token that is used for operations through the Central Publisher Portal and related Publisher APIs. If you need to interact with the Central Publisher Portal programmatically (or use portal-based publishing features), generate a user token and store it as a repository secret.

How to generate the portal token
1. Sign in at https://central.sonatype.com with your account.
2. Open the user tokens page: https://central.sonatype.com/usertoken
3. Click **Generate User Token**.
4. Enter a display name (e.g., `JCoDroneEdu CI token`) and select an expiration (or no expiration if supported and desired).
5. Copy the generated token immediately — the token is shown once in a modal and cannot be retrieved again.

Storing the portal token in GitHub
- Save the token as a repository secret named `SONATYPE_PORTAL_TOKEN` (Settings → Secrets → Actions). Because the token cannot be retrieved later, ensure you copy it before closing the modal.

How to use the portal token in CI
- Add the secret to the publish step so your workflow or scripts can call the Central Publisher Portal or Publisher APIs:

```yaml
env:
  SONATYPE_PORTAL_TOKEN: ${{ secrets.SONATYPE_PORTAL_TOKEN }}
  # existing secrets you already use:
  SIGNING_KEY: ${{ secrets.SIGNING_KEY }}
  SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
run: |
  # example: call a portal API (this is illustrative — follow portal API docs)
  curl -H "Authorization: Bearer $SONATYPE_PORTAL_TOKEN" https://central.sonatype.com/api
  ./gradlew publishStudentPublicationToOSSRHRepository -Pversion=${{ steps.get_version.outputs.VERSION }} -PossrhUsername=${{ secrets.OSSRH_USERNAME }} -PossrhPassword=${{ secrets.OSSRH_PASSWORD }}
```

Notes on usage
- The portal token is primarily for portal/API operations. Gradle's `maven-publish` plugin typically uses `OSSRH_USERNAME` and `OSSRH_PASSWORD` (or an equivalent deploy token) to upload artifacts to the Sonatype repository endpoints. If you want to use the Central Publisher API / portal for programmatic promotion or other flows, use `SONATYPE_PORTAL_TOKEN` when calling the portal endpoints.
- Tokens cannot be retrieved after generation. If you lose it, generate a new token and update the repository secret.
- For details and API usage examples, see: https://central.sonatype.org/publish/generate-portal-token/ and the Portal API docs linked from that page.

---

### Filling the central.sonatype.com form: Deployment & Description fields

When the central.sonatype.com form asks for a "Deployment" or "Description" field, paste concise, copy-ready text into each. Below are suggested values that make approval and later auditing easy.

Deployment (copy/paste)
```
Target repository: Maven Central (OSSRH / s01)
GroupId: com.otabi
ArtifactId(s): codrone-edu-java (student & teacher distributions)
Packaging: jar
Versioning: semantic versioning (e.g. 1.0.13). SNAPSHOT versions will be used for pre-releases only.
Publishing process: artifacts built by Gradle (root project), signed using PGP in CI (SIGNING_KEY + SIGNING_PASSWORD secret), and uploaded to Sonatype OSSRH staging repository via Gradle's maven-publish plugin. You do not need to pre-create a "component" in Sonatype; the staging repository and component are created as part of the staging/upload process.
CI: GitHub Actions workflow `.github/workflows/release.yml` will build artifacts (student/teacher/javadoc/sources) and run `./gradlew publishStudentPublicationToOSSRHRepository -Pversion=<version>` when publishing releases.
Staging: artifacts will be staged in Sonatype and manually or automatically promoted to Central after verification.
```

Description (copy/paste)
```
JCoDroneEdu Java library (codrone-edu-java) provides two packaged distributions: a student edition and a teacher edition (teacher includes testing utilities like MockDrone and DroneTest helpers). Artifacts are built with Gradle (Kotlin DSL) and published to Maven Central under groupId `com.otabi`. The repository contains source, Javadoc, and sources JARs. Releases are produced by an automated GitHub Actions workflow that compiles, signs (PGP), and uploads artifacts to Sonatype OSSRH staging repositories. Contact: <your name and email>.

Repository: https://github.com/scerruti/JCoDroneEdu
Build command (CI): `./gradlew -Pversion=<version> studentJar javadocJar sourcesJar` then `./gradlew publishStudentPublicationToOSSRHRepository -Pversion=<version> -PossrhUsername=<username> -PossrhPassword=<password>`
```

- Keep the Deployment section factual and short — it helps Sonatype reviewers locate the artifact shape and the automation that will publish it.
- In the Description, explicitly mention signing (PGP) and where the CI workflow lives so reviewers can validate the release flow.
- If you use multiple publications (student + teacher), explain that the teacher artifact is a separate JAR published as a classifier or a separate publication; give the Gradle publication names (`student` and `teacher`) for clarity.
- If Sonatype asks for the exact staging/promote process, reference the Gradle tasks above and that artifacts will be promoted in Sonatype UI after automated checks.
- If you do not need to create a component in Sonatype manually, do not specify one in the form: the release/staging process will create the component on upload.

### Providing GitHub credentials and testing the release flow

When you're ready to provide GitHub credentials for release automation, add a repository secret named `RELEASE_PAT` (see earlier PAT creation steps). This token will be picked up by the `Prepare release token` step in the workflow and used for release creation and upload steps.

Steps to add the token and run a test:

1. Add `RELEASE_PAT` to the repository secrets (Settings → Secrets → Actions). Use the PAT you generated earlier.

2. Optionally set `SIGNING_KEY` and `SIGNING_PASSWORD` secrets now so the publish step can sign artifacts during CI.

3. Trigger a test run:
  - Push a test tag (for example `v1.0.13-test`) or use the workflow_dispatch button in the Actions UI.

4. Inspect the Actions run logs (open the run in GitHub Actions):
  - `Prepare release token` step — verify `pat_present=true` in the step outputs (this confirms `RELEASE_PAT` was used).
  - `Create GitHub Release` — confirm the release exists and assets are uploaded.
  - `Upload * JAR` steps — confirm assets appear on the Release page.
  - `Publish to Maven Central` step — observe Gradle output. If `SIGNING_KEY` and OSSRH credentials are present, Gradle will attempt to upload to Sonatype; otherwise it may fail early (which confirms secrets are required).

5. If the release creation/upload works but Sonatype publish fails with group ownership or authorization errors, attach the central.sonatype.com request ID to the CI logs and wait for Sonatype to approve the group.

Security notes
- Do not paste tokens into chat. Use GitHub repository secrets. I will not ask for or accept secret values directly.
- After testing, if you created a test tag that you don't want to keep, delete it from the repo and the Release page.
- Keep the Deployment section factual and short — it helps Sonatype reviewers locate the artifact shape and the automation that will publish it.
- In the Description, explicitly mention signing (PGP) and where the CI workflow lives so reviewers can validate the release flow.
- If you use multiple publications (student + teacher), explain that the teacher artifact is a separate JAR published as a classifier or a separate publication; give the Gradle publication names (`student` and `teacher`) for clarity.
- If Sonatype asks for the exact staging/promote process, reference the Gradle tasks above and that artifacts will be promoted in Sonatype UI after automated checks.

---

---

## 2) Secrets and key prep (medium lead time)

Required GitHub repository secrets (add via repo Settings → Secrets → Actions):
- `OSSRH_USERNAME` — Sonatype username
- `OSSRH_PASSWORD` — Sonatype password or API key
- `SIGNING_KEY` — PGP private key (armored ASCII). Recommended: base64-encoded armored key.
- `SIGNING_PASSWORD` — passphrase for the PGP key
- `RELEASE_PAT` — personal access token for release/upload steps (preferred over GITHUB_TOKEN for reliable release creation/uploads)

How to export and base64-encode a GPG private key (macOS example):

```bash
# export armored private key to a file (replace KEYID)
gpg --export-secret-keys --armor <KEYID> > private-key.asc

# base64-encode (single-line) for safe secret upload
base64 private-key.asc | pbcopy
# now paste into SIGNING_KEY secret in GitHub (it will be one long line)
```

Alternative: paste the raw armored file contents directly into the secret (multi-line). The Gradle logic detects both base64 and raw armored formats.

Important: Do NOT commit private keys or passphrases into the repository. Use GitHub Secrets only.

### Maven `settings.xml` server entry (optional local setup)

If you prefer to publish from your local machine using Maven/Gradle with a `settings.xml`, you can add a `<server>` entry to `~/.m2/settings.xml`. Only do this on a trusted machine and do not commit `settings.xml` into version control.

Example server entry (copy/paste and replace `${server}` with your repository id if needed). DO NOT include real credentials in the repo — use placeholders here and store real values in `~/.m2/settings.xml` locally or in CI secrets.

```xml
<server>
  <id>${server}</id>
  <username>your-ossrh-username</username>
  <password>your-ossrh-password-or-api-key</password>
</server>
```

Notes:
- Replace `${server}` with the repository id your Gradle/Maven publish configuration expects (for example `OSSRH` or `ossrh` depending on your config).
- This snippet uses placeholders; do NOT leave real credentials in version control. Prefer using CI secrets (`OSSRH_USERNAME`/`OSSRH_PASSWORD`) for automated workflows and avoid storing plaintext credentials in shared machines.
- If you need stronger local security, consider using Maven's password encryption (`mvn --encrypt-password`) and the `settings-security.xml` mechanism; see Maven docs for details.

---

## 3) CI change (short lead time)

Make this small change to `.github/workflows/release.yml` so Gradle receives the computed version and credentials. Replace the existing publish run line:

From:
```yaml
run: ./gradlew publishStudentPublicationToOSSRHRepository
```

To (recommended):
```yaml
env:
  SIGNING_KEY: ${{ secrets.SIGNING_KEY }}
  SIGNING_PASSWORD: ${{ secrets.SIGNING_PASSWORD }}
run: |
  ./gradlew publishStudentPublicationToOSSRHRepository \
    -Pversion=${{ steps.get_version.outputs.VERSION }} \
    -PossrhUsername=${{ secrets.OSSRH_USERNAME }} \
    -PossrhPassword=${{ secrets.OSSRH_PASSWORD }}
```

Why: ensures the exact same version built earlier is published and avoids accidental fallback to the 1.0.0 default. Also provides credentials explicitly to Gradle.

Optional: If you prefer to set `RELEASE_VERSION` env var instead, export it in the step and keep the Gradle invocation as-is.

---

## 4) Local verification steps (quick, run before CI)

Run these locally to validate artifact generation, POM contents, and local publishing:

```bash
# quick compile and property check
./gradlew properties -q

# build artifacts with a specific version (adjust version as needed)
./gradlew -Pversion=1.0.13 studentJar javadocJar sourcesJar teacherJar

# publish to local Maven (inspect POM & groupId)
./gradlew -Pversion=1.0.13 publishStudentPublicationToMavenLocal

# inspect target in ~/.m2
ls -la ~/.m2/repository/com/otabi/codrone-edu-java/1.0.13
cat ~/.m2/repository/com/otabi/codrone-edu-java/1.0.13/codrone-edu-java-1.0.13.pom
```

If you want to test signing locally, temporarily set env vars in your shell (do not commit them):

```bash
export SIGNING_KEY=$(base64 -w0 private-key.asc)
export SIGNING_PASSWORD="your-passphrase"
./gradlew -Pversion=1.0.13 publishStudentPublicationToMavenLocal
```

---

## 5) CI dry-run and verification (what to inspect on GitHub Actions)

After pushing a tag (e.g. `v1.0.13`) or triggering the workflow manually:

- `Prepare release token` step — check `pat_present` output to confirm whether `RELEASE_PAT` or `GITHUB_TOKEN` was used.
- `Create GitHub Release` — verify the release was created and has correct tag and body.
- `Upload * JAR` steps — ensure assets appear on the GitHub Release page.
- `Publish to Maven Central` step — check Gradle logs for successful upload (HTTP 201) and `BUILD SUCCESSFUL`.

If Sonatype rejects the publish because of group ownership, you will see an error indicating authorization for that groupId. In that case, attach the Sonatype ticket number in the CI logs and wait for Sonatype to approve the group.

---

## 6) Common failure modes and fixes

- Resource not accessible by integration (release create/upload)
  - Cause: `GITHUB_TOKEN` lacks permissions or token is from a fork. Fix: use `RELEASE_PAT` with repo write permissions and set it as a repo secret.

- Gradle publish task not present
  - Cause: root `publishing.repositories` not configured. Fix: add OSSRH repo block to `build.gradle.kts` (already added in current tree).

- Signing errors (no key available / incorrect passphrase)
  - Cause: SIGNING_KEY missing or improperly formatted; SIGNING_PASSWORD wrong. Fix: ensure the secret is the armored key or base64 of the armored key and the passphrase matches.

- Sonatype groupId ownership rejection
  - Cause: groupId doesn't belong to your account/organization. Fix: open an OSSRH ticket requesting ownership and include SCM and license evidence (see section 1).

---

## 7) Checklist (working order)

1. Draft and submit Sonatype OSSRH request for `com.otabi` (long lead-time).
2. Create repository secrets: OSSRH_USERNAME, OSSRH_PASSWORD, SIGNING_KEY, SIGNING_PASSWORD, RELEASE_PAT.
3. Update `.github/workflows/release.yml` to pass version and credentials to Gradle (see section 3).
4. Run local verification (section 4) and confirm artifacts & POM look correct.
5. Trigger CI by pushing `v<version>` tag. Inspect Actions logs and Sonatype staging UI.
6. If publish succeeds, promote staging repo on Sonatype and close the ticket.

---

## 8) Copy-friendly artifacts

- Sonatype request template: use section 1 block.
- CI snippet to paste: use section 3 block.
- Local commands: use section 4 block.

If you want, I can open a PR with the CI change and this document added to `docs/OSSRH_PUBLISHING.md`, or I can just commit this file (done). Tell me which and I will proceed.
