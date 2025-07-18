# canaiguess backend

canaiguess is a backend for an interactive game where players try to determine:  
**Was this image made by AI, or is it real?**

Players can try different game modes:
1. Single image: Guess if it’s real or AI
2. Pair: Which is AI? (one real, one fake)
3. Multiple images: Which ones are AI?

Leaderboards for signed-in users keep things competitive and fun!

---

## **Frontend Project**

The frontend for canaiguess is built with **React** and **Vite**:  
👉 [canaiguess-frontend repository](https://github.com/JavaBootcampJuly2025/canaiguess-frontend)  
(Stack: React, Vite)

---

## **Backend Stack**

- **Framework:** Spring Boot
- **Build Tool:** Maven
- **Database:** PostgreSQL

---

Database logic model:

<img width="1434" height="956" alt="attels" src="https://github.com/user-attachments/assets/87d49fab-e36c-45d3-8c1e-09dc41faf086" />

App flowchart schema:

<img width="1180" height="465" alt="attels" src="https://github.com/user-attachments/assets/211fff08-5cf4-4fb0-9a70-552ebc0ca02b" />

## Dataset

We use public datasets to provide real and AI-generated images.

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
