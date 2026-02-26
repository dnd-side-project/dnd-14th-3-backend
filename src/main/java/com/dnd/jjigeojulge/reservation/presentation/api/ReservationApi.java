package com.dnd.jjigeojulge.reservation.presentation.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.dnd.jjigeojulge.global.common.response.ApiResponse;
import com.dnd.jjigeojulge.global.common.response.PageResponse;
import com.dnd.jjigeojulge.reservation.application.dto.query.AppliedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.CreatedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationCommentDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationDetailDto;
import com.dnd.jjigeojulge.reservation.presentation.request.ReservationCreateRequest;

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
	@Operation(summary = "사전 예약 탐색 리스트 조회 (피드)", description = """
			동행 예약 목록(피드)을 페이지네이션 방식으로 탐색합니다.

			- `cursor` : 마지막으로 조회한 예약 ID (없으면 가장 최신글부터 조회)
			- `limit`   : 한 페이지에 조회할 개수 (기본값 10)

			**예약 카드 구성 필드 반환 목록**
			- 날짜/시간, 장소 요약(1Depth), 촬영 유형 스냅샷, 요청자 신뢰도, 상태 배지 (RECRUITING 등)
			""")
	ResponseEntity<ApiResponse<PageResponse<com.dnd.jjigeojulge.reservation.application.dto.query.ReservationListResponseDto>>> getList(
			@Parameter(description = "마지막 조회 예약 ID (cursor 기반 페이징)", example = "10") Long cursor,
			@Parameter(description = "한 페이지에 조회할 개수", example = "10") int limit);

	@Operation(summary = "예약 상세 탐색 (동행 상세 페이지)", description = """
			특정 동행 예약글의 상세 정보를 조회합니다.

			**노출 정보**
			- 게시글 조회수, 누적 지원자 수
			- 요청자(호스트) 프로필 (닉네임, 프로필, 신뢰도, 성별)
			- 요청 내용(메시지), 약속 시간/구체적 장소, 예상 촬영 시간, 스냅샷 촬영 유형

			*참고: 상세조회 시 댓글 목록은 포함되지 않습니다. 별도의 `/comments` API를 호출해야 합니다.*
			""")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "예약 상세 정보 조회 성공"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음", content = @Content(examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "예약 글을 찾을 수 없습니다.",
					  "code": "RESERVATION_NOT_FOUND",
					  "data": null
					}
					""")))
	})
	ResponseEntity<ApiResponse<ReservationDetailDto>> getDetail(
			@Parameter(description = "예약 글 ID", required = true, example = "101") Long reservationId);

	@Operation(summary = "Reservation 생성", description = "동행 예약을 생성하는 API")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "예약 생성 성공 (생성된 예약 ID 반환)", content = @Content(schema = @Schema(implementation = Long.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(examples = @ExampleObject(value = """
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
					""")))
	})
	ResponseEntity<ApiResponse<Long>> create(
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId,
			@RequestBody(required = true, description = "동행 예약 생성 요청 데이터", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ReservationCreateRequest.class))) ReservationCreateRequest request);

	@Operation(summary = "Reservation 부분 수정 (PATCH)", description = "방장이 예약 파라미터를 부분적으로 수정합니다. (넘기지 않은 값은 기존 값이 유지됩니다)")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공")
	})
	ResponseEntity<ApiResponse<Void>> update(
			@Parameter(description = "예약 ID") Long reservationId,
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId,
			@RequestBody(required = true, description = "동행 예약 수정(PATCH) 요청 데이터") com.dnd.jjigeojulge.reservation.presentation.request.ReservationUpdateRequest request);

	@Operation(summary = "Reservation 취소", description = "방장이 예약 방명록 자체를 취소(폭파)합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "취소 성공")
	})
	ResponseEntity<ApiResponse<Void>> cancel(
			@Parameter(description = "예약 ID") Long reservationId,
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId);

	@Operation(summary = "동행 지원하기", description = "게스트가 모집 중인 방에 동행을 지원합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지원 성공")
	})
	ResponseEntity<ApiResponse<Void>> apply(
			@Parameter(description = "예약 ID") Long reservationId,
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId);

	@Operation(summary = "동행 지원 취소하기", description = "게스트가 자신이 지원했던 내역을 취소합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지원 취소 성공")
	})
	ResponseEntity<ApiResponse<Void>> cancelApplication(
			@Parameter(description = "예약 ID") Long reservationId,
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId);

	@Operation(summary = "지원자 수락(매칭 성사)", description = "방장이 특정 게스트의 지원을 수락하여 매칭을 확정(CONFIRMED)합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지원자 수락 성공")
	})
	ResponseEntity<ApiResponse<Void>> acceptApplicant(
			@Parameter(description = "예약 ID") Long reservationId,
			@Parameter(description = "수락할 지원자의 고유 Applicant ID") Long applicantId,
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId);

	@Operation(summary = "지원자 개별 거절", description = "방장이 특정 게스트의 지원을 단독으로 거절합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지원자 거절 성공")
	})
	ResponseEntity<ApiResponse<Void>> rejectApplicant(
			@Parameter(description = "예약 ID") Long reservationId,
			@Parameter(description = "거절할 지원자의 고유 Applicant ID") Long applicantId,
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId);

	@Operation(summary = "예약 완료(COMPLETED) 처리", description = "매칭이 확정된 방에 대하여 스케줄 만료 후 완료 처리를 수행합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "완료 처리 성공")
	})
	ResponseEntity<ApiResponse<Void>> complete(
			@Parameter(description = "예약 ID") Long reservationId,
			@Parameter(description = "인증 사용자", hidden = true) Long currentUserId);

	@Operation(summary = "내가 올린 동행 예약 리스트 (호스트)", description = """
			내가 방장(Owner)으로서 작성한 동행 매집글의 목록입니다.
			커서 기반 무한 스크롤 페이징(Cursor Pagination)을 지원합니다.

			**방 상태(status) Enum 안내**
			- `RECRUITING`: 모집 중 (지원자를 받고 있는 상태)
			- `CONFIRMED`: 매칭 확정 (지원자 중 1명을 수락함)
			- `RECRUITMENT_CLOSED`: 기간 만료 (약속 시간이 될 때까지 아무도 수락하지 않아 자동 마감됨)
			- `COMPLETED`: 일정 완료 (매칭 확정 후 약속 시간이 지남)
			- `CANCELED`: 모집 취소 (방장이 방을 폭파함)
			""")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내가 올린 예약 목록 조회 성공")
	})
	ResponseEntity<ApiResponse<PageResponse<CreatedReservationListDto>>> getMyCreatedReservations(
			@Parameter(description = "인증된 사용자의 ID", hidden = true) Long currentUserId,
			@Parameter(description = "다음 페이지 조회를 위한 마지막 예약 ID (새로고침 시 null)") Long cursor,
			@Parameter(description = "페이지당 항목 수") int limit);

	@Operation(summary = "내가 지원한 동행 예약 리스트 (게스트)", description = """
			내가 다른 사람이 올린 방에 게스트(Applicant)로 지원했던 이력 목록을 조회합니다.
			커서 기반 무한 스크롤 페이징을 지원하며, 방 자체의 상태가 아닌 **내 입장에서의 매칭 결과(Virtual Status)**를 반환합니다.

			**가상 매칭 상태(status) Enum 안내**
			- `WAITING` (대기 중): 내가 지원하고 아직 호스트가 수락/거절을 안 했으며 예약 시간도 남은 상태
			- `MATCHED` (매칭 확정): 호스트가 나를 선택했고, 아직 약속 시간이 지나지 않은 상태
			- `COMPLETED` (일정 완료): 내가 선택되었고(MATCHED), 이미 약속 시간이 지남 (리뷰 작성 활성화 대상)
			- `REJECTED` (거절/실패): 호스트가 거절함, 타인이 선택됨, 혹은 방 기한이 만료되어 모집 실패함
			- `CANCELED` (취소됨): 내가 스스로 지원을 취소했거나, 호스트가 모임 방을 폭파한 경우
			""")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "내가 지원한 예약 목록 조회 성공")
	})
	ResponseEntity<ApiResponse<PageResponse<AppliedReservationListDto>>> getMyAppliedReservations(
			@Parameter(description = "인증된 사용자의 ID", hidden = true) Long currentUserId,
			@Parameter(description = "다음 페이지 조회를 위한 마지막 예약 ID (새로고침 시 null)") Long cursor,
			@Parameter(description = "페이지당 항목 수") int limit);

	@Operation(summary = "동행 예약 댓글 목록 조회", description = """
			특정 동행 예약글 하단에 달린 소통용 댓글 목록을 무한 스크롤(커서 페이징)로 조회합니다.

			**노출 정보**
			- 작성자 프로필, 댓글 본문, 작성/수정 일시
			- 삭제 여부(`isDeleted` = true 일 경우 프론트에서 '삭제된 댓글입니다' 처리 필요)
			""")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
	})
	ResponseEntity<ApiResponse<PageResponse<ReservationCommentDto>>> getComments(
			@Parameter(description = "예약 글 ID", required = true, example = "101") Long reservationId,
			@Parameter(description = "마지막 조회 댓글 ID (cursor)", example = "500") Long cursor,
			@Parameter(description = "한 페이지 조회 개수", example = "10") int limit);

	@Operation(summary = "방장 - 예약 지원자 목록 조회", description = "내가 만든 동행 예약에 지원한 게스트(지원자) 목록을 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지원자 목록 조회 성공")
	})
	ResponseEntity<ApiResponse<com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto>> getApplicants(
			@Parameter(description = "인증된 사용자의 ID", hidden = true) Long currentUserId,
			@Parameter(description = "예약 글 ID", required = true, example = "101") Long reservationId);
}
/*
 * - 동행 요청하기 기능 post /api/v1/reservations/{id}
 * - 동행 예약 상세보기에서 댓글 작성하기 기능 post /api/v1/reservations/{id}/comments
 * 
 * - 동행 예약 상세보기에서 댓글 삭제하기 기능 delete
 * /api/v1/reservations/{reservationId}/comments/{commentId}
 * - 동행 예약 상세보기에서 댓글 수정하기 기능 patch
 * /api/v1/reservations/{reservationId}/comments/{commentId}
 *
 * 내가 올린 동행 예약에 지원한 사람 수락하기 기능 post
 * 내가 올린 동행 예약에 지원한 사람 거절하기 기능 post
 *
 * 내가 지원한 동행 예약 취소하기 기능 delete
 */
