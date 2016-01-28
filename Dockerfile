#to run: docker run --net="host" -P IMAGE
#yes: this needs to improvements
FROM mesosphere/mesos:0.23.0-1.0.ubuntu1404

RUN apt-get update && apt-get install -y git openjdk-7-jdk unzip wget


WORKDIR /root/
RUN git clone https://github.com/pnakibar/hdfs.git
WORKDIR /root/hdfs

RUN ./bin/build-hdfs

WORKDIR /root/hdfs/build/hdfs-mesos-0.1.5

EXPOSE 50070

ENV frameworkName testhdfs
ENV zookeeperhost 127.0.0.1
ENV mesoauthsuser master
ENV mesosauthpassword 1234

CMD ["./bin/hdfs-mesos"]
