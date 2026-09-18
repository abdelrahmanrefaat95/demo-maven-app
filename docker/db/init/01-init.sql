-- Runs only on the first start, while the pgdata volume is still empty.
-- Flyway creates the items table; this script only adds a read-only role.
DO $$
BEGIN
  IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'reporting') THEN
    CREATE ROLE reporting LOGIN PASSWORD 'reporting-password';
  END IF;
  EXECUTE format('GRANT CONNECT ON DATABASE %I TO reporting', current_database());
END
$$;

GRANT USAGE ON SCHEMA public TO reporting;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO reporting;
