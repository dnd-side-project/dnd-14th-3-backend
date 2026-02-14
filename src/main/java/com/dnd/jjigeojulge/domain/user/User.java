package com.dnd.jjigeojulge.domain.user;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.dnd.jjigeojulge.domain.base.BaseUpdatableEntity;
import com.dnd.jjigeojulge.domain.photostyle.PhotoStyle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

	@Column(length = 30, nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false, unique = true)
	private String kakaoUserEmail;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Gender gender;

	@Column(length = 512)
	private String profileImageUrl;

	@Column(length = 20)
	private String phoneNumber;

	@OneToOne(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
	private UserSetting userSetting;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserPhotoStyle> photoStyles = new HashSet<>();

	@Builder
	public User(String nickname, String kakaoUserEmail, Gender gender, String profileImageUrl, String phoneNumber) {
		this.nickname = nickname;
		this.kakaoUserEmail = kakaoUserEmail;
		this.gender = gender;
		this.profileImageUrl = profileImageUrl;
		this.phoneNumber = phoneNumber;
	}

	// 양방향 동기화
	public void setUserSetting(UserSetting userSetting) {
		this.userSetting = userSetting;
		if (userSetting != null && userSetting.getUser() != this) {
			userSetting.setUser(this);
		}
	}

	public void update(String newNickname, Gender newGender, Set<PhotoStyle> newPhotoStyles) {
		if (newNickname != null && !newNickname.equals(this.nickname)) {
			this.nickname = newNickname;
		}
		if (newGender != null && !newGender.equals(this.gender)) {
			this.gender = newGender;
		}

		this.photoStyles.removeIf(ups -> !newPhotoStyles.contains(ups.getPhotoStyle()));

		// 현재 유저가 유지하고 있는 PhotoStyle 목록 추출
		Set<PhotoStyle> currentStyles = this.photoStyles.stream()
			.map(UserPhotoStyle::getPhotoStyle)
			.collect(Collectors.toSet());

		// 새로운 목록 중 기존에 없는 것만 추가
		newPhotoStyles.stream()
			.filter(ps -> !currentStyles.contains(ps))
			.forEach(this::addPhotoStyle);
	}

	private void addPhotoStyle(PhotoStyle photoStyle) {
		this.photoStyles.add(UserPhotoStyle.of(this, photoStyle));
	}
}
