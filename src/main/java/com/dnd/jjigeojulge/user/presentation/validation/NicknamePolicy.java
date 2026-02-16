package com.dnd.jjigeojulge.user.presentation.validation;

import java.util.regex.Pattern;

public final class NicknamePolicy {

	private NicknamePolicy() {
	}

	public static final int MIN_LENGTH = 2;
	public static final int MAX_LENGTH = 10;

	/**
	 * 한글(가-힣), 영문(a-zA-Z), 숫자(0-9)만 허용
	 * - 공백/특수문자/이모지/자모(ㄱ-ㅎ,ㅏ-ㅣ) 단독은 불허
	 */
	public static final Pattern ALLOWED_PATTERN =
		Pattern.compile("^[가-힣a-zA-Z0-9]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$");

	public static final Pattern NUMBER_ONLY_PATTERN =
		Pattern.compile("^[0-9]{" + MIN_LENGTH + "," + MAX_LENGTH + "}$");

	public static final String[] FORBIDDEN_WORDS = {
		"운영자",
		"admin",
		"관리자",
		"system",
		"시스템"
	};
}
