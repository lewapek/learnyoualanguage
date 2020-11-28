docker run --name postgres -d --restart unless-stopped --memory "1.5g" --cpus "1.0" -p 5432:5432 \
 -e POSTGRES_PASSWORD=pass \
 -e POSTGRES_DB=lyal \
 postgres:13-alpine