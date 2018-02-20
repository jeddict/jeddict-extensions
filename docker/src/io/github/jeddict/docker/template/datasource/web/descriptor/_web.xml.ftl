    <data-source>
        <!--  environment-specific properties applied using maven-war-plugin Webapp resource filtering -->
        <name>${JNDI}</name>
        <class-name>${DRIVER_CLASS}</class-name>
        <server-name>${r"${db.svc}"}</server-name>
        <port-number>${r"${db.port"}}</port-number>
        <database-name>${r"${db.name"}}</database-name>
        <user>${r"${db.user}"}</user>
        <password>${r"${db.password}"}</password>
    </data-source>