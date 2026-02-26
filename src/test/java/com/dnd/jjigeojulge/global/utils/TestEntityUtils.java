package com.dnd.jjigeojulge.global.utils;

import org.springframework.test.util.ReflectionTestUtils;
import com.dnd.jjigeojulge.global.common.entity.BaseEntity;

public class TestEntityUtils {

    /**
     * 리플렉션을 사용하여 엔티티의 ID를 강제로 설정합니다.
     * 엔티티가 BaseEntity를 상속받은 경우에만 동작합니다.
     * 
     * @param <T>    엔티티 타입
     * @param entity ID를 설정할 엔티티 인스턴스
     * @param id     설정할 ID 값
     * @return ID가 설정된 엔티티
     */
    public static <T extends BaseEntity> T setId(T entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
