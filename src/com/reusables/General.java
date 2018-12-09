package com.reusables;

import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

//import com.sun.management.OperatingSystemMXBean;

public class General {
	
	private static final boolean isPrinting = true;
	private static final boolean isPrintingError = true;
	
	public static void printUsage(OperatingSystemMXBean osBean) {

		printCpuLoad(osBean);
//		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
//      OperatingSystemMXBean.class);
		// What % CPU load this current JVM is taking, from 0.0-1.0
//		System.out.println("CPU Process load: "+osBean.getProcessCpuLoad());
//		System.out.println("CPU Process time: "+osBean.getProcessCpuTime());
		
		// What % load the overall system is at, from 0.0-1.0
//		System.out.println(osBean.getSystemCpuLoad());
	}
	
	/* printCpuLoad code taken from Bartosz Wieczorek (2015) */
	private static void printCpuLoad(OperatingSystemMXBean mxBean) {
      for (Method method : mxBean.getClass().getDeclaredMethods()) {
        method.setAccessible(true);
        String methodName = method.getName();
        if (methodName.startsWith("get") && methodName.contains("Cpu") && methodName.contains("Load")
                && Modifier.isPublic(method.getModifiers())) {
 
            Object value;
            try {
               value = method.invoke(mxBean);
            } catch (Exception e) {
                value = e;
            }
            System.out.println(methodName);
            System.out.println(value);
        }
      }
      System.out.println("");
    }
	
	public static void PRINT_TIME() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
		LocalDateTime now = LocalDateTime.now();
		System.out.println(dtf.format(now));  
	}
	
	/**
	 * Static printing function.
	 * @param message
	 */
	public static void PRINT(String message) {
		if(isPrinting) {
			System.out.println(message);
		}
	}
	public static void PRINT(ArrayList<Integer> message, int limit) {
		if(isPrintingError) {
			for(int i = 0; i < limit; i++) {
				System.out.println(message.get(i)+" ");
			}
		}
	}
	public static void PRINT_ERROR(String message) {
		if(isPrintingError) {
			System.out.println(message);
		}
	}
}
