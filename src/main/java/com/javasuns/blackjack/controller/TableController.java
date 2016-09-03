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

import java.util.List;

import com.javasuns.blackjack.controller.animation.Animation;
import com.javasuns.blackjack.model.Game;
import com.javasuns.blackjack.model.Table;
import com.javasuns.blackjack.model.comps.Card;
import com.javasuns.blackjack.model.comps.Chip;
import com.javasuns.blackjack.model.comps.Hand;
import com.javasuns.blackjack.view.layout.CardView;
import com.javasuns.blackjack.view.layout.ChipView;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class TableController {

	@FXML
	HBox playerChipsPane;

	@FXML
	Pane betChipsPane, secondHandChipsPane, playerHandPane, playerSecondHandPane, dealerHandPane, messagePane;

	@FXML
	Label lblMoney, lblBetMoney;

	@FXML
	Button btnPrevious, btnNext, btnBet, btn2x, btnSplit, btnStand, btnMenu;

	@FXML
	ImageView imgView2X;

	@FXML
	VBox vboxSplitHand, buttonsPane;

	private Table table;
	private Animation animation;

	private EventHandler<MouseEvent> chipPressedEvent;

	@FXML
	public void initialize() {
		Animation.init(playerHandPane, dealerHandPane, playerSecondHandPane, btn2x, btnSplit, btnStand,
				       betChipsPane, secondHandChipsPane, playerChipsPane, messagePane, lblBetMoney);
		animation = Animation.getInstance();
		table = Game.getGame().getTable();
		btn2x.setVisible(false);
		btnSplit.setVisible(false);
		btnStand.setVisible(false);

		
		initButtonEvents();
		initMouseEvents();
		initBindings();
		initPropertyListeners();

		// Load 1, 5, 10, 50 and 100 chips.
		for (int i = 0; i < 5; i++)
			playerChipsPane.getChildren().add(new ChipView(Chip.values()[i], chipPressedEvent));
	} // initialize()
	
	private void initPropertyListeners() {
		// Make side buttons visible with fade-in effect (after betting phase).
		table.getPlayer().canPlayProperty().addListener((canPlay) -> {
			animation.playShowSideButtons(table.getPlayer().canStand(), 
										  table.getPlayer().canDouble(),
										  table.getPlayer().canSplit());
		});

		// Listen on changes made on DEALER's hand (Main hand)
		table.getDealer().getHand().cardsProperty().addListener(new ListChangeListener<Card>() {
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Card> c) {
				while (c.next()) {
					for (Card card : c.getAddedSubList())
						animation.playAddCardToDealerHand(new CardView(card));
				} // while
			} // onChanged()
		});

		// Listen on changes made on PLAYER's first hand (Main hand)
		table.getPlayer().getHand().cardsProperty().addListener(new ListChangeListener<Card>() {
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Card> c) {
				while (c.next()) {
					if(c.getRemovedSize() == 1)
						animation.removeCard(1, playerHandPane);

					for (Card card : c.getAddedSubList()) {
						if(table.getPlayer().getActiveHandIndex() == Hand.FIRST_HAND) {
							animation.removeHandIndicator();
							animation.playAddCardToPlayerHand(new CardView(card));
						    animation.playShowHandIndicator(Hand.FIRST_HAND);
						}
						else
							animation.playAddCardToPlayerHand(new CardView(card));
					} // for
				} // while
			} // onChanged()
		});

		// Listen on changes made on second hand (When split)
		table.getPlayer().getHand(Hand.SECOND_HAND).cardsProperty().addListener(new ListChangeListener<Card>() {
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends Card> c) {

				if (c.getList().size() == 1) {
					animation.playSplitHand(new CardView(c.getList().get(0)));
					return;
				} // if

				while (c.next()) {
					for (Card card : c.getAddedSubList()) {
						if(table.getPlayer().getActiveHandIndex() == Hand.SECOND_HAND) {
							animation.removeHandIndicator();
							animation.playAddCardToPlayerSecondHand(new CardView(card));
							animation.playShowHandIndicator(Hand.SECOND_HAND);
						}
						else
							animation.playAddCardToPlayerSecondHand(new CardView(card));
					} // for
				} // while
			} // onChanged()
		});
		
		// When a hand is getting activated show the hand indicator on it.
		table.getPlayer().activeHandProperty().addListener((a,b,handIndex) -> {
			if(handIndex.intValue() != -1)
				animation.playShowHandIndicator(handIndex.intValue());
		});

		table.roundResultsProperty().addListener((a, b, handResult) -> {
			if (!handResult.isEmpty())
				animation.playGiveBetsToWinnerAndShowMessage(handResult.get(0), handResult.get(1));
		});

		// If first hand is closed and user has already split then consolidate cards.
		table.getPlayer().getHand().openHandProperty().addListener((a, b, isHandOpen) -> {
			if (!isHandOpen) {
				animation.removeHandIndicator();
				if(table.getPlayer().hasSplit())
					animation.playConsolidateSplitHand(playerHandPane);
				table.setPhase(Table.PLAYER_HAND_CLOSED);
			} // if
		});

		// If second hand is closed consolidate cards.
		table.getPlayer().getHand(Hand.SECOND_HAND).openHandProperty().addListener((a, b, isHandOpen) -> {
			if (!isHandOpen) {
				animation.removeHandIndicator();
				animation.playConsolidateSplitHand(playerSecondHandPane);
				table.setPhase(Table.PLAYER_HAND_CLOSED);
			} // if
		});

		// When pot is doubled play double the pot animation.
		table.getPot().getBetInPotDoubledProperty().addListener((a, b, doubled) -> {
			if (doubled)
				animation.playDoubleBet(imgView2X);
		});
		
		// Reveal dealer's card
		table.getDealer().getRevealHiddenCardProperty().addListener((a,b, cardRevealed) -> {
			if(cardRevealed)
				animation.playRevealDealerCard();
		});
		
		table.getCurrentPhaseProperty().addListener((a,b,phase) -> {
			// If game-over show main menu
			if(phase.intValue() == Table.GAME_OVER)
				animation.playGameOver();
			else if(phase.intValue() == Table.NEW_GAME)
				animation.clearTable();
		});
	}

	private void addChipToBetChips(ChipView chipViewPressed) {

		List<Chip> chips = table.getPot().getChipsBetOn(table.getPlayer().getHand());
		chipViewPressed.markForRemoval(chips.contains(chipViewPressed.getChip()));
		animation.playAddChip(chipViewPressed);
	} // addChipToBettedChips()

	private void initButtonEvents() {
		
		// Replace chips when left/right buttons are pressed.
		EventHandler<ActionEvent> buttonChipEvent = (event) -> {
			int smallestChipIndex = ((ChipView) playerChipsPane.getChildren().get(0)).getChip().getIndex();
			int newSmallestChipIndex, chipIndex;
			if (event.getSource().equals(btnPrevious))
				newSmallestChipIndex = chipIndex = smallestChipIndex - 1;
			else
				newSmallestChipIndex = chipIndex = smallestChipIndex + 1;

			for (Node node : playerChipsPane.getChildren())
				((ChipView) node).setChip(Chip.values()[chipIndex++]);

			btnPrevious.setVisible(newSmallestChipIndex != 0);
			btnNext.setVisible(newSmallestChipIndex + 4 != Chip.values().length - 1);
		};

		btnPrevious.setOnAction(buttonChipEvent);
		btnNext.setOnAction(buttonChipEvent);

		// Play consolidation animation when go button is pressed
		btnBet.setOnAction((event) -> {
			animation.playConsolidateChips();
			table.setPhase(Table.DEAL_CARDS_PHASE);
		});

		// Change the phase when one of the side buttons are pressed.
		btn2x.setOnAction((event) -> table.setPhase(Table.PLAYER_DOUBLED_PHASE));
		btnSplit.setOnAction((event) -> table.setPhase(Table.PLAYER_SPLITS_PHASE));
		btnStand.setOnAction((event) -> table.setPhase(Table.PLAYER_STANDS));
		
		// Show main menu
		btnMenu.setOnAction((event) -> playerChipsPane.getScene().getRoot().getChildrenUnmodifiable().get(1).setVisible(true));
	} // initButtonEvents()

	private void initMouseEvents() {

		// Listener for adding or removing a chip when pressed
		chipPressedEvent = (c) -> {
			ChipView chipView = (ChipView) c.getSource();
			Chip chip = chipView.getChip();

			// If the chip pressed is a chip that has been bet, then remove it.
			if (chipView.isBet()) {
				boolean chipRemoved = table.getPot().removeChip(table.getPlayer().getHand(), chip);
				if (!chipRemoved)
					return;
				table.getPlayer().getWallet().credit(chip.getValue());
				List<Chip> chips = table.getPot().getChipsBetOn(table.getPlayer().getHand());
				chipView.markForRemoval(!chips.contains(chip));
				animation.playRemoveChip(chipView);
			} else {
				// Check
				if (table.getPlayer().getWallet().debit(chip.getValue())) {
					addChipToBetChips(chipView);
					table.getPot().betChip(table.getPlayer().getHand(), chip);
				}
			}

		};

		playerHandPane.setOnMouseReleased((c) -> {
			// If user is asking for card then hide double and split buttons.
			if (btn2x.isVisible()) {
				btn2x.setVisible(false);
				btnSplit.setVisible(false);
			} // if

			table.getDealer().giveCardToPlayerHand(table.getPlayer().getHand());
		});

		playerSecondHandPane.setOnMouseReleased((c) -> {
			table.getDealer().giveCardToPlayerHand(table.getPlayer().getHand(Hand.SECOND_HAND));
		});
	} // initMouseEvents()
	
	private void initBindings() {
		lblMoney.textProperty().bind(table.getPlayer().getWallet().getMoneyProperty().asString());
		lblBetMoney.textProperty()
				.bind(table.getPot().getTotalAmountProperty(table.getPlayer().getHand()).asString());

		// Show button go only in betting phase and when the player has bet at least one chip
		btnBet.visibleProperty().bind(Bindings.and(Bindings.equal(table.getCurrentPhaseProperty(), Table.BETTING_PHASE),
									  			   Bindings.notEqual(lblBetMoney.textProperty(), "0")));

		// Visibility Bindings
		vboxSplitHand.managedProperty().bind(vboxSplitHand.visibleProperty());
		btn2x.managedProperty().bind(btn2x.visibleProperty());
		btnBet.managedProperty().bind(btnBet.visibleProperty());
		btnStand.managedProperty().bind(btnStand.visibleProperty());
		btnSplit.managedProperty().bind(btnSplit.visibleProperty());
		playerHandPane.disableProperty().bind(Bindings.notEqual(table.getPlayer().activeHandProperty(), Hand.FIRST_HAND));
		playerSecondHandPane.disableProperty().bind(Bindings.notEqual(table.getPlayer().activeHandProperty(), Hand.SECOND_HAND));
		betChipsPane.disableProperty().bind(playerChipsPane.disableProperty());
	} // initBindings()
} // class
