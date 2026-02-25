package com.dnd.jjigeojulge.reservation.infra;

import static com.dnd.jjigeojulge.reservation.domain.QApplicant.*;
import static com.dnd.jjigeojulge.reservation.domain.QReservation.*;
import static com.dnd.jjigeojulge.user.domain.QUser.*;

import static com.dnd.jjigeojulge.reservation.domain.QApplicant.applicant;
import static com.dnd.jjigeojulge.reservation.domain.QReservation.reservation;
import static com.dnd.jjigeojulge.reservation.domain.QReservationComment.reservationComment;
import static com.dnd.jjigeojulge.user.domain.QUser.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import static com.dnd.jjigeojulge.reservation.domain.QApplicant.*;
import static com.dnd.jjigeojulge.reservation.domain.QReservation.*;

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
import com.dnd.jjigeojulge.user.domain.PhotoStyle;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
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
		List<Tuple> results = queryFactory
				.select(reservation, user.nickname, user.gender, user.profileImageUrl)
				.from(reservation)
				.leftJoin(user).on(reservation.ownerInfo.userId.eq(user.id))
				.where(
						region1DepthEq(condition.region1Depth()),
						scheduledAtEq(condition.date()),
						photoStyleContains(condition.photoStyle()),
						genderEq(condition.gender()),
						keywordContains(condition.keyword()),
						reservation.status.in(ReservationStatus.RECRUITING, ReservationStatus.CONFIRMED),
						cursor != null ? reservation.id.lt(cursor) : null)
				.orderBy(reservation.id.desc())
				.limit(limit)
				.fetch();

		List<ReservationSummaryDto> content = results.stream().map(tuple -> {
			Reservation r = tuple.get(reservation);
			String nickname = tuple.get(user.nickname);
			com.dnd.jjigeojulge.user.domain.Gender userGender = tuple.get(user.gender);
			String profileImageUrl = tuple.get(user.profileImageUrl);
			return new ReservationSummaryDto(
					r.getId(),
					r.getTitle().getValue(),
					r.getScheduledTime().getTime(), // ScheduledTime 객체의 time 필드
					r.getPlaceInfo().getRegion1Depth(),
					r.getPlaceInfo().getSpecificPlace(),
					r.getShootingDuration(),
					r.getOwnerInfo().getPhotoStyleSnapshot(), // ElementCollection List<String>
					0,
					r.getStatus(),
					r.getOwnerInfo().getUserId(),
					nickname,
					userGender,
					profileImageUrl);
		}).toList();

		Long totalCount = queryFactory
				.select(reservation.count())
				.from(reservation)
				.leftJoin(user).on(reservation.ownerInfo.userId.eq(user.id))
				.where(
						region1DepthEq(condition.region1Depth()),
						scheduledAtEq(condition.date()),
						photoStyleContains(condition.photoStyle()),
						genderEq(condition.gender()),
						keywordContains(condition.keyword()),
						reservation.status.in(ReservationStatus.RECRUITING, ReservationStatus.CONFIRMED))
				.fetchOne();

		return new PageImpl<>(content, PageRequest.of(0, limit), totalCount != null ? totalCount : 0L);
	}

	@Override
	public Page<CreatedReservationListDto> getMyCreatedReservations(Long ownerId, Long cursor, int limit) {
		LocalDateTime now = LocalDateTime.now();

		List<Tuple> results = queryFactory
				.select(
						reservation,
						JPAExpressions.select(applicant.count())
								.from(applicant)
								.where(applicant.reservation.id.eq(reservation.id)))
				.from(reservation)
				.where(
						reservation.ownerInfo.userId.eq(ownerId),
						cursor != null ? reservation.id.lt(cursor) : null)
				.orderBy(reservation.id.desc())
				.limit(limit)
				.fetch();

		List<CreatedReservationListDto> content = results.stream().map(tuple -> {
			Reservation r = tuple.get(reservation);
			Long applicantCount = tuple.get(1, Long.class);

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

		List<Tuple> results = queryFactory
				.select(
						reservation,
						applicant.status,
						JPAExpressions.select(applicant.count())
								.from(applicant)
								.where(applicant.reservation.id.eq(reservation.id)))
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
			Long currentApplicantCount = tuple.get(2, Long.class);

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
		Tuple tuple = queryFactory
				.select(
						reservation,
						user.nickname,
						user.profileImageUrl,
						user.gender,
						JPAExpressions.select(applicant.count().intValue())
								.from(applicant)
								.where(applicant.reservation.id.eq(reservation.id)),
						JPAExpressions.select(reservationComment.count().intValue())
								.from(reservationComment)
								.where(reservationComment.reservationId.eq(reservation.id)))
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

		Integer applicantCount = tuple.get(4, Integer.class);
		Integer commentCount = tuple.get(5, Integer.class);

		ReservationDetailDto detail = new ReservationDetailDto(
				r.getId(),
				r.getViewCount(),
				applicantCount != null ? applicantCount : 0,
				commentCount != null ? commentCount : 0,
				r.getOwnerInfo().getUserId(),
				nickname,
				profileImageUrl,
				0, // ownerTrustScore
				gender,
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

	private BooleanExpression photoStyleContains(PhotoStyle photoStyle) {
		return photoStyle != null ? reservation.ownerInfo.photoStyleSnapshot.contains(photoStyle.getName().name())
				: null;
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
