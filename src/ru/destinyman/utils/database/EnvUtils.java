package ru.destinyman.utils.database;

import ru.destinyman.utils.ErrorText;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

    public static Map<String, String> getFromCli(String connectionString) {
        Map<String, String> result = new HashMap<>();
        String[] connectionArgs = connectionString.split(":");
        if (connectionArgs.length != 5) {
            throw new Error(ErrorText.CONNECTION_STRING_NOT_VALID.getMessage());
        }
        result.put("POSTGRES_HOST", connectionArgs[0]);
        result.put("POSTGRES_PORT", connectionArgs[1]);
        result.put("POSTGRES_DB", connectionArgs[2]);
        result.put("POSTGRES_USER", connectionArgs[3]);
        result.put("POSTGRES_PASSWORD", connectionArgs[4]);
        return result;
    }
}
