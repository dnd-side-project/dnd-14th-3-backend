package com.dnd.jjigeojulge.presentation.user.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.dnd.jjigeojulge.global.common.ApiResponse;
import com.dnd.jjigeojulge.presentation.user.data.ConsentDto;
import com.dnd.jjigeojulge.presentation.user.data.ProfileDto;
import com.dnd.jjigeojulge.presentation.user.request.UserConsentUpdateRequest;
import com.dnd.jjigeojulge.presentation.user.request.UserUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "프로필 관리", description = "프로필 관련 API")
public interface UserApi {

	@Operation(summary = "닉네임 사용 가능 여부 확인", description = """
		입력된 이메일이 **회원 가입에 사용 가능한지** 확인합니다.
		
		- `data=true`  : 사용 가능 (중복 아님)
		- `data=false` : 사용 불가 (이미 사용 중)
		
		※ 중복 여부는 정상적인 비즈니스 결과이므로 `success=true`로 응답합니다.
		""")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "닉네임 중복 여부 조회 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "닉네임 형식 정책 위반",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "닉네임은 2~20자이며 한글, 영문, 숫자만 사용할 수 있습니다.",
					  "code": "INVALID_NICKNAME_FORMAT",
					  "data": null
					}
					"""))
		)
	})
	ResponseEntity<ApiResponse<Boolean>> checkNicknameAvailability(
		@Parameter(required = true, description = "검사할 닉네임", example = "사진수집가") String nickname
	);

	@Operation(summary = "User 프로필 조회", description = "프로필 조회 API")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "User 프로필이 성공적으로 조회됨",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "SUCCESS",
					  "code": "",
					  "data": {
					    "nickname": "홍길동",
					    "profileImageUrl": "https://example.com/profile.jpg"
					  }
					}
					""")
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "User를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "User를 찾을 수 없습니다.",
					  "code": "USER_NOT_FOUND",
					  "data": null
					}
					""")))
	})
	ResponseEntity<ApiResponse<ProfileDto>> find(
		@Parameter(description = "조회할 User ID") Long userId
	);

	@Operation(summary = "User 프로필 수정")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "User 프로필이 성공적으로 수정됨",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "SUCCESS",
					  "code": "",
					  "data": {
					    "nickname": "홍길동",
					    "profileImageUrl": "https://example.com/updated_profile.jpg"
					  }
					}
					"""))
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "프로필 업데이트 실패",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "요청한 프로필 정보가 올바르지 않습니다.",
					  "code": "INVALID_PROFILE_REQUEST",
					  "data": null
					}
					""")))
	})
	ResponseEntity<ApiResponse<ProfileDto>> update(
		@Parameter(description = "수정할 User ID") Long userId,
		@Parameter(
			description = "수정할 User 정보",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
		) UserUpdateRequest userUpdateRequest,
		@Parameter(
			description = "수정할 User 프로필 이미지",
			content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
		) MultipartFile profileImage
	);

	@Operation(summary = "User 권한 동의 설정 조회", description = "사용자의 위치 공유 알림 설정을 조회합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "사용자의 위치 공유 알림 설정 조회 성공",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "SUCCESS",
					  "code": "",
					  "data": {
					    "notificationAllowed": true,
					    "locationAllowed": true
					  }
					}
					""")
			)),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "User를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "User를 찾을 수 없습니다.",
					  "code": "USER_NOT_FOUND",
					  "data": null
					}
					""")))
	})
	ResponseEntity<ApiResponse<ConsentDto>> getConsentPermission(@Parameter(description = "조회할 유저 ID") Long userId);

	@Operation(summary = "User 권한 동의 설정 수정", description = "사용자의 위치 공유 알림 설정을 수정합니다.")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "사용자의 위치 공유 알림 설정 수정 성공",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "SUCCESS",
					  "code": "",
					  "data": {
					    "notificationAllowed": false,
					    "locationAllowed": true
					  }
					}
					""")
			)),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "권한 동의 설정 수정 실패",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "요청한 권한 동의 설정 정보가 올바르지 않습니다.",
					  "code": "INVALID_CONSENT_REQUEST",
					  "data": null
					}
					"""))),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "User를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "User를 찾을 수 없습니다.",
					  "code": "USER_NOT_FOUND",
					  "data": null
					}
					""")))
	})
	ResponseEntity<ApiResponse<ConsentDto>> updateConsentPermission(
		@Parameter(description = "수정할 유저 ID") Long userId,
		@Parameter(
			description = "수정할 User 권한",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
		) UserConsentUpdateRequest request
	);

}
