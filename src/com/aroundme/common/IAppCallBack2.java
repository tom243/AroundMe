package com.aroundme.common;

public interface IAppCallBack2<T> {
	
	/**
	 * @param ret the object that returned
	 * @param e value of exception
	 */
	void done2(T ret,Exception e);
	
}
