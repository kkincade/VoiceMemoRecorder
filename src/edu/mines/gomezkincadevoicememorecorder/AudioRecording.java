package edu.mines.gomezkincadevoicememorecorder;

import java.io.File;

public class AudioRecording {
	private File audio_file;
	private String name;
	private String subject;
	private String notes;
	
	public AudioRecording(File audio_file, String name, String subject, String notes) {
		this.audio_file = audio_file;
		this.name = name;
		this.subject = subject;
		this.setNotes(notes);
	}

	/** Getters and Setters **/
	public File getAudio_file() {
		return audio_file;
	}

	public void setAudio_file(File audio_file) {
		this.audio_file = audio_file;
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
