#!/bin/sh

docker-machine create -d virtualbox --virtualbox-cpu-count 2 --virtualbox-memory 4096 --virtualbox-disk-size 10240 akka-exchange
