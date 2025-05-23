# Use official PostgreSQL image
FROM postgres:15-alpine

# Set environment variables
ENV POSTGRES_DB=event_management
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

# Copy initialization scripts
COPY init-scripts/ /docker-entrypoint-initdb.d/

# Expose PostgreSQL port
EXPOSE 5432