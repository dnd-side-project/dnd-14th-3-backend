package com.dnd.jjigeojulge.notification.infra;

import static com.dnd.jjigeojulge.notification.domain.QNotification.notification;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.dnd.jjigeojulge.notification.application.dto.NotificationResponseDto;
import com.dnd.jjigeojulge.notification.application.dto.QNotificationResponseDto;
import com.dnd.jjigeojulge.notification.domain.repository.NotificationQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {

        private final JPAQueryFactory queryFactory;

        @Override
        public Page<NotificationResponseDto> getNotifications(Long receiverId, Long cursor, int limit) {
                List<NotificationResponseDto> content = queryFactory
                                .select(new QNotificationResponseDto(
                                                notification.id,
                                                notification.type,
                                                notification.message.value,
                                                notification.relatedUrl.value,
                                                notification.isRead,
                                                notification.createdAt))
                                .from(notification)
                                .where(
                                                notification.receiverId.eq(receiverId),
                                                cursor != null ? notification.id.lt(cursor) : null)
                                .orderBy(notification.id.desc())
                                .limit(limit)
                                .fetch();

                Long totalCount = queryFactory
                                .select(notification.count())
                                .from(notification)
                                .where(notification.receiverId.eq(receiverId))
                                .fetchOne();

                return new PageImpl<>(content, PageRequest.of(0, limit), totalCount != null ? totalCount : 0L);
        }
}
