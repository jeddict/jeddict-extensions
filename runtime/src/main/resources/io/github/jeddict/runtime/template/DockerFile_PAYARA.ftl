FROM payara/server-full:5.2020.7

ARG BINARY
ARG EXPOSE_PORT

# Deploy the binary
ADD $BINARY $PAYARA_PATH/glassfish/domains/domain1/autodeploy

# Expose the ports we're interested in
EXPOSE $EXPOSE_PORT

CMD $PAYARA_PATH/bin/asadmin start-domain --verbose
