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
package me.mjolnir.mineconomy.database;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import me.mjolnir.mineconomy.Config;
import me.mjolnir.mineconomy.MineConomy;

public class DatabaseFactory {
    
    private static Database db = null;
    
    public static void init() {
        if (Config.MYSQL_USE.getBoolean()) {
            db = new MySQL(Config.MYSQL_IP.getString(), Config.MYSQL_USER.getString(), Config.MYSQL_PASSWORD.getString(), Config.MYSQL_DATABASE.getString(), Config.MYSQL_PORT.getInteger());
        } else {
            db = new SQLite(new File(MineConomy.getInstance().getDataFolder(), "Database.db"));
        }
        
        if (db.testConnection()) {
            createTables();
            MineConomy.getInstance().getLogger().log(Level.INFO, "Connected to database via {0}", db.getClass().getSimpleName());
        } else {
            MineConomy.getInstance().getLogger().log(Level.SEVERE, "Could not connect to the Database!");
        }
        
    }
    
    private static void createTables() {
        try {   
            String ACCOUNT_TABLE;
            if (db.getType() == Database.Type.SQLITE) {               
                ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS `mineconomy_accounts` ("
                        + "`id` INTEGER PRIMARY KEY NOT NULL AUTOINCREMENT,"
                        + "`account` varchar(64) NOT NULL,"
                        + "`balance` DOUBLE NOT NULL"
                        + ");";                             
            } else {
                ACCOUNT_TABLE = "CREATE TABLE IF NOT EXISTS `mineconomy_accounts` ("
                        + "`id` INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,"
                        + "`account` varchar(64) NOT NULL,"
                        + "`balance` DOUBLE NOT NULL"
                        + ");";              
            }
            db.executeStatement(ACCOUNT_TABLE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static Database getDatabase() {
        return db;
    }
    
}
