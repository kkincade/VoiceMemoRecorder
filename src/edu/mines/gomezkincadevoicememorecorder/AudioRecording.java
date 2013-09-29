package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;

public class AudioRecording {
	private File audioFile;
	private String name;
	private String subject;
	private String notes;
	
	public AudioRecording(File audioFile, String name, String subject, String notes) {
		this.audioFile = audioFile;
		this.name = name;
		this.subject = subject;
		this.setNotes(notes);
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
