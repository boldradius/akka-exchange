# Detect if docker is running, then init some vars
#

if [ ! -f "/.dockerinit" ]
then
  exit
fi

echo "Docker Container Environment Detected. Setting up..." 


export NAMED_HOSTNAME=${HOSTNAME}
export HOSTNAME=`hostname -I | tr -d '[[:space:]]'`

echo "Hostname (${NAMED_HOSTNAME}) translated to IP Address (${HOSTNAME}) for Docker Resolution..."




