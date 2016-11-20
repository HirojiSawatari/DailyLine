package edu.njnu.dailyline.domain;

public class LoginResult {

	private boolean flag;
	private Data data;

	
	@Override
	public String toString() {
		return "LoginResult [flag=" + flag + ", data=" + data + "]";
	}


	public boolean isFlag() {
		return flag;
	}


	public void setFlag(boolean flag) {
		this.flag = flag;
	}


	public Data getData() {
		return data;
	}


	public void setData(Data data) {
		this.data = data;
	}


	public class Data{

		
		private String account;
		private String sex;
		private String icon;
		public String getAccount() {
			return account;
		}
		public void setAccount(String account) {
			this.account = account;
		}
		public String getSex() {
			return sex;
		}
		public void setSex(String sex) {
			this.sex = sex;
		}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		@Override
		public String toString() {
			return "data [account=" + account + ", sex=" + sex + ", icon="
					+ icon + "]";
		}
		
		
	}
}

