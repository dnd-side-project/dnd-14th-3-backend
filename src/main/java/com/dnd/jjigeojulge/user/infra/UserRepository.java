package com.dnd.jjigeojulge.user.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dnd.jjigeojulge.global.common.enums.OAuthProvider;
import com.dnd.jjigeojulge.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByNickname(String nickname);

	@Query("select u from User u left join fetch u.userSetting where u.id = :userId")
	Optional<User> findByUserIdWithUserSetting(@Param("userId") Long userId);

	@Query("select u from User u " +
		"left join fetch u.photoStyles ups " +
		"left join fetch ups.photoStyle " +
		"left join fetch u.userSetting " +
		"where u.id = :userId")
	Optional<User> findByIdWithPhotoStyles(@Param("userId") Long userId);

	@Query("select u from User u where u.oauthInfo.providerId = :providerId and u.oauthInfo.provider = :provider")
	Optional<User> findByOAuthInfo(@Param("providerId") String providerId, @Param("provider") OAuthProvider provider);
}
