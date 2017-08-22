package com.vivek.audit;

public class HttpSessionUtil {

	private static ThreadLocal threadLocal = new ThreadLocal<>();

	public static String getUser() {
		return (String) threadLocal.get();
	}

	public void setUser(String user) {
		threadLocal.set(user);
	}

}
