package ru.destinyman.utils.file;

import ru.destinyman.parsers.Entity;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface IFileUtils {

    Map<String, List<Entity>> read(Path fileToRead);

    void write(String textToWrite, Path fileToWrite);

}
