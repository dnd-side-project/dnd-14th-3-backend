package com.dnd.jjigeojulge.user.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.infra.PhotoStyleRepository;
import com.dnd.jjigeojulge.user.presentation.data.PhotoStyleDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/photo-style")
@RequiredArgsConstructor
@Tag(name = "촬영 스타일", description = "촬영 스타일 관련 API")
public class PhotoStyleController {

	private final PhotoStyleRepository photoStyleRepository;

	@GetMapping
	@Operation(
		summary = "촬영 스타일 전체 조회",
		description = "회원가입 또는 프로필 설정 시 선택 가능한 촬영 스타일 목록을 조회합니다."
	)
	public ResponseEntity<ApiResponse<List<PhotoStyleDto>>> findAll() {
		List<PhotoStyle> photoStyles = photoStyleRepository.findAll();
		List<PhotoStyleDto> dtos = photoStyles.stream()
			.map(photoStyle -> new PhotoStyleDto(photoStyle.getId(), photoStyle.getName()))
			.toList();
		return ResponseEntity.ok(ApiResponse.success(dtos));
	}
}
