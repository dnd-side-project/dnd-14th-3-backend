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
import com.dnd.jjigeojulge.user.domain.StyleName;

class ReservationTest {

	private OwnerInfo ownerInfo;
	private ScheduledTime scheduledTime;
	private PlaceInfo placeInfo;
	private ShootingDurationOption shootingDuration;
	private RequestMessage requestMessage;

	@BeforeEach
	void setUp() {
		ownerInfo = OwnerInfo.of(1L, List.of(StyleName.SNS_UPLOAD, StyleName.FULL_BODY));
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
		assertThat(reservation.getOwnerInfo().getPhotoStyleSnapshot()).containsExactly(StyleName.SNS_UPLOAD, StyleName.FULL_BODY);
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
	@DisplayName("모집 중(RECRUITING)일 때 예약 정보를 수정할 수 있다.")
	void update_Reservation_Success() {
		// given
		Reservation reservation = Reservation.create(
			ownerInfo, scheduledTime, placeInfo, shootingDuration, requestMessage
		);
		PlaceInfo newPlace = PlaceInfo.of("홍대입구역", 37.5568, 126.9242);
		ShootingDurationOption newDuration = ShootingDurationOption.THIRTY_PLUS_MINUTES;

		// when
		reservation.update(scheduledTime, newPlace, newDuration, requestMessage);

		// then
		assertThat(reservation.getPlaceInfo().getSpecificPlace()).isEqualTo("홍대입구역");
		assertThat(reservation.getShootingDuration()).isEqualTo(ShootingDurationOption.THIRTY_PLUS_MINUTES);
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
			.isThrownBy(() -> reservation.update(scheduledTime, placeInfo, shootingDuration, requestMessage))
			.withMessage("모집 중(RECRUITING)인 상태에서만 예약 정보를 수정할 수 있습니다.");
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
