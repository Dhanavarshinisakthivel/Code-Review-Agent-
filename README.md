# AI Code Review Agent

A simple full-stack web app with a login screen where users paste code and get
an AI-powered review: a score, a summary, strengths, and a list of issues with
suggestions.

## Tech Stack
- Backend: Java 17 + Spring Boot 3 (plain MVC: Controller → Service → Model)
- Frontend: plain HTML + CSS + JavaScript (no framework, no build step)
- AI: Google Gemini API
- Auth: simple session-based login (in-memory demo users, no database)

## Project Structure
```
src/main/java/com/example/codereviewagent/
  CodeReviewAgentApplication.java   -> main() method, starts the app
  controller/
    CodeReviewController.java       -> handles POST /api/review
    AuthController.java             -> handles /api/login, /api/logout, /api/session
  service/
    CodeReviewService.java          -> validation + orchestration
    AiClientService.java            -> talks to the Gemini API, builds the prompt
    AuthService.java                -> checks username/password (in-memory demo users)
  model/
    ReviewRequest.java              -> what the frontend sends
    ReviewResponse.java             -> what the backend sends back
    Issue.java                      -> one problem found in the code
    LoginRequest.java               -> username/password sent from the login page
  config/
    AuthFilter.java                 -> blocks home.html/review.html/api if not logged in

src/main/resources/
  application.properties            -> config (port, API key, model name)
  static/
    index.html                      -> redirects straight to login.html
    login.html / login.js           -> login page
    home.html / home.js             -> "about this tool" page + button into the reviewer
    review.html / script.js         -> the actual code review tool
    style.css                       -> styling for all pages
```

## Page Flow
1. `index.html` → redirects to `login.html`
2. `login.html` → user logs in (demo credentials below) → redirected to `home.html`
3. `home.html` → explains what the tool does and how it works → "Start Reviewing Code" button → `review.html`
4. `review.html` → the actual paste-code-and-get-a-review tool

`AuthFilter` runs on every request and blocks direct access to `home.html`,
`review.html`, and the `/api/review` endpoint if you're not logged in - it
bounces you back to the login page (or returns a 401 for API calls).

### Demo login credentials
```
username: student
password: password123
```
(or `admin` / `admin123`) - both are hardcoded in `AuthService.java` for now,
since there's no database. See the comment in that file for how you'd do this
properly (hashed passwords in a database) in a real app.

## How It Works (High Level)
1. User logs in on `login.html` → `AuthController` checks credentials via
   `AuthService` and stores the username in an `HttpSession` if correct.
2. `home.html` loads, checks `/api/session` to confirm login and show the
   username, then the user clicks through to `review.html`.
3. User pastes code and clicks "Review Code" (`script.js`).
4. The browser sends a `POST /api/review` request with `{ code, language }`.
5. `CodeReviewController` receives it and calls `CodeReviewService`.
6. `CodeReviewService` does basic checks (not empty, not too long).
7. `AiClientService` builds a prompt instructing Gemini to review the code and
   reply ONLY in JSON, then calls the Gemini API over HTTPS.
8. The JSON reply from Gemini is parsed into a `ReviewResponse` object.
9. That object is sent back to the browser as JSON, and `script.js` renders it
   as a score, summary, strengths, and issue cards.

## Setup

### 1. Get a Gemini API key
Go to https://aistudio.google.com/apikey and create a free API key with your Google account.

### 2. Set it as an environment variable
Don't paste your key directly into any file (especially if you push this to GitHub).

**Windows (PowerShell):**
```
setx GEMINI_API_KEY "your-key-here"
```
(then open a new terminal so it takes effect)

**Mac/Linux:**
```
export GEMINI_API_KEY="your-key-here"
```

### 3. Run the app
```
mvn spring-boot:run
```

### 4. Open the app
Go to http://localhost:8080 in your browser - it'll take you straight to the login page.

## Possible Extensions (good for a final year project write-up)
- Move users into a real database (MySQL/PostgreSQL) with hashed passwords (BCrypt)
- Add a signup page so new users can register themselves
- Save each user's review history so they can view past reviews
- Support uploading whole files, not just pasted snippets
- Add a "compare before/after" view once the user fixes the issues
- Support multiple AI providers and let the user pick one
- Add unit tests for `CodeReviewService` and `AuthService`

## Notes
- `MAX_CODE_LENGTH` in `CodeReviewService` limits input to 20,000 characters -
  adjust if needed.
- If the AI occasionally doesn't return perfectly valid JSON, you'll see a
  "Failed to parse AI response" error. In a production version you'd add
  retry logic; for a student project it's fine to just click Review again.
- The login here uses a plain `HttpSession` (cookie-based) - fine for a demo,
  but a real app would add CSRF protection, password hashing, and rate limiting
  on login attempts.
