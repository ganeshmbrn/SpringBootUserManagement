package com.git.util;

public class UnlockAccount {

	private String emailId;

	private String temporaryPwd;

	private String newPwd;

	private String confirmPwd;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getTemporaryPwd() {
		return temporaryPwd;
	}

	public void setTemporaryPwd(String temporaryPwd) {
		this.temporaryPwd = temporaryPwd;
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}

	public String getConfirmPwd() {
		return confirmPwd;
	}

	public void setConfirmPwd(String confirmPwd) {
		this.confirmPwd = confirmPwd;
	}

}
