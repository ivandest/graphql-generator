package ru.destinyman.utils;

import ru.destinyman.parsers.Entity;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public interface IFileUtils {

    List<Entity> read(Path fileToRead);

    void write(List<String> textToWrite, Path fileToWrite);

}
