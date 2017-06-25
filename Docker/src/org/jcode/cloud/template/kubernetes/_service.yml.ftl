apiVersion: v1
kind: Service
metadata:
  name: ${APP_NAME}
spec:
  selector:
    app: ${APP_NAME}
  type: LoadBalancer
  ports:
  - port: 8080
