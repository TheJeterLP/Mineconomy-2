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

import java.util.List;
import java.util.UUID;

public abstract class AccountingBase {

    public abstract double getBalance(UUID uuid);

    public abstract void setBalance(UUID uuid, double amount);

    public abstract boolean exists(UUID uuid);

    public abstract void delete(UUID uuid);

    public abstract void create(UUID uuid);

    public abstract List<UUID> getAccounts();
}
