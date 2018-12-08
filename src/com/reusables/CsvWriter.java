package com.reusables;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CsvWriter {

    public static void write(ArrayList<Integer> list){
        String filename = "CSV writer";

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename+".csv"));
            for(int i = 0; i < list.size(); i++) {
            	out.write(list.get(i)+",\n");
            }
            
            out.close();
            System.out.println("File created successfully");
         }
         catch (IOException e) {
         }
    }

    public static void write(ArrayList<Integer> list, String filename){
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename+".csv"));
            for(int i = 0; i < list.size(); i++) {
                out.write(list.get(i)+",\n");
            }

            out.close();
            System.out.println("File created successfully");
        }
        catch (IOException e) {
        }
    }
}
