apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  name: ${DB_SVC}
spec:
  replicas: 1
  template:
    metadata:
      labels:
        app: ${DB_SVC}
    spec:
      volumes:
      - name: data
        emptyDir: {}
      containers:
      - name: ${DB_TYPE}
        image: ${DB_TYPE}:${DB_VERSION}
        env:
        - name: MYSQL_USER
          value: ${DB_USER}
        - name: MYSQL_PASSWORD
          value: ${DB_PASSWORD}
        - name: MYSQL_DATABASE
          value: ${DB_NAME}
        ports:
        - containerPort: ${DB_PORT}
        volumeMounts:
        - name: data
          mountPath: /var/lib/mysql/
---
apiVersion: v1
kind: Service
metadata:
  name: ${DB_SVC}
spec:
  selector:
    app: ${DB_SVC}
  ports:
  - port: ${DB_PORT}
