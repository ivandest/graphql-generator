package ru.destinyman.utils.database;

import ru.destinyman.parsers.Entity;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface IDbObjects {
    Map<String, List<Entity>> getTableDescription(Connection connection, String tableName);

}
