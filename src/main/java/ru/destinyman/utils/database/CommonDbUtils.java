package ru.destinyman.utils.database;

import ru.destinyman.parsers.Entity;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class CommonDbUtils {
    public static Map<String, List<Entity>> getDataFromDb(String arg, String schemaName, String[] tableNames) {
        PostgresConnection pgConn = new PostgresConnection();
        Connection connection = pgConn.create(arg);
        PostgresDbObjects postgresDbObjects = new PostgresDbObjects();

        return postgresDbObjects.getTableListDescription(connection, schemaName, tableNames);
    }
}
