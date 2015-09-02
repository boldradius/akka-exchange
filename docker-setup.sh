#!/bin/sh
#
# Sets up the docker-machine we expect for use in the 
#   - Adjust cpu count and memory as needed, remembering we'll run several nodes
#   * This sets up an expected virtualbox container for the akka-exchange system

source docker-env.sh

DEFAULT_CPU_COUNT = 2

DEFAULT_RAM = 4096

if [[ -n "${1+x}" ]]; then 
  DEFAULT_CPU_COUNT = $1
fi

if [[ -n "${2+x}" ]]; then 
  DEFAULT_RAM = $2
fi

echo "Setting up 'akka-exchange' Docker Machine with $DEFAULT_RAM MB RAM and $DEFAULT_CPU_COUNT CPUs."

docker-machine create -d virtualbox --virtualbox-memory $DEFAULT_RAM --virtualbox-cpu-count $DEFAULT_CPU_COUNT akka-exchange
