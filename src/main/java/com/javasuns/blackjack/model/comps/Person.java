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

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Person {

	private String name;
	protected int handsNo;
	protected List<Hand> hands;
	protected Hand mainHand;
	protected IntegerProperty activeHandProperty = new SimpleIntegerProperty(0);

	public Person(String name) {
		this.name = name;
		this.hands = new ArrayList<Hand>();
		hands.add(mainHand = new Hand(Hand.FIRST_HAND));
		handsNo = 1;
	}

	public String toString() {
		return name;
	} // toString

	public Hand getHand() {
		return mainHand;
	} // getHand()

	public void clear() {
		for (Hand hand : hands)
			hand.clearHand();
		handsNo = 1;
		activeHandProperty.set(-1);
	} // clear()

	public Hand getActiveHand() {
		if(activeHandProperty.get() == -1)
			return null;
		return hands.get(activeHandProperty.get());
	} // getActiveHand()
	
	public int getActiveHandIndex() {
		return activeHandProperty.get();
	}

	public void setActiveHand(int handToActivate) {
		activeHandProperty.set(handToActivate);
	} // setActiveHand()
	
	public IntegerProperty activeHandProperty() {
		return activeHandProperty;
	} // activeHandProperty()

} // class Person
