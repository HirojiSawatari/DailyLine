package edu.njnu.dailyline.domain;

public class HTTPResult {

	private boolean flag;
	private int errorCode;
	private String errorString;
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorString() {
		return errorString;
	}
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	@Override
	public String toString() {
		return "HTTPResult [flag=" + flag + ", errorCode=" + errorCode
				+ ", errorString=" + errorString + "]";
	}
	
}
