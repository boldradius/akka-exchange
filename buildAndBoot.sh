#!/bin/sh
sbt docker:stage && docker-compose stop && docker-compose rm && docker-compose build && docker-compose up -d
