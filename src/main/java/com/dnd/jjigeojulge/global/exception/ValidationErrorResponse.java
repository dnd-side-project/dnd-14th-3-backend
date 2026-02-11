package com.dnd.jjigeojulge.global.exception;

import java.util.List;

import org.springframework.validation.FieldError;

public record ValidationErrorResponse(
	List<FieldErrorItem> fieldErrors
) {
	public record FieldErrorItem(
		String field,
		String message,
		String code
	) {
		public static FieldErrorItem from(FieldError fieldError) {
			return new FieldErrorItem(
				fieldError.getField(),
				fieldError.getDefaultMessage(),
				fieldError.getCode()
			);
		}
	}
}
