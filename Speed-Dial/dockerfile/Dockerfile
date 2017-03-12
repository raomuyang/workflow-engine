# Image with WF-Engine/ Base zookeeper
# Author Rao-Mengnan

FROM raomengnan/oracle-jdk-1.8.92-ubuntu
MAINTAINER raomengnan

ADD Workflow-Engine-Service/ /usr/local/WF-Engine/Workflow-Engine-Service
ADD Workflow-Engine-Provider/ /usr/local/WF-Engine/Workflow-Engine-Provider
ADD service-jars/ /usr/local/WF-Engine/service-jars
ADD wfService.sh  /usr/local/WF-Engine/
COPY wfService.sh  /usr/local/bin/wfService
RUN chmod 755 /usr/local/bin/wfService

ENV WF_ENGINE_HOME /usr/local/WF-Engine
ENV PATH $PATH:/usr/local/WF-Engine

EXPOSE 80
EXPOSE 8081
EXPOSE 8082

CMD /bin/zsh
