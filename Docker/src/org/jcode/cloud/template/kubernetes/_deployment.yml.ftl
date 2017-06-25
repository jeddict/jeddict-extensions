apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: ${APP_NAME}
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: ${APP_NAME}
    spec:
      containers:
      - name: ${APP_NAME}-app
        image: ${DOCKER_IMAGE}
        imagePullPolicy: IfNotPresent
        env:
        - name: JAVA_OPTS
          value: " -Xmx256m -Xms256m"
        resources:
          requests:
            memory: "256Mi"
            cpu: "500m"
          limits:
            memory: "512Mi"
            cpu: "1"
        ports:
        - name: web
          containerPort: 8080
        readinessProbe:
          httpGet:
            path: /management/health
            port: web
        livenessProbe:
          httpGet:
            path: /management/health
            port: web
          initialDelaySeconds: 180
