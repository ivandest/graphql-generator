package ru.destinyman.utils;

import ru.destinyman.parsers.Entity;
import ru.destinyman.parsers.MarkdownParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MarkdownFileUtils implements IFileUtils {
    @Override
    public List<Entity> read(Path fileToRead) {

        List<Entity> result = new ArrayList<>();
        MarkdownParser markdownParser = new MarkdownParser();

        BufferedReader reader;
        int index = 0;
        try {
            reader = new BufferedReader(new FileReader(fileToRead.toFile()));
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
            reader.close();
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
    public void write(List<String> textToWrite, Path fileToWrite) {

    }
}
