package com.dnd.jjigeojulge.websocket;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.dnd.jjigeojulge.auth.infra.jwt.JwtTokenProvider;
import com.dnd.jjigeojulge.auth.infra.security.CustomUserDetails;
import com.dnd.jjigeojulge.global.exception.BusinessException;
import com.dnd.jjigeojulge.global.exception.ErrorCode;
import com.dnd.jjigeojulge.user.domain.User;
import com.dnd.jjigeojulge.user.exception.UserNotFoundException;
import com.dnd.jjigeojulge.user.infra.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationChannelInterceptor implements ChannelInterceptor {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;
	private final RoleHierarchy roleHierarchy;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			String accessToken = resolveAccessToken(accessor)
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));
			jwtTokenProvider.validateAccessToken(accessToken);
			String userId = jwtTokenProvider.getPayload(accessToken);
			User user = userRepository.findById(Long.parseLong(userId))
				.orElseThrow(UserNotFoundException::new);
			UserDetails userDetails = CustomUserDetails.from(user);

			UsernamePasswordAuthenticationToken auth =
				new UsernamePasswordAuthenticationToken(userDetails, null,
					roleHierarchy.getReachableGrantedAuthorities(userDetails.getAuthorities())
				);
			accessor.setUser(auth);
		}

		return message;
	}

	private Optional<String> resolveAccessToken(StompHeaderAccessor accessor) {
		String prefix = "Bearer ";
		return Optional.ofNullable(accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION))
			.map(value -> {
				if (value.startsWith(prefix)) {
					return value.substring(prefix.length());
				} else {
					return null;
				}
			});
	}
}
