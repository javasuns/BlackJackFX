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

import com.javasuns.blackjack.model.comps.Card;
import com.javasuns.blackjack.model.comps.Deck;
import com.javasuns.blackjack.model.comps.Hand;
import com.javasuns.blackjack.model.comps.Person;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Dealer extends Person {
	private Deck deck;
	private BooleanProperty revealHiddenCardProperty = new SimpleBooleanProperty();

	public Dealer(String name) {
		super(name);
	} // Constructor Method

	public void setDeckToDrawFrom(Deck deck) {
		this.deck = deck;
	} // setDeckToDrawFrom()

	public void shuffleDeck() {
		deck.shuffle();
		mainHand.clearHand();
		mainHand.openHand();
	} // shuffleDeck

	public void giveCardToPlayerHand(Hand hand) {
		hand.addCard(drawCardFromDeck());
	} // giveCardToPlayer()

	public void drawCard() {
		mainHand.addCard(drawCardFromDeck());
	} // drawCard()

	public void drawHiddenCard() {
		Card card = drawCardFromDeck();
		card.setHidden(true);
		mainHand.addCard(card);
	} // drawHiddenCard()

	// Draw One Card from the deck
	private Card drawCardFromDeck() {
		return deck.draw();
	} // drawCard()

	public void revealHiddenCard() {
		mainHand.getCard(1).setHidden(false);
		revealHiddenCardProperty.set(true);
	} // revealHiddenCard()
	
	public BooleanProperty getRevealHiddenCardProperty() {
		return revealHiddenCardProperty;
	} // getRevealHiddenCardProperty()
	
	@Override
	public void clear() {
		super.clear();
		revealHiddenCardProperty.set(false);
	} // clear
}
