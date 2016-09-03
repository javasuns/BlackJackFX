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

package com.javasuns.blackjack.controller;

import com.javasuns.blackjack.model.Game;
import com.javasuns.blackjack.model.Table;
import com.javasuns.blackjack.tools.PropertiesManager;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class MainMenuController {

	@FXML
	StackPane mainMenuPane;
	
	@FXML
	Button btnContinue, btnNewGame;
	
	@FXML
	public void initialize() {
		btnContinue.visibleProperty().bind(Bindings.notEqual(Game.getGame().getTable().getPlayer().getWallet().getMoneyProperty(), 0));
		
		Game.getGame().getTable().getPlayer().getWallet().credit(PropertiesManager.getScore());
		
		btnNewGame.setOnAction((event) -> {
			Game.getGame().getTable().setPhase(Table.NEW_GAME);
			mainMenuPane.setVisible(false);
		});
		
		btnContinue.setOnAction((event) -> {
			mainMenuPane.setVisible(false);
		});
	} // initialize()
} // class MainMenuController
