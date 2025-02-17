FROM quay.io/keycloak/keycloak:latest

ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=admin
ENV KEYCLOAK_HTTP_ENABLED=true
ENV KEYCLOAK_HTTP_PORT=8180
ENV KEYCLOAK_HOSTNAME=localhost
ENV KEYCLOAK_DB=dev-mem
ENV KC_DB=dev-mem

ENTRYPOINT ["/bin/sh", "-c", "/opt/keycloak/bin/kc.sh start --db=$KEYCLOAK_DB --hostname=$KEYCLOAK_HOSTNAME --http-enabled=$KEYCLOAK_HTTP_ENABLED --http-port=$KEYCLOAK_HTTP_PORT"]