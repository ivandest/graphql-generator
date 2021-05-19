package ru.destinyman.utils.database;

import ru.destinyman.parsers.Entity;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class CommonDbUtils {
    public static List<Entity> getDataFromDb(String arg, String tableName) {
        PostgresConnection pgConn = new PostgresConnection();
        Connection connection = pgConn.create(arg);

        Map<String, List<Entity>> entities;
        PostgresDbObjects postgresDbObjects = new PostgresDbObjects();

        entities = postgresDbObjects.getTableDescription(connection, tableName);

        return entities.get(tableName);
    }
}
