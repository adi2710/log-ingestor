package io.dyte.logingestor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import io.dyte.logingestor.controller.LogController;
import io.dyte.logingestor.model.ApiResponse;
import io.dyte.logingestor.model.Log;
import io.dyte.logingestor.model.QueryPayload;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LogIngestorApplicationTests {
	private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
	@Autowired
	private LogController logController;

	@Test
	void testIngestLogSuccess() throws JsonProcessingException {
		String payload = """
			{
				"level": "error",
				"message": "Failed to connect to VerticaDB",
				"resourceId": "server-1234",
				"timestamp": "2023-09-16T09:59:00Z",
				"traceId": "abc-xyz-123",
				"spanId": "span_456",
				"commit": "5e5342f",
				"metadata": {
					"parentResourceId": "server-0988"
				}
			}
		""";
		Log log = objectMapper.readValue(payload, Log.class);
		ResponseEntity<ApiResponse<List<Log>>> response = logController.ingestLog(log);
		assertEquals(201, response.getStatusCodeValue());
		assertEquals("Success", response.getBody().getMessage());
		System.out.println("Inject Log Success Test Case Passed");
	}

	@Test
	void testIngestLogFailure() {
		String payload = """
			{
				"level": "error",
				"message": "Failed to connect to VerticaDB",
				"resourceId": "server-1234",
				"timestamp": "2023-09-16 09:59:00",
				"traceId": "abc-xyz-123",
				"spanId": "span_456",
				"commit": "5e5342f",
				"metadata": {
					"parentResourceId": "server-0988"
				}
			}
		""";
		try {
			Log log = objectMapper.readValue(payload, Log.class);
		} catch (Exception e) {
			assertEquals("java.time.format.DateTimeParseException: Text '2023-09-16 09:59:00' could not be parsed at index 10", e.getCause().toString());
		}
		System.out.println("Inject Log Failure Test Case Passed");
	}

	@Test
	void testQueryWithMultipleFilters() throws JsonProcessingException {
		String payload = """
			{
				"level": "error",
				"message": "connect to vertica",
				"resourceId": "server-1234",
				"timestamp": "2023-09-16T09:59:00Z",
				"traceId": "abc-xyz-123",
				"spanId": "span_456",
				"commit": "5e5342f",
				"parentResourceId": "server-0988"
			}
		""";
		QueryPayload queryPayload = objectMapper.readValue(payload, QueryPayload.class);
		ResponseEntity<ApiResponse<List<Log>>> response = logController.queryLogs(queryPayload);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Success", response.getBody().getMessage());
		System.out.println("Query With Multiple Filters Test Case Passed");
	}

	@Test
	void testQueryWithBadRequest() throws JsonProcessingException {
		String payload = """
			{
				"level": "error",
				"message": "connect to vertica",
				"resourceId": "server-1234",
				"timestamp": "2023-09-16T09:59:00Z",
				"startTimestamp": "2023-09-16T09:10:00Z",
				"endTimestamp": "2023-09-16T11:00:00Z",
				"traceId": "abc-xyz-123",
				"spanId": "span_456",
				"commit": "5e5342f",
				"parentResourceId": "server-0988"
			}
		""";
		QueryPayload queryPayload = objectMapper.readValue(payload, QueryPayload.class);
		ResponseEntity<ApiResponse<List<Log>>> response = logController.queryLogs(queryPayload);
		assertEquals(400, response.getStatusCodeValue());
		assertEquals("Invalid parameters: Either provide 'timestamp' or 'startTimestamp' and 'endTimestamp', not both.", response.getBody().getMessage());
		System.out.println("Query With Bad Request Test Case Passed");
	}

	@Test
	void testQueryWithRegexFilters() throws JsonProcessingException {
		String payload = """
			{
				"spanId": "^span_\\\\d+$",
				"parentResourceId": "^server-\\\\d+$"
			}
		""";
		QueryPayload queryPayload = objectMapper.readValue(payload, QueryPayload.class);
		ResponseEntity<ApiResponse<List<Log>>> response = logController.queryLogs(queryPayload);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals("Success", response.getBody().getMessage());
		System.out.println("Query With Regex Filters Test Case Passed");
	}
}