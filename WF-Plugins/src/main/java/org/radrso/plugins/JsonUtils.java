package org.radrso.plugins;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raomengnan on 16-9-12.
 */
@Log4j
public class JsonUtils {

    public static <T> T mapToBean(String jsonStr, Class<T> clazz){
        Gson gson = new Gson();
        return gson.fromJson(jsonStr, clazz);
    }

    public static List mapToList(String jsonStr){
        List l = null;
        try {
            l = mapToBean(jsonStr, List.class);
        }catch (Exception e){
            log.error(e);
            l = new ArrayList();
            l.add(jsonStr);
        }
        return l;
    }

    public static <T> T loadJsonFile(String filePath, Class<T> clazz){

        FileInputStream fileInputStream = null;
        try {
            Gson gson = new Gson();
            filePath = filePath.replace("%20", " ");
            fileInputStream = new FileInputStream(filePath);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fileInputStream)
            );

            return gson.fromJson(reader, clazz);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        }finally {
            try {
                if(fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String str = "['test', 'test2', 'test3']";
        List<String> test = mapToList(str);
        test.forEach(o->{
            System.out.println(o);
        });
    }
}
