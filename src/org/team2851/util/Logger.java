package org.team2851.util;

import java.io.FileWriter;
import java.io.IOException;

public class Logger
{
    private Logger() { }

    public static void println(String message)
    {
        FileWriter fw;
        try
        {
            fw = new FileWriter("/home/lvuser/log");
            fw.append(message);
            fw.close();
        } catch (IOException e) {
            System.err.println("Unable to Print Message");
        }

        System.out.println(message);
    }

    public static void printerr(String message)
    {
        FileWriter fw;
        try
        {
            fw = new FileWriter("/home/lvuser/log");
            fw.append("[ERROR!]: " + message);
            fw.close();
        } catch (IOException e) {
            System.err.println("Unable to Print Message");
        }

        System.out.println(message);
    }
}
