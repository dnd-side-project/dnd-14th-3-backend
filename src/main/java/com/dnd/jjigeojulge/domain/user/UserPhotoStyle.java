package com.dnd.jjigeojulge.domain.user;

import com.dnd.jjigeojulge.domain.photostyle.PhotoStyle;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "user_photo_styles",
	uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "photo_style_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPhotoStyle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_style_id", nullable = false)
	private PhotoStyle photoStyle;

	public UserPhotoStyle(User user, PhotoStyle photoStyle) {
		this.user = user;
		this.photoStyle = photoStyle;
	}

	public static UserPhotoStyle of(User user, PhotoStyle photoStyle) {
		return new UserPhotoStyle(user, photoStyle);
	}

}
