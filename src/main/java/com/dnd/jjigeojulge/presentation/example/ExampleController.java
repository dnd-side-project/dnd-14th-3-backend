package com.dnd.jjigeojulge.presentation.example;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.global.common.ApiResponse;
import com.dnd.jjigeojulge.presentation.example.api.ExampleApi;
import com.dnd.jjigeojulge.presentation.example.response.ExampleDtoRecord;

@RestController
@RequestMapping("/api/v1/examples")
public class ExampleController implements ExampleApi {

	@GetMapping
	public ResponseEntity<ApiResponse<ExampleDtoRecord>> exampleEndpoint() {
		ExampleDtoRecord dto = new ExampleDtoRecord(1L, "example@google.com",
			new ExampleDtoRecord.Profile("exampleNickname", "http://example.com/profile.jpg"),
			List.of());

		return ResponseEntity.ok(ApiResponse.success("Example endpoint reached successfully", dto));
	}

}
