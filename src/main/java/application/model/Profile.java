package application.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "profile")
@XmlAccessorType(XmlAccessType.FIELD)
public class Profile implements Serializable {
	
	private static final long serialVersionUID = 5224425299619734139L;
	
	private String name = "";
	@XmlElementWrapper(name="saves")
	@XmlElement(name="save")
	private List<Save> saves;
	private boolean active;
	private int saveSlot;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Save> getSaves() {
		if (saves == null) {
			return new ArrayList<>();
		}
		return saves;
	}
	public void addSave(Save save) {
		if (saves == null) {
			saves = new ArrayList<>();
		}
		saves.add(save);
	}
	public void removeSave(String saveName) {
		saves.removeIf(save -> save.getName().equals(saveName));
	}
	public void setSaves(List<Save> saves) {
		this.saves = saves;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getSaveSlot() {
		return saveSlot;
	}

	public void setSaveSlot(int saveSlot) {
		this.saveSlot = saveSlot;
	}

	public void deactivateProfile() {
		this.active = false;
	}

	public void activateProfile() {
		this.active = true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Profile profile = (Profile) o;
		return name.equals(profile.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public String toString() {
		return name;
	}

}
