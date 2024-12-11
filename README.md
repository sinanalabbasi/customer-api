# Customer API Application

This document provides step-by-step instructions to build, deploy, and test the Customer API application. The application is a RESTful API for managing customer records, integrated with Docker, Kubernetes for deployment and a CI/CD pipeline.

## Prerequisites

Ensure the following tools are installed on your system:

- Java 17
- Maven
- Docker
- Minikube
- Kubectl
- Postman (optional, for API testing)

Ensure to Navigate to the project root directory after you do the Git repository clone step to preform the other steps listed below.


## Project Setup

### Step 1: Clone the Repository

Clone the repository to your local machine:

```cmd
cd your-workspace
```
```cmd
git clone <repository-url>
```

Replace `<repository-url>` with the actual GitHub repository URL.

### Step 2: Build the Application

Navigate to the project root directory and build the application using Maven:

```cmd
cd customer-api
```
```cmd
mvn clean package
```

This command compiles the code, runs the tests, and generates a JAR file in the `target/` directory (e.g., `target/customer-api-1.0.0.jar`).

Note: running the tests include unit tests and integration tests (the real tests that hit all application layers including DataBase) and it is located in the package com.example.customerapi.integration.

## Running Locally

To run the application locally:

1. Start the application:
   ```cmd
   java -jar target/customer-api-1.0.0.jar
   ```

2. Test the application by accessing the health endpoint:
   ```cmd
   curl http://localhost:8080/actuator/health

   from the browser http://localhost:8080/actuator/health
   ```

Expected output:
```json
{"status":"UP"}
```

3. Use Postman or any REST client to test CRUD operations.

### Example Payloads for Postman

#### Create Customer (POST `/api/customers`)
```json
{
  "firstName": "John",
  "middleName": "Michael",
  "lastName": "Doe",
  "emailAddress": "john.doe@example.com",
  "phoneNumber": "+1234567890"
}
```

#### Other Endpoints
- GET `/api/customers/{id}`: Fetch a customer by ID.
- DELETE `/api/customers/{id}`: Delete a customer by ID.
- PUT `/api/customers/{id}`: Update a customer.

## Building the Docker Image

Ensure to Navigate to the project root directory where the Dockerfile located.

### Step 1: Build the Docker Image

Build the Docker image for the application:

```cmd
docker build -t myrepo/customer-api:latest .
```

### Step 2: Verify the Docker Image

Ensure the image is built successfully:

```cmd
docker images
```
The command above will list the created image.

### Step 3: Run the Docker Container Locally

Run the Docker container:

```cmd
docker run -d -p 8080:8080 myrepo/customer-api:latest
```

Test the application as described in the (Running Locally) section you can also use Postman payload provided previously.

## Deploying with Minikube

### Step 1: Start Minikube

Start Minikube using Docker as the driver:

```cmd
minikube start --driver=docker
```

### Step 2: Load the Docker Image into Minikube

Load the Docker image into Minikube:

```cmd
minikube image load myrepo/customer-api:latest
```

### Step 3: Apply Kubernetes Manifests

Deploy the application to Kubernetes:

```cmd
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### Step 4: Verify the Deployment

Check the status of the pods:

```cmd
kubectl get pods
```

Check the service details:

```cmd
kubectl get service
```

### Step 5: Test the Application

Access the application using Minikube's built-in service command:

```cmd
minikube service customer-api-service
```

This opens the application in your default browser or provides a URL to access it,
Make sure to append for example (/actuator/health) to the end of the provided browser URL in order to hit the application also you can test it using Postman payload provided previously, 
but you also need to append for example (/api/customers) to the end of the opened default browser or provided browser URL.

Expected output:
```json
{"status":"UP"}
```

## CI/CD Pipeline with GitHub Actions

### Overview

The GitHub Actions CI/CD pipeline performs the following tasks:

1. Builds and tests the application using Maven.
2. Builds a Docker image for the application.
3. Deploys the application to Kubernetes using Minikube.

### Pipeline overview

`.github/workflows/ci.yml` file is configured with multiple steps and explanation is provided as a comments for each step in the pipeline and documented in the file.

Make sure to read the comments in the ci.yml file to understand the details.

### Testing the Pipeline

Push changes to the repository or create a pull request to trigger the pipeline. Monitor the progress in the Actions tab on GitHub.


## Application Observability

### Overview

The observability has been implemented in the application using:

1. Micrometer Prometheus for collecting application metrics for example to track the number of customer creation requests which is implemented in the application service layer.
2. Micrometer Tracing for generating trace IDs and span IDs to correlate logs and requests across services.
3. Logging includes detailed logging for observability.


## Conclusion

This guide outlines the steps to build, deploy, and test the Customer API application locally and with Kubernetes. Use the provided configurations and commands to ensure a seamless development and deployment experience.
