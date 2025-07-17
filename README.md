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

Database logical model:

<img width="744" height="386" alt="attels" src="https://github.com/user-attachments/assets/e0064cbd-64be-4a02-addd-730138dfea0b" />

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

### Authentication

- **POST** `/api/v1/auth/register`  
  Register a new user and receive a JWT.

- **POST** `/api/v1/auth/authenticate`  
  Authenticate and receive a JWT.

### Game

- **POST** `/api/game`  
  Create a new game for the authenticated user.

- **GET** `/api/game/{gameId}`  
  Get details of a specific game.

- **POST** `/api/game/{gameId}/batch`  
  Get the next image batch for a game.

- **POST** `/api/game/{gameId}/results`  
  Get the result (correct/incorrect) stats for a game.

### Guess

- **POST** `/api/guess`  
  Validate image guesses; returns which were correct.

<!-- END API DOCS -->
