test:
	docker compose up -d
	sbt run test
	docker compose down