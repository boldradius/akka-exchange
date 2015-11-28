# Detect if docker is running, then init some vars
#

if [ ! -f "/.dockerinit" ]
then
  exit
fi

echo "Docker Container Environment Detected. Setting up..."

#echo "*** LINUX ENV ***"
#echo "??? proc/version ???"
#cat /proc/version

# Akka's default levelDB journal needs libstdc++.so which isn't included in Alpine by default
# check also if we are on alpine based on package manager command
which apk
APK_RETVAL=$?
if [ $APK_RETVAL -eq 0 ] && [ ${CLUSTER_ROLE:=ERR_NO_ROLE_ENV_SET} = "shared-journal" ]
then
    echo "Installing build tools (some libs needed for shared journal). This might take a minute or two..."

    su -c "apk add --update build-base" #alpine-sdk

    echo "Cleaning up after build tool install"
    su -c "rm -rf /var/cache/apk/*"
else
   echo "Note: Did not find either APK (indicating Alpine Linux) or a shared-journal cluster role."
   echo "APK Find return code was $APK_RETVAL; Role Set to $CLUSTER_ROLE ; won't install build tools."
fi

export NAMED_HOSTNAME=${HOSTNAME}
export HOSTNAME=`hostname -i | tr -d '[[:space:]]'`

echo "Hostname (${NAMED_HOSTNAME}) translated to IP Address (${HOSTNAME}) for Docker Resolution..."




