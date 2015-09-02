#!/bin/zsh
# Function that you can use to import a docker-env for a particular machine 
function docker-env () {
  if [[ -n "${1+x}" ]]; then
    eval "$(docker-machine env $1)"
  else
    eval "$(docker-machine env akka-exchange)"
  fi
}
docker-env

export DOCKER_CERT_PATH
export DOCKER_HOST
export DOCKER_MACHINE_NAME
export DOCKER_TLS_VERIFY
