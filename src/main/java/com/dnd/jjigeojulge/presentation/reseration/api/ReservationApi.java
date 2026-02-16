package com.dnd.jjigeojulge.presentation.reseration.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.dnd.jjigeojulge.presentation.common.response.ApiResponse;
import com.dnd.jjigeojulge.presentation.common.response.PageResponse;
import com.dnd.jjigeojulge.presentation.reseration.data.ReservationDto;
import com.dnd.jjigeojulge.presentation.reseration.request.ReservationCreateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "동행 예약 관리", description = "동행 예약 관련 API")
public interface ReservationApi {
	@Operation(
		summary = "Reservation 목록 조회",
		description = """
			동행 예약 목록을 페이지네이션 방식으로 조회합니다.
			
			- `cursor` : 마지막으로 조회한 예약 ID (없으면 첫 페이지)
			- `size`   : 한 페이지에 조회할 개수 (기본값 10)
			
			`nextCursor` 값이 존재하면 다음 페이지가 존재합니다.
			"""
	)
	ResponseEntity<ApiResponse<PageResponse<ReservationDto>>> getList(
		@Parameter(description = "마지막 조회 예약 ID (cursor 기반 페이징)", example = "10")
		Long cursor,
		@Parameter(description = "한 페이지에 조회할 개수", example = "10")
		int limit
	);

	@Operation(summary = "Reservation 상세 조회", description = "동행 예약의 상세 정보를 조회하는 API")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200", description = "예약 상세 정보 조회 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "404", description = "예약을 찾을 수 없음",
			content = @Content(examples = @ExampleObject(value = """
				{
				  "success": false,
				  "message": "예약 글을 찾을 수 없습니다.",
				  "code": "RESERVATION_NOT_FOUND",
				  "data": null
				}
				"""))
		)
	})
	ResponseEntity<ApiResponse<ReservationDto>> getDetail(
		@Parameter(description = "예약 글 ID", required = true, example = "101")
		Long reservationId
	);

	@Operation(summary = "Reservation 생성", description = "동행 예약을 생성하는 API")
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201", description = "예약 생성 성공"
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "400", description = "잘못된 요청 데이터",
			content = @Content(examples = @ExampleObject(value = """
				{
				  "success": false,
				  "message": "유효하지 않은 요청입니다.",
				  "code": "INVALID_REQUEST",
				   "data": {
				      "fieldErrors": [
				        {
				          "field": "location.longitude",
				          "message": "경도는 필수입니다.",
				          "code": "NotNull"
				        }
				      ]
				    }
				}
				"""))
		)
	})
	ResponseEntity<ApiResponse<ReservationDto>> create(
		@RequestBody(
			required = true, description = "동행 예약 생성 요청 데이터",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
				schema = @Schema(implementation = ReservationCreateRequest.class))
		) ReservationCreateRequest request
	);
}

/*
 * - 동행 요청하기 기능 post /api/v1/reservations/{id}
 * - 동행 예약 상세보기에서 댓글 작성하기 기능 post /api/v1/reservations/{id}/comments
  - 동행 예약 상세보기에서 댓글 목록 조회하기 기능 get /api/v1/reservations/{id}/comments
  - 동행 예약 상세보기에서 댓글 삭제하기 기능 delete /api/v1/reservations/{reservationId}/comments/{commentId}
  - 동행 예약 상세보기에서 댓글 수정하기 기능 patch /api/v1/reservations/{reservationId}/comments/{commentId}
  *
  * 내가 올린 동행 예약 목록 조회 기능
  * 내가 올린 동행 예약에 지원한 사람 목록 조회 기능
  * 내가 올린 동행 예약에 지원한 사람 수락하기 기능
  * 내가 올린 동행 예약에 지원한 사람 거절하기 기능
  *
  * 내가 지원한 동행 예약 목록 조회 기능
  * 내가 지원한 동행 예약 취소하기 기능
 * */
