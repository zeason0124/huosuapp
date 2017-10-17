package com.huosuapp.text.bean;

public class UserWalletBean  {

	private int code;

	private String msg;

	private Data data;

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return this.data;
	}

	public class Data {
		private String remain;

		public void setRemain(String remain) {
			this.remain = remain;
		}

		public String getRemain() {
			return this.remain;
		}

	}
}
