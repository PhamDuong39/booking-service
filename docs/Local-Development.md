# Local development infrastructure

The Java application runs from the IDE. Compose runs its infrastructure.

## Start and stop

```bash
cp .env.example .env
# Replace all example passwords in .env, then:
docker compose up -d
docker compose run --rm postgres-init
```

If `.env` already exists, merge the new keys from `.env.example` instead of overwriting it.
Compose reads `.env` automatically; an IDE does not. Supply the corresponding variables
in your application's run configuration when application configuration is implemented.

Wait for `postgres-init` to exit successfully before starting the IDE application.
The initializer creates the database/schema if missing and explicitly sets the database
default isolation to `read committed`; it does not create application tables. PostgreSQL
continues running after the client exits. Repeating initialization preserves existing data.

Core services and monitoring start by default. Enable optional integrations with:

```bash
docker compose --profile workflow --profile identity up -d
```

Inspect and stop the complete stack:

```bash
docker compose --profile workflow --profile identity ps
docker compose logs -f postgres
docker compose --profile workflow --profile identity down
```

Named volumes preserve data after `down`. `down -v` deletes those databases and metrics.
Changing initialization passwords in `.env` does not update existing PostgreSQL users
or the existing Grafana/Keycloak administrator; change those through the running service.

## Services and addresses

| Service | Purpose | From the IDE / host | From another Compose container |
| --- | --- | --- | --- |
| PostgreSQL | Authoritative booking, inventory, order and ticket state | `localhost:5432`, database `booking_db` | `postgres:5432` |
| postgres-init | One-shot database/schema preparation after PostgreSQL is healthy | Exits after completion | Connects to `postgres:5432` |
| Redis | Shared cache and rate-limit state | `localhost:6379` | `redis:6379` |
| Kafka | Outbox publication and payment/refund events | `localhost:9092` | `kafka:29092` |
| Prometheus | Collect and retain metrics | http://localhost:9090 | `prometheus:9090` |
| Grafana | Explore metrics and view the Booking dashboard | http://localhost:3000 | `grafana:3000` |
| Temporal (`workflow`) | Saga execution, timers and workflow history | `localhost:7233`; UI http://localhost:8233 | `temporal:7233` |
| Keycloak (`identity`) | OIDC identity and token issuance | http://localhost:8180 | `keycloak:8080` |
| Keycloak database (`identity`) | Isolated identity persistence | Not published | `keycloak-db:5432` |

The table shows default host ports. The current core port mappings are fixed in
`docker-compose.yaml`; edit both the mappings and the corresponding application variables
if you change them. Optional Temporal/Keycloak ports use `.env` and bind to loopback. Kafka advertises separate host/container addresses so clients can connect
after bootstrap. Kafka is a single broker with replication factor one for local use.
Redis enables AOF persistence. Each stateful service uses its own named volume.

Temporal uses the CLI development server with a persistent SQLite database and its
built-in UI. It supports local workflow development and restart exercises; it does not
model a production Temporal cluster. Keycloak uses its own PostgreSQL database and
manages that schema itself. Realm, client, role, audience, and SMS provider configuration
remain a separate identity integration step.

## Metrics and dashboards

The current Grafana Compose configuration uses initial login `admin` / `admin`.
The provisioned Prometheus data source uses Compose DNS, not localhost. Open
**Dashboards → Booking → Booking Service Overview** to see scrape health, HTTP request
rate, JVM heap usage, and process CPU usage.

Prometheus scrapes itself and `http://host.docker.internal:8080/actuator/prometheus`.
The host alias works on Linux through the Compose `host-gateway` mapping. If the Java
application uses another port, update `docker/prometheus/prometheus.yml` and restart
Prometheus. The Java server must listen on an interface reachable from Docker.

Application configuration now includes datasource, Liquibase, Redis/Kafka addresses, and
Actuator health/Prometheus exposure. Run with the `local` Spring profile to allow health
and metrics without Keycloak; other HTTP routes are denied. The local profile disables
the Redis health check so the migration demo only requires PostgreSQL. No Kafka listeners
or Temporal workers exist yet. HTTP panels populate after requests are served.

Follow [the Liquibase demo guide](Liquibase-Demo.md) for the first startup and SQL checks.

PostgreSQL/Redis/Kafka infrastructure metrics need dedicated exporters; this dashboard
currently covers the Java application, not those servers. No production security, TLS,
HA, or alert-routing configuration is implied by this local setup.

Search/Report infrastructure (Elasticsearch, MongoDB, Spark, MinIO) and Notification's
email sandbox belong to later service integration work. They are not required to run
Booking and are not included in this Compose stack.

## Validate configuration without starting containers

```bash
docker compose --env-file .env.example --profile workflow --profile identity config --quiet
```

Implementation references: [Kafka Docker networking](https://docs.docker.com/guides/kafka),
[Grafana provisioning](https://grafana.com/docs/grafana/latest/administration/provisioning/),
[Temporal local server](https://docs.temporal.io/cli), and
[Keycloak containers](https://www.keycloak.org/server/containers).
