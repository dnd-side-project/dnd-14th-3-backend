package com.dnd.jjigeojulge.reservation.infra;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.dnd.jjigeojulge.config.AppConfig;
import com.dnd.jjigeojulge.global.common.enums.ShootingDurationOption;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.vo.ReservationTitle;
import com.dnd.jjigeojulge.reservation.domain.ReservationComment;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentQueryRepository;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationCommentRepository;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationRepository;
import com.dnd.jjigeojulge.reservation.domain.vo.CommentContent;
import com.dnd.jjigeojulge.reservation.domain.vo.OwnerInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.PlaceInfo;
import com.dnd.jjigeojulge.reservation.domain.vo.RequestMessage;
import com.dnd.jjigeojulge.reservation.domain.vo.ScheduledTime;

@DataJpaTest
@Import({
    ReservationRepositoryImpl.class, 
    ReservationCommentRepositoryImpl.class, 
    ReservationCommentQueryRepositoryImpl.class,
    AppConfig.class
})
class ReservationCommentRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationCommentRepository reservationCommentRepository;

    @Autowired
    private ReservationCommentQueryRepository reservationCommentQueryRepository;

    @Test
    @DisplayName("댓글을 저장하고 조회할 수 있다.")
    void saveAndFind() {
        // given
        Reservation reservation = createReservation();
        reservationRepository.save(reservation);

        ReservationComment comment = ReservationComment.create(
                reservation.getId(),
                1L,
                CommentContent.from("테스트 댓글입니다.")
        );

        // when
        ReservationComment savedComment = reservationCommentRepository.save(comment);
        ReservationComment foundComment = reservationCommentRepository.findById(savedComment.getId()).orElseThrow();

        // then
        assertThat(foundComment.getId()).isNotNull();
        assertThat(foundComment.getContent().getValue()).isEqualTo("테스트 댓글입니다.");
        assertThat(foundComment.getAuthorId()).isEqualTo(1L);
        assertThat(foundComment.getReservationId()).isEqualTo(reservation.getId());
    }

    @Test
    @DisplayName("특정 예약의 모든 댓글을 생성 순으로 조회할 수 있다.")
    void findAllByReservationId() {
        // given
        Reservation reservation = createReservation();
        reservationRepository.save(reservation);

        reservationCommentRepository.save(ReservationComment.create(reservation.getId(), 1L, CommentContent.from("첫 번째 댓글")));
        reservationCommentRepository.save(ReservationComment.create(reservation.getId(), 2L, CommentContent.from("두 번째 댓글")));

        // when
        List<ReservationComment> comments = reservationCommentQueryRepository.findAllByReservationId(reservation.getId());

        // then
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getContent().getValue()).isEqualTo("첫 번째 댓글");
        assertThat(comments.get(1).getContent().getValue()).isEqualTo("두 번째 댓글");
    }

    private Reservation createReservation() {
        OwnerInfo ownerInfo = OwnerInfo.of(1L, List.of("SNS_UPLOAD"));
        LocalDateTime future = LocalDateTime.now().plusDays(1).withMinute(30).withSecond(0).withNano(0);
        ScheduledTime scheduledTime = ScheduledTime.of(future, LocalDateTime.now());
        PlaceInfo placeInfo = PlaceInfo.of("서울특별시", "강남역", 37.4979, 127.0276);
        return Reservation.create(ownerInfo, ReservationTitle.from("테스트 제목"), scheduledTime, placeInfo, ShootingDurationOption.TEN_MINUTES, RequestMessage.from(""));
    }
}
