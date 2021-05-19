package ru.destinyman.utils.file;

import ru.destinyman.parsers.Entity;
import java.nio.file.Path;
import java.util.List;

public interface IFileUtils {

    List<Entity> read(Path fileToRead);

    void write(String textToWrite, Path fileToWrite);

}
