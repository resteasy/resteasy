#!/bin/bash
set -ex
git clone --depth=1 --branch=ee9-experimental https://github.com/jimma/jboss-jakarta-jaxrs-api_spec.git $TRAVIS_HOME/jboss-jakarta-jaxrs-api_spec
pushd $TRAVIS_HOME/jboss-jakarta-jaxrs-api_spec/jaxrs-api
mvn --quiet clean install -DskipTest=true
popd

git clone --depth=1 --branch=fix-smallrye-duplication-tag https://github.com/rsearls/optimus.git $TRAVIS_HOME/optimus
pushd $TRAVIS_HOME/optimus
mvn --quiet clean install -DskipTest=true
popd

git clone --depth=1 --branch=master https://github.com/wildfly/wildfly.git $TRAVIS_HOME/wildfly
pushd $TRAVIS_HOME/wildfly
mvn --quiet clean install -DskipTest=true -Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2
popd