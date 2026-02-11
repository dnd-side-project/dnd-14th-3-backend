package com.dnd.jjigeojulge.global.exception;

import java.util.List;

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
	public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException exception) {
		log.warn("Validation failed for method argument", exception);
		List<ValidationErrorResponse.FieldErrorItem> fieldErrors = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(ValidationErrorResponse.FieldErrorItem::from)
			.toList();
		ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(fieldErrors);
		return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.getStatus())
			.body(ApiResponse.failure(ErrorCode.VALIDATION_FAILED, validationErrorResponse));
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

/*
TODO validation error example
org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [1] in public org.springframework.http.ResponseEntity<com.dnd.jjigeojulge.global.common.ApiResponse<com.dnd.jjigeojulge.presentation.user.response.ProfileDto>> com.dnd.jjigeojulge.presentation.user.UserController.update(java.lang.Long,com.dnd.jjigeojulge.presentation.user.request.UserUpdateRequest,org.springframework.web.multipart.MultipartFile): [Field error in object 'request' on field 'newUsername': rejected value [a]; codes [Size.request.newUsername,Size.newUsername,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [request.newUsername,newUsername]; arguments []; default message [newUsername],50,2]; default message [사용자 이름은 3자 이상 50자 이하여야 합니다]]
	at org.springframework.web.servlet.mvc.method.annotation.RequestPartMethodArgumentResolver.resolveArgument(RequestPartMethodArgumentResolver.java:148)
	at org.springframework.web.method.support.HandlerMethodArgumentResolverComposite.resolveArgument(HandlerMethodArgumentResolverComposite.java:122)
	at org.springframework.web.method.support.InvocableHandlerMethod.getMethodArgumentValues(InvocableHandlerMethod.java:227)
	at org.springframework.web.method.support.InvocableHandlerMethod.invokeForRequest(InvocableHandlerMethod.java:181)
	at org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod.invokeAndHandle(ServletInvocableHandlerMethod.java:118)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.invokeHandlerMethod(RequestMappingHandlerAdapter.java:991)
	at org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter.handleInternal(RequestMappingHandlerAdapter.java:896)
	at org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter.handle(AbstractHandlerMethodAdapter.java:87)

 */
