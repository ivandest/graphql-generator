package ru.destinyman.utils.database;

import ru.destinyman.parsers.Entity;

import java.sql.*;
import java.util.*;

public class PostgresDbObjects implements IDbObjects {
    @Override
    public Map<String, List<Entity>> getTableDescription(Connection connection, String schemaName, String tableName) {
        Map<String, List<Entity>> result = new HashMap<>();
        try {
                PreparedStatement statement = connection.prepareStatement("select column_name, " +
                        "                     udt_name, " +
                        "                     is_nullable, " +
                        "                     column_default " +
                        "from information_schema.\"columns\" c\n" +
                        "where table_name = ? and table_schema = ?;");

                statement.setString(1, tableName);
                statement.setString(2, schemaName);

                ResultSet rs = statement.executeQuery();

                ArrayList<Entity> entities = new ArrayList<>();
                while (rs.next()) {
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
                result.put(tableName, entities);
                statement.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, List<Entity>> getTableListDescription(Connection connection, String schemaName, String[] tableNames) {
        Map<String, List<Entity>> result = new HashMap<>();
        try {
            PreparedStatement statement;
            if (tableNames.length == 0) {
                statement = connection.prepareStatement("select table_name, " +
                        "column_name, " +
                        "                     udt_name, " +
                        "                     is_nullable, " +
                        "                     column_default " +
                        "from information_schema.\"columns\" c\n" +
                        "where table_schema = ?" +
                        "group by table_name;");

                statement.setString(1, schemaName);
            } else {
                statement = connection.prepareStatement("select table_name, " +
                        "column_name, " +
                        "udt_name, " +
                        "is_nullable, " +
                        "column_default " +
                        "from information_schema.\"columns\" c\n" +
                        "where table_schema = ? " +
                        "and table_name = any(?) " +
                        "group by table_name, column_name, udt_name, is_nullable, column_default;");

                statement.setString(1, schemaName);

                Array tableList = connection.createArrayOf("VARCHAR", Arrays.stream(tableNames).toArray());
                statement.setArray(2, tableList);
            }

            ResultSet rs =  statement.executeQuery();

            ArrayList<Entity> entities = new ArrayList<>();
            while (rs.next()){
                String tableName = rs.getString(1);

                String column = rs.getString(2);
                String dataType = rs.getString(3);
                String comment = rs.getString(5);
                String reference = null;
                if (dataType.contains("enum")) {
                    comment = getEnumDescription(connection, dataType);
                    dataType = "enum";
                }
                if (dataType.contains("id")) {
                    reference = getReference(connection, schemaName, rs.getString(1), column);
                }
                Entity entity = new Entity(column, "comment", dataType, rs.getString(4), reference, comment);
                entities.add(entity);

                result.put(tableName, entities);
            }

            statement.close();

        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        return result;
    }

    @Override
    public ArrayList<String> getTablesInSchema(Connection connection, String schemaName) {
        ArrayList<String> tables = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("select table_name from information_schema.tables where table_schema = ?");
            statement.setString(1, schemaName);

            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                tables.add(rs.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return tables;
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
