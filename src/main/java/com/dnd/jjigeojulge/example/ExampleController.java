package com.dnd.jjigeojulge.example;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Example API", description = "Example API endpoints")
@RestController
@RequestMapping("/api/v1/examples")
public class ExampleController {

	@Operation(summary = "예시 API 엔드포인트", description = "이 엔드포인트는 [예시] 응답을 반환합니다.")
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "성공적으로 예시 응답을 반환함"
			)
		})
	@GetMapping
	public ResponseEntity<ApiResponse<ExampleDtoRecord>> exampleEndpoint() {
		ExampleDtoRecord dto = new ExampleDtoRecord(1L, "example@google.com",
			new ExampleDtoRecord.Profile("exampleNickname", "http://example.com/profile.jpg"),
			List.of());

		return ResponseEntity.ok(ApiResponse.success("Example endpoint reached successfully", dto));
	}
}
