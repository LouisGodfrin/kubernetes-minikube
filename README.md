# kubernetes-minikube

Minikube is a tool that lets you run Kubernetes locally.
minikube runs a single-node Kubernetes cluster on your personal computer (including Windows, macOS and Linux PCs) so that you can try out Kubernetes, or for daily development work.

## Architecture

This project implements a cloud native micro-services architecture with the following components:

- **Gateway**: NGINX Ingress controller routing external traffic
- **Service 1** (`myservice`): Spring Boot REST API on port 8080
- **Service 2** (`myservice2`): Spring Boot REST API on port 8081 with PostgreSQL persistence
- **Database**: PostgreSQL deployed as a Kubernetes service
```
Internet → Ingress (NGINX) → myservice (Service 1) → myservice2 (Service 2) → PostgreSQL
```

## Docker installation

### installation for Mac, Windows 10 Pro, Enterprise, or Education

https://www.docker.com/get-started

Choose Docker Desktop

### installation for Windows home

https://docs.docker.com/docker-for-windows/install-windows-home/

## Kubernetes Minikube installation

https://minikube.sigs.k8s.io/docs/start/

Minikube provides a dashboard (web portal). Access the dashboard using the following command:
```
minikube dashboard
```

## Project Structure
```
kubernetes-minikube/
├── MyService/                        # Service 1 - Spring Boot app
│   ├── src/
│   ├── Dockerfile
│   └── build.gradle
├── MyService2/                       # Service 2 - Spring Boot app with PostgreSQL
│   ├── src/
│   ├── Dockerfile
│   └── build.gradle
├── myservice-deployment.yml          # Service 1 deployment (2 replicas)
├── myservice-loadbalancing-service.yml
├── myservice-service.yml
├── myservice2-deployment.yml         # Service 2 deployment
├── postgres-deployment.yml           # PostgreSQL deployment + service
└── ingress.yml                       # NGINX Ingress routing rules
```

## Test Service 1 using Docker

Build the docker image:
```
docker build -t myservice .
```

Start the container:
```
docker run -p 4000:8080 -t myservice
```

Test in browser: http://localhost:4000 — displays "Hello from Service 1!"

## Build and Publish images to Docker Hub

### Service 1
```
cd MyService
docker build -t lgdf/myservice:3 .
docker push lgdf/myservice:3
```

### Service 2
```
cd MyService2
docker build -t lgdf/myservice2:2 .
docker push lgdf/myservice2:2
```

Docker Hub images:
- `lgdf/myservice:3`
- `lgdf/myservice2:2`

## Deploy Everything on Minikube

### 1. Start Minikube and enable Ingress
```
minikube start --memory=4096 --cpus=2
minikube addons enable ingress
```

### 2. Deploy in order
```
kubectl apply -f postgres-deployment.yml
kubectl apply -f myservice2-deployment.yml
kubectl apply -f myservice-deployment.yml
kubectl apply -f myservice-loadbalancing-service.yml
kubectl apply -f ingress.yml
```

### 3. Check all pods are running
```
kubectl get pods
kubectl get services
kubectl get ingress
```

All pods should show `1/1 Running`.

### 4. Access the application
```
minikube service myservice --url
```

This gives you a URL like `http://127.0.0.1:XXXXX`. Test in your browser:

- `http://127.0.0.1:XXXXX/` → **"Hello from Service 1!"**
- `http://127.0.0.1:XXXXX/data` → **JSON list of items from PostgreSQL via Service 2**

## Ingress routing

Enable the NGINX Ingress controller:
```
minikube addons enable ingress
```

Verify it is running:
```
kubectl get pods -n ingress-nginx
```

Apply ingress rules:
```
kubectl apply -f ingress.yml
```

Retrieve the Ingress IP address:
```
kubectl get ingress
```

Start the tunnel (keep this terminal open):
```
minikube tunnel
```

On Windows, optionally edit `c:\windows\system32\drivers\etc\hosts` and add:
```
192.168.49.2 myservice.info
```

Then test in your browser:
- http://myservice.info/
- http://myservice.info/data

## Scaling and load balancing

Service 1 is deployed with 2 replicas for high availability. To scale manually:
```
kubectl scale --replicas=3 deployment/myservice
kubectl get pods
```

## Rolling updates

Update to a new image version with zero downtime:
```
kubectl set image deployments/myservice myservice=lgdf/myservice:4
kubectl rollout status deployments/myservice
```

Roll back if needed:
```
kubectl rollout undo deployments/myservice
```

## Delete resources
```
kubectl delete services myservice myservice2 postgres-service
kubectl delete deployment myservice myservice2 postgres
```
