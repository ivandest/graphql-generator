package ru.destinyman.utils;

import ru.destinyman.parsers.Entity;
import ru.destinyman.parsers.MarkdownParser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MarkdownFileUtils implements IFileUtils {
    @Override
    public List<Entity> read(Path fileToRead) {

        List<Entity> result = new ArrayList<>();
        MarkdownParser markdownParser = new MarkdownParser();

        int index = 0;
        try (BufferedReader reader = Files.newBufferedReader(fileToRead)) {
            int i = 0;
            while (reader.read() != -1) {
                String currentLine = reader.readLine();
                Entity record = markdownParser.parse(currentLine);

                if (record.getCode().contains("-")){
                    index = i;
                }
                result.add(record);
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (index > 0){
            while (index >= 0) {
                result.remove(index);
                index--;
            }
        }

        return result;
    }

    @Override
    public void write(String textToWrite, Path fileToWrite) {

        try (BufferedWriter writer = Files.newBufferedWriter(fileToWrite, StandardCharsets.UTF_8)) {
                writer.append(textToWrite);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
