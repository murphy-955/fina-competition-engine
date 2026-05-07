# AGENTS.md — fina-competition-engine

> This file is intended for AI coding agents. It describes the project as it actually exists, not as it is planned to be.

---

## Project Overview

`fina-competition-engine` is a Java-based competition engine project. It is currently in the initial scaffolding phase: the Maven build system is configured, a documentation site skeleton exists, and a reference PDF (世界泳联技术说明 — FINA/World Aquatics technical specification, dated 2026-02-18) is present in the repository root. No application source code, tests, or runtime dependencies have been added yet.

- **Group ID**: `io.github.murphy955`
- **Artifact ID**: `fina-competition-engine`
- **Version**: `1.0-SNAPSHOT`
- **Java Version**: 17 (source and target)
- **Encoding**: UTF-8

---

## Technology Stack

- **Language**: Java 17
- **Build Tool**: Apache Maven (no wrapper committed yet)
- **Documentation**: Docsify v4 (served from `docs/` for GitHub Pages)
- **IDE**: IntelliJ IDEA (`.idea/` metadata committed)

No runtime dependencies, frameworks, or plugins are declared in `pom.xml` at this time.

---

## Project Structure

```
fina-competition-engine/
├── pom.xml                          # Maven POM (minimal, no deps/plugins yet)
├── .gitignore                       # Composite ignore rules (Java + Node + JetBrains)
├── docs/                            # Docsify documentation site
│   ├── index.html                   # Docsify entry point (vue theme)
│   ├── README.md                    # Placeholder docs homepage
│   └── .nojekyll                    # Disables Jekyll processing on GitHub Pages
├── .idea/                           # IntelliJ IDEA project files (committed)
└── 2026-2-18世界泳联技术说明.pdf      # FINA technical specification reference (Chinese)
```

**Notable**: There is no `src/` directory yet. Standard Maven directories (`src/main/java`, `src/test/java`, `src/main/resources`, `src/test/resources`) do not exist.

---

## Build and Test Commands

Because the project is empty, only basic Maven lifecycle phases are applicable:

```bash
# Validate the POM
mvn validate

# Compile (will succeed trivially because there are no sources)
mvn compile

# Run tests (will succeed trivially because there are no tests)
mvn test

# Package
mvn package

# Clean build artifacts
mvn clean
```

There are no custom Maven profiles, plugins, or test frameworks configured yet.

---

## Code Style Guidelines

No explicit style configuration (e.g., Checkstyle, Spotless, or `.editorconfig`) is present. Until one is added:

- Follow standard Java naming conventions.
- Use UTF-8 source encoding (enforced by `project.build.sourceEncoding` in `pom.xml`).
- Target Java 17 language features (e.g., `var`, `switch` expressions, records, sealed classes are permitted).

---

## Testing Instructions

No testing framework is configured yet. When tests are introduced:

- Add the chosen test dependency (e.g., JUnit 5) to `pom.xml` under `<dependencies>`.
- Place unit tests under `src/test/java` following standard Maven directory layout.
- Run tests with `mvn test`.

---

## Documentation

The `docs/` folder is set up as a **Docsify** site intended for GitHub Pages:

- `docs/index.html` loads Docsify v4 from CDN with the `vue.css` theme.
- `docs/.nojekyll` prevents GitHub Pages from using Jekyll, which is required for Docsify.
- `docs/README.md` is currently a placeholder.

To preview docs locally, any static file server serving `docs/` will work (Docsify CLI is not installed in the repo).

---

## Security Considerations

- No secrets, credentials, or environment files are present.
- `.gitignore` already excludes common sensitive patterns (`.env`, `.env.*`).
- Do not commit secrets, API keys, or database credentials.
- The committed `.idea/` folder does not currently contain sensitive data, but be cautious when committing IDE metadata in the future (it may contain run configurations with hardcoded paths or keys).

---

## Current State Checklist

- [x] Maven POM initialized (`pom.xml`)
- [x] `.gitignore` configured
- [x] Docsify docs scaffolded (`docs/`)
- [x] IntelliJ IDEA project files committed
- [x] FINA reference PDF added
- [ ] `src/main/java` directory and application code
- [ ] `src/test/java` directory and tests
- [ ] Runtime dependencies declared in `pom.xml`
- [ ] Build plugins (e.g., compiler plugin, shade/assembly plugin, static analysis)
- [ ] `AGENTS.md` updated as the project evolves
