package com.sample.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.*;

public class ConfigReader {

    private static Properties prop;

    public static Object[][] readProperties(String filePath){
        try{
            FileInputStream fis = new FileInputStream(filePath);
            prop = new Properties();
            prop.load(fis);
            fis.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return new Object[0][];
    }
    public static String getProperty(String key){
        return prop.getProperty(key);
    }
}
