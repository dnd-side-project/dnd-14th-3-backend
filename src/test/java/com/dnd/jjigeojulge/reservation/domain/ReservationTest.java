package com.dnd.jjigeojulge.reservation.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;
import com.dnd.jjigeojulge.reservation.domain.vo.ReservationTitle;

class ReservationTest {

	private OwnerInfo ownerInfo;
	private ScheduledTime scheduledTime;
	private PlaceInfo placeInfo;
	private ShootingDurationOption shootingDuration;
	private RequestMessage requestMessage;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.of(2026, 2, 23, 12, 0); // 기준 시간: 12:00
		ownerInfo = OwnerInfo.of(1L, List.of("SNS_UPLOAD", "FULL_BODY"));

		// 1시간 뒤인 13:00으로 예약 설정 (30분 단위 준수)
		LocalDateTime future = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);
		scheduledTime = ScheduledTime.of(future, now);
		placeInfo = PlaceInfo.of("서울특별시", "강남역", 37.4979, 127.0276);
		shootingDuration = ShootingDurationOption.TWENTY_MINUTES;
		requestMessage = RequestMessage.from("사진 이쁘게 찍어주세요");
	}

	private void forceChangeStatus(Reservation reservation, ReservationStatus status) {
		try {
			java.lang.reflect.Field statusField = Reservation.class.getDeclaredField("status");
			statusField.setAccessible(true);
			statusField.set(reservation, status);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	@DisplayName("올바른 정보로 예약(Reservation)을 생성하면 RECRUITING 상태가 된다.")
	void create_Reservation_Success() {
		// when
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration, requestMessage);

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RECRUITING);
		assertThat(reservation.getOwnerInfo().getUserId()).isEqualTo(1L);
		assertThat(reservation.getOwnerInfo().getPhotoStyleSnapshot()).containsExactly("SNS_UPLOAD", "FULL_BODY");
	}

	@Test
	@DisplayName("모집 중(RECRUITING)일 때 작성자 본인이 예약 정보를 수정할 수 있다.")
	void update_Reservation_Success() {
		// given
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		PlaceInfo newPlace = PlaceInfo.of("서울특별시", "홍대입구역", 37.5568, 126.9242);
		ShootingDurationOption newDuration = ShootingDurationOption.THIRTY_PLUS_MINUTES;

		// when
		reservation.update(ownerInfo.getUserId(), ReservationTitle.from("수정된 제목"), scheduledTime, newPlace, newDuration,
				requestMessage, now);

		// then
		assertThat(reservation.getPlaceInfo().getSpecificPlace()).isEqualTo("홍대입구역");
		assertThat(reservation.getShootingDuration()).isEqualTo(ShootingDurationOption.THIRTY_PLUS_MINUTES);
	}

	@Test
	@DisplayName("예약 시간이 이미 지난 경우(Expired) 수정을 시도하면 예외가 발생한다.")
	void update_Reservation_Fail_Expired() {
		// given
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		LocalDateTime expiredTime = now.plusHours(2); // 예약 시간(13:00)보다 늦은 14:00

		// when & then
		assertThatIllegalStateException()
				.isThrownBy(() -> reservation.update(ownerInfo.getUserId(), ReservationTitle.from("수정된 제목"),
						scheduledTime, placeInfo, shootingDuration,
						requestMessage, expiredTime))
				.withMessage("모집 기간이 지난 예약 정보는 수정할 수 없습니다.");
	}

	@Test
	@DisplayName("모집 중(RECRUITING)인 예약에 지원할 수 있다.")
	void apply_Success() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);

		reservation.apply(applicant, now);

		assertThat(reservation.getApplicants()).hasSize(1);
	}

	@Test
	@DisplayName("예약 시간이 지난 경우 지원할 수 없다.")
	void apply_Fail_Expired() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);
		LocalDateTime expiredTime = now.plusHours(2);

		assertThatIllegalStateException()
				.isThrownBy(() -> reservation.apply(applicant, expiredTime))
				.withMessage("모집 기간이 지난 예약에는 지원할 수 없습니다.");
	}

	@Test
	@DisplayName("예약을 취소하면 관련된 지원자(APPLIED, SELECTED)의 상태도 CANCELED로 변경된다.")
	void cancel_Reservation_Changes_Applicants_State() throws Exception {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant1 = Applicant.create(reservation, 2L); // APPLIED
		Applicant applicant2 = Applicant.create(reservation, 3L); // SELECTED
		Applicant applicant3 = Applicant.create(reservation, 4L); // REJECTED

		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class
				.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant1, 10L);
		idField.set(applicant2, 20L);
		idField.set(applicant3, 30L);

		reservation.apply(applicant1, now);
		reservation.apply(applicant2, now);
		reservation.apply(applicant3, now);

		// applicant2 수락 (applicant2: SELECTED, applicant1&3: REJECTED가 됨)
		reservation.acceptApplicant(ownerInfo.getUserId(), 20L, now);

		// 이제 확정된 상태에서 예약 취소
		reservation.cancel(ownerInfo.getUserId(), now);

		// SELECTED 였던 applicant2는 CANCELED가 되어야 함
		assertThat(applicant2.getStatus()).isEqualTo(ApplicantStatus.CANCELED);
		// REJECTED 였던 1,3 은 그대로 REJECTED 여야 함
		assertThat(applicant1.getStatus()).isEqualTo(ApplicantStatus.REJECTED);
		assertThat(applicant3.getStatus()).isEqualTo(ApplicantStatus.REJECTED);
	}

	@Test
	@DisplayName("지원자 수락 시 선택된 지원자는 SELECTED, 나머지는 REJECTED 상태가 되며 예약은 CONFIRMED 된다.")
	void acceptApplicant_Success() throws Exception {
		// given
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant1 = Applicant.create(reservation, 2L);
		Applicant applicant2 = Applicant.create(reservation, 3L);

		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class
				.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant1, 10L);
		idField.set(applicant2, 20L);

		reservation.apply(applicant1, now);
		reservation.apply(applicant2, now);

		// when
		reservation.acceptApplicant(ownerInfo.getUserId(), 10L, now);

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
		assertThat(applicant1.getStatus()).isEqualTo(ApplicantStatus.SELECTED);
		assertThat(applicant2.getStatus()).isEqualTo(ApplicantStatus.REJECTED);
	}

	@Test
	@DisplayName("작성자 본인이 모집 중(RECRUITING)일 때 예약을 취소할 수 있다.")
	void cancel_Reservation_Success() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);

		reservation.cancel(ownerInfo.getUserId(), now);

		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
	}

	@Test
	@DisplayName("예약 시간이 지난 후에는 취소할 수 없다.")
	void cancel_Reservation_Fail_Expired() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		LocalDateTime expiredTime = now.plusHours(2);

		assertThatIllegalStateException()
				.isThrownBy(() -> reservation.cancel(ownerInfo.getUserId(), expiredTime))
				.withMessage("모집 기간이 지난 예약은 취소할 수 없습니다.");
	}

	@Test
	@DisplayName("확정됨(CONFIRMED) 상태인 예약을 예약 시간이 지난 후 완료(COMPLETED) 처리할 수 있다.")
	void complete_Reservation_Success() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		forceChangeStatus(reservation, ReservationStatus.CONFIRMED);
		LocalDateTime afterAppointment = now.plusHours(2);

		reservation.complete(afterAppointment);

		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.COMPLETED);
	}

	@Test
	@DisplayName("예약 시간이 지나기 전에 완료 처리하려 하면 예외가 발생한다.")
	void complete_Reservation_Fail_Before_Appointment() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		forceChangeStatus(reservation, ReservationStatus.CONFIRMED);

		assertThatIllegalStateException()
				.isThrownBy(() -> reservation.complete(now))
				.withMessage("약속 시간이 지나기 전에는 완료 처리할 수 없습니다.");
	}

	@Test
	@DisplayName("방장(Owner)은 특정 지원자를 단건으로 거절할 수 있다.")
	void rejectApplicant_Success() throws Exception {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);

		// Reflection으로 ID 세팅
		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class
				.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant, 10L);

		reservation.apply(applicant, now);

		reservation.rejectApplicant(ownerInfo.getUserId(), 10L, now);

		assertThat(applicant.getStatus()).isEqualTo(ApplicantStatus.REJECTED);
	}

	@Test
	@DisplayName("방장(Owner)이 아닌 사람이 특정 지원자를 거절하려 하면 예외가 발생한다.")
	void rejectApplicant_Fail_Not_Owner() throws Exception {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);

		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class
				.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant, 10L);

		reservation.apply(applicant, now);

		assertThatIllegalArgumentException()
				.isThrownBy(() -> reservation.rejectApplicant(999L, 10L, now))
				.withMessage("예약 작성자 본인만 지원자를 거절할 수 있습니다.");
	}

	@Test
	@DisplayName("확정된 예약(CONFIRMED)을 취소하려 할 때 작성자가 아니면 예외가 발생한다.")
	void cancel_Reservation_Fail_Not_Owner_When_Confirmed() throws Exception {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);

		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class
				.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant, 10L);

		reservation.apply(applicant, now);
		reservation.acceptApplicant(ownerInfo.getUserId(), 10L, now);

		assertThatIllegalArgumentException()
				.isThrownBy(() -> reservation.cancel(2L, now))
				.withMessage("예약은 작성자 본인만 취소할 수 있습니다.");
	}

	@Test
	@DisplayName("모집 중(RECRUITING)일 때 지원자는 본인의 지원을 취소할 수 있다.")
	void cancelApplication_Success() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);
		reservation.apply(applicant, now);

		reservation.cancelApplication(2L, now);

		assertThat(applicant.getStatus()).isEqualTo(ApplicantStatus.CANCELED);
	}

	@Test
	@DisplayName("모집 중(RECRUITING)이 아닌 상태에서는 지원을 취소할 수 없다.")
	void cancelApplication_Fail_Not_Recruiting() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);
		reservation.apply(applicant, now);
		forceChangeStatus(reservation, ReservationStatus.CONFIRMED);

		assertThatIllegalStateException()
				.isThrownBy(() -> reservation.cancelApplication(2L, now))
				.withMessage("모집 중(RECRUITING)인 상태에서만 지원을 취소할 수 있습니다.");
	}

	@Test
	@DisplayName("지원 내역이 없는 사용자가 지원 취소를 시도하면 예외가 발생한다.")
	void cancelApplication_Fail_Not_Applied() {
		Reservation reservation = Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime,
				placeInfo, shootingDuration,
				requestMessage);

		assertThatIllegalArgumentException()
				.isThrownBy(() -> reservation.cancelApplication(2L, now))
				.withMessage("취소할 지원 내역이 없습니다.");
	}
}
