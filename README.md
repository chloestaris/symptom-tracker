# Health Symptom Tracker API

A Spring Boot application for tracking health symptoms and medications with GitHub OAuth2 authentication.

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- GitHub account (for OAuth2 authentication)

## Setup

### 1. GitHub OAuth Application

1. Go to GitHub Settings > Developer Settings > OAuth Apps > New OAuth App
2. Fill in the application details:
   - Application name: `Symptom Tracker`
   - Homepage URL: `http://localhost:8081`
   - Authorization callback URL: `http://localhost:8081/login/oauth2/code/github`
3. After creating the application, note down:
   - Client ID
   - Client Secret (generate a new client secret if needed)

### 2. Environment Configuration

1. Create a `.env` file in the project root:
```env
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

2. Add `.env` to your `.gitignore`:
```bash
echo ".env" >> .gitignore
```

## Running the Application

### Using Docker (Recommended)

1. Build and start the containers:
```bash
docker-compose up -d --build
```

2. Check the application status:
```bash
docker-compose ps
```

3. View logs if needed:
```bash
docker-compose logs -f app
```

### Using Local Development Environment

1. Set environment variables:
```bash
export GITHUB_CLIENT_ID=your_github_client_id
export GITHUB_CLIENT_SECRET=your_github_client_secret
```

2. Run the application:
```bash
./mvnw spring-boot:run
```

## Authentication

1. Visit `http://localhost:8081/oauth2/authorization/github` in your browser
2. Complete the GitHub authentication flow
3. Note the `JSESSIONID` cookie from your browser's developer tools
4. Use this session ID in your API requests

## API Endpoints

### Medications

```bash
# Add medication
curl -X POST "http://localhost:8081/api/medications" \
-H "Content-Type: application/json" \
-H "Cookie: JSESSIONID=your_session_id" \
-d '{
  "name": "Medication Name",
  "dosage": "Dosage",
  "frequency": "Frequency",
  "reminderTimes": ["09:00", "21:00"],
  "instructions": "Instructions",
  "active": true,
  "notes": "Notes"
}'

# Get active medications
curl -X GET "http://localhost:8081/api/medications/active" \
-H "Cookie: JSESSIONID=your_session_id"
```

### Symptoms

```bash
# Log symptom
curl -X POST "http://localhost:8081/api/symptoms" \
-H "Content-Type: application/json" \
-H "Cookie: JSESSIONID=your_session_id" \
-d '{
  "name": "Symptom Name",
  "severity": 5,
  "description": "Description",
  "bodyLocation": "Location"
}'

# Get symptoms by date range
curl -X GET "http://localhost:8081/api/symptoms?start=2024-03-01T00:00:00&end=2024-03-31T23:59:59" \
-H "Cookie: JSESSIONID=your_session_id"
```

## Development

### Database

- H2 in-memory database is used by default
- H2 Console available at: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:symptomdb`
- Username: `sa`
- Password: (empty)

### API Documentation

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8081/v3/api-docs`

## Security

- All `/api/**` endpoints require authentication
- Session timeout: 30 minutes
- CSRF protection is disabled for API endpoints
- H2 Console is accessible for development

## Troubleshooting

### Docker Container is Unhealthy

1. Check container logs:
```bash
docker-compose logs -f app
```

2. Verify the health endpoint:
```bash
curl -v http://localhost:8081/actuator/health
```

3. Ensure GitHub OAuth credentials are correct in `.env`

### Authentication Issues

1. Clear browser cookies and try re-authenticating
2. Ensure you're using a valid JSESSIONID
3. Check application logs for authentication errors

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
