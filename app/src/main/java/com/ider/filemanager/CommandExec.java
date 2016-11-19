package com.ider.filemanager;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ider-eric on 2016/11/1.
 */

public class CommandExec {

    public static ArrayList<String> execCommand(String command) {
        ArrayList<String> result = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec(command);
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            while(br.readLine() != null) {
                result.add(br.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return result;
    }

}
