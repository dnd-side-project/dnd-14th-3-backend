package com.dnd.jjigeojulge.websocket.data;

public enum MatchSessionMessageType {
	LOCATION,      // 실시간 위치 업데이트
	USER_ARRIVED,  // 특정 유저의 도착 완료 알림
	SESSION_READY, // 두 명 모두 도착하여 세션 준비 완료
	SESSION_END    // 촬영 종료 및 세션 닫힘
}
