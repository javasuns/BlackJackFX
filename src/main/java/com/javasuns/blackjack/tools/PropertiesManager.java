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

package com.javasuns.blackjack.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertiesManager {
	// Class that is used to store the player's score (cash amount).
	
	private static Properties appProps = new Properties();
	private static String propertiesFile = System.getProperty("java.io.tmpdir") + "/" + "app.properties";
	private static File file = new File(propertiesFile);
	private static boolean fileLoaded = false;
	
	private static void load() {
		try {
			if(file.exists()) {
				InputStream in;
				in = new FileInputStream(file);
				appProps.load(in);
				in.close();
			} // if
		} catch (IOException e) {e.printStackTrace();}
	} // load()
	
	private static void save() {
		try {
			OutputStream out = new FileOutputStream(file);
			appProps.store(out, "--=== BlackJackFX ===--");
			out.close();
		} catch (IOException e) {e.printStackTrace();}
	} // save()
	
	public static int getScore() {
		if(!fileLoaded)
			load();
		String scoreAsString = appProps.getProperty("score");
		return scoreAsString == null ? 0 : Integer.parseInt(scoreAsString);
	} // getScore()
	
	public static void setScore(int score) {
		appProps.setProperty("score", String.valueOf(score));
		save();
	} // setScore()
	
} // PropertiesManager()
