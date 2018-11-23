package com.controller;

import com.main.Print;

/**
 * Contains connector functions to alter servers and clients
 * @author Candy
 *
 */
public class ControllerManager {
	private static ControllerManager instance;
	
	public static ControllerManager Instance() {
		if(instance == null) {
			instance = new ControllerManager();
		}
		return instance;
	}
	
	public static void StartServer() {
		Print.system("Starting server.");
	}
}
