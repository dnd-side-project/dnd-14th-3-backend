package com.dnd.jjigeojulge.reservation.infra;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationCommentDto;
import com.dnd.jjigeojulge.reservation.domain.ReservationComment;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentQueryRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import static com.dnd.jjigeojulge.reservation.domain.QReservationComment.reservationComment;
import static com.dnd.jjigeojulge.user.domain.QUser.user;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationCommentQueryRepositoryImpl implements ReservationCommentQueryRepository {

    private final JpaReservationCommentRepository jpaReservationCommentRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReservationComment> findAllByReservationId(Long reservationId) {
        return jpaReservationCommentRepository.findAllByReservationIdOrderByCreatedAtAsc(reservationId);
    }

    @Override
    public Page<ReservationCommentDto> getReservationComments(Long reservationId, Long cursor, int limit) {
        List<Tuple> results = queryFactory
                .select(reservationComment, user.nickname, user.profileImageUrl)
                .from(reservationComment)
                .leftJoin(user).on(reservationComment.authorId.eq(user.id))
                .where(
                        reservationComment.reservationId.eq(reservationId),
                        cursor != null ? reservationComment.id.lt(cursor) : null)
                .orderBy(reservationComment.id.desc())
                .limit(limit)
                .fetch();

        List<ReservationCommentDto> content = results.stream().map(tuple -> {
            ReservationComment comment = tuple.get(reservationComment);
            String nickname = tuple.get(user.nickname);
            String profileImageUrl = tuple.get(user.profileImageUrl);

            return new ReservationCommentDto(
                    comment.getId(),
                    comment.getReservationId(),
                    comment.getAuthorId(),
                    nickname,
                    profileImageUrl,
                    comment.getContent().getValue(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt());
        }).toList();

        Long totalCount = queryFactory
                .select(reservationComment.count())
                .from(reservationComment)
                .where(reservationComment.reservationId.eq(reservationId))
                .fetchOne();

        return new PageImpl<>(content, PageRequest.of(0, limit), totalCount != null ? totalCount : 0L);
    }
}
