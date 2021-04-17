package ru.destinyman.utils.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class PostgresConnection {

    public Connection create () {
        Map<String, String> env = EnvUtils.getEnv();
        String url = "jdbc:postgresql://" + env.get("POSTGRES_HOST") + "/"
                + env.get("POSTGRES_DB") + "?user=" + env.get("POSTGRES_USER") + "&password=" + env.get("POSTGRES_PASSWORD");
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
