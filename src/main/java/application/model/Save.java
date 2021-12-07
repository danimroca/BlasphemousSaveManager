package application.model;

import java.io.File;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "save")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Save implements Serializable {
	
	private static final long serialVersionUID = -3123314282466102000L;
	
	private String name;
	private int order;
	@XmlTransient
	private File saveFile;
	@XmlTransient
	private File backupSaveFile;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public File getSaveFile() {
		return saveFile;
	}
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}
	public File getBackupSaveFile() {
		return backupSaveFile;
	}
	public void setBackupSaveFile(File backupSaveFile) {
		this.backupSaveFile = backupSaveFile;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

}
