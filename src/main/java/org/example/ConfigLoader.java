package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigLoader {

    public static AppConfig load(String path) throws FileNotFoundException, JsonSyntaxException {

        File f = new File(path);

        if (!f.exists()) {
            throw new FileNotFoundException("Fisierul de configurare '" + path + "' nu exista.");
        }

        Gson gson = new Gson();
        FileReader reader = new FileReader(f);

        return gson.fromJson(reader, AppConfig.class);
    }
}
