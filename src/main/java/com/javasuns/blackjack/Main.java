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

package com.javasuns.blackjack;

import com.javasuns.blackjack.controller.MainMenuController;
import com.javasuns.blackjack.controller.TableController;
import com.javasuns.blackjack.model.pane.FXMLPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLPane<TableController> tablePane = new FXMLPane<TableController>("/com/javasuns/blackjack/view/Table.fxml");
			FXMLPane<MainMenuController> menuPane = new FXMLPane<MainMenuController>("/com/javasuns/blackjack/view/MainMenu.fxml");
			StackPane rootPane = new StackPane(tablePane.getPane(), menuPane.getPane());
			Scene scene = new Scene(rootPane);
			scene.getStylesheets().add("/com/javasuns/blackjack/view/css/root.css");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	} // main
} // class Main
