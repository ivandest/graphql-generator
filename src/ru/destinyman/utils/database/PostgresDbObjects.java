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
            PreparedStatement statement = connection.prepareStatement("select table_schema, " +
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
                String dataType = rs.getString(4);
                String comment = rs.getString(6);
                if (dataType.contains("enum")) {
                    comment = getEnumDescription(connection, dataType);
                    dataType = "enum";
                }
                Entity entity = new Entity(rs.getString(3), "comment", dataType, rs.getString(5), "link", comment);
                entities.add(entity);
            }
            result.put(objectName, entities);
            statement.close();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    private String getEnumDescription(Connection connection, String dataType) {
        try {
            PreparedStatement statement = connection.prepareStatement("select e.enumlabel from pg_catalog.pg_enum e\n" +
                    "join pg_catalog.pg_type t on t.oid = e.enumtypid\n" +
                    "where t.typname = ?\n" +
                    "order by e.enumsortorder;");

            statement.setString(1, dataType);

            ResultSet rs =  statement.executeQuery();
            StringBuilder enumData = new StringBuilder();
            while (rs.next()) {
                enumData.append(rs.getString(1)).append(",");
            }
            return enumData.toString();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
