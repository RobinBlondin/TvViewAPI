# TV View API

A Kotlin Spring Boot backend for displaying calendar events, uploading files, tracking public transportation departures, and more.
This project serves as the backend for the companion frontend, TvViewWeb: https://github.com/RobinBlondin/TvViewWeb.

It functions as an OAuth2 Resource Server, handling authenticated requests from the frontend and integrating various third-party services into a unified API.

![image](https://github.com/user-attachments/assets/be826092-1baa-4f3d-81d7-21fe0f9a9434)


## Technologies Used

- **Kotlin** + Spring Boot
- Java 21
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

### Google Cloud Console Setup

Before running the application, you need to set up a Google Cloud Console project and configure OAuth2 credentials:

#### 1. Create a Google Cloud Project

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the following APIs:
   - Google Calendar API
   - Google+ API (for profile information)

#### 2. Configure OAuth2 Consent Screen

1. Navigate to **APIs & Services** > **OAuth consent screen**
2. Choose **External** user type (unless you're using Google Workspace)
3. Fill in the required information:
   - App name: `TV View API`
   - User support email: Your email
   - Developer contact information: Your email
4. Add scopes:
   - `openid`
   - `profile`
   - `email`
   - `https://www.googleapis.com/auth/calendar.readonly`
5. Add test users (your email addresses that will use the app)

#### 3. Create OAuth2 Credentials

1. Navigate to **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **OAuth 2.0 Client IDs**
3. Choose **Web application** as the application type
4. Configure the following:

   **For Backend OAuth2 Client:**
   - Name: `TV View API`
   - Authorized JavaScript origins:
     ```
     http://localhost:8080
     https://your_own_domain (Optional)
     ```
   - Authorized redirect URIs:
     ```
     http://localhost:8080/login/oauth2/code/google
     http://localhost:8080/auth/google
     https://your_own_domain/login/oauth2/code/google (Optional)
     https://your_own_domain/auth/google (Optional)
     ```

5. Download the JSON credentials

#### 4. Create a Service Account

1. Navigate to **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **Service Account**
3. Fill in the service account details:
   - Name: `tv-view-service-account`
   - Description: `Service account for TV View API calendar access`
4. Grant the service account the **Calendar API** access
5. Click **Done**
6. Click on the created service account
7. Go to the **Keys** tab
8. Click **Add Key** > **Create new key** > **JSON**
9. Download the JSON key file - this will be your `credentials.json`

#### 5. Share Your Calendar

1. Open Google Calendar
2. Find the calendar you want to display
3. Click the three dots next to the calendar name > **Settings and sharing**
4. Under **Share with specific people**, add the service account email (found in the credentials.json file)
5. Give it **See all event details** permission
6. Copy the **Calendar ID** from the **Integrate calendar** section

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

FRONTEND_GOOGLE_CLIENT_ID=<your_google_client_id_for_frontend_app>
FRONTEND_GOOGLE_CLIENT_SECRET=<your_google_client_secret_for_frontend_app>

CREDENTIALS=/path/to/credentials.json
CREDENTIALS_JSON=contents_of_credentials.json_file_as_a_string

CALENDAR_ID=your_calendar_id (owner email of calendar)

COMMUTE_API_KEY=your_resrobot_api_key
COMMUTE_STOP_ID=your_commute_stop_id (e.g. 740000605)

FILE_UPLOAD_DIR=http://localhost:8081/uploads

SERVICE_ACCOUNT_EMAIL=<email_for_applications_service_account>

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
