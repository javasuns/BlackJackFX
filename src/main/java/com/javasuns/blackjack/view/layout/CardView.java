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

package com.javasuns.blackjack.view.layout;

import com.javasuns.blackjack.model.comps.Card;

import javafx.scene.CacheHint;
import javafx.scene.image.ImageView;

public class CardView extends ImageView {
	public final static double CARD_HEIGHT = 135;
	public final static double CARD_WIDTH = 96;
	private Card card;

	public CardView(Card card) {
		this.card = card;
		this.setPreserveRatio(false);
		this.setSmooth(false);
		this.setFitHeight(CARD_HEIGHT);
		this.setFitWidth(CARD_WIDTH);
		this.setImage(card.getImage());
		this.setCache(true);
		this.setCacheHint(CacheHint.SPEED);
	} // ChipColor()

	public void setCard(Card card) {
		this.card = card;
		loadImage();
	}

	public Card getCard() {
		return card;
	} // getChip()

	private void loadImage() {
		this.setImage(card.getImage());
	} // loadImage()
	
	public void revealImage() {
		if(!card.isHidden())
			loadImage();
	} // revealImage()
	
} // class CardView
