# canaiguess backend

canaiguess is a backend for an interactive game where players try to determine:  
**Was this image made by AI, or is it real?**

Players can try different game modes:
1. Single image: Guess if it’s real or AI
2. Pair: Which is which? (one real, one fake)
3. Multiple images: Which ones are AI?

Leaderboards for signed-in users keep things competitive and fun!
Users can also get hints for any inage with the help of Gemini API.

---

## **Frontend Project**

The frontend for canaiguess is built with **React** and **Vite** is in [canaiguess-frontend repository](https://github.com/JavaBootcampJuly2025/canaiguess-frontend)  

---

## **Backend Stack**

- **Framework:** Spring Boot
- **Build Tool:** Maven
- **Database:** PostgreSQL

---

Database logic model:

<img src="https://github.com/user-attachments/assets/87d49fab-e36c-45d3-8c1e-09dc41faf086" />

App flowchart schema:

<img src="https://github.com/user-attachments/assets/211fff08-5cf4-4fb0-9a70-552ebc0ca02b" />

## Dataset

We use [this](https://www.kaggle.com/datasets/tristanzhang32/ai-generated-images-vs-real-images?select=test) public dataset from Kaggle to provide real and AI-generated images. 
- Image Storage: Cloudflare R2
- Database Hosting: Supabase

## **Potential Features**

- Registered users can upload their own images or try to “find an AI equivalent” of a real image (by matching labels or using a free AI generation API in the background)
- Users can challenge each other to solve their custom levels
- Real-time competitive games with friends
- Show how “difficult” an image is (“only 9% of users guessed correctly!”). Tag images with difficulty level based on historical guess data.
- Let users see which images fooled them and others the most

---

## **“Can you _really_ spot the difference between real and AI anymore?”**

Data from the game could be valuable for researchers working on human-AI perception, dataset labeling, or even training new AI-detection models.

---

## **Game Security**

- CAPTCHA test at the beginning for unauthorized users

## Running the Backend with Docker

### Step 1: Authenticate with GitHub Container Registry
```
echo YOUR_GITHUB_PAT | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
```

### Step 2: Pull the Image
```
docker pull ghcr.io/javabootcampjuly2025/canaiguess-backend/canaiguess:<IMAGE_TAG>
```

*Replace <IMAGE_TAG> with the specific version.*
  
### Step 3: Create a .env File

Create a .env file in the same directory where you’ll run Docker. Contact any team member for required .env fields.
```
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
GOOGLE_API_KEY=
```

### Step 4: Run the Container
```
docker run --env-file .env -p 8080:8080 ghcr.io/javabootcampjuly2025/canaiguess-backend/canaiguess:<IMAGE_TAG>
```

<!-- START API DOCS -->

## API Endpoints

You can view and interact with the API using Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

<img width="653" height="552" alt="attels" src="https://github.com/user-attachments/assets/23653b43-c5ea-44a8-859e-4e543a5fecba" />

RegisterRequest: username, email, password

AuthenticationResponse: token

NewGameRequestDTO: batchCount, batchSize, difficulty

GameResultsDTO: correct, incorrect, accuracy, score

GuessRequestDTO: guesses: boolean[]

GuessResultDTO: correct: boolean[]

ImageBatchResponseDTO: images: string[]

LeaderboardDTO: username, score

GameInfoResponseDTO: batchCount, batchSize, currentBatch, difficulty

<!-- END API DOCS -->
