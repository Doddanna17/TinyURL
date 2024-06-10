# TinyURL

For Postgres DB
docker run --name shortUrlPostgres -e POSTGRES_PASSWORD=password -e POSTGRES_USER=postgres -e POSTGRES_DB=testdb -p 5432:5432 -d postgres

For Zookeeper
docker run -p 2181:2181 -d zookeeper:latest
