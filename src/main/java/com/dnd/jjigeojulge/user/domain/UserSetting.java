package com.dnd.jjigeojulge.user.domain;

import com.dnd.jjigeojulge.domain.base.BaseUpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	name = "user_settings",
	uniqueConstraints = @UniqueConstraint(name = "uk_user_settings_user_id", columnNames = "user_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSetting extends BaseUpdatableEntity {

	@Column(name = "notification_enabled", nullable = false)
	private boolean notificationEnabled;

	@Column(name = "location_sharing_enabled", nullable = false)
	private boolean locationSharingEnabled;

	@Setter(AccessLevel.PACKAGE)
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true,
		foreignKey = @ForeignKey(name = "fk_user_settings_user")
	)
	private User user;

	@Builder
	public UserSetting(boolean notificationEnabled, boolean locationSharingEnabled) {
		this.notificationEnabled = notificationEnabled;
		this.locationSharingEnabled = locationSharingEnabled;
	}

	public void updateSettings(Boolean notificationEnabled, Boolean locationSharingEnabled) {
		if (notificationEnabled != null && notificationEnabled != this.notificationEnabled) {
			this.notificationEnabled = notificationEnabled;
		}
		if (locationSharingEnabled != null && locationSharingEnabled != this.locationSharingEnabled) {
			this.locationSharingEnabled = locationSharingEnabled;
		}
	}
}
