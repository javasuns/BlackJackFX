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

package com.javasuns.blackjack.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.javasuns.blackjack.model.comps.Deck;
import com.javasuns.blackjack.model.comps.Hand;
import com.javasuns.blackjack.tools.PropertiesManager;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class Table {
	
	public static final int NEW_ROUND = 0,
							BETTING_PHASE = 1,
							DEAL_CARDS_PHASE = 2,
							DOUBLE_OR_SPLIT_PHASE = 3,
							PLAYER_DOUBLED_PHASE = 4,
							PLAYER_SPLITS_PHASE = 5,
							PLAYER_PLAYS = 6,
							PLAYER_STANDS = 7,
							PLAYER_HAND_CLOSED = 8,
							DEALER_PLAYS_PHASE = 9,
							PAYING_PHASE = 10,
							ROUND_ENDED = 11,
							GAME_OVER = 12,
							NEW_GAME  = 13;

	private IntegerProperty CURRENT_PHASE = new SimpleIntegerProperty(-1);
	
	// Property to keep track at the end of each round the result (WIN,LOSE OR TIE)
	private ListProperty<Integer> roundResultsProperty = new SimpleListProperty<Integer>();
	private List<Integer> roundResults = new ArrayList<Integer>();
	
	private Dealer dealer;
	private Player player;
	private Pot    pot   ;

	public Table() {
		dealer = new Dealer("Johny The Dealer");
		dealer.setDeckToDrawFrom(new Deck());
		player = new Player("Java The Player");
		pot = new Pot();
		pot.setInitialHand(player.getHand());
		roundResultsProperty.set(FXCollections.observableArrayList(roundResults));
		listeners();
		newRound();
	} // Table()

	public IntegerProperty getCurrentPhaseProperty() {
		return CURRENT_PHASE;
	} // getCurrentPhaseProperty()
	
	public int getCurrentPhase() {
		return getCurrentPhaseProperty().get();
	} // getCurrentPhase()

	private void listeners() {
		// Whenever wallet is changed save score.
		player.getWallet().getMoneyProperty().addListener((a,b,amount) -> PropertiesManager.setScore(amount.intValue()));
	} // listeners()
	
	public synchronized void setPhase(int phase) {
		CURRENT_PHASE.set(phase);
		switch(phase) {
			case	NEW_GAME:			   newGame(); break;
			case	NEW_ROUND: 			   newRound(); break;
			case 	BETTING_PHASE:		   player.setCanPlay(true); break;
			case	DEAL_CARDS_PHASE: 	   dealCards(); break;
			case 	DOUBLE_OR_SPLIT_PHASE: showDoubleOptions(); break;
			case 	PLAYER_DOUBLED_PHASE:  playerDoubles(); break;
			case	PLAYER_SPLITS_PHASE:   playerSplits(); break;
			case 	PLAYER_STANDS:		   playerStands(); break;
			case	PLAYER_HAND_CLOSED:	   moreHandsToPlay(); break;
			case	PLAYER_PLAYS:		   playerPlays(); break;
			case	DEALER_PLAYS_PHASE:	   dealerPhase(); break;
			case	PAYING_PHASE:    	   payingPhase(); break;
			case 	ROUND_ENDED:		   roundEnds(); break;
			case 	GAME_OVER:			   break;
		} // switch
	} // setPhase

	public Dealer getDealer() {
		return dealer;
	} // getDealer()

	public Player getPlayer() {
		return player;
	} // getPlayer()

	public Pot getPot() {
		return pot;
	} // getPot()

	private void newGame() {
		player.getWallet().setEmpty().credit(1000);
		setPhase(NEW_ROUND);
	} // newGame()

	private void newRound() {
		roundResultsProperty.clear();
		dealer.clear();
		player.clear();
		pot.clear();
		setPhase(BETTING_PHASE);
	}

	private void dealCards() {
		player.setCanPlay(false);
		dealer.shuffleDeck();
		player.openHand();
		dealer.giveCardToPlayerHand(player.getHand());
		dealer.drawCard();
		dealer.giveCardToPlayerHand(player.getHand());
		dealer.drawHiddenCard();

		if(player.getHand().isBlackJack())
			player.getHand().closeHand();
		else
			setPhase(DOUBLE_OR_SPLIT_PHASE);
	} // dealCards()

	private void showDoubleOptions() {
		player.checkDoubleAndSplit(pot);
		player.setCanStand(true);
		player.setActiveHand(Hand.FIRST_HAND);
		setPhase(PLAYER_PLAYS);
	} // showDoubleOptions()

	// Give only one card to player who doubled
	private void playerDoubles() {
		player.setCanPlay(false);
		player.doubleThePot(pot);
		dealer.giveCardToPlayerHand(player.getHand());
		player.getHand().closeHand();
	} // playerDoubles()

	// Give only one card to player who doubled
	private void playerSplits() {
		player.splitCards(pot);
		dealer.giveCardToPlayerHand(player.getHand());
		dealer.giveCardToPlayerHand(player.getHand(Hand.SECOND_HAND));
	} // playerSplits()

	// Player is able to play
	private void playerPlays() {
		player.setCanPlay(true);
	} // playerPlays()

	// Player stands
	private void playerStands() {
		if(player.getActiveHand() != null)
			player.getActiveHand().closeHand();
	} // playerStands()

	// Method to check if play has more hands to play. If all hands are plyaed(closed) then it's dealer's turn.
	private void moreHandsToPlay() {			
		boolean moreHandsToPlay = false;
		for(Hand hand : player.getHands())
			if(hand.isOpen()) {
				player.setActiveHand(player.getHands().indexOf(hand));
				moreHandsToPlay = true;
				break;
			} // if

		
		if(!moreHandsToPlay) {
			player.setCanPlay(false);
			setPhase(DEALER_PLAYS_PHASE);
		} // if
		else
			setPhase(PLAYER_PLAYS);
	} // moreHandsToPlay()

	// Dealer draw cards if user's hand(s) is not burned(busted).
	private void dealerPhase() {
		boolean playerHandsBusted = true;
		for(Hand hand : player.getHands())
			playerHandsBusted &= hand.isBusted();

		// If player's hand(s) busted or his hand is a blackjack (first 2 cards equals 21)
		// then just reveal dealers card. Otherwise the dealer plays normally. 
		if(playerHandsBusted || player.getHand().isBlackJack())
			dealer.revealHiddenCard();
		else {
			dealer.revealHiddenCard();
			while(dealer.getHand().getTotalPoints() < 17)
				dealer.drawCard();
		} // else
		setPhase(PAYING_PHASE);
	} // dealerPhase()

	// Check the player hand(s) and take or give bet price.
	private void payingPhase() {
		if(player.getHand().isBlackJack())
			blackJackPay();
		else
			payHands();
		setPhase(ROUND_ENDED);
	} // payingPhase()

	private void payHands() {
		int firstHandResult = player.getHand(Hand.FIRST_HAND).comparedTo(dealer.getHand());
		int secondHandResult = -1;
		if(player.hasSplit()) {
			secondHandResult = player.getHand(Hand.SECOND_HAND).comparedTo(dealer.getHand());
			int bothHandResults = firstHandResult + secondHandResult;
			switch (bothHandResults) {
				case 0: // Both hands won, so user gets the winnings
						player.wins(pot.getMoneyOut(player.getHand(Hand.FIRST_HAND)) * 2);
						player.wins(pot.getMoneyOut(player.getHand(Hand.SECOND_HAND)) * 2);
						break;
				case 1: // One hand wins, the other ties
						player.wins(pot.getMoneyOut(player.getHand(Hand.FIRST_HAND)) * 2);
						player.wins(pot.getMoneyOut(player.getHand(Hand.SECOND_HAND)));
						break;
				case 2: // It's a tie (Both hands equals to dealers, or, one hand wins and the other loses
						player.wins(pot.getMoneyOut(player.getHand(Hand.FIRST_HAND)));
						player.wins(pot.getMoneyOut(player.getHand(Hand.SECOND_HAND)));
						break;
				case 3: // One hand is a tie and the other loses
						player.wins(pot.getMoneyOut(player.getHand(Hand.FIRST_HAND)));
						break;
			} // switch
		} // if
		else {
			// User has only one hand
			switch(firstHandResult) {
				case Hand.HAND_WINS: player.wins(pot.getMoneyOut(player.getHand(Hand.FIRST_HAND)) * 2);
									 break;
				case Hand.HAND_TIES: player.wins(pot.getMoneyOut(player.getHand(Hand.FIRST_HAND)));
									 break;
			} // switch
		} // else
		roundResultsProperty.addAll(Arrays.asList(firstHandResult,secondHandResult));
	} // payHands
	
	// Special Rule: When the two cards of player is 21 then he gets twice his bets
	private void blackJackPay() {
		player.wins(pot.getMoneyOut(player.getHand(Hand.FIRST_HAND)) * 3);
		roundResultsProperty.addAll(Arrays.asList(Hand.BLACK_JACK_HAND,-1));
	} // blackJackPhase

	// When round ends check if user has more money to bet and start a new round
	// otherwise the game overs.
	private void roundEnds() {
		setPhase(player.getWallet().getAmount() == 0 ||
						  player.getWallet().getAmount() > 999999 ? GAME_OVER : NEW_ROUND);
	} // roundEnds
	
	public ListProperty<Integer> roundResultsProperty() {
		return roundResultsProperty;
	} // roundResultsProperty()

} // class Game
