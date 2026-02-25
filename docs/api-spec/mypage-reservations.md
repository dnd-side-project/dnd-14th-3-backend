# [FRONTEND] API 스펙: 마이페이지 동행 예약 조회 API

| API | 설명 |
| --- | --- |
| `GET /api/v1/reservations/created` | 내가 올린(작성한) 동행 예약 리스트 (호스트) |
| `GET /api/v1/reservations/applied` | 내가 지원한 동행 예약 리스트 (게스트) |

둘 다 **커서 기반 무한 스크롤 페이징(Cursor Pagination)** 을 사용합니다.

---

## 1. 내가 올린 동행 예약 리스트
**내가 방장(Owner)으로서 작성한 예약 모집글의 목록입니다.**

### [Request]
`GET /api/v1/reservations/created`

| Query Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `cursor` | Number (Long) | No | 이전 페이지의 마지막 항목 `id` (처음 요청 시 생략) |
| `limit` | Number (int) | No | 페이지 당 가져올 항목 수 (기본값: 10) |

### [Response]
```json
{
  "code": 200,
  "message": "내가 올린 예약 목록 조회 성공",
  "data": {
    "totalElements": 2,          // 총 데이터 개수 (커서 무관 전체 개수)
    "totalPages": 1,             // 총 페이지 수 (기본 페이징 사용 시)
    "size": 10,
    "content": [
      {
        "id": 101,                 // 예약 ID (다음 cursor 값)
        "status": "RECRUITING",     // [Enum] 방 자체의 상태 (아래 설명 참조)
        "title": "이번 주말 서울숲 출사 가실 분",
        "scheduledAt": "2026-03-01T14:00:00",
        "region1Depth": "SEOUL",    // [Enum] 큰 지역 (SEOUL, BUSAN 등)
        "specificPlace": "서울숲",
        "shootingDuration": "THIRTY_PLUS_MINUTES",  // [Enum] (TEN_MINUTES, TWENTY_MINUTES, THIRTY_PLUS_MINUTES)
        "applicantCount": 3,       // 현재 이 방에 지원한 대기자 수
        "createdAt": "2026-02-25T10:00:00"
      },
      // ...
    ]
  }
}
```

#### 📌 방 상태 Enum (`status`)
방장이 개설한 방 본연의 라이프사이클 상태입니다.
- `RECRUITING` : 모집 중 (지원자를 받고 있는 상태)
- `CONFIRMED` : 매칭 확정 (지원자 중 1명을 수락함)
- `RECRUITMENT_CLOSED` : 기간 만료 (약속 시간이 될 때까지 아무도 수락하지 않아 자동 마감됨)
- `COMPLETED` : 일정 완료 (매칭 확정 후 약속 시간이 지남)
- `CANCELED` : 모집 취소 (방장이 방을 폭파함)

---

## 2. 내가 지원한 동행 예약 리스트
**내가 다른 사람이 올린 방에 게스트(Applicant)로 지원했던 이력 목록입니다.**

### [Request]
`GET /api/v1/reservations/applied`

| Query Parameter | Type | Required | Description |
| --- | --- | --- | --- |
| `cursor` | Number (Long) | No | 이전 페이지의 마지막 항목 `id` (처음 요청 시 생략) |
| `limit` | Number (int) | No | 페이지 당 가져올 항목 수 (기본값: 10) |

### [Response]
```json
{
  "code": 200,
  "message": "내가 지원한 예약 목록 조회 성공",
  "data": {
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "content": [
      {
        "id": 105,                 // 예약 ID (다음 cursor 값)
        "status": "WAITING",       // [Enum] ★나의 게스트 가상 매칭 상태 (아래 설명 참조)
        "title": "홍대입구역 카페 스냅",
        "scheduledAt": "2026-03-05T15:00:00",
        "region1Depth": "SEOUL",
        "specificPlace": "홍대입구역",
        "shootingDuration": "TWENTY_MINUTES",
        "applicantCount": 1,       // (내가 속한) 이 방의 경쟁자(총 지원자) 수
        "createdAt": "2026-02-24T18:00:00"
      }
    ]
  }
}
```

#### 📌 가상 매칭 상태 Enum (`status`) - 중요 🚨
"내가 지원한" 리스트에서는 방의 상태(`RECRUITING` 등)가 아니라, **내 입장에서의 매칭 결과 배지**를 띄워주기 위해 백엔드에서 5가지 가상 상태로 변환하여 내려줍니다.

- `WAITING` (대기 중) : 내가 지원하고 아직 호스트가 수락/거절을 안 했으며 예약 시간도 남은 상태
- `MATCHED` (매칭 확정) : 호스트가 나를 선택했고, 아직 약속 시간이 지나지 않은 상태
- `COMPLETED` (일정 완료) : 내가 선택되었고(MATCHED), 이미 약속 시간이 지남. (✅ 리뷰 작성 버튼 노출용 플래그)
- `REJECTED` (거절/실패) : 
  - 호스트가 나를 명시적으로 거절함
  - 타인이 선택되어 내가 자동으로 떨어짐
  - 아무도 선택 안 됨 + 예약 시간 지나서 모임 방 자체가 만료(폭파)됨
- `CANCELED` (취소됨) : 내가 스스로 지원을 취소했거나, 호스트가 모임 방을 삭제(폭파)한 경우
