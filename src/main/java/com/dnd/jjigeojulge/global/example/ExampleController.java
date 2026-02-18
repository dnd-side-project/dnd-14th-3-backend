package com.dnd.jjigeojulge.global.example;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.global.example.api.ExampleApi;
import com.dnd.jjigeojulge.global.example.response.ExampleDtoRecord;

@RestController
@RequestMapping("/api/v1/examples")
public class ExampleController implements ExampleApi {

	@Override
	@GetMapping
	public ResponseEntity<ApiResponse<ExampleDtoRecord>> exampleEndpoint() {
		ExampleDtoRecord dto = new ExampleDtoRecord(1L,
			new ExampleDtoRecord.Profile("exampleNickname", "http://example.com/profile.jpg"),
			List.of());

		return ResponseEntity.ok(ApiResponse.success("Example endpoint reached successfully", dto));
	}

}
