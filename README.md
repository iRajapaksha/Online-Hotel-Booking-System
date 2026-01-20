# 🏨 Online Hotel Booking System (Microservices on AWS)

A cloud-native **online hotel booking system** built using **Spring Boot microservices**, **AWS-managed services**, **Docker**, **Kubernetes**, and **CI/CD pipelines**. This project demonstrates real-world enterprise architecture with authentication, authorization, scalability, and automated deployments.

---

## 📌 Project Overview

This system is designed as a **microservices-based architecture** to handle hotel bookings securely and efficiently. Each service is independently deployable and scalable, following cloud and DevOps best practices.

### Key Goals
- Secure authentication & authorization using **AWS Cognito**
- Scalable microservices using **Spring Boot**
- Containerized deployments using **Docker**
- CI/CD using **AWS CodeBuild & CodePipeline**
- Orchestration using **Amazon EC2 Self managed k8s**
- Event-driven communication where applicable

---

### Services

| Service | Responsibility |
|-------|----------------|
| **Auth Service** | Authentication, JWT validation, role-based access |
| **User Service** | User profile management |
| **Hotel Service** | Hotel & room management |
| **Booking Service** | Booking creation, availability checks |

---

## 🔐 Authentication & Authorization

### AWS Cognito
- User Pools for authentication
- OAuth 2.0 / OpenID Connect
- JWT-based authentication
- Role-based access control (RBAC)

**Flow:**
1. User signs up / logs in via Cognito
2. Cognito returns JWT token
3. Token is validated by each microservice
4. Access is granted based on roles/claims

---

## ☁️ AWS Services Used

| Category | Service                |
|-------|------------------------|
| Authentication | AWS Cognito            |
| Containers | Amazon ECR             |
| Orchestration | Kubernetes             |
| CI/CD | CodePipeline, CodeBuild |
| Database | Amazon RDS (PostgreSQL), DynamoDB |
| Secrets | AWS Secrets Manager    |
| IAM | IAM Roles & Policies   |
 | Messaging | Amazon SNS/SQS         |

---

## 🐳 Docker & Containerization

Each microservice is containerized using google jib plugin.

Docker images are pushed to **Amazon ECR**.

---

## ⚙️ CI/CD Pipeline (AWS)

### CI/CD Flow
```
GitHub
  ↓
CodePipeline
  ↓
CodeBuild
  ↓
Amazon ECR
  ↓
Amazon EC2
```

### Steps
1. Code pushed to GitHub
2. CodePipeline triggers
3. CodeBuild:
    - Builds Docker image
    - Pushes image to ECR
4. Kubernetes deployment updated

---

## 🧾 buildspec.yml (Example)

```yaml
version: 0.2

env:
  variables:
    AWS_REGION: ap-south-1
    ECR_REPO: auth-service

phases:
  pre_build:
    commands:
      - aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com
  build:
    commands:
      - mvn clean package -DskipTests
      - docker build -t $ECR_REPO .
      - docker tag $ECR_REPO:latest $ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:latest
  post_build:
    commands:
      - docker push $ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPO:latest
```

---

## ☸️ Kubernetes 

### Deployment Example
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: auth-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: auth-service
  template:
    metadata:
      labels:
        app: auth-service
    spec:
      containers:
        - name: auth-service
          image: <ECR_IMAGE_URI>
          ports:
            - containerPort: 8080
```


## 🔑 Secrets & Configuration

- Environment variables managed via:
    - AWS Secrets Manager
- No secrets stored in GitHub

---

## 🚀 How to Run Locally

```bash
# Build
mvn clean package

# Docker
docker build -t auth-service .
docker run -p 8080:8080 auth-service
```

---

## 📁 Repository Structure

```
stayescape/
 ├── auth-service/
 ├── user-service/
 ├── hotel-service/
 ├── booking-service/
 ├── k8s/
 ├── buildspec.yml
 └── README.md
```

---

## 🧠 Future Improvements

- API Gateway integration
- Circuit breaker (Resilience4j)
- Event-driven booking confirmation (SNS/SQS)
- Payment gateway integration
- Canary deployments

---

## 👨‍💻 Author

**Ishara Rajapaksha**  
Computer Engineering Graduate  
Full Stack | Cloud | DevOps | AI Enthusiast

---