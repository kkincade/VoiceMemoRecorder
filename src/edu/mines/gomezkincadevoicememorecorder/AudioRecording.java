package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;
import java.io.Serializable;



public class AudioRecording implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private File audioFile;
	private String name;
	private String subject;
	private String notes;
	private String date;
	private String duration;
	
	public AudioRecording(File audioFile, String name, String subject, String notes, String date, String length) {
		this.audioFile = audioFile;
		this.name = name;
		this.subject = subject;
		this.date = date;
		this.duration = length;
		this.setNotes(notes);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	/** Getters and Setters **/
	public File getAudioFile() {
		return audioFile;
	}

	public void setAudioFile(File audioFile) {
		this.audioFile = audioFile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	
}
