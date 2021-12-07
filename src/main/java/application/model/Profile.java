package application.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "profile")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Profile implements Serializable {
	
	private static final long serialVersionUID = 5224425299619734139L;
	
	private String name;
	private List<Save> saves;
	private boolean active;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Save> getSaves() {
		return saves;
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
	
	
}
