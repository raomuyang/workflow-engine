#!/usr/bin/env bash

WFP='Workflow-Engine-Provider'
WFS='Workflow-Engine-Service'

NAME=`hostname`
CHECK=`cat /etc/hosts | grep ${NAME}`
if [ x == x$CHECK ]; then
    echo "127.0.0.1 \t"${NAME}
fi

root=$(pwd)
cd $WF_ENGINE_HOME
if [ x = x$WF_ENGINE_HOME ];then
  cd $root
fi

if [ x != x$HOST_IP ];then
  cp /etc/hosts /etc/hosts.t
  sed -i 's/1.*'$(hostname)'/'`HOST_IP`'    '`hostname`'/g' /etc/hosts.t
  cat /etc/hosts.t > /etc/hosts
  printf "\e[0;32;1mUSE DEFAULT HOST_IP: ${HOST_IP}\e[0m \n"
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
            printf "\e[1;33m[INFO] NEED PARAM [provider | service]\e[0m \n"
            exit 1
    fi
    printf "\e[1;33m[INFO] EXECUTE ${P} \e[0m \n"
    rm $P'.jar' -f
    cd $P

    jar cvfm0 ../$P.jar META-INF/MANIFEST.MF .
    cd ..
    java -jar $P'.jar'

  fi

  if [ x$COMMAND = x'--ip' ] || [ x$COMMAND = x'-i' ] || [ x$COMMAND = $'-ip' ];then

    IP=$ARG
    if [ IP = IP$ARG ]; then
      printf "\e[1;31m[INFO]Need Param [ --ip host-ip-address ]\e[0m \n"
      exit 2
    fi

    ip=$(echo $IP | grep "^-")
    if [ x$ip != x ]; then
      printf "\e[1;31m[INFO] HOST IP FORMAT ERROR: ${ip}\e[0m \n"
      exit 3
    fi

    printf "\e[1;33m[INFO] [USE HOST_IP] ${IP}\e[0m \n"
    cp /etc/hosts /etc/hosts.t
    sed -i 's/.*'$(hostname)'/'${IP}'    '`hostname`'\n/g' /etc/hosts.t
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
