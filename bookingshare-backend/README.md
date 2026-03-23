mvn spring-boot:run

curl -X POST http://localhost:8080/api/bookings/patterns \
  -H "Content-Type: application/json" \
  -d '{"name":"Standard Hours","serviceType":"consultation","days":["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"],"openTime":"09:00","closeTime":"17:00","slotType":"FIXED_DURATION"}'


curl http://localhost:8080/api/bookings/patterns

curl -X POST http://localhost:8080/api/bookings/exceptions \
  -H "Content-Type: application/json" \
  -d '{"date":"2026-12-25","type":"CLOSED","serviceType":"consultation"}'

curl -X POST http://localhost:8080/api/bookings/exceptions \
  -H "Content-Type: application/json" \
  -d '{"date":"2026-12-24","type":"HOURS_CHANGED","serviceType":"consultation","openTime":"10:00","closeTime":"15:00"}'



curl -X GET "http://localhost:8080/api/bookings/slots?serviceType=consultation&startDate=2026-03-25&endDate=2026-03-31"

docker run -d --name bookingshare-db \
-e POSTGRES_USER=myuser \
-e POSTGRES_PASSWORD=mypassword \
-e POSTGRES_DB=bookingshare-db \
-p 5432:5432 -d postgres:15