#!/usr/bin/env bash

WFP='Workflow-Engine-Provider'
WFS='Workflow-Engine-Service'

root=$(pwd)
cd $WF_ENGINE_HOME
if [ x = x$WF_ENGINE_HOME ];then
  cd $root
fi

if [ x != x$HOST_IP ];then
  cp /etc/hosts /etc/hosts.t
  sed -i 's/1.*'$(hostname)'/'`HOST_IP`'    '`hostname`'/g' /etc/hosts.t
  cat /etc/hosts.t > /etc/hosts
  echo "USE DEFAULT HOST_IP: "$HOST_IP
fi

function execute(){
  COMMAND=$1
  ARG=$2
  if [ x$COMMAND = x'--run' ] || [ x$COMMAND = x'-r' ] || [ x$COMMAND = $'-run' ];then
    if [ x$ARG = x'provider' ]; then
      P=$WFP
    fi

    if [ x$ARG =  x'service' ]; then
      P=$WFS
    fi

    if [ P = P$ARG ]; then
            echo "NEED PARAM [provider | service]"
            exit
    fi
    echo "[EXECUTE] "$P
    rm $P'.jar' -f
    cd $P

    jar cvfm0 ../$P.jar META-INF/MANIFEST.MF .
    cd ..
    java -jar $P'.jar'

  fi

  if [ x$COMMAND = x'--ip' ] || [ x$COMMAND = x'-i' ] || [ x$COMMAND = $'-ip' ];then

    IP=$ARG
    if [ IP = IP$ARG ]; then
      echo "NEED PARAM [ --ip host-ip-address ]"
      exit 1
    fi

    ip=$(echo $IP | grep "^-")
    if [ x$ip != x ]; then
      echo "HOST IP FORMAT ERROR: "$ip
      exit 1
    fi

    echo "[USE HOST_IP] "$IP
    cp /etc/hosts /etc/hosts.t
    sed -i 's/.*'$(hostname)'/'$IP'    '`hostname`'\n/g' /etc/hosts.t
    cat /etc/hosts.t > /etc/hosts

  fi


}

COMMAND1=$1
PARAM1=$2
COMMAND2=$3
PARAM2=$4

if [[ x$3 != x ]]; then
  str=$(echo $3 | grep "-i")
  if [[ x$str!=x ]]; then
    COMMAND2=$1
    PARAM2=$2
    COMMAND1=$3
    PARAM1=$4
  fi
fi
execute $COMMAND1 $PARAM1
execute $COMMAND2 $PARAM2
