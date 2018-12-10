package com.reusables;

import com.main.Info;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Stopwatch {
    static final String default_name = "RUN_";
    static int RUN_COUNT = 0;
    static HashMap<String, Instant> startMap;
    static Queue<String> names;
//    static Instant start;
    static Instant end;

    static int AGGR_COUNT = 0;
    static ArrayList<Instant[]> toAggregate;

    public static void start_aggregate(){
        Instant start = Instant.now();
        if(toAggregate == null)
            toAggregate = new ArrayList<>();
        Instant[] e = new Instant[2];
        e[0] = start;
        toAggregate.add(e);
    }

    public static void stop_aggregate(){
        Instant stop = Instant.now();
        toAggregate.get(AGGR_COUNT)[1] = stop;
        AGGR_COUNT++;
    }

    public static long getAggregate(){
        long aggr = 0;
        for(Instant[] e : toAggregate){
            aggr += Duration.between(e[0],e[1]).toMillis();
        }
        aggr /= toAggregate.size();
        return aggr;
    }

    public static long getAggregateAndPrint(){
        long aggr = 0;
        for(Instant[] e : toAggregate){
            aggr += Duration.between(e[0],e[1]).toMillis();
        }
        aggr /= toAggregate.size();
        System.out.println("STOPWATCH:: Average of "+aggr+"ms in a total of "+toAggregate.size()+" laps!");
        return aggr;
    }

    public static void start(){
        Instant start = Instant.now();
        if(startMap == null ){
            startMap = new HashMap<>();
            names = new ConcurrentLinkedQueue<>();
        }
        startMap.put(default_name+RUN_COUNT, start);
        names.add(default_name+RUN_COUNT);
        RUN_COUNT++;
    }

    public static void start(String name){
        Instant start = Instant.now();
        if(startMap == null ){
            startMap = new HashMap<>();
            names = new ConcurrentLinkedQueue<>();
        }
        startMap.put(name,start);
        names.add(name);
        RUN_COUNT++;
    }

    public static long end(String name){
        end = Instant.now();
        if(startMap.isEmpty())
            return -1;

        Instant start = startMap.get(name);
        names.removeIf(a -> a.equals(name));
        return Duration.between(start,end).toMillis();
    }

    public static long end() {
        end = Instant.now();
        if(names.isEmpty())
            return -1;

        Instant start = startMap.get(names.poll());
        return Duration.between(start,end).toMillis();
    }

    public static long endAndPrint(){
        end = Instant.now();
        if(names.isEmpty())
            return -1;

        String name = names.poll();
        Instant start = startMap.get(names.poll());
        long duration = Duration.between(start,end).toMillis();
        System.out.println("STOPWATCH:: Stopped watch for process '"+name+"' after "+duration+"ms.");
        return duration;
    }

    public static long endAndPrint(String name){
        end = Instant.now();
        if(names.isEmpty())
            return -1;

        Instant start = startMap.get(name);
        names.removeIf(a -> a.equals(name));
        long duration = Duration.between(start,end).toMillis();
        System.out.println("STOPWATCH:: Stopped watch for process '"+name+"' after "+duration+"ms.");
        return duration;
    }

    public static void reset(){
        startMap.clear();
        startMap = null;
        names.clear();
        end = null;
    }
}
