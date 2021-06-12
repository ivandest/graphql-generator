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
            PreparedStatement statement = connection.prepareStatement("select column_name, " +
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
                String column = rs.getString(1);
                String dataType = rs.getString(2);
                String comment = rs.getString(4);
                String reference = null;
                if (dataType.contains("enum")) {
                    comment = getEnumDescription(connection, dataType);
                    dataType = "enum";
                }
                if (dataType.contains("id")) {
                    reference = getReference(connection, schemaName, tableName, column);
                }
                Entity entity = new Entity(column, "comment", dataType, rs.getString(3), reference, comment);
                entities.add(entity);
            }
            result.put(objectName, entities);
            statement.close();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    private String getReference(Connection connection, String schemaName, String tableName, String column) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    select rel_tco.table_name || '.id'
                    from information_schema.table_constraints tco
                    join information_schema.key_column_usage kcu
                              on tco.constraint_schema = kcu.constraint_schema
                              and tco.constraint_name = kcu.constraint_name
                    join information_schema.referential_constraints rco
                              on tco.constraint_schema = rco.constraint_schema
                              and tco.constraint_name = rco.constraint_name
                    join information_schema.table_constraints rel_tco
                              on rco.unique_constraint_schema = rel_tco.constraint_schema
                              and rco.unique_constraint_name = rel_tco.constraint_name
                    where tco.constraint_type = 'FOREIGN KEY' and kcu.table_schema = ? and kcu.table_name = ? and kcu.column_name = ?;""");

            statement.setString(1, schemaName);
            statement.setString(2, tableName);
            statement.setString(3, column);

            ResultSet rs =  statement.executeQuery();
            StringBuilder referenceData = new StringBuilder();
            while (rs.next()) {
                referenceData.append(rs.getString(1));
            }
            return referenceData.toString();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private String getEnumDescription(Connection connection, String dataType) {
        try {
            PreparedStatement statement = connection.prepareStatement("""
                    select e.enumlabel from pg_catalog.pg_enum e
                    join pg_catalog.pg_type t on t.oid = e.enumtypid
                    where t.typname = ?
                    order by e.enumsortorder;""");

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
