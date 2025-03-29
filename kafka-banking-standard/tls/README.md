  # TLS Keystores for IBM MQ (Development Only)

This folder contains region-specific TLS files for secure MQ communication.

---

## Files

| Region | Keystore File             | Truststore File              |
|--------|---------------------------|-------------------------------|
| EMEA   | mq-keystore-emea.jks.dev  | mq-truststore-emea.jks.dev   |
| NAM    | mq-keystore-nam.jks.dev   | mq-truststore-nam.jks.dev    |
| ASPAC  | mq-keystore-aspac.jks.dev | mq-truststore-aspac.jks.dev  |

---

## Instructions

1. **Rename each `.jks.dev` to `.jks` before running Docker:**

```bash
mv mq-keystore-emea.jks.dev mq-keystore-emea.jks
mv mq-truststore-emea.jks.dev mq-truststore-emea.jks
# Repeat for NAM and ASPAC
