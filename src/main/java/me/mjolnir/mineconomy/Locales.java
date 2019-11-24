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
package me.mjolnir.mineconomy;

import java.io.File;
import java.io.IOException;
import me.mjolnir.mineconomy.internal.util.MCFormat;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Locales {

    MESSAGE_WELCOME("Message.Join", "&fWelcome to MineConomy, %player%! Your balance is &a%balance% &f%currency%. Type '/mc help' for help."),
    MESSAGE_BALANCE_GET("Message.GetBalance", "&f%player%'s account balance is %balance% %currency%"),
    MESSAGE_BALANCE_SET("Message.SetBalance", "&f%player%'s account balance is set to %balance% %currency%."),
    MESSAGE_GIVE("Message.Give", "&fGave %player% %amount% %currency%."),
    MESSAGE_PAYED_TO("Message.PayedTo", "&f%amount% %currency% has been payed to %player%."),
    MESSAGE_PAYED_FROM("Message.PayedFrom", "&f%player% has payed you %amount% %currency%."),
    MESSAGE_TOOK("Message.Took", "&fTook %amount% %currency% from %account%."),
    MESSAGE_EMPTY("Message.Empty", "&fSet %account%'s balance to 0."),
    MESSAGE_COMPLETE("Message.Complete", "&fTransaction complete."),
    ACCOUNT_CREATED("Account.FirstJoin", "&fYour MineConomy Account has been created!"),
    ACCOUNT_BALANCE("Account.Balance", "&fBalance: &a%balance% &f%currency%"),
    BANK_BALANCE("Bank.Balance", "&f[%bank%] Balance: &a%balance%"),
    ERROR_NOPERM("Errors.NoPermission", "&cYou do not have permission for this."),
    ERROR_INT("Errors.NoInteger", "&cYou must enter an integer!"),
    ERROR_INVALID_ARGS("Errors.InvalidArgs", "&cInvalid number of arguements!"),
    ERROR_WRONG_USE("Errors.WrongUse", "&fMineConomy did not recognize your command. Type &7/mc help &ffor help with MineConomy commands."),
    ERROR_MAX_DEBT("Errors.MaxDebt", "&cThe value requested is below the maxium allowed debt."),
    ERROR_THEY_ENOUGH("Errors.TheyEnough", "&c%player% did not have enough money!"),
    ERROR_YOU_ENOUGH("Errors.YouEnough", "&cYou do not have enough money!"),
    ERROR_NO_ACCOUNT("Errors.NoAccount", "&cThe account you requested does not exist."),
    ERROR_MONEY_FORMAT("Errors.MoneyFormat", "&cYou must enter the correct monetarily formatted number."),
    ERROR_WRONG_USER("Error.WrongUser", "By now, Only Players can use commands!"),
    HELP_1("Help", "<br>&a===== MineConomy Help Page =====<br>&f<REQUIRED> [OPTION] (OPTIONAL)<br><br>&7/mc help <PAGE>&f - displays this menu.<br>&7/mc balance &for &7/money &f- displays your account balance.<br>&7/mc pay <ACCOUNT> &f- pays specified account.<br>&7/mc get <ACCOUNT> &f- displays specified account's balance.<br>&7/mc set <ACCOUNT> <AMOUNT> &f- sets specified account's balance.<br>&7/mc empty <ACCOUNT> &f- sets specified account's balance to 0.<br>&7/mc give <ACCOUNT> <AMOUNT> &f- gives specified account the specified amount.<br>&7/mc take <ACCOUNT> <AMOUNT> &f- takes specified amount from the specified account."),;

    private Locales(String path, Object val) {
        this.path = path;
        this.value = val;
    }

    private final Object value;
    private final String path;
    private static YamlConfiguration cfg;
    private static final File f = new File(MineConomy.getInstance().getDataFolder(), "locales.yml");
    private static final String tag = "§a[" + MineConomy.getInstance().getDescription().getName() + "] ";

    public String getPath() {
        return path;
    }

    public Object getDefaultValue() {
        return value;
    }

    public String getString() {
        return MCFormat.color(tag + cfg.getString(path));
    }

    public static void load() {
        MineConomy.getInstance().getDataFolder().mkdirs();
        reload(false);
        for (Locales c : values()) {
            if (!cfg.contains(c.getPath())) {
                c.set(c.getDefaultValue(), false);
            }
        }
        try {
            cfg.save(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void set(Object value, boolean save) {
        cfg.set(path, value);
        if (save) {
            try {
                cfg.save(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            reload(false);
        }
    }

    public static void reload(boolean complete) {
        if (!complete) {
            cfg = YamlConfiguration.loadConfiguration(f);
            return;
        }
        load();
    }

    public String replace(String to, String val) {
        String ret = getString();
        ret = ret.replaceAll("%currency%", Config.CURRENCY_NAME.getString());
        ret = ret.replaceAll(to, val);
        return ret;
    }

}
