#!/usr/bin/env bash
WF_SERVICE_FLOODER="Workflow-Engine-Service"
WF_PROVIDER_FLO0DER="Workflow-Engine-Provider"

COMMAND=$1
if [ x${COMMAND} = x ]; then
    printf "\e[0;31;1mUse [-b|--build, -u|--update] \e[0m \n"
    exit 1
fi

if [ x${COMMAND} = x'-b' ] || [ x${COMMAND} = x'--build' ] ; then
    printf "\e[0;33;1m[INFO] Start Build \e[0m \n"
    cd ../
    printf "\e[0;33;1m[STEP-1] Maven package \e[0m \n"
    printf"\e[0;33;1m" ---------------------------------------
    mvn clean
    mvn package

    if [ $? != 0 ]; then
        printf "\e[0;31;1m[ERROR] Package failed \e[0m \n"
        exit 2
    fi
    printf "\e[0;32;1m[SUCCESS] Package success  \e[0m \n\n"

    printf "\e[0;33;1m[STEP-2] Move file \e[0m \n"
    if [ ! -d Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER} ]; then
        mkdir Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}
    fi
    if [ ! -d Speed-Dial/dockerfile/${WF_SERVICE_FLOODER} ]; then
        mkdir Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}
    fi

    rm -rf Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}/*
    rm -rf Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}/*

    tar -zxvf WF-Service/target/Workflow-Engine-*.jar -C Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}/
    tar -zxvf WF-Provider/target/Workflow-Engine-*.jar -C Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}/

    if [ $? != 0 ]; then
        printf "\e[0;31;1m[ERROR] Package failed \e[0m \n"
        exit 3
    fi
    printf "\e[0;32;1m[SUCCESS] Move success \e[0m \n\n"
fi

if [ x${COMMAND} = x'-u' ] || [ x${COMMAND} = x'--update' ] ; then
    printf "\e[1;33m[INFO] Start Update, ignoe mvn package \e[0m \n"
    cd ../
    printf "\e[0;33;1m[STEP-1] Move WF-Engine-UPDATE \e[0m \n"
    printf "\e[0;33;1m--------------------------------------- \e[0m \n"
    mkdir -p Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}

    if [ ! -d Speed-Dial/dockerfile/${WF_SERVICE_FLOODER} ]; then
        mkdir Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}
    fi
    mkdir -p tmp
    tar -zxvf WF-Service/target/Workflow-Engine-*.jar.original -C tmp/
    cp -r tmp/org Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}/
    rm -r tmp/*

    tar -zxvf WF-Provider/target/Workflow-Engine-*.jar.original -C tmp/
    cp -r tmp/org Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}/
    if [ $? != 0 ]; then
        printf "\e[0;31;1m[ERROR] Package failed \e[0m \n"
        exit 4
    fi
    rm -r tmp
    printf "\e[0;32;1m[SUCCESS] Move WF-Engine-UPDATE successed \e[0m \n\n"

    printf "\e[0;33;1m[STEP-2] UPDATE org.radrso.*  \e[0m \n"
    printf "\e[0;33;1m--------------------------------------- \e[0m \n"

    cp WF-Plugins/target/org.radrso.plugins-*.jar  Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}/lib/
    result=${result}$?
    cp WF-Plugins/target/org.radrso.plugins-*.jar  Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}/lib/
    result=${result}$?
    cp WF-Public/target/org.radrso.workflow.core-*.jar  Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}/lib/
    result=${result}$?
    cp WF-Public/target/org.radrso.workflow.core-*.jar  Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}/lib/
    result=${result}$?

    if [ ${result} != "0000" ]; then
        printf "\e[0;31;1m[ERROR] UPDATE failed \e[0m \n"
        exit 5
    fi
    printf "\e[0;32;1m[SUCCESS] UPDATE success \e[0m \n\n"

fi

printf "\e[0;33;1m[STEP-3] Copy config files \e[0m \n"
printf "\e[0;33;1m--------------------------------------- \e[0m \n"
cp WF-Service/target/classes/*.* Speed-Dial/dockerfile/${WF_SERVICE_FLOODER}/
cp WF-Provider/target/classes/*.* Speed-Dial/dockerfile/${WF_PROVIDER_FLO0DER}/
rm -r Speed-Dial/dockerfile/service-jars/*
cp -r service-jars Speed-Dial/dockerfile/
if [ $? != 0 ]; then
    printf "\e[0;31;1m[ERROR] Move WF-Engine-Plugins-UPDATE failed \e[0m \n"
    exit 5
fi
printf "\e[0;32;1m[SUCCESS] Move WF-Engine-Plugins-UPDATE successed \e[0m \n\n"


printf "\e[0;33;1m[STEP-4] Make image \e[0m \n"
printf "\e[0;33;1m--------------------------------------- \e[0m \n"
cd Speed-Dial/dockerfile
docker build -t raomengnan/workflow-engine ./
if [ $? != 0 ]; then
        printf "\e[0;31;1m[ERROR] Make image failed \e[0m \n"
        exit 6
fi
printf "\e[0;32;1m[SUCCESS] Make image success \e[0m \n"

