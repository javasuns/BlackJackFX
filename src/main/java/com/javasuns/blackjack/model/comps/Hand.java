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

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Hand {
	public static final int FIRST_HAND = 0;
	public static final int SECOND_HAND = 1;

	public static final int HAND_WINS = 0;
	public static final int HAND_TIES = 1;
	public static final int HAND_LOSES = 2;
	public static final int BLACK_JACK_HAND = 3;

	public int handIndex;
	
	protected ObservableList<Card> cards = FXCollections.observableArrayList();
	private ListProperty<Card> cardsProperty = new SimpleListProperty<>(cards);
	private BooleanProperty openHandProperty = new SimpleBooleanProperty(false);

	public Hand(int index) {
		this.handIndex = index;
	} // Constructor Method
	
	public boolean addCard(Card card) {
		if(!isOpen())
			return false;

		cards.add(card);
		if(getTotalPoints() >= 21 && !isBlackJack())
			closeHand();
		return true;
	} // addHand()

	public Card removeCard(int cardIndex) {
		return cards.remove(cardIndex);
	} // removeCard()

	public void clearHand() {
		cards.clear();
		this.closeHand();
	} // returnCards

	public Card getCard(int index) {
		return cards.get(index);
	} // getCard
	
	public int getNumberOnCard(int cardIndex) {
		return getCard(cardIndex).getNumber();
	} // getNumberOfCard()

	public List<Card> getCards() {
		return cards;
	} // getCards()

	public int getCardNo() {
		return cards.size();
	} // getCardNo()

	public int getTotalPoints() {
		int totalPoints = 0;
		boolean hasAnAce = false;
		for(Card card : cards) {
			totalPoints += card.getPoints();
			if(card.getPoints() == 1)
				hasAnAce = true;
		} // for
		if(totalPoints <= 11 && hasAnAce)
			totalPoints += 10;
		return totalPoints;
	} // getTotalPoints()

	// Check if this hand has been closed and no other cards can be added
	public boolean isOpen() {
		return openHandProperty.get();
	} // isClosed()

	// Closing a hand so that user is not adding anymore cards to it.
	public void closeHand() {
		openHandProperty.set(false);
	} // closeHand()

	// Open a new hand so you can add cards into it
	public void openHand() {
		openHandProperty.set(true);
	}

	public boolean isBlackJack() {
		return getCardNo() == 2 && getTotalPoints() == 21;
	} // isBlackJack()

	// A burned hand is one that passed the 21 points
	public boolean isBusted() {
		return getTotalPoints() > 21;
	} // isBusted

	public ListProperty<Card> cardsProperty() {
		return cardsProperty;
	} // getCardsProperty()

	public BooleanProperty openHandProperty() {
		return openHandProperty;
	} // getOpenHandProperty()
	
	public int getIndex() {
		return handIndex;
	} // getIndex()

	// Check whether player's hand wins against dealer's hand.
	public int comparedTo(Hand otherHand) {
		if (this.isBusted())
			return HAND_LOSES;
		else if (otherHand.isBusted() || this.isBlackJack() || otherHand.getTotalPoints() < this.getTotalPoints())
			return HAND_WINS;
		else if (this.getTotalPoints() == otherHand.getTotalPoints())
			return HAND_TIES;
		return HAND_LOSES;
	} // comparedTo
}
