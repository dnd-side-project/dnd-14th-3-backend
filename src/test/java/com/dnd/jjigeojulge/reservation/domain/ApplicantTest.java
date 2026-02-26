package com.dnd.jjigeojulge.reservation.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApplicantTest {

	private Reservation stubReservation;
	private Long stubUserId;
	private Applicant applicant;

	@BeforeEach
	void setUp() {
		// given (스텁 객체와 유저 ID)
		stubReservation = new Reservation();
		stubUserId = 100L;
		// 매 테스트 시작 전 새로운 지원자를 생성합니다.
		applicant = Applicant.create(stubReservation, stubUserId);
	}

	@Test
	@DisplayName("예약 정보가 null이면 지원자를 생성할 수 없다.")
	void create_Fail_NullReservation() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Applicant.create(null, stubUserId))
				.withMessage("예약 정보는 필수입니다.");
	}

	@Test
	@DisplayName("사용자 ID가 null이면 지원자를 생성할 수 없다.")
	void create_Fail_NullUserId() {
		assertThatIllegalArgumentException()
				.isThrownBy(() -> Applicant.create(stubReservation, null))
				.withMessage("지원자 사용자 ID는 필수입니다.");
	}

	@Test
	@DisplayName("지원자를 처음 생성하면 상태는 APPLIED(지원)이다.")
	void create_Applicant_StatusIsApplied() {
		// then
		assertThat(applicant.getStatus()).isEqualTo(ApplicantStatus.APPLIED);
		assertThat(applicant.getReservation()).isEqualTo(stubReservation);
		assertThat(applicant.getUserId()).isEqualTo(stubUserId);
	}

	@Test
	@DisplayName("작성자가 지원자를 선택하면 상태가 SELECTED 로 변경된다.")
	void markAsSelected_Success() {
		// when (작성자가 수락)
		applicant.markAsSelected();

		// then
		assertThat(applicant.getStatus()).isEqualTo(ApplicantStatus.SELECTED);
	}

	@Test
	@DisplayName("작성자가 지원자를 거절(미선택)하면 상태가 REJECTED 로 변경된다.")
	void markAsRejected_Success() {
		// when (작성자가 다른 사람을 선택하여 자동 거절됨)
		applicant.markAsRejected();

		// then
		assertThat(applicant.getStatus()).isEqualTo(ApplicantStatus.REJECTED);
	}

	@Test
	@DisplayName("지원자 본인이 지원을 취소하면 상태가 CANCELED 로 변경된다.")
	void cancelApplication_Success() {
		// when (지원자 본인이 취소)
		applicant.cancelApplication();

		// then
		assertThat(applicant.getStatus()).isEqualTo(ApplicantStatus.CANCELED);
	}

	@Test
	@DisplayName("선택된(SELECTED) 지원자도 예약을 취소하면 CANCELED 로 변경된다.")
	void cancelApplication_Success_When_Selected() {
		// given (상태가 변경됨)
		applicant.markAsSelected();

		// when
		applicant.cancelApplication();

		// then
		assertThat(applicant.getStatus()).isEqualTo(ApplicantStatus.CANCELED);
	}

	@Test
	@DisplayName("이미 거절된(REJECTED) 지원자를 다시 선택하려고 하면 예외가 발생한다.")
	void markAsSelected_Fail_AlreadyRejected() {
		// given (상태가 변경됨)
		applicant.markAsRejected();

		// when & then
		assertThatIllegalStateException()
				.isThrownBy(() -> applicant.markAsSelected())
				.withMessage("지원 대기 중(APPLIED)인 상태에서만 선택할 수 있습니다.");
	}

	@Test
	@DisplayName("거절된(REJECTED) 지원자를 취소하려고 하면 예외가 발생한다.")
	void cancelApplication_Fail_Rejected() {
		// given (상태가 변경됨)
		applicant.markAsRejected();

		// when & then
		assertThatIllegalStateException()
				.isThrownBy(() -> applicant.cancelApplication())
				.withMessage("지원 대기 중(APPLIED)이거나 선택된(SELECTED) 상태일 때만 취소할 수 있습니다.");
	}
}
