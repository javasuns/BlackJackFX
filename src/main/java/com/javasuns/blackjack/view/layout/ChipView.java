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

import com.javasuns.blackjack.model.comps.Chip;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class ChipView extends ImageView {
	public final static double CHIP_DIAMETER = 45;
	private boolean markToBeRemoved = false;
	private BooleanProperty removeFromUIProperty;
	private Chip chip;
	private boolean isBet = false; // Keeping track if this type of chip has been already bet.
	private EventHandler<MouseEvent> eventHandler;

	public ChipView(Chip chip) {
		this.chip = chip;
		this.setPreserveRatio(true);
		this.setSmooth(false);
		this.setFitHeight(CHIP_DIAMETER);
		this.setImage(new Image(chip.getImageURL()));
		this.setCache(true);
		this.setCacheHint(CacheHint.SPEED);
	} // ChipColor()

	public ChipView(Chip chip, EventHandler<MouseEvent> event) {
		this(chip);
		this.eventHandler = event;
		this.setOnMouseReleased(event);
	} // Chip

	public void setChip(Chip chip) {
		this.chip = chip;
		loadImage();
	} // setChip()

	public Chip getChip() {
		return chip;
	} // getChip()

	private void loadImage() {
		this.setImage(new Image(chip.getImageURL()));
	}
	
	// This method is used when animating this node in order to decide whether at the end
	// of the animation it should be removed from the UI or not.
	public boolean isMarkedForRemoval() {
		return markToBeRemoved;
	} // isMarkedForRemoval()
	
	public void markForRemoval(boolean markedItForRemove) {
		this.markToBeRemoved = markedItForRemove;
	} // setMarkedForRemoval
	
	public BooleanProperty removeFromUIProperty() {
		if(removeFromUIProperty == null) {
			removeFromUIProperty = new SimpleBooleanProperty(false);
			removeFromUIProperty.addListener((a,b,remove) -> {
				if(remove)
					((Pane) this.getParent()).getChildren().remove(this);
			});
		} // if
		return removeFromUIProperty;
	} // removeFromUIProperty
	
	public ChipView clone() {
		ChipView chip = new ChipView(this.chip, this.eventHandler);
		chip.setIsBet(this.isBet);
		return chip;
	} // clone()
	
	public void setIsBet(boolean isBet) {
		this.isBet = isBet;
	} // setIsBet()
	
	public boolean isBet() {
		return isBet;
	} // isBet()
	
} // class ChipView
