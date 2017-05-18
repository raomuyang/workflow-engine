### 使用Docker容器快速部署

> docker容器可以快速将服务部署到单机或多机器中，通过zookeeper注册服务构建集群，本文将阐述如何打通docker容器之间的网络。

  
使用dubbo的RPC框架，是基于Zookeeper进行注册服务发现的，dubbo向Zookeeper注册IP时，默认会经过如下几步：
1. 读取dubbo.host配置，若无，则进入下一步，自行读取IP
2. 获取主机名，读取/etc/hosts文件，在hosts文件中查找主机名对应的ip，若无，则进入第4步
3. 若从host中读取的ip是127开头的loopback地址，则进入第4步，否则以该ip向Zookeeper注册服务发现
4. 扫描遍历本地网卡，直到找到第一个非loopback的ip地址，用于注册服务发现

这里所带来的问题，造成注册的服务发现有问题： 
* VPS、EC2等服务器，在eth0网卡中记录的是内网IP
* docker容器是使用虚拟网卡的，与宿主机是独立的

解决办法
* 容器使用host网络，修改hosts文件
* 容器使用桥接网络，手动传入宿主机网络的IP地址

#### host网络
如下：
`docker run --net=host raomengnan/workflow-engine wfService -r service/provider`
使用docker的net参数，指定容易与宿主机使用相同的网络
* 缺点： 一台机器上只能部署一个服务，可能会影响不同服务的端口的使用
* 注意： 需要在宿主机的/etc/hosts文件中，修改 `xxx.xxx.xxx.xxx hostname`记录，将ip改为外网ip


#### 桥接网络
使用桥接网络，将容器中的服务以指定的ip注册到Zookeeper上
`docker run raomengnan/workflow-engine wfService -r service/provider -i xxx.xxx.xxx.xxx`
使用-i 或--ip 参数，将指定的ip地址传入，向Zookeeper注册
