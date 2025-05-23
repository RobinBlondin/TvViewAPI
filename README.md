# TV View API

A Kotlin Spring Boot backend for displaying calendar events, uploading files, tracking public transportation departures, and more.
This project serves as the backend for the companion frontend, TvViewWeb: https://github.com/RobinBlondin/TvViewWeb.

It functions as an OAuth2 Resource Server, handling authenticated requests from the frontend and integrating various third-party services into a unified API.

![image](https://github.com/user-attachments/assets/be826092-1baa-4f3d-81d7-21fe0f9a9434)


## Technologies Used

- **Kotlin** + Spring Boot
- Java 21
- PostgreSQL
- OAuth2 with Google
- JWT Authentication
- REST API
- External APIs: Google Calendar, Trafiklab

## Getting Started

### Prerequisites

- Java 21+
- Docker (for PostgreSQL, if desired)
- PostgreSQL running on port 5432 (or configure otherwise)
- `.env` file in your project root (see below)

### Environment Configuration

Create a `.env` file in your project root:

#### `.env.example`

```env
DB_URL=jdbc:postgresql://localhost:5432/tvviewdb
DB_USER=your_db_username
DB_PASSWORD=your_db_password

GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
GOOGLE_CLIENT_SCOPE=openid,profile,email,https://www.googleapis.com/auth/calendar.readonly

CREDENTIALS=/path/to/credentials.json
CREDENTIALS_JSON=contents_of_credentials.json_file_as_a_string

CALENDAR_ID=your_calendar_id (usually your_email@gmail.com)

COMMUTE_API_KEY=your_resrobot_api_key
COMMUTE_STOP_ID=your_commute_stop_id (e.g. 740000605)

FILE_UPLOAD_DIR=http://localhost:8081/uploads

JWT_SECRET=your_jwt_secret (must be at least 64 characters for HS512)
```

### Run the App

You can start the app using:

```bash
./gradlew bootRun
```

Or package and run:

```bash
./gradlew build
java -jar build/libs/tvview-api.jar
```

## API Overview

The API includes endpoints for:

- Google OAuth2 login
- Calendar event fetching (Google Calendar)
- Public transport data from Trafiklab(ResRobot API)
- Highway bridge data from Dataportal(Södertälje API)
- File upload and retrieval
- Reminders with checkbox functionality

### Authentication

- The **frontend initiates the Google OAuth2 login** and retrieves an authorization code.
- The **backend handles the code-to-token exchange** via the `/auth/google` endpoint.
- Upon successful exchange, the backend:
  - Validates the ID token using Google's public keys
  - Issues a **custom JWT token** (`tv_token`) for access to protected API endpoints
- API endpoints that require authentication expect the custom JWT in the `Authorization` header:
```
Authorization: Bearer <token>
```
  Note: The backend accepts both tv_token (custom JWT) and Google ID tokens, but the custom JWT has a longer expiration date for longer exposure on a tv screen.


- The backend supports both Google ID tokens and the custom JWT for flexibility, but protected endpoints should use the custom JWT.

Note: The user must be registered in the database for a login and token exchange to be successful from the frontend. Use the following query to add a user to the database in your database tool:
```
INSERT INTO public."_user"
(id, created, created_by, updated, updated_by, display_name, email, enabled)
VALUES(gen_random_uuid(), now(), 'your_name', now() , 'your_name', 'your_name', 'your_google_email_address', true);
```


## Using Docker for PostgreSQL (Optional)

If you don’t have PostgreSQL installed, you can spin up a container:

```bash
docker run --name tvview-postgres \
  -e POSTGRES_DB=tvviewdb \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -d postgres
```

### Security Notice

This project uses environment variables to handle secrets like Google client credentials and JWT secrets. Be sure to:

- Add `.env` to your `.gitignore`
- Never commit actual credentials
- Rotate and manage keys securely in production environments
