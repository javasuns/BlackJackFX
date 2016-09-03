/*
 * Copyright (C) 2016-2017 BlackJackFX 
 * Giannos Hadjipanayis
 * All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.javasuns.blackjack.model.comps;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Wallet {

	private IntegerProperty moneyProperty = new SimpleIntegerProperty(0);

	public boolean debit(int amount) {
		if(amount > moneyProperty.intValue())
			return false;
		moneyProperty.set(moneyProperty.get() - amount);
		return true;
	} // debit()

	public boolean credit(int amount) {
		moneyProperty.set(moneyProperty.get() + amount);
		return true;
	} // credit()

	public int getAmount() {
		return moneyProperty.get();
	} // getMoney()

	public IntegerProperty getMoneyProperty() {
		return moneyProperty;
	} // getMoneyProperty()
	
	public Wallet setEmpty() {
		moneyProperty.set(0);
		return this;
	} // setEmpty()

} // class Wallet
