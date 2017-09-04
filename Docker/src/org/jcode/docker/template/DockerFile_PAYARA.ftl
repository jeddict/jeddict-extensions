FROM payara/server-full:5-SNAPSHOT

ARG BINARY

# Deploy the binary
ADD $BINARY $PAYARA_PATH/glassfish/domains/domain1/autodeploy

# Expose the ports we're interested in
EXPOSE 8080

CMD $PAYARA_PATH/bin/asadmin start-domain --verbose
