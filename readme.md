# Flightservice Container Orchestration Project

## Project Introduction Video

A project introduction video is available [here](https://drive.google.com/file/d/179TIb7I5mBFTHQex0A2VKc_nr7_7QVXs/view?usp=share_link). It provides an overview of the project and a walkthrough of the deployment steps. 


## Project Overview

The Flightservice Container Orchestration Project demonstrates the deployment and management of a microservice-based application using multiple container orchestration technologies. The project includes:

- **Spring Boot Flightservice Application**: A Java-based microservice with enhanced features (version reporting, logging to file, auto-populating SQL database, and a Fibonacci calculation for load simulation).
- **MySQL Database**: Provides backend storage for the flight reservation system.
- **ELK Stack Integration**: Comprises Elasticsearch, Logstash, and Kibana to collect, parse, and visualize application logs.
- **Docker Compose Implementation**: A multi-container deployment solution for local development and testing.
- **Docker Swarm Implementation**: A distributed container orchestration solution for scaling, rolling updates, and rollbacks.
- **Kubernetes Deployment with Helm**: A production-grade deployment using Kubernetes (via Minikube) with a custom Helm chart for the Flightservice application and a Bitnami Helm chart for MySQL. Horizontal Pod Autoscaling (HPA) is also configured to dynamically adjust to load.

Additionally, a project introduction video (not included in this repository) provides an overview and walkthrough of the entire setup.

## Setup and Installation

### Prerequisites
Ensure you have the following installed on your system:
- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/)
- [Docker Swarm](https://docs.docker.com/engine/swarm/) (built into Docker)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/)
- [Helm](https://helm.sh/docs/intro/install/)
- [Maven](https://maven.apache.org/install.html) for building the Java application

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/<your-username>/flightservice-orchestration.git
   cd flightservice-orchestration
   ```

2. **Build the Java Application**
   - Navigate to the project root directory.
   - Use Maven to clean and package the application:
     ```bash
     mvn clean package
     ```
   - This command generates a JAR file in the `target` directory.

3. **Build and Push Docker Images**
   - Build the Docker image for version 1.0:
     ```bash
     docker build -t rushikesh0028/flightservice:v1.0 .
     ```
   - Build the Docker image for version 2.0 (for upgrades and rollbacks):
     ```bash
     docker build -t rushikesh0028/flightservice:v2.0 .
     ```
   - Push the images to Docker Hub:
     ```bash
     docker push rushikesh0028/flightservice:v1.0
     docker push rushikesh0028/flightservice:v2.0
     ```

4. **Configure Environment Variables**
   - Update configuration files (e.g., `application.properties`, `logstash.conf`, `kibana.yml`) as needed.
   - Ensure credentials and hostnames are correctly set:
     - MySQL credentials: `Rushikesh@1`
     - Database name: `reservation`
     - Docker network: `flightservice-network`

## Usage Instructions

### Docker Compose

1. **Create the Docker Network**
   ```bash
   docker network create flightservice-network
   ```

2. **Run the Docker Compose Deployment**
   - Use the provided `dockercompose.yaml` file:
     ```bash
     docker-compose -f dockercompose.yaml up -d
     ```

3. **Verify Deployment**
   - Check running containers:
     ```bash
     docker ps
     ```
   - Test the API endpoint:
     - Use Postman to send a `GET` request to `http://localhost:8080/version`
   - Verify that logs are generated in the mapped log directory and view them in Kibana at `http://localhost:5601`.

### Docker Swarm

1. **Initialize Docker Swarm**
   ```bash
   docker swarm init
   ```

2. **Create the Overlay Network**
   ```bash
   docker network create --driver overlay flightservice-network
   ```

3. **Deploy the Stack**
   ```bash
   docker stack deploy -c docker-compose.yml flightservice-swarm
   ```

4. **Scaling Services**
   - Scale the Flightservice application:
     ```bash
     docker service scale flightservice-swarm_springboot-app=5
     ```

5. **Perform Upgrades and Rollbacks**
   - **Upgrade to v2.0:**
     ```bash
     docker service update --image rushikesh0028/flightservice:v2.0 flightservice-swarm_springboot-app
     ```
   - **Rollback to v1.0:**
     ```bash
     docker service rollback flightservice-swarm_springboot-app
     ```

6. **Verify**
   - Use `docker service ls` and `docker service ps` to check service status.
   - Test API endpoints using Postman.

### Kubernetes Deployment

1. **Start Minikube**
   ```bash
   minikube start
   ```

2. **Deploy MySQL Using Helm**
   ```bash
   helm repo add bitnami https://charts.bitnami.com/bitnami
   helm repo update
   helm install mysql bitnami/mysql \
     --set auth.rootPassword=Rushikesh@1 \
     --set auth.database=reservation \
     --set auth.username=rushikesh \
     --set auth.password=Rushikesh@1
   ```

3. **Deploy Flightservice Using Custom Helm Chart**
   - Create a custom Helm chart for Flightservice (files included: `values.yaml`, `flightservice-deployment.yaml`, `flightservice-service.yaml`, `hpa.yaml`).
   - Install the chart:
     ```bash
     helm install flightservice ./flightservice
     ```

4. **Expose the Service**
   - Expose the Flightservice URL using Minikube:
     ```bash
     minikube service flightservice
     ```

5. **Test and Monitor**
   - Access the application at the exposed URL and test the `GET /version` endpoint.
   - Use the Minikube dashboard:
     ```bash
     minikube dashboard
     ```
   - Monitor horizontal pod autoscaling:
     ```bash
     kubectl get hpa
     ```

6. **Load Testing**
   - A Python script is provided to simulate load by sending concurrent API requests:
     ```python
     import requests
     import threading

     def hit_service():
         response = requests.get("http://<service-url>/version")
         print(response.json())

     threads = []
     for _ in range(100):
         thread = threading.Thread(target=hit_service)
         threads.append(thread)
         thread.start()

     for thread in threads:
         thread.join()
     ```

## Troubleshooting

### Common Issues and Solutions

1. **Docker Network Issues**
   - **Problem**: Containers cannot communicate.
   - **Solution**: Verify that the custom network is created and all containers are attached:
     ```bash
     docker network inspect flightservice-network
     ```

2. **Image Build Failures**
   - **Problem**: Docker image build fails.
   - **Solution**: Check the Dockerfile for syntax errors and ensure the JAR file is present in the `target` directory. Rebuild using:
     ```bash
     mvn clean package && docker build -t rushikesh0028/flightservice:v1.0 .
     ```

3. **Deployment Failures in Docker Swarm**
   - **Problem**: Services do not start correctly.
   - **Solution**: Verify Swarm initialization with `docker swarm init` and inspect service logs:
     ```bash
     docker service ps flightservice-swarm_springboot-app
     docker logs <container_name>
     ```

4. **Kubernetes Deployment Issues**
   - **Problem**: Pods fail to start or HPA does not scale.
   - **Solution**: Check pod status and logs:
     ```bash
     kubectl get pods
     kubectl logs <pod_name>
     ```
     - Ensure resource requests and limits are configured correctly in the Helm chart.
     - Use the Minikube dashboard to visualize cluster status.

5. **ELK Stack Integration Problems**
   - **Problem**: Logs are not appearing in Kibana.
   - **Solution**: Verify Logstash configuration (`logstash.conf`) and check connectivity to Elasticsearch:
     ```bash
     docker logs logstash
     curl -X GET "http://localhost:9200/_cat/indices?v"
     ```



## References

1. Kubernetes Documentation: [Horizontal Pod Autoscaling](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale/)  
2. Kubernetes Documentation: [What is Kubernetes?](https://kubernetes.io/docs/concepts/overview/what-is-kubernetes/)  
3. Kubernetes Documentation: [Working with Objects](https://kubernetes.io/docs/concepts/overview/working-with-objects/)  
4. Docker Documentation: [Docker Swarm Overview](https://docs.docker.com/engine/swarm/)
