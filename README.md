# Tickex

Tickex backend services in a single Java 21 monorepo.

## Layout

```text
services/
  auth-service/          Authentication and identity
  event-service/         Event and venue management
  ticketing-service/     Orders, tickets, and check-in
  integration-service/   Downstream integration orchestration
  notification-service/  Notification intake and delivery queueing
```

## Requirements

- Java 21
- Maven 3.9+
- Docker Compose for local databases

## Build

Build all five application services from the repository root:

```bash
mvn clean verify
```

The integration service forwards notification requests to the notification
service. Its downstream URL can be overridden with
`TICKEX_NOTIFICATION_BASE_URL`.

## Local configuration

Each service that needs local credentials contains a committed `.env.example`
with empty values. Copy it to `.env` inside that service directory and fill the
values locally. `.env` files and credential material are ignored by Git.
