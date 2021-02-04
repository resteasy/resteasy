set -x

pushd ../resteasy-spring
SPRING_VER=$(mvn dependency:tree | grep spring | grep 'org.springframework:spring-core' | awk -F':' '{print $4}' | head -n 1)
popd

sed -ie "s:_SPRING_VER_:${SPRING_VER}:" "${BASEDIR}"/reference/en/en-US/modules/RESTEasy_Spring_Integration.xml
