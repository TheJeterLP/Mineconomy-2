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
package de.thejeterlp.mineconomy.exceptions;

/**
 * Interface for standard MineConomy Exceptions.
 *
 * @author TheJeterLP
 */
public interface MCException {

    /**
     * Returns the method that caused the exception.
     *
     * @return The method that caused the exception.
     */
    public String getMethodCause();

    /**
     * Returns the variable that caused the exception.
     *
     * @return The variable that caused the exception.
     */
    public String getVariableCause();

    /**
     * Returns the description of the exception.
     *
     * @return A description of the exception.
     */
    public String getErrorDescription();

    /**
     * Returns the exception.
     *
     * @return Exception
     */
    public Exception getError();
}
