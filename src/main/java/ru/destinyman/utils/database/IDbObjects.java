package ru.destinyman.utils.database;

import ru.destinyman.parsers.Entity;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IDbObjects {
    Map<String, List<Entity>> getTableDescription(Connection connection, String schemaName, String tableName);

    Map<String, List<Entity>> getTableListDescription(Connection connection, String schemaName, String[] tableNames);

    ArrayList<String> getTablesInSchema(Connection connection, String schemaName);
}
