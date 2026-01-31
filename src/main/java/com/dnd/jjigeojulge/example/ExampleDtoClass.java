package com.dnd.jjigeojulge.example;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExampleDtoClass {
	private Long orderId;
	private UserInfo user;
	private List<ItemInfo> items;

	@AllArgsConstructor
	@Getter
	public static class UserInfo {
		private Long id;
		private String name;
	}

	@Getter
	@AllArgsConstructor
	public static class ItemInfo {
		private String itemName;
		private int price;
		private LocalDateTime orderedAt;
	}
}
