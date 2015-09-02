#!/bin/bash
docker run -d -p 8080:8080 -h frontend --name frontend akka-exchange-frontend:0.1-SNAPSHOT
docker run -d --link frontend:seed -h shared-journal --name shared-journal akka-exchange-journal:0.1-SNAPSHOT
docker run -d --link frontend:seed -h trader-db --name trader-db akka-exchange-trader-db:0.1-SNAPSHOT
