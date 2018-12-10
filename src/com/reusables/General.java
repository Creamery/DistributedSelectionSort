package com.reusables;

import com.main.Info;

import java.lang.management.ManagementFactory;
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

	private static Runtime recentRT;
	private static OperatingSystemMXBean recentOSBean;
	private static long recentUsedMemBefore;

	public static void trackStats_start(String stopwatchName){
		// Stopwatch (1)
		Stopwatch.start(stopwatchName);
		// CPU Usage (1)
		recentOSBean = ManagementFactory.getOperatingSystemMXBean();
		// MEMORY Usage (1)
		recentRT = Runtime.getRuntime();
		System.gc();
		recentUsedMemBefore = recentRT.totalMemory() - recentRT.freeMemory();
		System.out.println("usedMemoryBefore: "+recentUsedMemBefore);

	}

	public static void trackStats_stop(String stopwatchName){
		// Stopwatch (2)
		Stopwatch.endAndPrint(stopwatchName);
		// MEMORY Usage (2)
		long usedMemoryAfter = recentRT.totalMemory() - recentRT.freeMemory();
		System.out.println("usedMemoryAfter: "+usedMemoryAfter);
		System.out.println("Memory increased:" + ((usedMemoryAfter-recentUsedMemBefore)));
		// CPU Usage (2)
		General.printUsage(recentOSBean);
	}

	/**
	 * This is done to keep the packet size uniform. Make sure to use .trim() when parsing the message
	 * @param originalMsg
	 * @return
	 */
	public static byte[] padMessage(byte[] originalMsg){
		byte[] paddedMsg = new byte[originalMsg.length + (Info.UDP_PACKET_SIZE - originalMsg.length)];
		System.arraycopy(originalMsg,0,paddedMsg,0,originalMsg.length);
		return paddedMsg;
	}

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
