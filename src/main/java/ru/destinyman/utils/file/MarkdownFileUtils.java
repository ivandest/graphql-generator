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
import java.util.stream.Collectors;

public class MarkdownFileUtils implements IFileUtils {

    @Override
    public Map<String, List<Entity>> read(Path fileToRead) {
        Map<String, List<Entity>> response = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(fileToRead)) {

            MarkdownParser markdownParser = new MarkdownParser();
            String entityName = "";
            String inputText = "";
            while (reader.read() != -1) {
                inputText = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            for (String item : inputText.split("[d+|).\\s]Таблица")) {
                List<Entity> result = new ArrayList<>();
                if (item.isEmpty()){
                    continue;
                }

                String[] itemRecords = item.split("\r\n");
                entityName = itemRecords[0].trim();
                for (int i = 1; i < itemRecords.length; i++) {
                    Entity entity = markdownParser.parse(itemRecords[i]);
                    if (entity.getCode().isEmpty() || entity.getCode().contains("--") || entity.getCode().toLowerCase().equals("код поля")){
                        continue;
                    }
                    result.add(entity);
                }

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
