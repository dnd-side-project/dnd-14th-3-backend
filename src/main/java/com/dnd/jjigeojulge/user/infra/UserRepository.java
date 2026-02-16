package com.dnd.jjigeojulge.user.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dnd.jjigeojulge.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByNickname(String nickname);

	@Query("select u from User u left join fetch u.userSetting where u.id = :userId")
	Optional<User> findByUserIdWithUserSetting(Long userId);

	@Query("select u from User u " +
		"left join fetch u.photoStyles ups " +
		"left join fetch ups.photoStyle " +
		"left join fetch u.userSetting " +
		"where u.id = :userId")
	Optional<User> findByIdWithPhotoStyles(Long userId);
}
