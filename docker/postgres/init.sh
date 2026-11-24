#!/bin/sh
# Run by postgres-init, after the separate postgres server becomes healthy.
# Abort on shell/SQL errors so an unsuccessful setup has a nonzero exit code.
set -eu

: "${BOOKING_DB_NAME:?BOOKING_DB_NAME must be set}"
: "${PGUSER:?PGUSER must be set}"

# CREATE DATABASE cannot run inside a transaction. psql's gexec executes the
# generated statement only if the database is absent. format(%I) quotes names.
psql -X --set=ON_ERROR_STOP=1 \
    --set=database_name="$BOOKING_DB_NAME" --set=database_owner="$PGUSER" <<'SQL'
SELECT format('CREATE DATABASE %I OWNER %I', :'database_name', :'database_owner')
WHERE NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = :'database_name')
\gexec

-- PostgreSQL already implements MVCC snapshots for READ COMMITTED queries.
-- There is no SQL Server READ_COMMITTED_SNAPSHOT switch to enable.
ALTER DATABASE :"database_name" SET default_transaction_isolation = 'read committed';
SQL

# Connect to the application database to create only its namespace, not tables.
psql -X --set=ON_ERROR_STOP=1 --dbname="$BOOKING_DB_NAME" \
    --set=database_owner="$PGUSER" <<'SQL'
CREATE SCHEMA IF NOT EXISTS booking AUTHORIZATION :"database_owner";
SQL

printf 'Database initialization completed; PostgreSQL remains running.\n'
