package com.dnd.jjigeojulge.user.presentation.validation;

import java.util.Arrays;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NicknameValidator implements ConstraintValidator<Nickname, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// null/blank 처리는 @NotBlank 쪽으로 위임 (역할 분리)
		if (value == null) {
			return true;
		}

		String inputNickname = value.trim();
		if (inputNickname.isEmpty()) {
			return true;
		}

		// 길이/문자셋 검증 (한글/영문/숫자, MIN~MAX)
		if (!NicknamePolicy.ALLOWED_PATTERN.matcher(inputNickname).matches()) {
			addConstraintViolation(context,
				"닉네임은 " + NicknamePolicy.MIN_LENGTH + "~" + NicknamePolicy.MAX_LENGTH
					+ "자이며 한글, 영문, 숫자만 사용할 수 있습니다.");
			return false;
		}

		// 숫자로만 구성된 닉네임 금지
		if (NicknamePolicy.NUMBER_ONLY_PATTERN.matcher(inputNickname).matches()) {
			addConstraintViolation(context, "숫자로만 구성된 닉네임은 사용할 수 없습니다.");
			return false;
		}

		// 금지 단어 포함 여부 (대소문자 무시)
		String lower = inputNickname.toLowerCase();
		boolean containsForbidden = Arrays.stream(NicknamePolicy.FORBIDDEN_WORDS)
			.anyMatch(lower::contains);

		if (containsForbidden) {
			addConstraintViolation(context, "사용할 수 없는 단어가 포함되어 있습니다.");
			return false;
		}

		return true;
	}

	private void addConstraintViolation(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation(); // 기본 메시지 비활성화
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation(); // 커스텀 메시지 설정
	}
}
