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

class ReservationTest {

	private OwnerInfo ownerInfo;
	private ScheduledTime scheduledTime;
	private PlaceInfo placeInfo;
	private ShootingDurationOption shootingDuration;
	private RequestMessage requestMessage;

	@BeforeEach
	void setUp() {
		ownerInfo = OwnerInfo.of(1L, List.of("SNS_UPLOAD", "FULL_BODY"));
		LocalDateTime future = LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0);
		scheduledTime = ScheduledTime.of(future, LocalDateTime.now());
		placeInfo = PlaceInfo.of("강남역", 37.4979, 127.0276);
		shootingDuration = ShootingDurationOption.TWENTY_MINUTES;
		requestMessage = RequestMessage.from("사진 이쁘게 찍어주세요");
	}

	@Test
	@DisplayName("올바른 정보로 예약(Reservation)을 생성하면 RECRUITING 상태가 된다.")
	void create_Reservation_Success() {
		// when
		Reservation reservation = Reservation.create(
			ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage
		);

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.RECRUITING);
		assertThat(reservation.getOwnerInfo().getUserId()).isEqualTo(1L);
		assertThat(reservation.getOwnerInfo().getPhotoStyleSnapshot()).containsExactly("SNS_UPLOAD", "FULL_BODY");
	}

	@Test
	@DisplayName("ownerInfo가 null이면 예약 생성 시 예외가 발생한다.")
	void create_Reservation_Fail_NullOwnerInfo() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Reservation.create(
				null, scheduledTime, placeInfo, shootingDuration, requestMessage
			))
			.withMessage("작성자(Owner) 정보는 필수입니다.");
	}

	@Test
	@DisplayName("예약 시간 정보가 null이면 예약 생성 시 예외가 발생한다.")
	void create_Reservation_Fail_NullScheduledTime() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Reservation.create(
				ownerInfo, null, placeInfo, shootingDuration, requestMessage
			))
			.withMessage("예약 시간 정보는 필수입니다.");
	}

	@Test
	@DisplayName("장소 정보가 null이면 예약 생성 시 예외가 발생한다.")
	void create_Reservation_Fail_NullPlaceInfo() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Reservation.create(
				ownerInfo, scheduledTime, null, shootingDuration, requestMessage
			))
			.withMessage("장소 정보는 필수입니다.");
	}

	@Test
	@DisplayName("촬영 소요 시간 옵션이 null이면 예약 생성 시 예외가 발생한다.")
	void create_Reservation_Fail_NullShootingDuration() {
		assertThatIllegalArgumentException()
			.isThrownBy(() -> Reservation.create(
				ownerInfo, scheduledTime, placeInfo, null, requestMessage
			))
			.withMessage("촬영 소요 시간 옵션은 필수입니다.");
	}

	@Test
	@DisplayName("모집 중(RECRUITING)일 때 작성자 본인이 예약 정보를 수정할 수 있다.")
	void update_Reservation_Success() {
		// given
		Reservation reservation = Reservation.create(
			ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage
		);
		PlaceInfo newPlace = PlaceInfo.of("홍대입구역", 37.5568, 126.9242);
		ShootingDurationOption newDuration = ShootingDurationOption.THIRTY_PLUS_MINUTES;

		// when
		reservation.update(ownerInfo.getUserId(), scheduledTime, newPlace, newDuration, requestMessage);

		// then
		assertThat(reservation.getPlaceInfo().getSpecificPlace()).isEqualTo("홍대입구역");
		assertThat(reservation.getShootingDuration()).isEqualTo(ShootingDurationOption.THIRTY_PLUS_MINUTES);
	}

	@Test
	@DisplayName("작성자 본인이 아니면 예약 정보를 수정할 수 없다.")
	void update_Reservation_Fail_NotOwner() {
		Reservation reservation = Reservation.create(
			ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage
		);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> reservation.update(999L, scheduledTime, placeInfo, shootingDuration, requestMessage))
			.withMessage("예약 작성자 본인만 예약 정보를 수정할 수 있습니다.");
	}

	@Test
	@DisplayName("모집 중(RECRUITING)이 아닐 때 수정하려고 하면 예외가 발생한다.")
	void update_Reservation_Fail_NotRecruiting() {
		// given
		Reservation reservation = Reservation.create(
			ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage
		);
		reservation.changeStatusToConfirmed(); // 강제 상태 변경 (테스트용)

		// when & then
		assertThatIllegalStateException()
			.isThrownBy(() -> reservation.update(ownerInfo.getUserId(), scheduledTime, placeInfo, shootingDuration, requestMessage))
			.withMessage("모집 중(RECRUITING)인 상태에서만 예약 정보를 수정할 수 있습니다.");
	}

	@Test
	@DisplayName("수정 시 예약 시간 정보가 null이면 예외가 발생한다.")
	void update_Reservation_Fail_NullScheduledTime() {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> reservation.update(ownerInfo.getUserId(), null, placeInfo, shootingDuration, requestMessage))
			.withMessage("예약 시간 정보는 필수입니다.");
	}

	@Test
	@DisplayName("수정 시 장소 정보가 null이면 예외가 발생한다.")
	void update_Reservation_Fail_NullPlaceInfo() {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> reservation.update(ownerInfo.getUserId(), scheduledTime, null, shootingDuration, requestMessage))
			.withMessage("장소 정보는 필수입니다.");
	}

	@Test
	@DisplayName("수정 시 촬영 소요 시간 옵션이 null이면 예외가 발생한다.")
	void update_Reservation_Fail_NullShootingDuration() {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> reservation.update(ownerInfo.getUserId(), scheduledTime, placeInfo, null, requestMessage))
			.withMessage("촬영 소요 시간 옵션은 필수입니다.");
	}

	@Test
	@DisplayName("모집 중(RECRUITING)일 때 작성자 본인이 예약을 취소할 수 있다.")
	void cancel_Reservation_Success() {
		// given
		Reservation reservation = Reservation.create(
			ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage
		);

		// when
		reservation.cancel(ownerInfo.getUserId());

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
	}

	@Test
	@DisplayName("모집 중(RECRUITING)인 예약에 지원할 수 있다.")
	void apply_Success() {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);

		reservation.apply(applicant);

		assertThat(reservation.getApplicants()).hasSize(1);
	}

	@Test
	@DisplayName("자신의 예약에는 지원할 수 없다.")
	void apply_Fail_OwnReservation() {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);
		Applicant applicant = Applicant.create(reservation, ownerInfo.getUserId());

		assertThatIllegalArgumentException()
			.isThrownBy(() -> reservation.apply(applicant))
			.withMessage("자신의 예약에는 지원할 수 없습니다.");
	}

	@Test
	@DisplayName("이미 지원한 예약에 중복 지원할 수 없다.")
	void apply_Fail_AlreadyApplied() {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);
		Applicant applicant1 = Applicant.create(reservation, 2L);
		Applicant applicant2 = Applicant.create(reservation, 2L);

		reservation.apply(applicant1);

		assertThatIllegalStateException()
			.isThrownBy(() -> reservation.apply(applicant2))
			.withMessage("이미 지원한 예약입니다.");
	}

	@Test
	@DisplayName("지원자 수락 시 선택된 지원자는 SELECTED, 나머지는 REJECTED 상태가 되며 예약은 CONFIRMED 된다.")
	void acceptApplicant_Success() throws Exception {
		// given
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);
		Applicant applicant1 = Applicant.create(reservation, 2L);
		Applicant applicant2 = Applicant.create(reservation, 3L);
		
		// ID 강제 주입 (리플렉션)
		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant1, 10L);
		idField.set(applicant2, 20L);

		reservation.apply(applicant1);
		reservation.apply(applicant2);

		// when
		reservation.acceptApplicant(ownerInfo.getUserId(), 10L);

		// then
		assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
		assertThat(applicant1.getStatus()).isEqualTo(ApplicantStatus.SELECTED);
		assertThat(applicant2.getStatus()).isEqualTo(ApplicantStatus.REJECTED);
	}

	@Test
	@DisplayName("작성자 본인이 아니면 지원자를 수락할 수 없다.")
	void acceptApplicant_Fail_NotOwner() throws Exception {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);
		
		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant, 10L);
		
		reservation.apply(applicant);

		assertThatIllegalArgumentException()
			.isThrownBy(() -> reservation.acceptApplicant(999L, 10L))
			.withMessage("예약 작성자 본인만 지원자를 수락할 수 있습니다.");
	}

	@Test
	@DisplayName("모집 중(RECRUITING) 상태가 아니면 지원자를 수락할 수 없다.")
	void acceptApplicant_Fail_NotRecruiting() throws Exception {
		Reservation reservation = Reservation.create(ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage);
		Applicant applicant = Applicant.create(reservation, 2L);
		
		java.lang.reflect.Field idField = com.dnd.jjigeojulge.global.common.entity.BaseEntity.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(applicant, 10L);
		
		reservation.apply(applicant);
		reservation.changeStatusToConfirmed(); // 강제 상태 변경

		assertThatIllegalStateException()
			.isThrownBy(() -> reservation.acceptApplicant(ownerInfo.getUserId(), 10L))
			.withMessage("모집 중(RECRUITING)인 상태에서만 지원자를 수락할 수 있습니다.");
	}

	@Test
	@DisplayName("작성자 본인이 아닌 사람이 취소를 요청하면 예외가 발생한다.")
	void cancel_Reservation_Fail_NotOwner() {
		// given
		Reservation reservation = Reservation.create(
			ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage
		);
		Long otherUserId = 999L;

		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> reservation.cancel(otherUserId))
			.withMessage("예약 작성자 본인만 예약을 취소할 수 있습니다.");
	}
}
