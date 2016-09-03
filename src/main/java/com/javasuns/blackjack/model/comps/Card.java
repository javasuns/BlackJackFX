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

import javafx.scene.image.Image;

public class Card {
	private static String hiddenCardImageURL = "/com/javasuns/blackjack/view/icons/cards/COVER.png";
	private int number;
	private int points;
	private String imageURL;
	private Image image;
	private boolean hidden;

	public Card(int points, String imageURL) {
		this(points, imageURL, false);
	} // Card()
	
	public Card(int number, String imageURL, boolean hidden) {
		this.number = number;
		this.points = number > 10 ? 10 : number;
		this.imageURL = imageURL;
		this.hidden = hidden;
		if(hidden)
			image = new Image(hiddenCardImageURL);
		else
			image = new Image(imageURL);
	}

	public int getPoints() {
		return points;
	} // getPoints()
	
	public int getNumber() {
		return number;
	} // getNumber()

	public Image getImage() {
		return image;
	} // getImage()

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
		if(hidden)
			image = new Image(hiddenCardImageURL);
		else
			image = new Image(imageURL);
	} // setHidden()

	public boolean isHidden() {
		return hidden;
	} // isHidden

	public String toString() {
		return String.valueOf(getPoints());
	} // toString()
} // class Card
