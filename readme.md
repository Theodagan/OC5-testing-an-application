# Test Project - Spring Boot, Angular, MySQL

## 📦 Tech Stack

- Backend: Java Spring Boot
- Frontend: Angular 14
- Database: MySQL
- Authentication: JWT
- Tests: JUnit, Cypress, Jest
- Cloud Dev Environment: Firebase Studio (custom template)

---

## ⚙️ Local Setup

### 1. Prerequisites

Ensure the following tools are installed:

- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Node.js (v16+)](https://nodejs.org/)
- [Angular CLI](https://angular.io/cli)
- [MySQL](https://dev.mysql.com/downloads/)

### 2. Clone the project

```bash
git clone https://github.com/Theodagan/OC5-testing-an-application.git
cd OC5-testing-an-application/
```

### 3. Initialize the database

- Create a MySQL database named `test` (or update the name in `back/src/main/resources/application.properties`)
- Run the initialization script:

```bash
mysql -u user -p test < ressources/sql/script.sql
```

### 4. Start the backend

```bash
cd back/
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080`.

### 5. Start the frontend

```bash
cd ../front/
npm install
ng serve
```

The frontend will be available at `http://localhost:4200`.

> **Note**: The `proxy.config.json` file handles request forwarding to the backend during local development.

---

## 🚀 Run the project with Firebase Studio (IDX)

### 🔗 Direct link to the project

If you have been granted access by the project owner, you can open the project directly in Firebase Studio using the link below:

👉 [Open in Firebase Studio](https://studio.firebase.google.com/test59-61507171)

> ⚠️ **Note**: You must be **explicitly granted access** to the Firebase Studio workspace by the project owner via a working gmail email address.  
> Without proper permissions, the link will **not work**.

### 🔗 Link to the template

This project can be launched in the cloud via Firebase Studio using a custom template, providing a fully configured dev environment.

Click the link below to start a new Firebase Studio workspace using the custom template:

➡️ https://studio.firebase.google.com/new?template=https%3A%2F%2Fgithub.com%2FTheodagan%2Fspring-angular-idx-template

### 📝 Detailed steps

1. **Sign in to Firebase Studio**  
   You must be signed in with a Google account. If not, you'll be prompted to log in when the link opens.

2. **Fill in the configuration form**  
   After opening the link, a form will appear with fields defined in the template’s `idx-template.json`. Provide:
   - Workspace name
   - Project Git repository URL
   - Default database credentials (⚠️ it's recommended to update these in the generated `dev.nix`):
     - Database name: yoga
     - Port: 3306
     - Username: user
     - Password: 123456

3. **Accept the Terms of Service**  
   Before creating the workspace, you'll be asked to accept Firebase Studio's Terms of Service.

4. **Workspace creation**  
   After completing the form and accepting the terms, click “Create”. Firebase Studio will use the `idx-template.nix` script to configure the environment.

5. **Service startup**  
   Services usually start automatically. If not, refer to the commands inside `.idx/dev.nix`.

   The frontend will be available at `https://[4200]-$WEB_HOST`, and the backend at `https://[8080]-$WEB_HOST`.

### ⚠️ Important notes

- **Google account required**  
- **Acceptance of Terms of Service is mandatory**  
- **Submitted information is used only to configure the environment and is not shared**  
  [Source](https://firebase.google.com/docs/studio/get-started-template)

---

## 🧪 Tests

### Backend

To run all tests:

```bash
cd back/
mvn test
```

Generate a coverage report:

```bash
cd back/
mvn test jacoco:report
```

### Frontend

Unit tests (Jest):

```bash
cd front/
npm run test
```

Generate a coverage report:

```bash
cd front/
npm test -- --reporters=default --reporters="jest-html-reporter" --reporterOptions="{\"pageTitle\": \"Test Report\", \"outputPath\": \"test-report.html\"}"
```

### 🌐 End-to-end Tests (Cypress)

#### Locally

```bash
npm run cypress:open   # Launch Cypress UI
npm run cypress:run    # Headless mode
```

#### CI/CD with Cypress Cloud

E2E tests are integrated into GitHub Actions and may be **connected to Cypress Cloud**:
- Each run can be tracked on Cypress Cloud dashboard (requires secret key)
- Includes logs, screenshots, and video recordings
- **Note**: Workflows using Cypress Cloud **cannot generate coverage reports**, and vice versa

##### Workflows using Cypress Cloud:
- **FullStack - E2E Tests (Angular + Spring + Cypress)**: spins up the entire stack (MySQL, backend, frontend)
- **E2E Tests Light (Angular)**: runs tests on Angular frontend only

##### Workflows generating coverage reports:
- **E2E coverage**: produces a coverage report (`lcov`, `text-summary`), incompatible with Cypress Cloud

#### CI/CD from terminal

Run tests:

```bash
ng run yoga:e2e-ci
```

Generate E2E coverage report:

```bash
npm run e2e:coverage
```

⚠️ Disclaimer: If you’re running these commands locally, make sure Cypress is installed first:

```bash
npm install cypress --save-dev
```


---

## 📂 Project structure

```text
project/
├── back/         → Spring Boot backend
├── front/        → Angular frontend
├── ressources/   → SQL files, Postman collection, test reports
```

---

## 📝 Notes

- Environment variables should be set in `application.properties` (backend) and `environment.ts` (frontend)
- Dev proxy config is in `front/src/proxy.config.json`
- Coverage and test reports are stored in `ressources/test-reports`

---