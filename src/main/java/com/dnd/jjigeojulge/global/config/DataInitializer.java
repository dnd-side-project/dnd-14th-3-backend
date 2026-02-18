package com.dnd.jjigeojulge.global.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.StyleName;
import com.dnd.jjigeojulge.user.infra.PhotoStyleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("local")
public class DataInitializer {

	private final PhotoStyleRepository photoStyleRepository;

	@Bean
	public CommandLineRunner initData() {
		return args -> {
			initializePhotoStyles();
		};
	}

	@Transactional
	public void initializePhotoStyles() {
		if (photoStyleRepository.count() > 0) {
			return; // 이미 데이터가 있으면 패스
		}

		log.info("Initializing PhotoStyle data...");
		Arrays.stream(StyleName.values())
			.forEach(styleName -> {
				photoStyleRepository.save(new PhotoStyle(styleName));
			});
		log.info("PhotoStyle data initialized. Count: {}", photoStyleRepository.count());
	}
}
