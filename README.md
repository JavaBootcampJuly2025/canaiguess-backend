![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.2-green)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue)
![Cloudflare R2](https://img.shields.io/badge/Object%20Storage-R2-orange)
![Docker](https://img.shields.io/badge/Docker-ready-blue)
![CI](https://img.shields.io/github/actions/workflow/status/JavaBootcampJuly2025/canaiguess-backend/maven.yml?label=build)
![Open Issues](https://img.shields.io/github/issues/JavaBootcampJuly2025/canaiguess-backend)
![Contributors](https://img.shields.io/github/contributors/JavaBootcampJuly2025/canaiguess-backend)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Last Commit](https://img.shields.io/github/last-commit/JavaBootcampJuly2025/canaiguess-backend)
![Render](https://img.shields.io/badge/Render-Live-blueviolet)
![Netlify](https://img.shields.io/badge/Deployed%20on-Netlify-success)

# CANAIGUESS Backend

CANAIGUESS is a Backend API for an interactive game where players try to determine:  
**Was this image made by AI, or is it real?**

Demo showcase:
https://youtu.be/r8REJZ0IzjU

## 📚 Table of Contents
- [Backend Stack](#backend-stack)
- [Frontend Project](#frontend-project)
- [Run with Docker Compose](#option-a-run-with-docker-compose)
- [Run with Docker Image](#option-b-run-with-docker-image)
- [API Endpoints](#api-endpoints)

Players can try different game modes depending both on the number of rounds and batch size for each round:
1. Single image: is it real or AI?
2. Pair of images: which is which? (one real, one fake)
3. Multiple images: how many are AI?

> “Can you _really_ spot the difference between real and AI anymore?”

- Algorithm selects unseen images depending on chosen difficulty
- Users can **get AI hints** for any image with the help of Gemini API
- Leaderboard and points for authorized users keep things competitive
- Global statistics on the most challenging images for both users and AI
- Admins can upload their own images and manage user profiles
- Unauthorized users can play the game after passing CAPTCHA

*Data from the game could be valuable for researchers working on human-AI perception, dataset labeling, or even training new AI-detection models.*

We use [this public dataset](https://www.kaggle.com/datasets/tristanzhang32/ai-generated-images-vs-real-images?select=test) from Kaggle to provide real and AI-generated images. 

----

## [**Backend Stack**](#backend-stack)

- **Framework:** Spring Boot
- **Build Tool:** Maven
  - Spring Data JPA
  - Docker Compose
  - Project Lombok
  - ((see [`pom.xml`](./pom.xml)))
- **Database:** PostgreSQL
- **Security:**
  - CAPTCHA for guest users
  - JWT-based authentication
- **Object Storage:** Cloudflare R2
- **External APIs / SDKs**:
  - Google Gemini 
  - AWS SDK (S3-compatible R2)

Deployed with Render from Github image registry

---

## [**Frontend Project**](#frontend-project)

The frontend for canaiguess is built with **React** and **Vite**

Deployed with Netlify from [canaiguess-frontend repository](https://github.com/JavaBootcampJuly2025/canaiguess-frontend)  

---

Database logical model:

<img src="https://github.com/user-attachments/assets/d536357b-14fa-4088-b8b6-f2dea31e91c3" />


Application flowchart schema:

<img src="https://github.com/user-attachments/assets/211fff08-5cf4-4fb0-9a70-552ebc0ca02b" />

---

## [Option A: Run with Docker Compose](#option-a-run-with-docker-compose)

```bash
docker compose up --build
```

*Ensure you have a valid `.env` file in the root directory:*

```
# imagination
JWT_SECRET=
ADMIN_DEFAULT_PASSWORD=

# get your own
JWT_SECRET=
GOOGLE_API_KEY=
CAPTCHA_SECRET_KEY=

# this is fair
DB_NAME=canaiguess
DB_USERNAME=canaiguess
DB_PASSWORD=canaiguess
DB_URL=jdbc:postgresql://db:5432/canaiguess

# get in touch
CLOUDFLARE_R2_ACCESS_KEY=
CLOUDFLARE_R2_SECRET_KEY=
CLOUDFLARE_R2_BUCKET_NAME=
CLOUDFLARE_R2_ENDPOINT=
```

---

## [Option B: Run with Docker Image](#option-b-run-with-docker-image)

### Step 1: Authenticate with GitHub Container Registry
```bash
echo YOUR_GITHUB_PAT | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
```

### Step 2: Create a .env File

*Create a `.env` file in the same directory where you’ll run Docker (refer to Option A)*

### Step 3: Run the Container
```bash
docker run --env-file .env -p 8080:8080 ghcr.io/javabootcampjuly2025/canaiguess-backend/canaiguess:latest
```

You can view and interact with the API using Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

Login as `admin` with the password specified in `.env` or create a new account.

---

## [API Endpoints](#api-endpoints)

[Swagger UI](https://canaiguess.onrender.com/swagger-ui/index.html)

---

<details>
<summary><strong>📦 API Data Transfer Objects]</strong></summary>

<br/>

### 🧾 Authentication

#### `RegisterRequest`
- `username` — `string`
- `email` — `string`
- `password` — `string`

#### `AuthenticationRequest`
- `username` — `string` 
- `password` — `string` 

#### `AuthenticationResponse`
- `token` — `string`
- `username` — `string`
- `role` — `string` 

---

### 🎮 Game

#### `NewGameRequestDTO`
- `batchCount` — `integer`
- `batchSize` — `integer`
- `difficulty` — `integer`

#### `NewGameResponseDTO`
- `gameId` — `string`

#### `GuessRequestDTO`
- `guesses` — `boolean[]`

#### `GuessResultDTO`
- `correct` — `boolean[]`

#### `ImageBatchResponseDTO`
- `images` — `ImageDTO[]`

#### `ImageDTO`
- `id` — `string`
- `url` — `string`

#### `GameDTO`
- `id` — `string`
- `correct` — `integer`
- `total` — `integer`
- `accuracy` — `double`
- `score` — `integer`
- `createdAt` — `string`
- `finished` — `boolean`
- `currentBatch` — `integer`
- `batchCount` — `integer`
- `batchSize` — `integer`
- `difficulty` — `integer`

---

### 👤 User

#### `UserDTO`
- `username` — `string`
- `score` — `integer`
- `accuracy` — `number (double)`
- `totalGuesses` — `integer`
- `correctGuesses` — `integer`
- `totalGames` — `integer`

#### `UpdateUserRequestDTO`
- `currentPassword` — `string`
- `newPassword` — `string` 
- `email` — `string` 

---

### 🖼️ Image

#### `UploadImageRequestDTO`
- `file` — `binary` 
- `fake` — `boolean` 

#### `HintResponseDTO`
- `fake` — `boolean`
- `signs` — `string[]`
---

### 🚨 Reporting

#### `SubmitReportRequestDTO`
- `description` — `string`

#### `ImageReportResponseDTO`
- `reportId` — `integer`
- `imageId` — `string`
- `imageUrl` — `string`
- `username` — `string`
- `description` — `string`
- `timestamp` — `string` 
- `resolved` — `boolean`

---

### 📊 Leaderboard

#### maps to `UserDTO`

</details>

---
💡 Built as part of the [Java Bootcamp July 2025](https://github.com/JavaBootcampJuly2025)
