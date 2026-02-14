package com.dnd.jjigeojulge.domain.photostyle;

import com.dnd.jjigeojulge.domain.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photo_styles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhotoStyle extends BaseEntity {

	@Column(name = "name", length = 50, nullable = false, unique = true)
	@Enumerated(EnumType.STRING)
	private StyleName name;

	public PhotoStyle(StyleName name) {
		this.name = name;
	}
}
