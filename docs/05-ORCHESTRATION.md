# 5. DevOps Orchestration & CI/CD Strategy

## 5.1 Containerization Strategy
[cite_start]We employ a **Multi-Stage Docker Build** strategy to ensure our production images are secure, lightweight, and optimized for deployment[cite: 49].

### Dockerfile Logic (Java Service)
1.  **Stage 1: The Builder**
    * **Base Image:** `maven:3.9-eclipse-temurin-17`
    * **Purpose:** Contains the full JDK and Maven toolchain required to compile the code and run tests. This layer is heavy (~600MB) but necessary for the build process.
2.  **Stage 2: The Runtime**
    * **Base Image:** `eclipse-temurin:17-jre-alpine`
    * **Purpose:** Contains *only* the Java Runtime Environment (JRE) needed to run the application.
    * **Benefit:** The final image size is reduced to ~180MB. By removing the JDK and Maven tools, we significantly reduce the "Attack Surface" available to a potential attacker.

---

## 5.2 CI/CD Pipeline Design (GitHub Actions)
[cite_start]The pipeline follows a "Fail-Fast" philosophy[cite: 37]. Cheap, fast checks run first; expensive, slow checks run last. This ensures we don't waste compute resources building a Docker image if the code has a simple syntax error.

### Pipeline Stages & Reasoning

| Stage | Tool | Justification (Why this stage exists) |
| :--- | :--- | :--- |
| **1. Checkout** | `actions/checkout` | [cite_start]Retrieves the source code for the pipeline to act upon[cite: 64]. |
| **2. Setup & Cache** | `actions/setup-java` | Installing dependencies is slow. [cite_start]We cache the `~/.m2` folder to speed up subsequent builds[cite: 35]. |
| **3. Linting** | **Checkstyle** | **Code Quality:** Enforces the "Google Java Style Guide". [cite_start]Prevents technical debt and ensures code readability before logic is tested[cite: 40, 89]. |
| **4. SAST** | **GitHub CodeQL** | [cite_start]**Security:** Scans the *source code* for logic flaws (e.g., SQL Injection, Hardcoded Secrets) before compilation[cite: 45, 69]. |
| **5. SCA** | **Dependency Check** | [cite_start]**Supply Chain Security:** Scans `pom.xml` to identify 3rd-party libraries with known vulnerabilities (e.g., Log4j)[cite: 46, 71]. |
| **6. Unit Tests** | **JUnit 5** | **Reliability:** Validates business logic. [cite_start]We use **Mockito** to mock the OpenBB service, ensuring tests pass even without network access[cite: 34, 91]. |
| **7. Build Artifact** | `mvn package` | [cite_start]Compiles the code into an executable JAR file[cite: 74]. |
| **8. Docker Build** | `docker build` | [cite_start]Packages the application into a container image[cite: 76]. |
| **9. Image Scan** | **Trivy** | [cite_start]**Infrastructure Security:** Scans the *built container image* (OS layer) for vulnerabilities (e.g., outdated Alpine Linux packages)[cite: 79, 98]. |
| **10. Registry Push**| `docker push` | **Delivery:** Pushes the trusted, scanned image to DockerHub. [cite_start]This enables downstream CD tools (Render) to pull the artifact[cite: 82, 102]. |

---

## 5.3 Security & Secrets Management
[cite_start]To adhere to DevSecOps principles, no sensitive data is hardcoded in the repository[cite: 154].

### Implemented Controls
1.  **GitHub Secrets:**
    * [cite_start]`DOCKERHUB_USERNAME` & `DOCKERHUB_TOKEN`: Used to authenticate with the Docker Registry securely[cite: 150].
    * `RENDER_DEPLOY_HOOK`: A secret webhook URL used to trigger the cloud deployment without exposing API keys.
2.  **Least Privilege:**
    * The CI runner uses a temporary, ephemeral environment that is destroyed after execution.
    * The Docker container runs as a non-root user (where supported) to prevent privilege escalation.

## 5.4 Continuous Deployment (CD)
We utilize a **GitOps-style** deployment flow:
1.  **Trigger:** The GitHub Action completes successfully and pushes a new tag `latest` to DockerHub.
2.  **Notification:** The pipeline sends a `POST` request to the Render Deploy Hook.
3.  **Deployment:** Render detects the signal, pulls the new image from DockerHub, and performs a zero-downtime rolling update.