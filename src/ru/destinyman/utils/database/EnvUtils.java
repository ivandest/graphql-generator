package ru.destinyman.utils.database;

import ru.destinyman.parsers.Entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvUtils {

    private static ArrayList<String> readEnvFile() {
        
        String filePath = ".env";
        Path fileToRead = Paths.get(filePath);
        ArrayList<String> result = new ArrayList<>();
        String currentLine;
        try (BufferedReader reader = Files.newBufferedReader(fileToRead)) {
            while ((currentLine = reader.readLine()) != null) {
                result.add(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    return result;
    }

    public static Map<String, String> getEnv() {
        ArrayList<String> envValues = readEnvFile();
        Map<String, String> result = new HashMap<>();
        String separator = "=";
        for (String value: envValues) {
            result.put(value.split(separator)[0].trim(), value.split(separator)[1].trim());
        }
        return result;
    }
}
