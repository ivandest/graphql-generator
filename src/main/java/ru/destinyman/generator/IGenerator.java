package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;
import java.util.List;
import java.util.Map;

public interface IGenerator {
    String generate(Map<String, List<Entity>> data, String fileName);

    String generateEntityType(Map<String, List<Entity>> data);

    String generateOnlyQueries(Map<String, List<Entity>> data);
}
