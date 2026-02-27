package com.dnd.jjigeojulge.user.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.dnd.jjigeojulge.global.common.entity.BaseUpdatableEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

	@Embedded
	private OAuthInfo oauthInfo;

	@Column(length = 30, nullable = false, unique = true)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	@Column(name = "age_group", length = 30)
	private AgeGroup ageGroup;

	@Embedded
	private Introduction introduction;

	@Column(length = 512)
	private String profileImageUrl;

	@OneToOne(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
	private UserSetting userSetting;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserPhotoStyle> photoStyles = new HashSet<>();

	@Builder
	public User(OAuthInfo oauthInfo, String nickname, Gender gender, AgeGroup ageGroup, Introduction introduction,
			String profileImageUrl) {
		this.oauthInfo = oauthInfo;
		this.nickname = nickname;
		this.gender = gender;
		this.ageGroup = ageGroup;
		this.introduction = introduction;
		this.profileImageUrl = profileImageUrl;
	}

	public static User create(OAuthInfo oauthInfo, String nickname, Gender gender, AgeGroup ageGroup,
			Introduction introduction, String profileImageUrl, Set<PhotoStyle> styles) {
		User user = User.builder()
				.oauthInfo(oauthInfo)
				.nickname(nickname)
				.gender(gender)
				.ageGroup(ageGroup)
				.introduction(introduction)
				.profileImageUrl(profileImageUrl)
				.build();

		if (styles != null) {
			styles.forEach(user::addPhotoStyle);
		}

		return user;
	}

	// 양방향 동기화
	public void setUserSetting(UserSetting userSetting) {
		this.userSetting = userSetting;
		if (userSetting != null && userSetting.getUser() != this) {
			userSetting.setUser(this);
		}
	}

	public void update(String newNickname, Gender newGender, AgeGroup newAgeGroup, Introduction newIntroduction,
			Set<PhotoStyle> newPhotoStyles) {
		if (newNickname != null && !newNickname.equals(this.nickname)) {
			this.nickname = newNickname;
		}
		if (newGender != null && !newGender.equals(this.gender)) {
			this.gender = newGender;
		}
		if (newAgeGroup != null && !newAgeGroup.equals(this.ageGroup)) {
			this.ageGroup = newAgeGroup;
		}
		if (newIntroduction != null && !newIntroduction.equals(this.introduction)) {
			this.introduction = newIntroduction;
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
