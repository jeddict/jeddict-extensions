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
        - name: POSTGRES_USER
          value: ${DB_USER}
        - name: POSTGRES_PASSWORD
          value: ${DB_PASSWORD}
        - name: POSTGRES_DB
          value: ${DB_NAME}
        ports:
        - containerPort: ${DB_PORT}
        volumeMounts:
        - name: data
          mountPath: /var/lib/postgresql/
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
