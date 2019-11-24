package me.mjolnir.mineconomy.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.mjolnir.mineconomy.internal.util.IOH;

@SuppressWarnings("javadoc")
public final class MySqlAccounting extends AccountingBase {

    private static Connection con = null;
    private static final String driver = "com.mysql.jdbc.Driver";

    protected MySqlAccounting() {
        //
    }

    @Override
    public void load() {
        IOH.log("Loading Accounts from database...", IOH.INFO);

        try {
            Class.forName(driver).newInstance(); //Settings.dburl + ":3306/"
            con = DriverManager.getConnection("jdbc:mysql://" + Settings.dburl + Settings.dbname, Settings.dbuser, Settings.dbpass);
            Statement st = con.createStatement();
            String com = "CREATE TABLE IF NOT EXISTS `mineconomy_accounts` (`id` int(8) NOT NULL AUTO_INCREMENT, `account` text NOT NULL, `balance` double NOT NULL, `status` text NOT NULL, PRIMARY KEY (`id`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;";
            st.execute(com);
            com = "SELECT * FROM mineconomy_accounts WHERE id = '1'";
            st.execute(com);
        } catch (Exception e) {
            IOH.error("MySQL Error", e);
        }

        List<String> result = getAccounts();

        hashaccount = new HashMap<>();
        treeaccount = new ArrayList<>();

        for (int i = 0; result.size() > i; i++) {
            hashaccount.put(result.get(i).toLowerCase(), result.get(i));
            treeaccount.add(result.get(i).toLowerCase());
        }

        IOH.log("Accounts loaded from database!", IOH.INFO);
    }

    @Override
    public String loadAccount(String account) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT account FROM mineconomy_accounts WHERE LOWER(account) = LOWER(?)");
            ps.setString(1, account);

            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                String result = rs.getString("account");
                hashaccount.put(result.toLowerCase(), result);
                treeaccount.add(result.toLowerCase());
                return result;
            }
        } catch (SQLException ex) {
            IOH.error("MySQL Error", ex);
        }
        return "";
    }

    @Override
    public void reload() {
    }

    @Override
    public void save() {
    }

    @Override
    protected double getBalance(String account) {
        try {
            ResultSet st = con.createStatement().executeQuery("SELECT balance FROM mineconomy_accounts WHERE account = '" + account + "'");
            st.next();
            return st.getDouble(1);
        } catch (SQLException e) {
            IOH.error("MySQL Error", e);
            return 0;
        }
    }

    @Override
    protected void setBalance(String account, double amount) {
        amount = (double) Math.round(amount * 100) / 100;

        if (Settings.maxBalance > 0 && amount > Settings.maxBalance) {
            amount = Settings.maxBalance;
        }

        try {
            Statement st = con.createStatement();
            String com = "UPDATE mineconomy_accounts SET balance = '" + amount + "' WHERE account = '" + account + "';";
            st.execute(com);
        } catch (Exception e) {
            IOH.error("MySQL Error", e);
        }
    }

    @Override
    protected boolean exists(String account) {
        try {
            ResultSet st = con.createStatement().executeQuery("SELECT * FROM mineconomy_accounts WHERE account = '" + account + "'");
            return st.next();
        } catch (SQLException e) {
            IOH.error("MySQL Error", e);
            return false;
        }
    }

    @Override
    protected void delete(String account) {
        try {
            Statement st = con.createStatement();
            String com = "DELETE FROM mineconomy_accounts WHERE account = '" + account + "';";
            st.execute(com);
            hashaccount.remove(account.toLowerCase());
            treeaccount.remove(account.toLowerCase());
        } catch (Exception e) {
            IOH.error("MySQL Error", e);
        }
    }

    @Override
    protected void create(String account) {
        try {
            Statement st = con.createStatement();
            String com = "INSERT INTO mineconomy_accounts(account, balance, status) VALUES ('" + account + "', " + Settings.startingBalance + ", 'NORMAL')";
            st.execute(com);
            hashaccount.put(account.toLowerCase(), account);
            treeaccount.add(account.toLowerCase());
        } catch (Exception e) {
            IOH.error("MySQL Error", e);
        }
    }
    
    protected String getStatus(String account) {
        try {
            ResultSet st = con.createStatement().executeQuery("SELECT status FROM mineconomy_accounts WHERE account = '" + account + "'");
            st.next();
            return st.getString(1);
        } catch (SQLException e) {
            IOH.error("MySQL Error", e);
            return "";
        }
    }

    protected void setStatus(String account, String status) {
        try {
            Statement st = con.createStatement();
            String com = "UPDATE mineconomy_accounts SET status = '" + status + "' WHERE account = '" + account + "';";
            st.execute(com);
        } catch (Exception e) {
            IOH.error("MySQL Error", e);
        }
    }

    protected ArrayList<String> getAccounts() {
        ArrayList<String> result = new ArrayList<String>();

        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT account FROM mineconomy_accounts");

            while (rs.next()) {
                result.add(rs.getString("account"));
            }
        } catch (SQLException e) {
            IOH.error("MySQL Error", e);
        }

        return result;
    }
}
