package me.mjolnir.mineconomy.database;

import java.util.List;

public abstract class AccountingBase {
   
    public abstract double getBalance(String account);

    public abstract void setBalance(String account, double amount);

    public abstract boolean exists(String account);

    public abstract void delete(String account);

    public abstract void create(String account);

    public abstract String getStatus(String account);

    public abstract void setStatus(String account, String status);

    public abstract List<String> getAccounts();
}
