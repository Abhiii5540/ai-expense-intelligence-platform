# Java And Spring Boot Migration Guide

This guide moves the running application from Express/Prisma and FastAPI to the parallel Java services without replacing the React frontend or changing its API contracts.

## Migration Layout

| Role | Current implementation | Java implementation |
| --- | --- | --- |
| Frontend | `frontend/` | Reused unchanged |
| Expense API | `backend/` | `backend-spring/` |
| AI insights | `ai-service/` | `ai-service-spring/` |
| Database | PostgreSQL through Prisma | Same PostgreSQL tables through JPA/Hibernate |

The Java services are additive. The legacy folders remain available until cutover is complete.

## What Is Preserved

- Frontend routes and behavior
- Public REST paths and HTTP methods
- Success and error response envelopes
- JWT bearer authentication
- BCrypt password hashes
- Existing `users` and `expenses` tables
- Internal `/ai/insights` request and response shape
- Gemini prompt rules for INR and 3-5 varied insights

## Prerequisites

- Java 21 available through `java -version`
- Maven 3.9+ available through `mvn -version`
- PostgreSQL access and a database backup
- Existing JWT secret
- Gemini API key
- Node.js and npm for the React frontend

Docker Desktop is optional. Use it for the simplest clean-room setup; use the manual path for an existing database.

## Step 1: Work On The Migration Branch

The migration branch created for this work is:

```text
codex/java-spring-migration
```

Confirm it before making more changes:

```powershell
git branch --show-current
git status
```

## Step 2: Back Up PostgreSQL

Before pointing the Java backend at existing data, create a database backup. Example:

```powershell
pg_dump -h localhost -p 5432 -U postgres -Fc ai_expense_db -f ai_expense_db_before_java.backup
```

Change the host, port, user, and database name to match your environment. Keep the backup outside the Git repository.

## Step 3: Configure The Java AI Service

Create its ignored local environment file:

```powershell
cd ai-service-spring
Copy-Item .env.example .env
```

Set these values in `ai-service-spring/.env`:

```env
PORT=8001
GEMINI_API_KEY=your_real_key
GEMINI_MODEL=gemini-2.5-flash
GEMINI_BASE_URL=https://generativelanguage.googleapis.com
GEMINI_CONNECT_TIMEOUT=5s
GEMINI_READ_TIMEOUT=30s
```

Port `8001` is recommended for parallel verification while the legacy FastAPI service still uses `8000`.

Start and verify it:

```powershell
mvn spring-boot:run
Invoke-RestMethod http://127.0.0.1:8001/health
```

Run the health request from another terminal. The expected status is `ok`.

## Step 4: Configure The Java Expense API

Create its ignored local environment file:

```powershell
cd backend-spring
Copy-Item .env.example .env
```

For parallel verification against an existing Prisma database:

```env
PORT=4001
DATABASE_URL=jdbc:postgresql://localhost:5432/ai_expense_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_database_password
JWT_SECRET=the_same_secret_used_by_the_node_backend
JWT_EXPIRES_IN=7d
CORS_ORIGIN=http://127.0.0.1:5173,http://localhost:5173
AI_SERVICE_URL=http://127.0.0.1:8001
AI_CONNECT_TIMEOUT=5s
AI_READ_TIMEOUT=30s
FLYWAY_ENABLED=false
```

Important details:

- Spring requires a JDBC URL beginning with `jdbc:postgresql://`.
- Use the exact PostgreSQL port that is listening on your computer.
- Use the same `JWT_SECRET` as Node so already-issued tokens remain valid.
- `FLYWAY_ENABLED=false` lets Hibernate validate the existing tables without adding Flyway history during the first check.
- Never paste real passwords or keys into `.env.example`, README files, commits, or chat messages.

Start and verify the API:

```powershell
mvn spring-boot:run
Invoke-RestMethod http://127.0.0.1:4001/health
```

## Step 5: Verify The Existing Frontend Against Java

Run the frontend with a temporary API override:

```powershell
cd frontend
$env:VITE_API_BASE_URL='http://127.0.0.1:4001'
npm run dev
```

Open `http://127.0.0.1:5173` and verify:

1. Sign up with a new test account.
2. Log out and log in again.
3. Create, edit, and delete an expense.
4. Confirm dashboard totals and charts update.
5. Search and filter expenses, then export CSV.
6. Generate AI insights twice and confirm the wording varies.
7. Confirm all displayed monetary values use `₹`.

Also log in with an account created by the Node backend. This verifies password-hash, email-normalization, table, and JWT compatibility.

## Step 6: Run Automated Validation

```powershell
cd backend-spring
mvn test
mvn package
```

```powershell
cd ai-service-spring
mvn test
mvn package
```

```powershell
cd frontend
npm run lint
npm run build
```

The PostgreSQL Testcontainers test runs automatically when Docker is available and skips itself otherwise.

## Step 7: Cut Over To The Standard Ports

After the parallel verification passes:

1. Stop the legacy Express service on port `4000`.
2. Stop the legacy FastAPI service on port `8000`.
3. Change `ai-service-spring/.env` to `PORT=8000`.
4. Change `backend-spring/.env` to `PORT=4000`.
5. Change `AI_SERVICE_URL` to `http://127.0.0.1:8000`.
6. Keep `VITE_API_BASE_URL=http://localhost:4000`.
7. Start the Java AI service, then the Java expense API, then the frontend.
8. Repeat the smoke test before considering the migration complete.

At this point the frontend needs no route or component changes; it reaches the Java API at the same address previously used by Express.

## Fresh Database Option

For a fresh database, leave `FLYWAY_ENABLED=true`. Flyway creates the same `users` and `expenses` structure used by the Prisma implementation, and Hibernate validates the result before the API accepts traffic.

For an existing Prisma database, you can keep Flyway disabled or enable it after deciding how migration history will be managed. Always test that decision on a restored database copy before production.

## Docker Compose Option

From the repository root:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

The Compose stack runs:

- React/Nginx on `5173`
- Spring expense API on `4000`
- Spring AI service on `8000`
- PostgreSQL in a named volume, exposed on host port `5433`

Docker Compose uses the Java services, not the legacy services.

## Rollback

Because the original folders and API contracts are retained, rollback is straightforward:

1. Stop `backend-spring` and `ai-service-spring`.
2. Restart `ai-service/` on port `8000`.
3. Restart `backend/` on port `4000`.
4. Keep the same PostgreSQL database and JWT secret.
5. Verify login, expenses, dashboard data, and AI insights.

Do not delete Java-created users or expenses during rollback; both implementations use the same data model.

## Commit And Push

Review the migration first:

```powershell
git status
git diff --stat
git diff --check
```

Then commit and publish the branch:

```powershell
git add .
git commit -m "feat: migrate expense platform services to Spring Boot"
git push -u origin codex/java-spring-migration
```

Open a pull request into `master`, review the checks and changed files, and merge only after the deployment environment variables are ready.

## Acceptance Checklist

- [ ] Existing account can log in through Spring
- [ ] New account can sign up and log in
- [ ] Existing JWT remains accepted during the configured lifetime
- [ ] Expense create, list, edit, and delete work
- [ ] Users cannot access another user's expenses
- [ ] Dashboard totals and charts match the database
- [ ] CSV export respects active frontend filters
- [ ] AI service returns 3-5 varied INR insights
- [ ] No real `.env` file is tracked
- [ ] Both Maven builds and the frontend build pass
- [ ] Rollback commands and database backup are available
