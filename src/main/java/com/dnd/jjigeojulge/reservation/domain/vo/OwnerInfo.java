package com.dnd.jjigeojulge.reservation.domain.vo;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerInfo {

	@Column(name = "owner_id", nullable = false)
	private Long userId;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "reservation_photo_style_snapshot", joinColumns = @JoinColumn(name = "reservation_id"))
	@Column(name = "style_name", nullable = false)
	@OrderColumn(name = "style_order")
	private List<String> photoStyleSnapshot = new ArrayList<>();

	private OwnerInfo(Long userId, List<String> photoStyleSnapshot) {
		this.userId = userId;
		this.photoStyleSnapshot = new ArrayList<>(photoStyleSnapshot);
	}

	public static OwnerInfo of(Long userId, List<String> photoStyleSnapshot) {
		validate(userId, photoStyleSnapshot);
		return new OwnerInfo(userId, photoStyleSnapshot);
	}

	private static void validate(Long userId, List<String> photoStyleSnapshot) {
		if (userId == null) {
			throw new IllegalArgumentException("작성자(Owner) ID는 필수입니다.");
		}
		if (photoStyleSnapshot == null || photoStyleSnapshot.isEmpty()) {
			throw new IllegalArgumentException("촬영 유형 스냅샷은 최소 1개 이상 존재해야 합니다.");
		}
	}

	public boolean isOwner(Long userId) {
		return this.userId.equals(userId);
	}
}
