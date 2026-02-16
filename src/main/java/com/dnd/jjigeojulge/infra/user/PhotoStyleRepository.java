package com.dnd.jjigeojulge.infra.user;

import java.util.Collection;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.dnd.jjigeojulge.user.domain.StyleName;

public interface PhotoStyleRepository extends JpaRepository<PhotoStyle, Long> {

	Set<PhotoStyle> findAllByNameIn(Collection<StyleName> names);
}

