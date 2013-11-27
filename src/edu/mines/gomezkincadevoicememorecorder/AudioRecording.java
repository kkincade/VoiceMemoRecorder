package edu.mines.gomezkincadevoicememorecorder;

import java.io.Serializable;

/** Simple class that holds all the information for a single voice memo. **/
public class AudioRecording implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String audioFilePath;
	private String name;
	private String subject;
	private String notes;
	private String date;
	private String duration;
	
	public AudioRecording(String audioFilePath, String name, String subject, String notes, String date, String length) {
		this.audioFilePath = audioFilePath;
		this.name = name;
		this.subject = subject;
		this.date = date;
		this.duration = length;
		this.setNotes(notes);
	}


	/** ----------------------------- Getters and Setters ----------------------------- **/
	
	public String getAudioFilePath() {
		return audioFilePath;
	}

	public void setAudioFilePath(String audioFilePath) {
		this.audioFilePath = audioFilePath;
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
}
