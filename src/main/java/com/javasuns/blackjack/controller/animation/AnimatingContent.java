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

package com.javasuns.blackjack.controller.animation;

import javafx.scene.Node;

public class AnimatingContent {

	// Type of plays
	public static final int BET_CHIP = 0,
							REMOVE_CHIP = 1,
							REMOVE_CHIP_FROM_UI = 15,
							CONSOLIDATE_CHIPS = 2,
							CENTER_CHIPS = 3,
							GIVE_PLAYER_CARD = 4,
							GIVE_CARD_TO_2ND_HAND = 5,
							GIVE_DEALER_CARD = 6,
							MAKE_BUTTONS_VISIBLE = 7,
							DOUBLE_HAND		 = 8,
							SPLIT_HAND 		 = 9,
							CONSOLIDATE_SPLIT_HAND = 10,
							REVEAL_DEALER_HAND = 11,
							REMOVE_CARD_FROM_PLAYER = 12,
							SHOW_HAND_INDICATOR = 13,
							REMOVE_HAND_INDICATOR = 14,
							REMOVE_ALL_BET_CHIPS = 15,
							GIVE_BETS_TO_WINNER = 16,
							GAME_OVER = 17;

	private int nodeType;
	private Object content;
	
	public AnimatingContent(int nodeType) {
		this.content = null;
		this.nodeType = nodeType;
	}

	public AnimatingContent(int nodeType, Object content) {
		this.content = content;
		this.nodeType = nodeType;
	}
	
	public AnimatingContent(int nodeType, boolean... content) {
		this.content = content;
		this.nodeType = nodeType;
	}
	
	public AnimatingContent(int nodeType, int... content) {
		this.content = content;
		this.nodeType = nodeType;
	}

	public int getType() {
		return nodeType;
	}

	public Node getNode() {
		return (Node) content;
	} // getNode()
	
	public boolean [] getAsBooleanArray() {
		return (boolean[]) content;
	} // getAsBooleanArray()
	
	public int [] getAsIntegerArray() {
		return (int[]) content;
	} // getAsIntegerArray()

	public Integer getContentAsInteger() {
		return (Integer) content;
	} // getContentAsInteger()
} // class AnimatingNode
