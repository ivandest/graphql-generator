package ru.destinyman.utils;

import ru.destinyman.parsers.Entity;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public interface IFileUtils {

    List<Entity> read(Path fileToRead);

    void write(String textToWrite, Path fileToWrite) throws IOException;

}
