package com.dnd.jjigeojulge.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.dnd.jjigeojulge.global.common.ApiResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException exception) {
		log.warn("Method argument type mismatch", exception);
		return buildResponseEntity(ErrorCode.INVALID_PARAMETER);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
		ConstraintViolationException exception) {
		log.warn("Constraint violation occurred", exception);
		return buildResponseEntity(ErrorCode.CONSTRAINT_VIOLATION);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException exception) {
		log.warn("Missing required query parameter", exception);
		return buildResponseEntity(ErrorCode.MISSING_PARAMETER);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Object>> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException exception) {
		String method = exception.getMethod();
		log.warn("HTTP Request method '{}' is not supported", method);
		return buildResponseEntity(ErrorCode.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ApiResponse<Object>> handleHttpMediaTypeNotSupportedException(
		HttpMediaTypeNotSupportedException exception) {
		log.warn("Unsupported media type", exception);
		return buildResponseEntity(ErrorCode.UNSUPPORTED_MEDIA_TYPE);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleNoResourceFoundException(NoResourceFoundException exception) {
		log.warn("Resource not found", exception);
		return buildResponseEntity(ErrorCode.RESOURCE_NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidationException(
		MethodArgumentNotValidException exception) {
		log.warn("Validation failed for method argument", exception);
		return buildResponseEntity(ErrorCode.VALIDATION_FAILED);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException exception) {
		log.warn("Business exception occurred: {}", exception.getMessage(), exception);
		return buildResponseEntity(exception.getErrorCode());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception exception) {
		log.error("Unhandled exception occurred", exception);
		return buildResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ApiResponse<Object>> buildResponseEntity(ErrorCode errorCode) {
		return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.failure(errorCode));
	}

}
