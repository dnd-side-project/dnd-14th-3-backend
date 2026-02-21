package com.dnd.jjigeojulge.reservation.domain.vo;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dnd.jjigeojulge.user.domain.StyleName;

class OwnerInfoTest {

	@Test
	@DisplayName("정상적인 사용자 ID와 스냅샷이 주어지면 OwnerInfo가 생성된다")
	void create_Success() {
		// given
		Long userId = 1L;
		List<StyleName> styles = List.of(StyleName.SNS_UPLOAD, StyleName.FULL_BODY);

		// when
		OwnerInfo ownerInfo = OwnerInfo.of(userId, styles);

		// then
		assertThat(ownerInfo.getUserId()).isEqualTo(userId);
		assertThat(ownerInfo.getPhotoStyleSnapshot()).containsExactly(StyleName.SNS_UPLOAD, StyleName.FULL_BODY);
	}

	@Test
	@DisplayName("사용자 ID가 null이면 예외가 발생한다")
	void create_Fail_NullUserId() {
		// given
		List<StyleName> styles = List.of(StyleName.SNS_UPLOAD);

		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> OwnerInfo.of(null, styles))
			.withMessage("작성자(Owner) ID는 필수입니다.");
	}

	@Test
	@DisplayName("촬영 유형 스냅샷이 null이거나 비어있으면 예외가 발생한다")
	void create_Fail_EmptySnapshot() {
		// given
		Long userId = 1L;

		// when & then
		assertThatIllegalArgumentException()
			.isThrownBy(() -> OwnerInfo.of(userId, null))
			.withMessage("촬영 유형 스냅샷은 최소 1개 이상 존재해야 합니다.");

		assertThatIllegalArgumentException()
			.isThrownBy(() -> OwnerInfo.of(userId, List.of()))
			.withMessage("촬영 유형 스냅샷은 최소 1개 이상 존재해야 합니다.");
	}
}
