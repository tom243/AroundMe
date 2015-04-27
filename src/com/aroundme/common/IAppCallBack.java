package com.aroundme.common;

public interface IAppCallBack<T> {
	
	void done(T ret,Exception e);
	
}
