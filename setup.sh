#!/usr/bin/env bash
IS_HOME=/home/farazath/IS/product-is/modules/distribution/target/wso2is-5.6.0-SNAPSHOT

mvn clean install

DESTINATION=$IS_HOME/repository/components/dropins
echo 'COPYING ARTIFACTS TO ' $DESTINATION
cp target/siddhi-decision-point-1.0.0.jar $DESTINATION