package com.dnd.jjigeojulge.auth.presentation.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CookieValue;

import com.dnd.jjigeojulge.auth.presentation.request.SignupRequest;
import com.dnd.jjigeojulge.auth.presentation.response.LoginResponse;
import com.dnd.jjigeojulge.auth.presentation.response.TokenResponse;
import com.dnd.jjigeojulge.global.common.response.ApiResponse;

import jakarta.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "인증 관리", description = "로그인 및 회원가입 API")
public interface AuthApi {

	@Operation(summary = "카카오 로그인", description = """
			카카오 인가 코드를 받아 로그인 또는 회원가입 필요 여부를 반환합니다.
			- `isNewUser: false` -> 기존 회원 (AccessToken 포함, Refresh 토큰은 쿠키 발급)
			- `isNewUser: true` -> 신규 회원 (Register Token 포함, 회원가입 필요)
			""")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공 (기존 회원)", content = @Content(schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "로그인 성공",
					  "code": "",
					  "data": {
					    "isNewUser": false,
					    "tokens": {
					      "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
					    }
					  }
					}
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 필요 (신규 회원)", content = @Content(schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "회원가입이 필요합니다.",
					  "code": "",
					  "data": {
					    "isNewUser": true,
					    "registerToken": "eyJhbGciOiJIUzI1NiJ9..."
					  }
					}
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 인가 코드", content = @Content(schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "소셜 로그인 요청이 올바르지 않습니다.",
					  "code": "INVALID_OAUTH_REQUEST",
					  "data": null
					}
					""")))
	})
	@GetMapping("/login/kakao")
	ResponseEntity<ApiResponse<LoginResponse>> login(
			@Parameter(description = "카카오 인가 코드", required = true) @RequestParam String code,
			HttpServletResponse response);

	@Operation(summary = "회원가입", description = "임시 토큰(Register Token)과 프로필 정보를 받아 회원가입을 완료합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "회원가입 성공",
					  "code": "",
					  "data": {
					    "accessToken": "eyJhbGciOiJIUzI1NiJ9..."
					  }
					}
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 존재하는 닉네임", content = @Content(schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = """
					{
					  "success": false,
					  "message": "이미 존재하는 닉네임입니다.",
					  "code": "INVALID_PARAMETER",
					  "data": null
					}
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (토큰 만료 또는 유효하지 않음)", content = @Content(schema = @Schema(implementation = ApiResponse.class), examples = {
					@ExampleObject(name = "토큰 만료 (재발급 필요)", value = """
							{
							  "success": false,
							  "message": "토큰이 만료되었습니다.",
							  "code": "TOKEN_EXPIRED",
							  "data": null
							}
							"""),
					@ExampleObject(name = "유효하지 않은 토큰 (로그아웃)", value = """
							{
							  "success": false,
							  "message": "유효하지 않은 토큰입니다.",
							  "code": "INVALID_TOKEN",
							  "data": null
							}
							""")
			})) })
	@PostMapping("/signup")
	ResponseEntity<ApiResponse<TokenResponse>> signup(
			@Parameter(description = "회원가입용 임시 토큰", required = true) @RequestHeader("Register-Token") String registerToken,
			@Valid @RequestBody SignupRequest request,
			HttpServletResponse response);

	@Operation(summary = "토큰 재발급", description = "Refresh Token 쿠키로 Access Token을 재발급합니다.")
	@PostMapping("/refresh")
	ResponseEntity<ApiResponse<TokenResponse>> refresh(
			@Parameter(description = "Refresh Token 쿠키", required = true) @CookieValue(name = "refresh_token") String refreshToken,
			HttpServletResponse response);

	@Operation(summary = "세션 검증", description = "현재 사용자의 Access Token이 유효한지 검증합니다.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "세션 유효 기간 정상 (인증됨)"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (만료되거나 유효하지 않은 Access Token)")
	})
	@GetMapping("/verify")
	ResponseEntity<ApiResponse<Void>> verifySession();
}
