mvn spring-boot:run




docker run -d --name bookingshare-db \
-e POSTGRES_USER=myuser \
-e POSTGRES_PASSWORD=mypassword \
-e POSTGRES_DB=bookingshare-db \
-p 5432:5432 -d postgres:15


mail":"contact@acme.com","exceptions":[],"id":1,"name":"Acme Corp","patterns":[]}

create user

curl -X POST http://localhost:8080/api/bookings/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Acme Corp","email":"contact@acme.com"}'

get token

curl -X POST http://localhost:8080/api/auth/token \
  -H "Content-Type: application/json" \
  -d '{"serviceId":"booking-service","secret":"my-secret"}'

  curl -X POST http://localhost:8080/api/bookings/patterns \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGc..." \
  -d '{"name":"Standard Hours","serviceType":"consultation","days":["MONDAY"],"openTime":"09:00","closeTime":"17:00","slotType":"FIXED_DURATION"}'

curl http://localhost:8080/api/bookings/patterns \
  -H "Authorization: Bearer eyJhbGc..."
