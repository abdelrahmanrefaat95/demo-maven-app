# DevOps Demo

A deliberately small Spring Boot application used as the running example in a five-day DevOps course. It shows how a build-time version and runtime environment configuration behave differently while providing a database, health probes, metrics, tests, an SBOM, and a safe place to practise deployment work.

## Build and run

```sh
mvn clean package
java -jar target/*.jar
```

Select an environment profile or override its color directly:

```sh
SPRING_PROFILES_ACTIVE=staging java -jar target/*.jar
APP_COLOR=#8b5cf6 java -jar target/*.jar
```

`application.yml` supplies defaults, `APP_COLOR` overrides them, and `--app.color=...` has the highest standard Spring precedence. Use `mvn clean package -Pfast` to skip tests for a build-speed demonstration.

To release a new version, change the `<version>` in `pom.xml` and run `mvn clean package`; the new version and build time are embedded in the jar. The same jar produces every environment on purpose: only runtime configuration changes the environment and color.

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | none (`local`) | Selects `dev`, `staging`, `prod`, or `postgres` profile |
| `APP_COLOR` | `slate` | Page background color |
| `DB_HOST` | `localhost` | PostgreSQL host (`postgres` profile) |
| `DB_PORT` | `5432` | PostgreSQL port (`postgres` profile) |
| `DB_NAME` | `devops_demo` | PostgreSQL database (`postgres` profile) |
| `DB_USER` | `postgres` | PostgreSQL user (`postgres` profile) |
| `DB_PASSWORD` | `postgres` | PostgreSQL password (`postgres` profile) |
| `HOSTNAME` | machine hostname | Hostname displayed by the page |

The default is an in-memory H2 database with a Flyway migration. Enable the `postgres` profile when a PostgreSQL service is available.
