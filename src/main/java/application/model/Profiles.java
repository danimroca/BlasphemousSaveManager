package application.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "profiles")
@XmlAccessorType (XmlAccessType.FIELD)
public class Profiles {

	@XmlElement(name = "profile")
    private List<Profile> profiles = null;

	public List<Profile> getProfiles() {

		if (profiles == null) {
			profiles = new ArrayList<>();
		}
		return profiles;
	}

	public void setProfiles(List<Profile> profiles) {
		this.profiles = profiles;
	}
	
}
