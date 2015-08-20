package com.aroundme.common;

public interface IAppCallBack<T> {
	
	/**
	 * @param ret the object that returned
	 * @param e value of exception
	 */
	void done(T ret,Exception e);
	
}
