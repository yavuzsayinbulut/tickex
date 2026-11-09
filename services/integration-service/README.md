# Integration Service

Receives integration requests and forwards notification work to the Tickex
notification service.

- Port: `8084`
- Endpoint: `POST /api/v1/integrations/notifications`
- Downstream URL: `TICKEX_NOTIFICATION_BASE_URL`
