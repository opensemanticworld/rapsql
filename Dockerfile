# 1 base image of database 
FROM postgres:12.14

# 2 install postgres server, maven, git, and stack dependencies
RUN apt-get update && apt-get install --assume-yes --no-install-recommends --no-install-suggests \
    bison flex build-essential maven git gcc g++ openssl libssl-dev \ 
    postgresql-server-dev-12 libecpg-dev libkrb5-dev
    
# 3 install maintained openjdk (!v11 dependency)
ENV JAVA_HOME=/opt/java/openjdk

COPY --from=eclipse-temurin:11 $JAVA_HOME $JAVA_HOME

ENV PATH="${JAVA_HOME}/bin:${PATH}"

# 4 install extension pljava (git)
RUN cd /tmp && \
    git clone -b REL1_5_STABLE https://github.com/tada/pljava.git && \
    cd pljava && mvn clean install -B && \
    java -jar pljava-packaging/target/pljava-pg12.14-amd64-Linux-gpp.jar 

# 5 install extension apache age (git)
RUN git clone -b PG12 https://github.com/apache/age.git age && \
    cd /age && make install 

# 6 provide transpiler source code
COPY dev/java/sparql-to-agecypher /tmp/s2agec

# 7 transpiler integration test and packaging (!testdb dependency)
RUN cd /tmp/s2agec && mvn clean package 

# 8 provide init scripts for enhanced db setup on entrypoint
COPY docker-entrypoint/* /docker-entrypoint-initdb.d/

# db config for age library access
CMD ["postgres", "-c", "shared_preload_libraries=age"]