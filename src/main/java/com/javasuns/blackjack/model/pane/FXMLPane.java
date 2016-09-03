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

package com.javasuns.blackjack.model.pane;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class FXMLPane<T> {

	private Parent pane;
	private T paneController;

	public FXMLPane(String url, boolean visible) throws IOException {

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(url));

		pane = fxmlLoader.load();
		paneController = fxmlLoader.<T> getController();

		pane.setVisible(visible);
		fxmlLoader = null;
	} // Constructor Method

	public FXMLPane(String url) throws IOException {
		this(url, true);
	} // Constuctor Method

	public Parent getPane() {
		return pane;
	} // getPane()

	public T getController() {
		return paneController;
	} // getController()

	public String toString() {
		return this.getPane().getClass().toString();
	} // toString()
} // FXMLPane
