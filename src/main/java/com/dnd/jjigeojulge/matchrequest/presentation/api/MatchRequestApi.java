package com.dnd.jjigeojulge.matchrequest.presentation.api;

import org.springframework.http.ResponseEntity;

import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.matchrequest.presentation.data.MatchRequestDto;
import com.dnd.jjigeojulge.matchrequest.presentation.request.MatchRequestCreateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "실시간 매칭 대기 관리", description = "실시간 동행 매칭을 위한 대기(요청) 생성/취소/상태조회 API")
public interface MatchRequestApi {

	@Operation(summary = "실시간 매칭 대기 생성", description = """
		실시간 동행 매칭을 시작합니다.
		
		- 사용자는 대기열(예: Redis GEO/Queue)에 등록됩니다.
		- 응답으로 `matchRequestId` 와 만료 시각을 반환합니다.
		- 초기 상태는 `WAITING` 입니다.
		""")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201", description = "매칭 대기 생성 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "요청 값 검증 실패",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "요청 값 검증에 실패했습니다.",
					  "code": "VALIDATION_FAILED",
					  "data": {
					       "fieldErrors": [
					         {
					           "field": "specificPlace",
					           "message": "구체적인 장소는 필수입니다.",
					           "code": "NotBlank"
					         },
					         {
							    "field": "expectedDuration",
							    "message": "예상 촬영 소요 시간은 필수입니다.",
							    "code": "NotNull"
					         }
					       ]
					     }
					}
					""")
			)
		)
	})
	ResponseEntity<ApiResponse<MatchRequestDto>> create(
		MatchRequestCreateRequest request,
		@Parameter(hidden = true) CustomUserDetails userDetails
	);

	@Operation(summary = "실시간 매칭 대기 상태 조회", description = """
		매칭 대기(요청)의 현재 상태를 조회합니다.
		
		- `WAITING`   : 대기 중
		- `MATCHED`   : 매칭 성사
		- `CANCELLED` : 취소됨
		- `EXPIRED`   : 만료됨
		""")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "매칭 대기 조회 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "매칭 요청을 찾을 수 없음.",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "매칭 요청을 찾을 수 없습니다.",
					  "code": "MATCH_REQUEST_NOT_FOUND",
					  "data": null
					}
					"""))
		)
	})
	ResponseEntity<ApiResponse<MatchRequestDto>> get(
		@Parameter(description = "매칭 요청 ID", example = "12", required = true)
		Long matchRequestId
	);

	@Operation(summary = "실시간 매칭 대기 취소", description = """
		내 진행 중인 실시간 매칭 대기(요청)를 취소합니다.
		
		- 대기열에서 제거됩니다.
		- 이미 취소되었거나 대기 중인 요청이 없더라도 멱등하게 성공(204) 처리할 수 있습니다.
		""")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "204", description = "매칭 대기 취소 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "409", description = "취소 불가 상태",
			content = @Content(schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "현재 상태에서는 취소할 수 없습니다.",
					  "code": "MATCH_REQUEST_CANCEL_NOT_ALLOWED",
					  "data": null
					}
					"""))
		)
	})
	ResponseEntity<Void> cancel(@Parameter(hidden = true) CustomUserDetails userDetails);
}
