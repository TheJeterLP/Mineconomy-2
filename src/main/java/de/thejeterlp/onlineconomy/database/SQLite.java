/*
 * Copyright (C) 2019 TheJeterLP
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.thejeterlp.onlineconomy.database;

import java.io.File;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import de.thejeterlp.onlineconomy.Config;

public class SQLite extends Database {

    private final File dbFile;

    /**
     * Creates a new instance for SQLite databases.
     *
     * @param dbFile Database file
     */
    public SQLite(File dbFile) {
        super("org.sqlite.JDBC", Type.SQLITE);
        dbFile.getParentFile().mkdirs();
        this.dbFile = dbFile;
    }

    @Override
    public void reactivateConnection() throws SQLException {
        setConnection(DriverManager.getConnection("jdbc:sqlite://" + dbFile.getAbsolutePath()));
    }

    @Override
    public double getBalance(UUID account) {
        try {
            PreparedStatement s = getPreparedStatement("SELECT * FROM `OnlineConomy_accounts` WHERE `account` = ?;");
            s.setString(1, account.toString());
            ResultSet rs = s.executeQuery();

            double balance = 0;

            if (rs.next()) {
                balance = rs.getDouble("balance");
            }

            closeResultSet(rs);
            closeStatement(s);
            return balance;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public void setBalance(UUID account, double amount) {
        amount = (double) Math.round(amount * 100) / 100;

        if (Config.MAX_BALANCE.getDouble() > 0 && amount > Config.MAX_BALANCE.getDouble()) {
            amount = Config.MAX_BALANCE.getDouble();
        }

        try {
            PreparedStatement st = getPreparedStatement("UPDATE `OnlineConomy_accounts` SET `balance` = ? WHERE `account` = ?;");
            st.setDouble(1, amount);
            st.setString(2, account.toString());
            st.executeUpdate();
            closeStatement(st);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean exists(UUID account) {
        try {
            PreparedStatement s = getPreparedStatement("SELECT * FROM `OnlineConomy_accounts` WHERE `account` = ?;");
            s.setString(1, account.toString());
            ResultSet rs = s.executeQuery();
            boolean ret = rs.next();
            closeResultSet(rs);
            closeStatement(s);
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void delete(UUID account) {
        try {
            PreparedStatement st = getPreparedStatement("DELETE FROM `OnlineConomy_accounts` WHERE `account` = ?;");
            st.setString(1, account.toString());
            st.executeUpdate();
            closeStatement(st);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create(UUID account) {
        try {
            PreparedStatement st = getPreparedStatement("INSERT INTO `OnlineConomy_accounts` (account, balance) VALUES (?,?);");
            st.setString(1, account.toString());
            st.setDouble(2, Config.STARTING_BALANCE.getDouble());
            st.executeUpdate();
            closeStatement(st);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<UUID> getAccounts() {
        List<UUID> result = new ArrayList<>();

        try {
            PreparedStatement s = getPreparedStatement("SELECT account FROM `OnlineConomy_accounts`;");
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String uu = rs.getString("account");
                UUID uuid = UUID.fromString(uu);
                result.add(uuid);
            }

            closeResultSet(rs);
            closeStatement(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

}
