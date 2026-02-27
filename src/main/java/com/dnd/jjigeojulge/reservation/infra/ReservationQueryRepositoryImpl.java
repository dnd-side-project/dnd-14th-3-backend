package com.dnd.jjigeojulge.reservation.infra;

import static com.dnd.jjigeojulge.reservation.domain.QApplicant.applicant;
import static com.dnd.jjigeojulge.reservation.domain.QReservation.reservation;
import static com.dnd.jjigeojulge.reservation.domain.QReservationComment.reservationComment;
import static com.dnd.jjigeojulge.user.domain.QUser.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.dnd.jjigeojulge.reservation.application.dto.query.AppliedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.CreatedReservationListDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationDetailDto;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSearchCondition;
import com.dnd.jjigeojulge.reservation.application.dto.query.ReservationSummaryDto;
import com.dnd.jjigeojulge.reservation.domain.Reservation;
import com.dnd.jjigeojulge.reservation.domain.ReservationStatus;
import com.dnd.jjigeojulge.reservation.domain.repository.ReservationQueryRepository;
import com.dnd.jjigeojulge.reservation.domain.vo.Region1Depth;
import com.dnd.jjigeojulge.user.domain.Gender;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationQueryRepositoryImpl implements ReservationQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<ReservationSummaryDto> searchReservations(ReservationSearchCondition condition, Long cursor,
			int limit) {
		List<ReservationSummaryDto> content = queryFactory
				.select(new com.dnd.jjigeojulge.reservation.application.dto.query.QReservationSummaryDto(
						reservation.id,
						reservation.title.value,
						reservation.scheduledTime.time,
						reservation.placeInfo.region1Depth,
						reservation.placeInfo.specificPlace,
						reservation.shootingDuration,
						reservation.status,
						reservation.ownerInfo.userId,
						user.nickname,
						user.gender,
						user.profileImageUrl))
				.from(reservation)
				.leftJoin(user).on(reservation.ownerInfo.userId.eq(user.id))
				.where(
						region1DepthEq(condition.region1Depth()),
						scheduledAtEq(condition.date()),
						genderEq(condition.gender()),
						keywordContains(condition.keyword()),
						reservation.status.in(ReservationStatus.RECRUITING, ReservationStatus.CONFIRMED),
						cursor != null ? reservation.id.lt(cursor) : null)
				.orderBy(reservation.id.desc())
				.limit(limit)
				.fetch();

		Long totalCount = queryFactory
				.select(reservation.count())
				.from(reservation)
				.leftJoin(user).on(reservation.ownerInfo.userId.eq(user.id))
				.where(
						region1DepthEq(condition.region1Depth()),
						scheduledAtEq(condition.date()),
						genderEq(condition.gender()),
						keywordContains(condition.keyword()),
						reservation.status.in(ReservationStatus.RECRUITING, ReservationStatus.CONFIRMED))
				.fetchOne();

		return new PageImpl<>(content, PageRequest.of(0, limit), totalCount != null ? totalCount : 0L);
	}

	@Override
	public Page<CreatedReservationListDto> getMyCreatedReservations(Long ownerId, Long cursor, int limit) {
		LocalDateTime now = LocalDateTime.now();
		NumberPath<Long> applicantCountPath = Expressions.numberPath(Long.class, "applicantCount");

		List<Tuple> results = queryFactory
				.select(
						reservation,
						ExpressionUtils.as(JPAExpressions.select(applicant.count())
								.from(applicant)
								.where(applicant.reservation.id.eq(reservation.id)), applicantCountPath))
				.from(reservation)
				.where(
						reservation.ownerInfo.userId.eq(ownerId),
						cursor != null ? reservation.id.lt(cursor) : null)
				.orderBy(reservation.id.desc())
				.limit(limit)
				.fetch();

		List<CreatedReservationListDto> content = results.stream().map(tuple -> {
			Reservation r = tuple.get(reservation);
			Long applicantCount = tuple.get(applicantCountPath);

			ReservationStatus virtualStatus = r.getVirtualStatus(now);

			return new CreatedReservationListDto(
					r.getId(),
					virtualStatus,
					r.getTitle().getValue(),
					r.getScheduledTime().getTime(),
					r.getPlaceInfo().getRegion1Depth(),
					r.getPlaceInfo().getSpecificPlace(),
					r.getShootingDuration(),
					applicantCount != null ? applicantCount : 0L);
		}).toList();

		Long totalCount = queryFactory
				.select(reservation.count())
				.from(reservation)
				.where(reservation.ownerInfo.userId.eq(ownerId))
				.fetchOne();

		return new PageImpl<>(content, PageRequest.of(0, limit), totalCount != null ? totalCount : 0L);
	}

	@Override
	public Page<AppliedReservationListDto> getMyAppliedReservations(Long applicantId, Long cursor, int limit) {
		LocalDateTime now = LocalDateTime.now();
		NumberPath<Long> applicantCountPath = Expressions.numberPath(Long.class, "applicantCount");

		List<Tuple> results = queryFactory
				.select(
						reservation,
						applicant.status,
						ExpressionUtils.as(JPAExpressions.select(applicant.count())
								.from(applicant)
								.where(applicant.reservation.id.eq(reservation.id)), applicantCountPath))
				.from(applicant)
				.join(applicant.reservation, reservation)
				.where(
						applicant.userId.eq(applicantId),
						cursor != null ? applicant.reservation.id.lt(cursor) : null)
				.orderBy(applicant.reservation.id.desc())
				.limit(limit)
				.fetch();

		List<AppliedReservationListDto> content = results.stream().map(tuple -> {
			Reservation r = tuple.get(reservation);
			com.dnd.jjigeojulge.reservation.domain.ApplicantStatus applicantStatus = tuple.get(applicant.status);
			Long currentApplicantCount = tuple.get(applicantCountPath);

			return AppliedReservationListDto.of(
					r.getId(),
					r.getStatus(),
					applicantStatus,
					r.getTitle().getValue(),
					r.getScheduledTime().getTime(),
					r.getPlaceInfo().getRegion1Depth(),
					r.getPlaceInfo().getSpecificPlace(),
					r.getShootingDuration(),
					currentApplicantCount != null ? currentApplicantCount : 0L,
					now);
		}).toList();

		Long totalCount = queryFactory
				.select(applicant.count())
				.from(applicant)
				.where(applicant.userId.eq(applicantId))
				.fetchOne();

		return new PageImpl<>(content, PageRequest.of(0, limit), totalCount != null ? totalCount : 0L);
	}

	@Override
	public Optional<ReservationDetailDto> getReservationDetail(Long reservationId) {
		NumberPath<Integer> applicantCountPath = Expressions.numberPath(Integer.class, "applicantCount");
		NumberPath<Integer> commentCountPath = Expressions.numberPath(Integer.class, "commentCount");

		Tuple tuple = queryFactory
				.select(
						reservation,
						user.nickname,
						user.profileImageUrl,
						user.gender,
						user.ageGroup,
						user.introduction.value,
						ExpressionUtils.as(JPAExpressions.select(applicant.count().intValue())
								.from(applicant)
								.where(applicant.reservation.id.eq(reservation.id)), applicantCountPath),
						ExpressionUtils.as(JPAExpressions.select(reservationComment.count().intValue())
								.from(reservationComment)
								.where(reservationComment.reservationId.eq(reservation.id)), commentCountPath))
				.from(reservation)
				.leftJoin(user).on(reservation.ownerInfo.userId.eq(user.id))
				.where(reservation.id.eq(reservationId))
				.fetchOne();

		if (tuple == null) {
			return Optional.empty();
		}

		Reservation r = tuple.get(reservation);
		String nickname = tuple.get(user.nickname);
		String profileImageUrl = tuple.get(user.profileImageUrl);
		Gender gender = tuple.get(user.gender);
		com.dnd.jjigeojulge.user.domain.AgeGroup ageGroup = tuple.get(user.ageGroup);
		String introduction = tuple.get(user.introduction.value);

		Integer applicantCount = tuple.get(applicantCountPath);
		Integer commentCount = tuple.get(commentCountPath);

		ReservationDetailDto detail = new ReservationDetailDto(
				r.getId(),
				r.getViewCount(),
				applicantCount != null ? applicantCount : 0,
				commentCount != null ? commentCount : 0,
				r.getOwnerInfo().getUserId(),
				nickname,
				profileImageUrl,
				gender,
				ageGroup,
				introduction,
				r.getTitle().getValue(),
				r.getScheduledTime().getTime(),
				r.getPlaceInfo().getRegion1Depth(),
				r.getPlaceInfo().getSpecificPlace(),
				r.getOwnerInfo().getPhotoStyleSnapshot(),
				r.getShootingDuration(),
				r.getRequestMessage().getValue(), // RequestMessage 객체의 value 필드
				r.getStatus());

		return Optional.of(detail);
	}

	@Override
	public boolean existsByIdAndOwnerId(Long reservationId, Long ownerId) {
		Integer fetchOne = queryFactory
				.selectOne()
				.from(reservation)
				.where(reservation.id.eq(reservationId),
						reservation.ownerInfo.userId.eq(ownerId))
				.fetchFirst();
		return fetchOne != null;
	}

	@Override
	public com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto getApplicants(
			Long reservationId) {
		List<com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantDto> applicantDtos = queryFactory
				.select(new com.dnd.jjigeojulge.reservation.application.dto.query.QApplicantDto(
						applicant.id,
						applicant.userId,
						user.nickname,
						user.profileImageUrl,
						user.gender,
						user.ageGroup,
						user.introduction.value,
						applicant.createdAt))
				.from(applicant)
				.leftJoin(user).on(applicant.userId.eq(user.id))
				.where(applicant.reservation.id.eq(reservationId))
				.orderBy(applicant.createdAt.asc())
				.fetch();

		return new com.dnd.jjigeojulge.reservation.application.dto.query.ApplicantListResponseDto(applicantDtos.size(),
				applicantDtos);
	}

	private BooleanExpression region1DepthEq(Region1Depth region1Depth) {
		return region1Depth != null ? reservation.placeInfo.region1Depth.eq(region1Depth) : null;
	}

	private BooleanExpression scheduledAtEq(LocalDate date) {
		if (date != null) {
			return reservation.scheduledTime.time.between(date.atStartOfDay(),
					date.plusDays(1).atStartOfDay().minusNanos(1));
		}
		return null;
	}

	private BooleanExpression genderEq(com.dnd.jjigeojulge.user.domain.Gender gender) {
		return gender != null ? user.gender.eq(gender) : null;
	}

	private BooleanExpression keywordContains(String keyword) {
		if (keyword == null || keyword.isBlank()) {
			return null;
		}
		return reservation.title.value.containsIgnoreCase(keyword)
				.or(reservation.placeInfo.specificPlace.containsIgnoreCase(keyword));
	}
}
