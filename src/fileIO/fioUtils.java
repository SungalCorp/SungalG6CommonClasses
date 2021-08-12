/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileIO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author danrothman
 */
public class fioUtils {

    public static void writeToFile(ArrayList<String> arrayList, String filename) throws IOException {
        PrintWriter writer = new PrintWriter(filename);
        for (String line : arrayList) {
            writer.println(line);
        }
        writer.close();
    }

    public static ArrayList<String> readFromFile(String fileName) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line="";
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
}
