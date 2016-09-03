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
import java.util.Random;

public class Deck {
	private static final String pathImages = "/com/javasuns/blackjack/view/icons/cards";
	private static final String[] cardSuits = {"DIAMOND","SPADES","CLUBS","HEARTS"};

	private List<Card> cards = new ArrayList<Card>();
	private List<Card> cardsDrawn = new ArrayList<Card>();
	private Random random = new Random();

	public Deck() {
		initDeck();
	} // Deck()

	private void initDeck() {
		for(String suit : cardSuits) {
			for(int i = 1; i <= 13; i++) {
				Card card = new Card(i, getCardImagePath(suit,i));
				cards.add(card);
			} // for
		} // for
	} // initDeck()

	// Draw a random card from deck
	public Card draw() {
		Card randomCard = cards.remove(random.nextInt(cards.size()));
		cardsDrawn.add(randomCard);
		return randomCard;
	} // draw()

	// Shuffles the deck
	public void shuffle() {
		cards.addAll(cardsDrawn);
		cardsDrawn.clear();
	} // shuffle()

	private static String getCardImagePath(String suit, int suitIndex) {
		return pathImages + "/" + suit + "-" + suitIndex + ".png";
	} // getCardImagePath()
} // class Deck
