FROM payara/micro:5.2020.4
WORKDIR $PAYARA_PATH

ARG BINARY

ADD $BINARY $PAYARA_PATH/deployments

CMD java -jar $PKG_FILE_NAME --deploymentDir deployments