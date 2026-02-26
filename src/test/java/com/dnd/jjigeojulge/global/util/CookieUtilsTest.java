package com.dnd.jjigeojulge.global.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.Cookie;

class CookieUtilsTest {

    @Test
    @DisplayName("쿠키를 안전하게(HttpOnly, Secure) 추가할 수 있다.")
    void addCookie() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        CookieUtils.addCookie(response, "test-cookie", "test-value", 3600);

        // then
        Cookie cookie = response.getCookie("test-cookie");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("test-value");
        assertThat(cookie.getMaxAge()).isEqualTo(3600);
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isTrue();
    }

    @Test
    @DisplayName("요청에서 이름으로 쿠키를 가져올 수 있다.")
    void getCookie() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        Cookie cookie = new Cookie("test-cookie", "test-value");
        request.setCookies(cookie);

        // when
        Cookie result = CookieUtils.getCookie(request, "test-cookie").orElse(null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo("test-value");
    }

    @Test
    @DisplayName("요청에 존재하지 않는 쿠키를 가져오면 빈 Optional을 반환한다.")
    void getCookie_NotFound() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other-cookie", "value"));

        // when
        boolean isPresent = CookieUtils.getCookie(request, "test-cookie").isPresent();

        // then
        assertThat(isPresent).isFalse();
    }

    @Test
    @DisplayName("쿠키를 삭제(만료시간 0)할 수 있다.")
    void deleteCookie() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        Cookie cookie = new Cookie("test-cookie", "test-value");
        request.setCookies(cookie);

        // when
        CookieUtils.deleteCookie(request, response, "test-cookie");

        // then
        Cookie deletedCookie = response.getCookie("test-cookie");
        assertThat(deletedCookie).isNotNull();
        assertThat(deletedCookie.getValue()).isEqualTo("");
        assertThat(deletedCookie.getMaxAge()).isEqualTo(0);
        assertThat(deletedCookie.getPath()).isEqualTo("/");
    }
}
