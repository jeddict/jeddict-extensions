FROM payara/micro:6.2023.11
WORKDIR $PAYARA_PATH

ARG BINARY

ADD $BINARY $PAYARA_PATH/deployments

CMD java -jar $PKG_FILE_NAME --deploymentDir deployments