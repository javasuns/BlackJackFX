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

package com.javasuns.blackjack.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.javasuns.blackjack.model.comps.Chip;
import com.javasuns.blackjack.model.comps.Hand;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Pot {

	private Map<Hand, List<Chip>> chipsBet = new HashMap<Hand,List<Chip>>();
	private Map<Hand, Integer> amountBet = new HashMap<Hand, Integer>();
	private IntegerProperty totalBetAmount = new SimpleIntegerProperty(0);
	private BooleanProperty betInPotDoubledProperty = new SimpleBooleanProperty(false);

	// Initial hand should be the first hand of the player.
	public void setInitialHand(Hand hand) {
		chipsBet.put(hand, new ArrayList<Chip>());
		amountBet.put(hand, 0);
	} // setInitialHand

	// Returns a list with all the chips that have been bet on a hand.
	public List<Chip> getChipsBetOn(Hand hand) {
		return chipsBet.get(hand);
	} // getChipsBetOn()

	// Add a list of chips to a hand
	public void betChips(Hand hand, List<Chip> chips) {
		if(!chipsBet.containsKey(hand)) {
			chipsBet.put(hand, new ArrayList<Chip>());
			amountBet.put(hand, 0);
		}
		for(Chip chip : chips)
			betChip(hand,chip);
	} // betChipsOnHand()

	public void betChip(Hand hand, Chip chip) {
		chipsBet.get(hand).add(chip);
		amountBet.put(hand, amountBet.get(hand) + chip.getValue());
		calcTotalAmount();
	} // betChip()

	public boolean removeChip(Hand hand, Chip chip) {
		if(!(chipsBet.get(hand).remove(chip)))
			return false;
		amountBet.put(hand, amountBet.get(hand) - chip.getValue());
		calcTotalAmount();
		return true;
	} // removeChip()

	public void doubleBet(Hand hand) {
		amountBet.put(hand, amountBet.get(hand) * 2);
		betInPotDoubledProperty.set(true);
	} // double bet

	public int getMoneyOut(Hand hand) {
		int betOnHand = getBettedAmount(hand);
		chipsBet.get(hand).clear();
		return betOnHand;
	} // getMoneyOut()

	public int getBettedAmount(Hand hand) {
		return amountBet.get(hand);
	} // getBettedAmount

	public IntegerProperty getTotalAmountProperty(Hand hand) {
		return totalBetAmount;
	} // getTotalAmountProperty()

	public BooleanProperty getBetInPotDoubledProperty() {
		return betInPotDoubledProperty;
	} // getBetInPotDoubledProperty()

	public void clear() {
		for (Entry<Hand, List<Chip>> entry : chipsBet.entrySet())
		    entry.getValue().clear();
		for (Entry<Hand, Integer> entry : amountBet.entrySet())
		    entry.setValue(0);
		betInPotDoubledProperty.set(false);
		totalBetAmount.set(0);
	}
	
	private void calcTotalAmount() {
		int totalBet = 0;
		for (Entry<Hand, Integer> entry : amountBet.entrySet())
		    totalBet += entry.getValue();
		totalBetAmount.set(totalBet);
	} // calcTotalAmount()
} // class Pot
