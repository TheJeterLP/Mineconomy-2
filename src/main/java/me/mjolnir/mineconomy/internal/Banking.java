package me.mjolnir.mineconomy.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.mjolnir.mineconomy.Config;

import me.mjolnir.mineconomy.MineConomy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Handles banking actions.
 *
 * @author MjolnirCommando
 */
public class Banking {

    private static final File bankFile = new File(MineConomy.getInstance().getDataFolder(), "banks.yml");
    private static YamlConfiguration banks;

    private static final List<String> banklist = new ArrayList<>();

    private static final Map<String, String> bankstatus = new HashMap<>();
    private static final Map<String, Double> bankstartingbalance = new HashMap<>();
    private static final Map<String, Double> bankmaxdebt = new HashMap<>();
    private static final Map<String, Map<String, String>> bankaccountstatus = new HashMap<>();
    private static final Map<String, Map<String, Double>> bankaccountbalance = new HashMap<>();
    private static final Map<String, List<String>> bankaccounts = new HashMap<>();

    private static List<String> bankaccountstemp = new ArrayList<>();
    private static Map<String, Double> bankaccountbalancetemp = new HashMap<>();
    private static Map<String, String> bankaccountstatustemp = new HashMap<>();


    /*
     * .: Hashtable Key :.
     * bankstatus = Bank, Status
     * bankstartingbalance = Bank, Balance
     * bankmaxdebt = Bank, Max Debt
     * bankaccountstatus = Bank, <Account, Status>
     * bankaccountbalance = Bank, <Account, Balance>
     *
     * .: Status Key :.
     * Public - All Access
     * Private-Group - No read or write except for account owners
     * Limited-Group - No read or write except for account owners and MineConomy
     * Private-Individual - No read or write except for account owner
     * Limited-Individual - No read or write except for account owner and MineConomy
     * Locked - No access except for MineConomy Bank Admin
     */
    /**
     * Loads bank file
     */
    public static void load() {
        banks = YamlConfiguration.loadConfiguration(bankFile);
        banks.options().header("=== MineConomy Banks ===\n\n    Do not edit!\n");

        if (!bankFile.exists()) {            
            banks.set("Banks", "");
            save();
        }
        reload();
    }

    /**
     * Reloads bank file
     */
    public static void reload() {
        banks = YamlConfiguration.loadConfiguration(bankFile);
        banklist.clear();
        bankstatus.clear();
        bankmaxdebt.clear();
        bankstartingbalance.clear();
        bankaccountstatus.clear();
        bankaccountbalance.clear();
        bankaccountstatustemp.clear();
        bankaccountbalancetemp.clear();
        bankaccounts.clear();
        bankaccountstemp.clear();

        ConfigurationSection cs = banks.getConfigurationSection("Banks");
        Object[] t;
        try {
            t = cs.getKeys(true).toArray();
        } catch (NullPointerException e) {
            return;
        }

        for (int i = 0; t.length > i; i++) {
            String[] parent = t[i].toString().replace(".", "-").split("-");

            if (parent.length == 1) {
                banklist.add(parent[0]);
            }
        }

        for (int i = 0; banklist.size() > i; i++) {
            String bank = banklist.get(i);
            cs = banks.getConfigurationSection("Banks." + bank);
            t = cs.getKeys(true).toArray();

            for (int j = 0; t.length > j; j++) {
                if (t[j].equals("Status")) {
                    bankstatus.put(bank, banks.get("Banks." + bank + ".Status")
                            .toString());
                } else if (t[j].equals("Max Debt")) {
                    bankmaxdebt.put(
                            bank,
                            Math.abs(Double.parseDouble(banks.get(
                                    "Banks." + bank + ".Max Debt").toString())));
                } else if (t[j].equals("Starting Balance")) {
                    bankstartingbalance.put(bank, Double.parseDouble(banks.get(
                            "Banks." + bank + ".Starting Balance").toString()));
                } else if (t[j].equals("Accounts")) {
                    ArrayList<String> accounts = new ArrayList<String>();

                    ConfigurationSection cs2 = banks
                            .getConfigurationSection("Banks." + bank
                                    + ".Accounts");
                    Object[] t2 = cs2.getKeys(true).toArray();

                    for (int k = 0; t2.length > k; k++) {
                        String[] account = t2[k].toString().replace(".", "-")
                                .split("-");

                        if (account.length == 1) {
                            bankaccountstemp.add(account[0]);
                            accounts.add(account[0]);
                        }
                    }

                    if (bankaccountstemp.size() > 0) {
                        bankaccounts.put(bank, bankaccountstemp);
                    }

                    Map<String, String> statustemp = new HashMap<>();
                    Map<String, Double> balancetemp = new HashMap<>();

                    for (int k = 0; accounts.size() > k; k++) {
                        String account = accounts.get(k);

                        cs2 = banks.getConfigurationSection("Banks." + bank
                                + ".Accounts." + account);
                        t2 = cs2.getKeys(true).toArray();

                        for (int l = 0; t2.length > l; l++) {
                            if (t2[l].equals("Status")) {

                                statustemp.put(
                                        account,
                                        banks.get(
                                                "Banks." + bank + ".Accounts."
                                                + account + ".Status")
                                                .toString());

                            } else if (t2[l].equals("Balance")) {

                                balancetemp.put(account, Double.parseDouble(banks.get(
                                        "Banks." + bank + ".Accounts."
                                        + account + ".Balance")
                                        .toString()));

                            }
                        }
                    }

                    bankaccountbalance.put(bank, balancetemp);
                    bankaccountstatus.put(bank, statustemp);
                }
            }
        }
    }

    /**
     * Saves bank file
     */
    public static void save() {
        banks.set("Banks", "");

        for (int i = 0; banklist.size() > i; i++) {
            String bank = banklist.get(i);
            banks.set("Banks." + bank + ".Max Debt", bankmaxdebt.get(bank));
            banks.set("Banks." + bank + ".Status", bankstatus.get(bank));
            banks.set("Banks." + bank + ".Starting Balance",
                    bankstartingbalance.get(bank));

            bankaccountstemp = bankaccounts.get(bank);
            bankaccountstatustemp = bankaccountstatus.get(bank);
            bankaccountbalancetemp = bankaccountbalance.get(bank);

            for (int j = 0; bankaccountstemp.size() > j; j++) {
                String account = bankaccountstemp.get(j);

                if (!account.equals("_Default")) {
                    banks.set("Banks." + bank + ".Accounts." + account
                            + ".Status", bankaccountstatustemp.get(account));
                    banks.set("Banks." + bank + ".Accounts." + account
                            + ".Balance", bankaccountbalancetemp.get(account));
                }
            }
        }

        try {
            banks.save(bankFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        reload();
    }

    protected static double getBalance(String bank, String account) {
        bankaccountbalancetemp = bankaccountbalance.get(bank);
        return bankaccountbalancetemp.get(account);
    }

    protected static void setBalance(String bank, String account, double amount) {
        amount = (double) Math.round(amount * 100) / 100;

        if (Config.MAX_BALANCE.getDouble() > 0 && amount > Config.MAX_BALANCE.getDouble()) {
            amount = Config.MAX_BALANCE.getDouble();
        }

        bankaccountbalancetemp = bankaccountbalance.get(bank);
        bankaccountbalancetemp.put(account, amount);
        bankaccountbalance.put(bank, bankaccountbalancetemp);
    }

    protected static String getStatus(String bank, String account) {
        bankaccountstatustemp = bankaccountstatus.get(bank);
        return bankaccountstatustemp.get(account);
    }

    protected static void setStatus(String bank, String account, String status) {
        bankaccountstatustemp = bankaccountstatus.get(bank);
        bankaccountstatustemp.put(account, status);
        bankaccountstatus.put(bank, bankaccountstatustemp);
    }

    protected static void create(String bank, String account) {
        bankaccountstatustemp = bankaccountstatus.get(bank);
        bankaccountstatustemp.put(account, "Public");
        bankaccountstatus.put(bank, bankaccountstatustemp);
        bankaccountbalancetemp = bankaccountbalance.get(bank);
        bankaccountbalancetemp.put(account, bankstartingbalance.get(bank));
        bankaccountbalance.put(bank, bankaccountbalancetemp);
        bankaccountstemp = bankaccounts.get(bank);

        if (bankaccountstemp.contains("_Default")) {
            bankaccountstemp.remove("_Default");
        }

        bankaccountstemp.add(account);
        bankaccounts.put(bank, bankaccountstemp);
    }

    protected static void delete(String bank, String account) {
        bankaccountstatustemp = bankaccountstatus.get(bank);
        bankaccountstatustemp.remove(account);
        bankaccountstatus.put(bank, bankaccountstatustemp);
        bankaccountbalancetemp = bankaccountbalance.get(bank);
        bankaccountbalancetemp.remove(account);
        bankaccountbalance.put(bank, bankaccountbalancetemp);
        bankaccountstemp = bankaccounts.get(bank);
        bankaccountstemp.remove(account);
        bankaccounts.put(bank, bankaccountstemp);
    }

    protected static boolean accountExists(String bank, String account) {
        bankaccountbalancetemp = bankaccountbalance.get(bank);
        return bankaccountbalancetemp.containsKey(account);
    }

    protected static String getStatus(String bank) {
        return bankstatus.get(bank);
    }

    protected static void setStatus(String bank, String status) {
        bankstatus.put(bank, status);
    }

    protected static double getMaxDebt(String bank) {
        return bankmaxdebt.get(bank);
    }

    protected static void setMaxDebt(String bank, double amount) {
        bankmaxdebt.put(bank, amount);
    }

    protected static double getStartingBalance(String bank) {
        return bankstartingbalance.get(bank);
    }

    protected static void setStartingBalance(String bank, double balance) {
        bankstartingbalance.put(bank, balance);
    }

    protected static List<String> getAccounts(String bank) {
        return bankaccounts.get(bank);
    }

    protected static void create(String bank) {
        banklist.add(bank);
        bankstatus.put(bank, "Public");
        bankstartingbalance.put(bank, 0.0);
        bankmaxdebt.put(bank, 0.0);

        bankaccountbalancetemp.clear();
        bankaccountbalancetemp.put("_Default", 0.0);
        bankaccountbalance.put(bank, bankaccountbalancetemp);
        bankaccountstatustemp.clear();
        bankaccountstatustemp.put("_Default", "Public");
        bankaccountstatus.put(bank, bankaccountstatustemp);
        bankaccountstemp.clear();
        bankaccountstemp.add("_Default");
        bankaccounts.put(bank, bankaccountstemp);
    }

    protected static void delete(String bank) {
        banklist.remove(bank);
        bankstatus.remove(bank);
        bankstartingbalance.remove(bank);
        bankmaxdebt.remove(bank);
        bankaccounts.remove(bank);
        bankaccountstatus.remove(bank);
        bankaccountbalance.remove(bank);
    }

    protected static void rename(String bank, String newBank) {
        banklist.remove(bank);

        String status = bankstatus.get(bank);
        bankstatus.remove(bank);

        double balance = bankstartingbalance.get(bank);
        bankstartingbalance.remove(bank);

        double maxdebt = bankmaxdebt.get(bank);
        bankmaxdebt.remove(bank);

        bankaccountbalancetemp = bankaccountbalance.get(bank);
        bankaccountbalance.remove(bank);

        bankaccountstatustemp = bankaccountstatus.get(bank);
        bankaccountstatus.remove(bank);

        bankaccountstemp = bankaccounts.get(bank);
        bankaccounts.remove(bank);

        banklist.add(newBank);
        bankstatus.put(newBank, status);
        bankstartingbalance.put(newBank, balance);
        bankmaxdebt.put(newBank, maxdebt);
        bankaccountbalance.put(newBank, bankaccountbalancetemp);
        bankaccountstatus.put(newBank, bankaccountstatustemp);
        bankaccounts.put(newBank, bankaccountstemp);
    }

    protected static boolean bankExists(String bank) {
        return banklist.contains(bank);
    }

    protected static List<String> getBanks() {
        return banklist;
    }
}
