/**
 * 
 */
package org.test.project.model;

/**
 * @author abdelgam
 *
 */
public class GameProfile {

	/**
	 * 
	 */
	public GameProfile() {
		super();
	}

	private Integer profileId;
	private String profileName;

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	/**
	 * @param profileId
	 */
	public GameProfile(Integer profileId) {
		super();
		this.profileId = profileId;
	}

}
