# AI Expense Intelligence Platform

![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=node.js&logoColor=white)
![Express](https://img.shields.io/badge/Express-000000?style=for-the-badge&logo=express&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Prisma](https://img.shields.io/badge/Prisma-2D3748?style=for-the-badge&logo=prisma&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Gemini AI](https://img.shields.io/badge/Gemini_AI-4285F4?style=for-the-badge&logo=google&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Recharts](https://img.shields.io/badge/Recharts-8B5CF6?style=for-the-badge&logo=chartdotjs&logoColor=white)

A full-stack AI-powered expense management platform that helps users track expenses, analyze spending patterns, export reports, and generate personalized financial insights with Gemini.

The platform combines a React + Vite frontend, a secure Express API, PostgreSQL persistence through Prisma, and a dedicated FastAPI microservice for AI insight generation. It is designed as a portfolio-ready example of a modern, service-oriented full-stack application with authentication, analytics, reporting, and AI integration.

# Highlights

- Built a full-stack AI-powered expense management platform.
- Implemented JWT-based authentication and secure API access.
- Developed expense analytics dashboards using Recharts.
- Integrated Gemini AI through a dedicated FastAPI microservice.
- Added CSV export, advanced filtering, and AI-generated financial insights.
- Designed a responsive dark-themed user interface.

## Architecture

```text
React (Frontend)
  -> Express + Prisma (Backend)
  -> PostgreSQL (Database)

Express
  -> FastAPI AI Service
  -> Gemini AI
```

## Project Metrics

- 3-tier architecture
- 3 independent services
- JWT authentication
- AI-powered financial insights
- Interactive analytics dashboard
- CSV export support
- Advanced filtering system

# Features

- User signup and login with JWT authentication
- Expense create, read, update, and delete operations
- Dashboard analytics with spending summaries
- Monthly expense trend chart
- Category distribution chart
- Top spending categories chart
- AI-generated financial insights using Gemini
- CSV export for currently filtered expense data
- Advanced search, category filtering, date ranges, and quick filters
- Toast notifications for success and error states
- Responsive dark theme UI

# Screenshots

## Login

![Login](screenshots/login.png)

## Signup

![Signup](screenshots/signup.png)

## Dashboard

![Dashboard](screenshots/dashboard.png)

## Expense Analytics

![Expense Analytics](screenshots/expense_chart.png)

## Expense Management

![Expense Management](screenshots/expense_management.png)

## AI Insights

![AI Insights](screenshots/ai_insights.png)

## Architecture

![Architecture](screenshots/architecture.png)

# System Architecture

![System Architecture](screenshots/architecture.png)

```mermaid
flowchart LR
  User["User"] --> Frontend["React + Vite Frontend"]
  Frontend --> Backend["Node.js + Express Backend"]
  Backend --> Prisma["Prisma ORM"]
  Prisma --> Database["PostgreSQL Database"]
  Backend --> AIService["FastAPI AI Service"]
  AIService --> Gemini["Gemini API"]
```

The application is separated into three primary services. The React frontend handles authentication screens, dashboard analytics, expense management, CSV export, and the AI insights interface. The Express backend owns authentication, protected REST APIs, request validation, and coordination between the database and AI service. Prisma provides the database access layer for PostgreSQL. The FastAPI AI service receives normalized expense data from the backend and communicates with Gemini to generate concise financial insights.

Request flow:

1. The user interacts with the React frontend.
2. The frontend sends REST API requests to the Express backend using Axios.
3. The backend validates JWT tokens for protected routes.
4. Expense data is created, read, updated, and deleted through Prisma.
5. Prisma persists and queries data from PostgreSQL.
6. For AI insights, the backend forwards expense data to the FastAPI AI service.
7. The AI service prompts Gemini and returns structured insights to the backend.
8. The backend sends the response back to the frontend for display.

# Tech Stack

## Frontend

- React
- Vite
- React Router
- Axios
- Recharts
- React Icons
- React Hot Toast

## Backend

- Node.js
- Express.js
- Prisma ORM
- JWT Authentication
- Bcrypt
- Morgan
- CORS

## Database

- PostgreSQL

## AI Service

- FastAPI
- Gemini API
- Pydantic
- Uvicorn

# Folder Structure

```text
ai-expense-intelligence-platform/
  ai-service/
    app/
      prompts/
      routes/
      schemas/
      services/
      main.py
      config.py
    requirements.txt
    .env

  backend/
    prisma/
      schema.prisma
      migrations/
    src/
      config/
      controllers/
      middleware/
      routes/
      services/
      utils/
      app.js
      server.js
    package.json
    .env.example

  frontend/
    public/
    src/
      components/
      context/
      hooks/
      layouts/
      pages/
      routes/
      services/
      styles/
      utils/
      App.jsx
      main.jsx
    package.json
    vite.config.js

  screenshots/
    login.png
    signup.png
    dashboard.png
    expense_chart.png
    expense_management.png
    ai_insights.png
    architecture.png

  README.md
```

# Installation Steps

## Prerequisites

- Node.js
- npm
- Python 3.10 or newer
- PostgreSQL database
- Gemini API key

Clone the repository:

```bash
git clone <repository-url>
cd ai-expense-intelligence-platform
```

Install frontend dependencies:

```bash
cd frontend
npm install
```

Install backend dependencies:

```bash
cd ../backend
npm install
```

Install AI service dependencies:

```bash
cd ../ai-service
python -m venv venv
```

Activate the virtual environment:

```bash
# Windows
venv\Scripts\activate

# macOS/Linux
source venv/bin/activate
```

Install Python packages:

```bash
pip install -r requirements.txt
```

# Running Frontend

From the `frontend` directory:

```bash
npm run dev
```

Default frontend URL:

```text
http://127.0.0.1:5173
```

# Running Backend

Create a `.env` file inside the `backend` directory. Use `backend/.env.example` as the starting point.

Generate Prisma client:

```bash
cd backend
npx prisma generate
```

Run database migrations:

```bash
npx prisma migrate dev
```

Start the backend server:

```bash
npm run dev
```

Default backend URL:

```text
http://localhost:4000
```

Health check:

```text
http://localhost:4000/health
```

# Running AI Service

Create a `.env` file inside the `ai-service` directory with your Gemini credentials.

Start the FastAPI service:

```bash
cd ai-service
uvicorn app.main:app --host 127.0.0.1 --port 8000
```

If using the local virtual environment directly on Windows:

```bash
venv\Scripts\python.exe -m uvicorn app.main:app --host 127.0.0.1 --port 8000
```

Default AI service URL:

```text
http://127.0.0.1:8000
```

Health check:

```text
http://127.0.0.1:8000/health
```

API documentation:

```text
http://127.0.0.1:8000/docs
```

# Environment Variables

## Backend `.env`

```env
NODE_ENV=development
PORT=4000
DATABASE_URL="postgresql://USER:PASSWORD@HOST:5432/DB_NAME?schema=public"
JWT_SECRET="replace_with_a_long_secure_secret"
JWT_EXPIRES_IN="7d"
CORS_ORIGIN="http://127.0.0.1:5173"
AI_SERVICE_URL="http://127.0.0.1:8000"
```

Example local PostgreSQL connection:

```env
DATABASE_URL="postgresql://postgres:password@localhost:5432/ai_expense_db?schema=public"
```

## AI Service `.env`

```env
GEMINI_API_KEY="your_gemini_api_key"
GEMINI_MODEL="gemini-2.0-flash"
```

## Frontend Configuration

The current frontend service configuration uses the backend URL defined in:

```text
frontend/src/services/api.js
```

By default, it points to:

```text
http://localhost:4000
```

# Deployment Strategy

Recommended hosting setup:

- Frontend: Vercel
- Backend: Render
- AI Service: Render
- Database: Neon PostgreSQL

## Production Stack

Frontend: Vercel

Backend: Render

AI Service: Render

Database: Neon PostgreSQL

Suggested deployment steps:

1. Create a Neon PostgreSQL database and copy the production connection string.
2. Deploy the backend API to Render as a Node.js web service.
3. Add backend environment variables in Render, including `DATABASE_URL`, `JWT_SECRET`, `CORS_ORIGIN`, and `AI_SERVICE_URL`.
4. Run Prisma generation and migrations during backend deployment or from a controlled local release step.
5. Deploy the FastAPI AI service to Render as a Python web service.
6. Add `GEMINI_API_KEY` and `GEMINI_MODEL` to the AI service environment.
7. Deploy the React frontend to Vercel.
8. Configure the frontend API base URL for the deployed backend.
9. Update backend CORS settings to allow the Vercel frontend domain.
10. Verify authentication, expense CRUD, dashboard analytics, CSV export, and AI insights in production.

# Future Enhancements

- Docker Compose setup for local multi-service development
- Budget limits and overspending alerts
- Recurring expense tracking
- PDF report generation
- Multi-currency support
- Role-based user management
- Automated backend and frontend test coverage
- CI/CD pipeline with build and deployment checks

# GitHub Repository Recommendations

Repository name:

```text
AI Expense Intelligence Platform
```

Repository description:

```text
AI-powered expense management platform with React, Node.js, PostgreSQL, FastAPI, and Gemini AI insights.
```

Suggested GitHub topics:

```text
react
nodejs
express
postgresql
prisma
fastapi
gemini
artificial-intelligence
expense-tracker
personal-finance
recharts
full-stack
```

# Project Status

The application is fully functional end to end. Authentication, expense CRUD, dashboard analytics, CSV export, advanced filtering, toast notifications, and Gemini-powered AI insights are implemented and ready for portfolio presentation.
