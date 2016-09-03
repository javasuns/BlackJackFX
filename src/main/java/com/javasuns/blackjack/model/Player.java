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

import java.util.List;

import com.javasuns.blackjack.model.comps.Hand;
import com.javasuns.blackjack.model.comps.Person;
import com.javasuns.blackjack.model.comps.Wallet;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Player extends Person{

	private Wallet wallet;
	private BooleanProperty canPlayProperty = new SimpleBooleanProperty(false);
	private boolean canSplit = false, canDouble = false, canStand = false;

	public Player(String name) {
		super(name);
		hands.add(new Hand(Hand.FIRST_HAND));
		wallet = new Wallet();
	} // Constructor Method


	public void openHand() {
		for(Hand hand : hands)
			hand.clearHand();
		handsNo = 1;
		mainHand.openHand();
	} // initBet()

	// Check if the player can double or split.
	// In order to split the initial pair must be the same, and the user must have the money to double
	public void checkDoubleAndSplit(Pot pot) {
		canDouble = canDouble(pot);
		canSplit = canDouble && mainHand.getCardNo() == 2 && mainHand.getNumberOnCard(0) == mainHand.getNumberOnCard(1);
	} // canDouble

	public void doubleThePot(Pot pot) {
		if(canDouble(pot)) {
			wallet.debit(pot.getBettedAmount(mainHand));
			pot.doubleBet(mainHand);
		}
	}

	// Split Cards
	public void splitCards(Pot pot) {
		handsNo++;
		wallet.debit(pot.getBettedAmount(mainHand));
		pot.betChips(hands.get(Hand.SECOND_HAND),pot.getChipsBetOn(mainHand));
		hands.get(Hand.SECOND_HAND).openHand();
		hands.get(Hand.SECOND_HAND).addCard(mainHand.removeCard(1));
	} // splitCards()

	// Call this method when user wins
	public void wins(int amount) {
		wallet.credit(amount);
	} // pay

	// This is the deck of user in case he split
	public Hand getHand(int handIndex) {
		return hands.get(handIndex);
	} // getSecondHand()

	// Check if a user has split his cards.
	public boolean hasSplit() {
		return handsNo > 1;
	} // hasSplit()

	public List<Hand> getHands() {
		return hands.subList(0, handsNo);
	} // getHands()

	public Wallet getWallet() {
		return wallet;
	} // getWallet()

	private boolean canPlay() {
		return canPlayProperty.get();
	}

	public boolean canSplit() {
		return canPlay() && canSplit;
	} // canSplitProperty

	private boolean canDouble(Pot pot) {
		return pot.getBettedAmount(mainHand) <= wallet.getAmount();
	} // canDouble()

	public boolean canDouble() {
		return canPlay() && canDouble;
	} // canDouble()
	
	public boolean canStand() {
		return canPlay() && canStand;
	} // canStand()
	
	public void setCanStand(boolean canStand) {
		this.canStand = canStand;
	} // setCanStand()

	public void setCanPlay(boolean canPlay) {
		canPlayProperty.set(canPlay);
	} // setCanPlay()

	public BooleanProperty canPlayProperty() {
		return canPlayProperty;
	} // canPlayProperty()

	public void clear() {
		super.clear();
		canDouble = false;
		canSplit = false;
		canStand = false;
	} // clearProperties()
} // class Player
