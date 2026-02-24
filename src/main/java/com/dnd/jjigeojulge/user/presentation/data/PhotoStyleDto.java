package com.dnd.jjigeojulge.user.presentation.data;

import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.StyleName;

public record PhotoStyleDto(
	Long id,
	StyleName name,
	String label
) {

	public static PhotoStyleDto from(PhotoStyle entity) {
		return new PhotoStyleDto(
			entity.getId(),
			entity.getName(),
			entity.getName().getLabel()
		);
	}
}
