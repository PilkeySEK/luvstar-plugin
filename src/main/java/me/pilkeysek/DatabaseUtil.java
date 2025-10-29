package me.pilkeysek;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;

import me.pilkeysek.data.ChestLockData;

public class DatabaseUtil {
    public Connection connection;
    private final String DEFAULT_DB = "postgres";
    private final String target_db;
    private final String host;
    private final String port;
    private final String user;
    private final String password;

    public DatabaseUtil(String target_db, String host, String port, String user, String password) {
        this.target_db = target_db;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        try {
            Class.forName("me.pilkeysek.libs.postgresql.Driver");
            createDatabaseIfNotExist();
            createTablesIfNotExist();
        } catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void createDatabaseIfNotExist() throws SQLException {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + DEFAULT_DB;
        try(Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + target_db + "'"
                );
                if(!rs.next()) {
                    stmt.executeUpdate("CREATE DATABASE " + target_db);
                    LuvstarPlugin.instance.logInfo("Created database");
                }
            }
    }
    private void createTablesIfNotExist() throws SQLException {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + target_db;
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT 1 FROM information_schema.tables " +
                "WHERE table_schema = 'public' AND table_name = 'chests'"
            );
            if(!rs.next()) {
                stmt.executeUpdate(
                    "CREATE TABLE CHESTS (" +
                    "world VARCHAR(64)," +
                    "x INT, y INT, z INT, " +
                    "owner VARCHAR(64), " +
                    "locked BOOLEAN, " +
                    "UNIQUE (world, x, y, z)" +
                    ")"
                );
                LuvstarPlugin.instance.logInfo("Created table");
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    private int upsertChest(String world, int x, int y, int z, String owner, boolean locked) {
        String sql = "INSERT INTO chests (world, x, y, z, owner, locked) VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (world, x, y, z) DO UPDATE " +
                     "SET owner = EXCLUDED.owner, locked = EXCLUDED.locked " +
                     "WHERE chests.owner IS DISTINCT FROM EXCLUDED.owner OR chests.locked IS DISTINCT FROM EXCLUDED.locked";
        try {
             PreparedStatement stmt = connection.prepareStatement(sql);

            stmt.setString(1, world);
            stmt.setInt(2, x);
            stmt.setInt(3, y);
            stmt.setInt(4, z);
            stmt.setString(5, owner);
            stmt.setBoolean(6, locked);
            int affected = stmt.executeUpdate();

            return affected;
        } catch (SQLException e) {
            return -1;
        }
    }

    public ChestLockData getChestLockData(Location loc) {
        if(connection == null) return null;
        int x = ((int)loc.getX());
        int y = ((int)loc.getY());
        int z = ((int)loc.getZ());
        try {
            Statement st = connection.createStatement();
            ResultSet res = st.executeQuery("SELECT * FROM chests WHERE x = " + x + " AND y = " + y + " AND z = " + z + " AND world = " + loc.getWorld().getName() + " LIMIT 1");
            if(!res.next()) return null;
            ChestLockData data = new ChestLockData(loc, res.getString("owner"), res.getBoolean("locked"));
            st.close();
            return data;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public int setChestLockData(ChestLockData data) {
        if(connection == null) return -2;
        return upsertChest(data.getWorldName(), (int) data.loc.getX(), (int) data.loc.getY(), (int) data.loc.getZ(), data.owner, data.locked);
    }
}
