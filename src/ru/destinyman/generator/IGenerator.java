package ru.destinyman.generator;

import ru.destinyman.parsers.Entity;

import java.util.HashMap;
import java.util.List;

public interface IGenerator {
    String generate(List<Entity> data, String fileName);

    String generateEntityType(List<Entity> data, String entityName);
}
