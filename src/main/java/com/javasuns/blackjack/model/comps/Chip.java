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

public enum Chip {

	RED("RED-1.png",              1),
	BLUE("BLUE-5.png",            5),
	GREEN("GREEN-10.png",         10),
	ORANGE("ORANGE-50.png",       50),
	PURPLE("PURPLE-100.png",      100),
	LIGHT_BLUE("LBLUE-500.png",   500),
	FUXIA("FUXIA-1K.png",         1000),
	ARMY("ARMY-5K.png",           5000),
	BLACK("BLACK-10K.png",        10000),
	LIGHT_GREEN("LGREEN-50K.png", 50000),
	DARK_BLUE("DBLUE-100K.png",   100000);

	private String imageURL;
	private int value;

	private Chip(String imageName, int value) {
		this.imageURL = "/com/javasuns/blackjack/view/icons/chips/" + imageName;
		this.value = value;
	} // ChipColor()

	public int getValue() {
		return value;
	} // getValue()

	public String getImageURL() {
		return imageURL;
	} // getImageURL()

	public int getIndex() {
		return getIndexOf(this);
	}

	public static int getIndexOf(Chip chip) {
		for(int i=0; i < values().length; i++)
			if(chip.equals(values()[i]))
				return i;
		return -1;
	}

} // class Chip
