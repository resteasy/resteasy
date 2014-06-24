FROM ubuntu

# Install prerequisites
RUN apt-get update
RUN apt-get install -y software-properties-common python-software-properties

# Install java7
RUN add-apt-repository -y ppa:webupd8team/java
RUN apt-get update
RUN echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | sudo /usr/bin/debconf-set-selections
RUN apt-get install -y oracle-java7-installer git maven curl

ENV JAVA_HOME /usr/lib/jvm/java-7-oracle

RUN env

RUN curl -L --cookie 'oraclelicense=accept-securebackup-cookie;'  http://download.oracle.com/otn-pub/java/jce/7/UnlimitedJCEPolicyJDK7.zip -o /tmp/policy.zip

RUN sudo unzip -j -o /tmp/policy.zip *.jar -d /usr/lib/jvm/java-7-oracle/jre/lib/security && rm /tmp/policy.zip

RUN mkdir /src
VOLUME /src

WORKDIR /src

ENV MAVEN_OPTS -Xmx1024m -XX:MaxPermSize=512m

RUN env

ENTRYPOINT  ["mvn", "clean", "install"]