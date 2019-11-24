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
package me.mjolnir.mineconomy.internal.util;

import java.text.NumberFormat;
import java.util.Locale;

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
