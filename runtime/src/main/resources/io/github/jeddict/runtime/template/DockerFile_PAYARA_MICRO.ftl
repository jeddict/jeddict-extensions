FROM payara/micro:5.2021.7
WORKDIR $PAYARA_PATH

ARG BINARY

ADD $BINARY $PAYARA_PATH/deployments

CMD java -jar $PKG_FILE_NAME --deploymentDir deployments