/**
 * This file is part of Keys, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.keys.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.helion3.keys.Keys;
import com.helion3.keys.locks.Lock;

public class H2StorageAdapter implements StorageAdapter {
    private final SqlService sql = Keys.getGame().getServiceManager().provide(SqlService.class).get();
    private DataSource db;

    public H2StorageAdapter() {
        String dbPath = Keys.getParentDirectory().getAbsolutePath().toString() + "/keys";
        try {
            // Get data source
            db = sql.getDataSource("jdbc:h2:" + dbPath);

            // Create table if needed
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    protected void createTables() throws SQLException {
        Connection conn = getConnection();

        try {
            String table = "CREATE TABLE IF NOT EXISTS locks (id int primary key auto_increment, user UUID, world UUID, x int, y int, z int, master boolean)";
            conn.prepareStatement(table).execute();

            String index = "CREATE INDEX IF NOT EXISTS location ON locks(world, x, y, z)";
            conn.prepareStatement(index).execute();
        }
        finally {
            conn.close();
        }
    }

    @Override
    public void addKey(Player player, Location<World> location) throws SQLException {
        addLockOrKey(player, location, false);
    }

    @Override
    public boolean allowsAccess(Player player, Location<World> location) throws SQLException {
        boolean allowsAccess = false;
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM locks WHERE world = ? AND x = ? AND y = ? AND z = ?";
            statement = conn.prepareStatement(sql);
            statement.setObject(1, location.getExtent().getUniqueId());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // "do" so we don't need to rewind results
                do {
                    if (resultSet.getObject("user") instanceof UUID) {
                        UUID user = (UUID) resultSet.getObject("user");

                        if (user.equals(player.getUniqueId())) {
                            allowsAccess = true;
                        }
                    }
                } while(resultSet.next());
            } else {
                // Allow access - no locks!
                allowsAccess = true;
            }
        }
        finally {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            conn.close();
        }

        return allowsAccess;
    }

    @Override
    public List<Lock> getLocks(Location<World> location) throws SQLException {
        List<Lock> locks = new ArrayList<Lock>();
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM locks WHERE world = ? AND x = ? AND y = ? AND z = ?";
            statement = conn.prepareStatement(sql);
            statement.setObject(1, location.getExtent().getUniqueId());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID userUuid = null;

                if (resultSet.getObject("user") instanceof UUID) {
                    userUuid = (UUID) resultSet.getObject("user");
                }

                locks.add(new Lock(userUuid, location));
            }
        }
        finally {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            conn.close();
        }

        return locks;
    }

    @Override
    public List<Lock> getMasterLocks(Location<World> location) throws SQLException {
        List<Lock> locks = new ArrayList<Lock>();
        Connection conn = getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM locks WHERE world = ? AND x = ? AND y = ? AND z = ? AND master = ?";
            statement = conn.prepareStatement(sql);
            statement.setObject(1, location.getExtent().getUniqueId());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.setBoolean(5, true);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UUID userUuid = null;

                if (resultSet.getObject("user") instanceof UUID) {
                    userUuid = (UUID) resultSet.getObject("user");
                }

                locks.add(new Lock(userUuid, location));
            }
        }
        finally {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            conn.close();
        }

        return locks;
    }

    @Override
    public boolean ownsLock(Player player, Location<World> location) throws SQLException {
        List<Lock> locks = getMasterLocks(location);
        boolean ownsLock = locks.isEmpty();

        for (Lock lock : getMasterLocks(location)) {
            if (lock.getUserId().equals(player.getUniqueId())) {
                ownsLock = true;
                break;
            }
        }

        return ownsLock;
    }

    @Override
    public void removeLock(Player player, Location<World> location) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM locks WHERE user = ? AND world = ? AND x = ? AND y = ? AND z = ?";
            statement = conn.prepareStatement(sql);
            statement.setObject(1, player.getUniqueId());
            statement.setObject(2, location.getExtent().getUniqueId());
            statement.setInt(3, location.getBlockX());
            statement.setInt(4, location.getBlockY());
            statement.setInt(5, location.getBlockZ());
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }

            conn.close();
        }
    }

    @Override
    public boolean removeLocks(Location<World> location) throws SQLException {
        boolean locksRemoved = false;
        Connection conn = getConnection();
        PreparedStatement statement = null;

        try {
            String sql = "DELETE FROM locks WHERE world = ? AND x = ? AND y = ? AND z = ?";
            statement = conn.prepareStatement(sql);
            statement.setObject(1, location.getExtent().getUniqueId());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            int results = statement.executeUpdate();

            locksRemoved = (results > 0);
        }
        finally {
            if (statement != null) {
                statement.close();
            }

            conn.close();
        }

        return locksRemoved;
    }

    @Override
    public void setLock(Player player, Location<World> location) throws SQLException {
        addLockOrKey(player, location, true);
    }

    protected void addLockOrKey(Player player, Location<World> location, boolean isMaster) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO locks(user, world, x, y, z, master) values(?, ?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(sql);
            statement.setObject(1, player.getUniqueId());
            statement.setObject(2, location.getExtent().getUniqueId());
            statement.setInt(3, location.getBlockX());
            statement.setInt(4, location.getBlockY());
            statement.setInt(5, location.getBlockZ());
            statement.setBoolean(6, isMaster);
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }

            conn.close();
        }
    }
}
