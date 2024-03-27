package com.axonivy.connector.docusign.bo;

import ch.ivyteam.ivy.bpm.error.BpmError;
import ch.ivyteam.ivy.scripting.objects.File;

public class DownloadResult {
	String filename;
	File file;
	byte[] content;
	BpmError error;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public BpmError getError() {
		return error;
	}

	public void setError(BpmError error) {
		this.error = error;
	}
}
