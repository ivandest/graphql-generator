package ru.destinyman.utils.database;

import ru.destinyman.parsers.Entity;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresDbObjects implements IDbObjects {
    @Override
    public Map<String, List<Entity>> getTableDescription(Connection connection, String objectName) {
        Map<String, List<Entity>> result = new HashMap<>();
        try {
            PostgresConnection postgresConnection = new PostgresConnection();
            PreparedStatement statement = postgresConnection.create().prepareStatement("select table_schema, " +
                    "                     table_name, " +
                    "                     column_name, " +
                    "                     udt_name, " +
                    "                     is_nullable, " +
                    "                     column_default " +
                    "from information_schema.\"columns\" c\n" +
                    "where table_name = ? and table_schema = ?;");

            String schemaName = objectName.split("\\.")[0];
            String tableName = objectName.split("\\.")[1];
            statement.setString(1, tableName);
            statement.setString(2, schemaName);

            ResultSet rs =  statement.executeQuery();

            ArrayList<Entity> entities = new ArrayList<>();
            while (rs.next()){
                Entity entity = new Entity(rs.getString(3), "comment", rs.getString(4), rs.getString(5), "link", rs.getString(6));
                entities.add(entity);
            }
            result.put(objectName, entities);
            statement.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }
}
