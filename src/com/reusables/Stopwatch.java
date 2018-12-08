package com.reusables;

import java.time.Duration;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Stopwatch {
    static final String default_name = "RUN_";
    static int RUN_COUNT = 0;
    static Queue<Instant> startQueue;
//    static Instant start;
    static Queue<String> names;
    static Instant end;

    public static void start(){
        Instant start = Instant.now();
        if(startQueue == null ){
            startQueue = new ConcurrentLinkedQueue<>();
            names = new ConcurrentLinkedQueue<>();
        }
        startQueue.add(start);
        names.add(default_name+RUN_COUNT);
        RUN_COUNT++;
    }

    public static void start(String name){
        Instant start = Instant.now();
        if(startQueue == null ){
            startQueue = new ConcurrentLinkedQueue<>();
            names = new ConcurrentLinkedQueue<>();
        }
        startQueue.add(start);
        names.add(name);
        RUN_COUNT++;
    }

    public static long end() throws Exception{
        if(startQueue.isEmpty())
            throw new Exception("Stopwatch has not been started yet!");
        end = Instant.now();
        Instant start = startQueue.poll();
        names.poll();
        return Duration.between(start,end).toMillis();
    }

    public static long endAndPrint() throws Exception{
        if(startQueue.isEmpty())
            throw new Exception("Stopwatch has not been started yet!");
        end = Instant.now();
        Instant start = startQueue.poll();
        String name = names.poll();
        long duration = Duration.between(start,end).toMillis();
        System.out.println("Stopped watch for process '"+name+"' after "+duration+"ms.");
		System.out.println();
        return duration;
    }

    public static void reset(){
        startQueue.clear();
        names.clear();
        end = null;
    }
}
