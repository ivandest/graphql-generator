package ru.destinyman.utils.menu;

import ru.destinyman.parsers.Entity;

import java.util.List;
import java.util.Map;

public record DataFromDatabase(String schema, Map<String, List<Entity>> data) {}
