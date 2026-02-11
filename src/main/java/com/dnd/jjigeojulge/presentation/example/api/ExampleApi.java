package com.dnd.jjigeojulge.presentation.example.api;

import org.springframework.http.ResponseEntity;

import com.dnd.jjigeojulge.global.common.ApiResponse;
import com.dnd.jjigeojulge.presentation.example.response.ExampleDtoRecord;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Example API", description = "Example API endpoints")
public interface ExampleApi {

	@Operation(summary = "예시 API 엔드포인트", description = "이 엔드포인트는 [예시] 응답을 반환합니다.")
	@ApiResponses(
		value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(
				responseCode = "200",
				description = "성공적으로 예시 응답을 반환함"
			)}
	)
	ResponseEntity<ApiResponse<ExampleDtoRecord>> exampleEndpoint();

}
