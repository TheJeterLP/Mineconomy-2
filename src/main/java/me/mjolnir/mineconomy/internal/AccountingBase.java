package me.mjolnir.mineconomy.internal;

import java.util.List;
import java.util.Map;

@SuppressWarnings("javadoc")
public abstract class AccountingBase {

    public Map<String, String> hashaccount;

    public List<String> treeaccount;

    public abstract void load();

    public abstract String loadAccount(String account);

    public abstract void reload();

    public abstract void save();

    protected abstract double getBalance(String account);

    protected abstract void setBalance(String account, double amount);

    protected abstract boolean exists(String account);

    protected abstract void delete(String account);

    protected abstract void create(String account);

    protected abstract String getStatus(String account);

    protected abstract void setStatus(String account, String status);

    protected abstract List<String> getAccounts();
}
