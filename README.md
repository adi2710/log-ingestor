# Log Ingestor Application

## Overview
This Spring Boot project serves as a log ingestor application with advanced search capabilities. It provides APIs for ingesting logs and querying logs based on various filters.

## System Design
Open the URL https://app.diagrams.net/ and then open existing diagram `LogIngestorDesign.drawio` to view the System Design.

## Features Implemented
1. **Ingest Logs**
    - Endpoint: `/ingest`
    - Method: `POST`
    - Ingests logs into the system.

2. **Query Logs**
    - Endpoint: `/query`
    - Method: `POST`
    - Queries logs based on specified filters.

   **Supported Log Query Filters:**
    - Search within specific date ranges.
    - Search with regular expressions.
    - Search message containing input string ignoring case.
    - Combine multiple filters for complex queries.

## Build and Run

### Normal Build
- Set up mongoDB server.
- Set the mongoDB connection string in `src/main/resources/application.properties`. (Eg: `spring.data.mongodb.uri=mongodb://localhost:27017/logs`)
- Run the application using the `src/main/java/io/dyte/logingestor/LogIngestorApplication.java` file in your IDE (Eg: IntelliJ, Eclipse, etc.)
- Do a CURL call to `http://localhost:3000/query` or `http://localhost:3000/ingest` with the appropriate request payload.

### Docker Build
- Run the file `./BuildAndRun.sh` in bash terminal (`Maven` and `Docker` required).
- Do a CURL call to `http://localhost:3000/query` or `http://localhost:3000/ingest` with the appropriate request payload.

**Note:** Test Cases have been included in the file `src/test/java/io/dyte/logingestor/LogIngestorApplicationTests.java`

## Usage

### Ingest Log
To ingest a log, send a POST request to `/ingest` with a JSON payload representing the log.

**Example:**
```json
{
   "level": "error",
   "message": "Failed to connect to DB",
   "resourceId": "server-1234",
   "timestamp": "2023-09-15T08:00:00Z",
   "traceId": "abc-xyz-123",
   "spanId": "span-456",
   "commit": "5e5342f",
   "metadata": {
      "parentResourceId": "server-0987"
   }
}
```

### Query Logs
To query logs, send a POST request to `/query` with a JSON payload containing filters.

**Example:**
- Query payload with start and end timestamp:
   ```json
   {
      "startTimestamp": "2023-09-16T09:10:00Z",
      "endTimestamp": "2023-09-16T11:00:00Z"
   }
   ```

- Query payload with timestamp and message:
   ```json
   {
      "timestamp": "2023-01-01T12:00:00Z",
      "message": "connect to db"
   }
   ```

- Query payload with regular expression:
   ```json
   {
     "spanId": "^span_\\d+$",
     "parentResourceId": "^server-\\d+$"
   }
   ```

**Note:** Ensure that you provide either `timestamp` or `startTimestamp` and `endTimestamp`, not both. Otherwise, the service will throw `400 Bad Request` response.

## Tech Stack
- Java 18
- Spring Boot(v3.2.0)
- Mongo DB(v7.0.3)
- Docker(v24.0.6)