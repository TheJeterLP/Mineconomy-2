package me.mjolnir.mineconomy.internal.util;

import java.text.NumberFormat;
import java.util.Locale;


@SuppressWarnings("javadoc")
public class MCFormat {

    public static String color(String message) {
        return message.replaceAll("&((?i)[0-9a-fk-or])", "§$1");
    }
    
    public static String format(double amount) {
        return format(amount, true);
    }

    public static String format(double amount, boolean flag) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(new Locale("US"));

        String result = numberFormatter.format(amount);

        if (flag) {
            int length = 0;
            try {
                length = result.replace(".", "-").split("-")[1].length();
            } catch (Exception e) {
                length = 0;
            }
            if (length == 0) {
                result += ".00";
            } else if (length == 1) {
                result += "0";
            }
        }

        return result;
    }
}
