# Run the application and verify the Liquibase demo

These are commands for you to run. Creating this guide did not start Docker,
the application, or tests.

## 1. What was added

- `Main.java` now starts Spring Boot.
- `application.yml` connects to PostgreSQL using environment variables and points
  Liquibase to `db/changelog/db.changelog-master.yaml`.
- `postgres-init` creates the application database if missing, explicitly sets its
  default isolation to `read committed`, and creates the `booking` schema if missing.
  It waits for PostgreSQL health, exits on completion, and leaves the server running.
- The master changelog first includes `001-schema.sql` to create the LLD section 2.5
  tables with primary and foreign keys, then `002-liquibase-demo.sql` to create
  `booking.liquibase_demo`. Secondary indexes are deferred; composite unique constraints
  needed as foreign-key targets are included. Schema preparation belongs to `postgres-init`.
- The table has an automatically generated `id`, a required `name` (100 characters
  maximum), and a `created_at` timestamp defaulted by PostgreSQL.
- Liquibase records applied changes in `public.databasechangelog` and coordinates
  migrations with `public.databasechangeloglock`.
- Hibernate uses `ddl-auto: validate`; it does not create or modify tables.
  There is no JPA entity for this demo, so verification uses SQL directly.
- The `local` Spring profile allows `/actuator/health` and `/actuator/prometheus`,
  denies other HTTP routes, and disables the Redis health check. This lets you
  try migrations with PostgreSQL alone. Keycloak integration is still pending.

## 2. Prepare your environment

Use JDK 25, Docker Engine/Desktop with Compose v2, and a terminal in the repository root.
Ensure the Docker daemon is running. Verify your Java installation:

```bash
java -version
```

If `.env` does not exist yet:

```bash
cp .env.example .env
```

If `.env` already exists, preserve it and add any missing variables from `.env.example`.
Replace example passwords with local values. For the commands below, keep `.env`
compatible with Bash assignments: no spaces around `=`, and single-quote passwords
containing spaces or shell metacharacters. `.env` is ignored by Git.

Compose reads `.env` automatically. Gradle and the IDE do not. In Bash, export its values
before running the application from the same terminal:

```bash
set -a
source ./.env
set +a
```

Treat `.env` as trusted local shell input. Do not print or commit its passwords.

## 3. Start PostgreSQL

For this demo, start only the database:

```bash
docker compose up -d --wait postgres
docker compose run --rm postgres-init
docker compose ps postgres
```

Expected: PostgreSQL is healthy and `postgres-init` prints its completion message
and exits with code 0. The first run may download the image and initialize an empty
data volume. The main container initializes the login and the `postgres` maintenance
database. `postgres-init` then prepares `booking_db` and the `booking` schema.
Application tables are created later by Liquibase. Do not start Java if initialization
fails. The `run --rm` command waits for completion and removes only its one-shot client.
It can be rerun safely with the same settings. The server continues running.

For a future containerized Spring Boot service, add a dependency on `postgres-init`
with `condition: service_completed_successfully`. The current IDE/Gradle process is
outside Compose, so it must be started after the initialization command succeeds.

If startup fails, inspect:

```bash
docker compose logs --tail=100 postgres
```

The current Compose file publishes PostgreSQL on host port `5432`. If another database
already uses it, change the mapping to e.g. `"5433:5432"`, set `BOOKING_DB_PORT=5433` in
`.env`, then reload the environment before starting the Java application.

## 4. Start Spring Boot

From the terminal where you exported `.env`:

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

On Windows, use `gradlew.bat` and set the equivalent environment variables in the
terminal or IDE instead of using Bash's `source` command.

For IntelliJ, use JDK 25, run `com.cts.booking.Main`, set program arguments to
`--spring.profiles.active=local`, and copy the database variables from `.env` into the
run configuration's environment variables. An IDE does not inherit exports made in
an unrelated terminal.

Expected startup behavior:

1. Liquibase connects to `booking_db`.
2. It applies the LLD table changesets and the demo-table changeset to the schema prepared by `postgres-init`.
3. Hibernate initializes, and Tomcat starts on port `8080`.

On a fresh database, the first startup should report the table changesets applied. Keep this terminal running.
There is no demo REST controller; visiting `/` is not the migration test.

## 5. Check the application

In another terminal:

```bash
curl -i http://localhost:8080/actuator/health
curl -i http://localhost:8080/actuator/prometheus
```

Expected: health returns HTTP 200 with `{"status":"UP"}`. The Prometheus endpoint
returns metrics text. The local profile exposes these two endpoints only.

## 6. Inspect the table and migration history

These commands use the PostgreSQL container's own environment, so you do not need
`psql` installed on your computer or the password exported in this second terminal:

```bash
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$BOOKING_DB_NAME" -c "\d booking.liquibase_demo"'
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$BOOKING_DB_NAME" -c "SELECT id, author, filename, exectype FROM public.databasechangelog ORDER BY orderexecuted;"'
```

Expected on a fresh database: the demo table has three columns and the history lists
the LLD table changesets followed by `002-create-liquibase-demo`, initially `EXECUTED`.
Older schema-creation history, if present, is preserved.

Insert and query a sample row using interactive `psql`:

```bash
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$BOOKING_DB_NAME"'
```

At the SQL prompt:

```sql
INSERT INTO booking.liquibase_demo (name)
VALUES ('First Liquibase demo')
RETURNING id, name, created_at;

SELECT * FROM booking.liquibase_demo;
```

Expected: PostgreSQL generates an ID and timestamp. Exit `psql` with `\q`.
This manual row is test data; it is not part of a schema migration.

## 7. Verify migrations are not reapplied

Stop Java with Ctrl+C, then start it again using the same `bootRun` command.
Recheck the migration history and the sample row from step 6.

Expected: Liquibase reports no new changesets to apply, the history row count is unchanged,
and the sample row remains. The database is not reset on application restart.

Do not edit or rename a changeset after it has been applied to a shared database.
For the next schema change, add a new numbered SQL file with a unique changeset ID
and include it at the end of the master changelog. The rollback comments document
reverse SQL; normal Spring Boot startup does not execute rollbacks.

## 8. Optionally start monitoring and the rest of the core stack

```bash
docker compose up -d --wait postgres redis kafka prometheus grafana
```

This also starts Redis, Kafka, Prometheus, and Grafana. The existing `lastest` tag
misspellings for Grafana and Temporal were corrected to `latest` to match the current
Compose tag choices; those tags can change between downloads.

- Open Prometheus at http://localhost:9090 and inspect the `booking-service` target.
- Open Grafana at http://localhost:3000; the current Compose login is `admin` / `admin`.
- Open **Dashboards → Booking → Booking Service Overview**.

Prometheus reaches the host at `host.docker.internal:8080`. If you change the Java HTTP
port, update `docker/prometheus/prometheus.yml` as well. If the target is DOWN, check
that Java is running with the `local` profile and that the host firewall permits
connections from Docker's bridge network. A server bound only to host loopback cannot
be scraped through the host gateway.

Temporal and Keycloak are optional for later integration:

```bash
docker compose --profile workflow --profile identity up -d
```

Starting Keycloak does not create Booking's realm, clients, or role mappings.

## 9. Automated tests and shutdown

When you want to run Gradle tests and coverage:

```bash
./gradlew test jacocoTestReport
```

No test classes have been added yet: `test` may report `NO-SOURCE`, and coverage may
be skipped. The SQL/startup checks above are manual smoke tests, not an automated suite.

Stop Java with Ctrl+C. Stop containers while preserving data:

```bash
docker compose --profile workflow --profile identity down
```

Avoid `down -v` unless you intend to delete all this stack's database, workflow, cache,
and monitoring volumes. Changing passwords in `.env` does not update an initialized
database's existing credentials.

## Common startup errors

| Symptom | What to check |
| --- | --- |
| Unresolved `BOOKING_DB_PASSWORD` | Export `.env` in the same terminal, or set the IDE environment. |
| Connection refused | PostgreSQL health, published host port, and `BOOKING_DB_PORT`. |
| Password authentication failed | Credentials in the existing volume may differ from `.env`. |
| Liquibase checksum mismatch | Restore the already-applied file and add a new migration. |
| Schema does not exist | Run `postgres-init` successfully before starting Java. |
| HTTP 403 on `/` | Expected: local security only allows health and Prometheus. |
| HTTP 401/403 on health | Ensure the `local` Spring profile is active. |
| Port 8080 already in use | Set `BOOKING_HTTP_PORT` and adjust the Prometheus scrape target. |

## Verify isolation and repeat initialization

After initialization, inspect a new database session:

```bash
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$BOOKING_DB_NAME" -c "SHOW default_transaction_isolation;"'
docker compose run --rm postgres-init
```

Expected: `read committed`; repeated initialization succeeds without dropping tables
or data. PostgreSQL MVCC already gives each READ COMMITTED statement a snapshot of
committed data. SQL Server's `READ_COMMITTED_SNAPSHOT` is not a PostgreSQL option.
See [PostgreSQL isolation documentation](https://www.postgresql.org/docs/17/transaction-iso.html).
Database defaults apply to new sessions; an application can explicitly override them.
The local bootstrap login needs permission to create databases and schemas. Existing
volumes keep their original login/password; changing `.env` does not recreate users.
