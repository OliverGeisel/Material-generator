package de.olivergeisel.materialgenerator;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	private String location = "upload-dir";

//region setter/getter
public String getLocation() {
	return location;
}

	public void setLocation(String location) {
		this.location = location;
	}
//endregion

}