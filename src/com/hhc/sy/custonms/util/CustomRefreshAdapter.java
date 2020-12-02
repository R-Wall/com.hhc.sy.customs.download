package com.hhc.sy.custonms.util;

import java.util.HashSet;
import java.util.Set;

public class CustomRefreshAdapter {
	
	private static Set<IRefreshListener> listeners = new HashSet<IRefreshListener>();
	
	public static void addListener(IRefreshListener listener) {
		listeners.add(listener);
	}
	
	public static Set<IRefreshListener> getAllListeners(){
		return listeners;
	}
	
	public static void removeListener(IRefreshListener listener) {
		listeners.remove(listener); 
	}
	
	public static void executePre() {
		for (IRefreshListener irl : listeners) {
			irl.pre();
		}
	}
	
	public static void executePost() {
		for (IRefreshListener irl : listeners) {
			irl.post();;
		}
	}
	
	public static void executeConditipon() {
		for (IRefreshListener irl : listeners) {
			irl.condition();
		}
	}

}
