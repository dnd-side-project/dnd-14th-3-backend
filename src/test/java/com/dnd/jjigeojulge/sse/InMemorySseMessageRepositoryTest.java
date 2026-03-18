package com.dnd.jjigeojulge.sse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class InMemorySseMessageRepositoryTest {

	private InMemorySseMessageRepository sut;
	private static final int CAPACITY = 3;

	@BeforeEach
	void setUp() {
		sut = new InMemorySseMessageRepository();
		ReflectionTestUtils.setField(sut, "eventQueueCapacity", CAPACITY);
	}

	@Nested
	@DisplayName("save() 호출 시")
	class SaveTest {
		@Test
		@DisplayName("메시지를 저장 후 반환한다.")
		void test1() {
			// given
			Long receiverId = 1L;
			SseMessage message = createMessage(UUID.randomUUID(), receiverId, true);

			// when
			SseMessage save = sut.save(message);

			// then
			assertThat(save).isSameAs(message);
		}

	}

	@Nested
	@DisplayName("findAllByLastEventIdAfterAndReceiverId() 호출 시")
	class FindAllByLastEventIdAfterAndReceiverIdTest {
		@Test
		@DisplayName("기준 이벤트 이후의 메시지만 반환한다.")
		void test1() throws Exception {
			//given
			Long receiverId = 1L;
			SseMessage first = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage second = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage third = createMessage(UUID.randomUUID(), receiverId, true);

			sut.save(first);
			sut.save(second);
			sut.save(third);

			//when
			List<SseMessage> result =
				sut.findAllByLastEventIdAfterAndReceiverId(first.getEventId(), receiverId);

			//then
			assertThat(result)
				.containsExactly(second, third);
		}

		@Test
		@DisplayName("기준 lastEventId 메시지는 포함하지 않는다.")
		void test2() throws Exception {
			//given
			Long receiverId = 1L;
			SseMessage first = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage second = createMessage(UUID.randomUUID(), receiverId, true);
			sut.save(first);
			sut.save(second);

			//when
			List<SseMessage> result =
				sut.findAllByLastEventIdAfterAndReceiverId(first.getEventId(), receiverId);

			//then
			assertThat(result)
				.doesNotContain(first)
				.containsExactly(second);
		}

		@Test
		@DisplayName("존재하지 않는 lastEventId면 빈 리스트를 반환한다.")
		void test3() throws Exception {
			//given
			Long receiverId = 1L;
			SseMessage first = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage second = createMessage(UUID.randomUUID(), receiverId, true);
			sut.save(first);
			sut.save(second);

			UUID notExistingEventId = UUID.randomUUID();

			//when
			List<SseMessage> result =
				sut.findAllByLastEventIdAfterAndReceiverId(notExistingEventId, receiverId);

			//then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("기준 메시지 eventId가 가장 마지막 메시지면 빈 리스트를 반환한다.")
		void test4() throws Exception {
			// given
			Long receiverId = 1L;
			SseMessage first = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage second = createMessage(UUID.randomUUID(), receiverId, true);

			sut.save(first);
			sut.save(second);

			// when
			List<SseMessage> result =
				sut.findAllByLastEventIdAfterAndReceiverId(second.getEventId(), receiverId);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("lastEventId가 null이면 빈 리스트를 반환한다.")
		void test5() throws Exception {
			// given
			Long receiverId = 1L;
			SseMessage first = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage second = createMessage(UUID.randomUUID(), receiverId, true);

			sut.save(first);
			sut.save(second);

			// when
			List<SseMessage> result =
				sut.findAllByLastEventIdAfterAndReceiverId(null, receiverId);

			// then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("수신 가능한 메시지만 반환한다.")
		void test6() throws Exception {
			// given
			Long receiverId = 1L;
			SseMessage first = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage second = createMessage(UUID.randomUUID(), receiverId, false);
			SseMessage third = createMessage(UUID.randomUUID(), receiverId, true);

			sut.save(first);
			sut.save(second);
			sut.save(third);

			// when
			List<SseMessage> result =
				sut.findAllByLastEventIdAfterAndReceiverId(first.getEventId(), receiverId);

			// then
			assertThat(result).containsExactly(third);
		}
	}

	@Nested
	@DisplayName("capacity를 관리할 때")
	class CapacityTest {
		@Test
		@DisplayName("capacity를 초가화면 가장 오래된 메시지부터 제거한다.")
		void test1() throws Exception {
			// given
			Long receiverId = 1L;
			SseMessage first = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage second = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage third = createMessage(UUID.randomUUID(), receiverId, true);
			SseMessage fourth = createMessage(UUID.randomUUID(), receiverId, true);

			sut.save(first);
			sut.save(second);
			sut.save(third);
			sut.save(fourth);

			// when
			List<SseMessage> result =
				sut.findAllByLastEventIdAfterAndReceiverId(second.getEventId(), receiverId);

			// then
			assertThat(result).containsExactly(third, fourth);

			@SuppressWarnings("unchecked")
			Map<UUID, SseMessage> messages =
				(Map<UUID, SseMessage>)ReflectionTestUtils.getField(sut, "messages");

			assertThat(messages).doesNotContainKey(first.getEventId());
		}
	}

	private SseMessage createMessage(UUID eventId, Long receiverId, boolean receivable) {
		SseMessage message = Mockito.mock(SseMessage.class);
		given(message.getEventId()).willReturn(eventId);
		given(message.isReceivable(receiverId)).willReturn(receivable);
		return message;
	}
}
