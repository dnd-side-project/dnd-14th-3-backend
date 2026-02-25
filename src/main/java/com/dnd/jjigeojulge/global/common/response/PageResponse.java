package com.dnd.jjigeojulge.global.common.response;

import java.util.List;

public record PageResponse<T>(
		List<T> content,
		Object nextCursor,
		int size,
		boolean hasNext,
		Long totalElements) {
	public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
		return new PageResponse<>(
				page.getContent(),
				null, // Next cursor is not directly supported by Page, usually offset based parsing
						// is needed or skip
				page.getSize(),
				page.hasNext(),
				page.getTotalElements());
	}
}
