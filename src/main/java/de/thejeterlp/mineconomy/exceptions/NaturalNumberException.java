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
 * Thrown when a specified number can not be negative or zero.
 *
 * @author TheJeterLP
 */
public class NaturalNumberException extends RuntimeException implements MCException {

    private final String method;
    private final String variable;

    /**
     * Creates new NaturalNumberException.
     *
     * @param method
     * @param variable
     */
    public NaturalNumberException(String method, String variable) {
        super();
        this.method = method;
        this.variable = variable;
    }

    @Override
    public String getMethodCause() {
        return method;
    }

    @Override
    public String getVariableCause() {
        return variable;
    }

    @Override
    public String getErrorDescription() {
        return "The specified variable must be a natural number.";
    }

    @Override
    public NaturalNumberException getError() {
        return this;
    }

}
