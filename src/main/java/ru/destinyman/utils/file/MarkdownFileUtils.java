package ru.destinyman.utils.file;

import ru.destinyman.parsers.Entity;
import ru.destinyman.parsers.MarkdownParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkdownFileUtils implements IFileUtils {

    @Override
    public Map<String, List<Entity>> read(Path fileToRead) {
        Map<String, List<Entity>> response = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(fileToRead)) {
            List<Entity> result = new ArrayList<>();
            MarkdownParser markdownParser = new MarkdownParser();
            String entityName = "";
            while (reader.read() != -1) {
                String currentLine = reader.readLine();

                if (currentLine.contains("Таблица")) {
                    entityName = currentLine.split(" ")[1];
                    result.clear();
                    continue;
                }

                if (currentLine.contains("--") || currentLine.contains("код поля")){
                    result.clear();
                    continue;
                }
                Entity record = markdownParser.parse(currentLine);
                result.add(record);
                response.put(entityName, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void write(String textToWrite, Path fileToWrite) {

        try (BufferedWriter writer = Files.newBufferedWriter(fileToWrite, StandardCharsets.UTF_8)) {
                writer.append(textToWrite);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}
