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

	public static <T, C> PageResponse<T> fromCursor(org.springframework.data.domain.Page<T> page,
			java.util.function.Function<T, C> cursorExtractor) {
		C nextCursor = null;
		if (page.hasNext() && !page.getContent().isEmpty()) {
			nextCursor = cursorExtractor.apply(page.getContent().get(page.getContent().size() - 1));
		}

		return new PageResponse<>(
				page.getContent(),
				nextCursor,
				page.getSize(),
				page.hasNext(),
				page.getTotalElements());
	}
}
