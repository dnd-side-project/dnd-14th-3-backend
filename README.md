# dnd-14th-3-backend

<!-- PROJECT LOGO -->
<br />
<div align="center">
<img src="images/logo.png" alt="Logo">

<h3 align="center">찍어줄게</h3>

  <p align="center">
    혼자여도, 기록은 함께
    <br />
    <a href="https://github.com/dnd-side-project/dnd-14th-3-backend/wiki"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://app.snapforyou.cloud/">View Demo</a>
    &middot;
    <a href="https://github.com/dnd-side-project/dnd-14th-3-backend/issues/new?labels=bug&template=bug_report.md">Report Bug</a>
    &middot;
    <a href="https://github.com/dnd-side-project/dnd-14th-3-backend/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

<!-- ABOUT THE PROJECT -->

## About The Project

> 내 사진은 남기고 싶은데.. 혼자 활동하는 편이라 난감한 적 있으시죠? <br/>
> 찍어줄게는 사진을 찍어주는 동행을 연결하는 서비스입니다. <br/>
> 말을 걸지 않아도, 부담을 느끼지 않아도, 같은 장소, 같은 시간에 있는 사람과 자연스럽게 매칭됩니다.

![product-screenshot]

## 🚀 Features

### 1. 온보딩

![feat-onboarding]

- 사용자 목적 기반 첫 진입 화면
- 촬영 스타일 선택 및 프로필 설정
- 사용자 정보 초기 세팅

---

### 2. 예약 매칭

![feat-booking-list]
![feat-booking-request]

- 일정 기반 예약 요청 생성
- 예약 상태 관리 (대기 / 수락 / 거절)
- MatchProposal 기반 제안 흐름 처리

---

### 3. 실시간 매칭

![feat-matching-live]

- Redis GEO 기반 거리 탐색
- 대기열 기반 매칭 후보 탐색
- SSE 실시간 대기 인원 업데이트
- 매칭 성공 시 MatchSession 생성

---

### 4. 마이페이지

![feat-profile]

- 사용자 프로필 조회/수정
- 촬영 스타일 관리 (N:M 매핑)
- 매칭 이력 조회

<!-- MARKDOWN LINKS & IMAGES -->

[product-screenshot]: https://github.com/user-attachments/assets/5ac5335e-467a-4dc9-ac25-78efa19e0938

[feat-onboarding]: images/features/feat_onboarding_intro.png

[feat-booking-list]: images/features/feat_booking_list.png

[feat-booking-request]: images/features/feat_booking_request.png

[feat-matching-live]: images/features/feat_matching_live.png

[feat-profile]: images/features/feat_mypage_profile.png
