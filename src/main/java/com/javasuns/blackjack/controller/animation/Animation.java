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

import java.util.ArrayList;
import java.util.List;

import com.javasuns.blackjack.model.comps.Hand;
import com.javasuns.blackjack.view.layout.CardView;
import com.javasuns.blackjack.view.layout.ChipView;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;


public class Animation {
	private Timeline animation;
	private Timeline handIndicatorAnimation;
	private final Duration gameSpeed = Duration.millis(300), chipSpeed = Duration.millis(200);
	private List<AnimatingContent> nodesWaitingForAnimation = new ArrayList<AnimatingContent>();
	
	private Button btn2x, btnSplit, btnStand;
	private Label lblBetMoney, lblMessage;
	private Pane playerChipsPane, playerHandPane, player2ndHandPane, dealerHandPane, messagePane,
						handChips, secondHandChips;
    
	private double startX, startY;
	private boolean isPlaying = false;
	private ColorAdjust colorAdjust= new ColorAdjust(0,-1,0,0);
	
	private ImageView imgSplit;
	private final ImageView imgHandIndicator = new ImageView(new Image("/com/javasuns/blackjack/view/icons/HAND.png"));
	
	// Measurement that is used to define how much a card is shifted when a new card is added to a hand.
	private double shiftValue = CardView.CARD_WIDTH*0.1;
	private boolean playerSplits = false;
	
	// ActionEvent that is called after a message is shown to clear the table from cards and chips.
	private EventHandler<ActionEvent> clearTable = (event) -> clearTable();

	// Media -- Commented out as media is not supported on Mobile yet! 
	//private final Media cardMedia = new Media(Animation.class.getResource("/com/javasuns/blackjack/sounds/cardPlace2.wav").toString());
	//private final Media chipMedia = new Media(Animation.class.getResource("/com/javasuns/blackjack/sounds/chipStack1.wav").toString());
    //private final MediaPlayer cardMediaPlayer = new MediaPlayer(cardMedia); 
    //private final MediaPlayer chipMediaPlayer = new MediaPlayer(chipMedia); 
	//private EventHandler<ActionEvent> startDrawCardAudio,startBetChipAudio,stopDrawCardAudio,stopBetChipAudio;
	//private Duration startTime = Duration.millis(0);
	
	// Singleton instance of this class
	private static Animation animInstance;
	
	public static void init(Pane playerHandPane, Pane dealerHandPane, Pane playerSecondHandPane, Button btnDouble,
			                Button btnSplit, Button btnStand, Pane handChips, Pane secondHandChips, Pane playerChipsPane,
			                Pane messagePane, Label lblBetMoney) {
		animInstance = new Animation(playerHandPane, dealerHandPane, playerSecondHandPane, btnDouble, btnSplit,
									 btnStand, handChips, secondHandChips, playerChipsPane, messagePane, lblBetMoney);
	} // init()

	public static Animation getInstance() {
		if (animInstance == null) {
			System.err.println("Make sure to run init() before calling getInstance()");
			System.exit(1);
		} // if
		return animInstance;
	} // getInstance()
	
	private Animation(Pane playerHandPane, Pane dealerHandPane, Pane playerSecondHandPane,
					  Button btnDouble, Button btnSplit, Button btnStand, Pane handChips, Pane secondHandChips, 
					  Pane playerChipsPane, Pane messagePane, Label lblBetMoney) {
		this.playerHandPane = playerHandPane;
		this.dealerHandPane = dealerHandPane;
		this.player2ndHandPane = playerSecondHandPane;
		this.btn2x = btnDouble;
		this.btnSplit = btnSplit;
		this.btnStand = btnStand;
		this.handChips = handChips;
		this.secondHandChips = secondHandChips;
		this.playerChipsPane = playerChipsPane;
		this.messagePane = messagePane;
		this.lblBetMoney = lblBetMoney;
		
		initMessageLabel();
		initHandIndicator();
		
		animation = new Timeline();
		animation.setOnFinished((event) -> {
				playAnyWaitingNodes();
		});
	} // Constructor Method

	public synchronized void playAddCardToPlayerHand(CardView animatedCard) {
		if(isPlaying)
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.GIVE_PLAYER_CARD, animatedCard));
		else {
			if(playerSplits)
				playAddCardToSplitHand(animatedCard, playerHandPane);
			else
				playAddCardToHand(animatedCard, playerHandPane);	
		}
	} // playAddCardToPlayerHand

	public synchronized void playAddCardToPlayerSecondHand(CardView animatedCard) {
		if(isPlaying)
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.GIVE_CARD_TO_2ND_HAND, animatedCard));
		else {
			playAddCardToSplitHand(animatedCard, player2ndHandPane);
		} // else
	} // playAddCardToPlayerSplitHand
	
	public synchronized void playAddCardToDealerHand(CardView animatedCard) {
		// If an animation is already playing add this card to queue.
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.GIVE_DEALER_CARD, animatedCard));
			return;
		} // if
		else
			playAddCardToHand(animatedCard, dealerHandPane);
	} // playAddCardToDealerHand
	
	// This method removes cards from hands. It is not animated, but it is placed in this class
	// as we want this action to take place after animation (in case an animation is playing).
	public synchronized void removeCard(int cardIndex, Pane handPane) {
		// If an animation is already playing add this card to queue.
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.REMOVE_CARD_FROM_PLAYER, cardIndex));
			return;
		} // if
		handPane.getChildren().remove(cardIndex);
		playAnyWaitingNodes();
	} // removeCard()


	private synchronized void playAddCardToHand(CardView animatedCard, Pane handPane) {
		isPlaying = true;
		
		Bounds bounds = handPane.localToScene(handPane.getBoundsInLocal());
		startX = - bounds.getMinX() - CardView.CARD_WIDTH;
		startY = - bounds.getMinY() - CardView.CARD_HEIGHT;

		animatedCard.setManaged(false);

		animatedCard.setLayoutX(startX);
		animatedCard.setLayoutY(startY);

		double destCenterX = handPane.getWidth()/2 - CardView.CARD_WIDTH/2;
		double destCenterY = handPane.getHeight()/2 - CardView.CARD_HEIGHT/2;
		animation.getKeyFrames().clear();
		
		// Shift all cards 
    	for(Node node : handPane.getChildren()) {
    		CardView card = (CardView) node;
    		animation.getKeyFrames().add(new KeyFrame(gameSpeed,
	    		new KeyValue(card.layoutXProperty(), card.getLayoutX() - shiftValue)));
    	} // for
	    
		animation.getKeyFrames().add(new KeyFrame(gameSpeed,
	    		new KeyValue(animatedCard.layoutXProperty(), destCenterX + handPane.getChildren().size() * shiftValue),
	    		new KeyValue(animatedCard.layoutYProperty(), destCenterY)));
		//animation.getKeyFrames().add(new KeyFrame(startTime, startDrawCardAudio));
		//animation.getKeyFrames().add(new KeyFrame(gameSpeed, stopDrawCardAudio));
		
		handPane.getChildren().add(animatedCard);	
		animation.play();
	} // playAddCardToHand
	
	// When hand is split animation of cards is different.
	private synchronized void playAddCardToSplitHand(CardView animatedCard, Pane handPane) {
		isPlaying = true;
		
		Bounds bounds = handPane.localToScene(handPane.getBoundsInLocal());
		startX = - bounds.getMinX() - CardView.CARD_WIDTH;
		startY = - bounds.getMinY() - CardView.CARD_HEIGHT;

		animatedCard.setManaged(false);

		animatedCard.setLayoutX(startX);
		animatedCard.setLayoutY(startY);

		double destCenterX = handPane.getWidth()/2 - CardView.CARD_WIDTH/2;
		double destCenterY = handPane.getHeight()/2 - CardView.CARD_HEIGHT/2;
		animation.getKeyFrames().clear();
		if(handPane == playerHandPane) {
			animation.getKeyFrames().add(new KeyFrame(gameSpeed,
		    		new KeyValue(animatedCard.layoutXProperty(), destCenterX + (handPane.getChildren().size() - 0.5) *  (2*shiftValue)),
		    		new KeyValue(animatedCard.layoutYProperty(), destCenterY)));
		} // if
		else if(handPane == player2ndHandPane) {
			// Shift all cards 
	    	for(Node node : handPane.getChildren()) {
	    		CardView card = (CardView) node;
	    		animation.getKeyFrames().add(new KeyFrame(gameSpeed,
		    		new KeyValue(card.layoutXProperty(), card.getLayoutX() - 2*shiftValue)));
	    	} // for

			animation.getKeyFrames().add(new KeyFrame(gameSpeed,
		    		new KeyValue(animatedCard.layoutXProperty(), destCenterX + shiftValue),
		    		new KeyValue(animatedCard.layoutYProperty(), destCenterY)));
		} // else if
		//animation.getKeyFrames().add(new KeyFrame(startTime, startDrawCardAudio));
		//animation.getKeyFrames().add(new KeyFrame(gameSpeed, stopDrawCardAudio));
		handPane.getChildren().add(animatedCard);	

		animation.play();
	} // playAddCardToSplitHand
	
	public void playConsolidateSplitHand(Pane handPane) {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.CONSOLIDATE_SPLIT_HAND, handPane));
			return;
		} // if
		isPlaying = true;

		int noOfCards = handPane.getChildren().size();
		double cardDistance = shiftValue * 0.1;
		double firstCardX  = 0;


		KeyValue[] keyValues = new KeyValue[noOfCards];
		for(int i=0; i < keyValues.length; i++) {
	    	CardView card = (CardView) handPane.getChildren().get(i);
	    	keyValues[i] = new KeyValue(card.layoutXProperty(), firstCardX + (i*cardDistance));
		} // for
		
		animation.getKeyFrames().clear();
		animation.getKeyFrames().add(new KeyFrame(gameSpeed, keyValues));
		animation.play();
	} // playConsolidateSplitHand

	// Split hand by giving to the second hand the 2nd card of the 1st hand.
	public synchronized void playSplitHand(CardView animatedCard) {
		removeHandIndicator(); // Remove Hand indicator before splitting the hand
		// If an animation is already playing add this animation to queue.
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.SPLIT_HAND, animatedCard));
			return;
		} // if
		isPlaying = true;
		playerSplits = true;

		double centerX = (player2ndHandPane.getWidth()/2 - CardView.CARD_WIDTH/2) + shiftValue;
		double centerY = player2ndHandPane.getHeight()/2 - CardView.CARD_HEIGHT/2;

		animatedCard.setManaged(false);
		animatedCard.setLayoutX(centerX);
		animatedCard.setLayoutY(centerY);
		player2ndHandPane.getChildren().add(animatedCard);

		// Make a clone of every chip bet.
		for(Node node : handChips.getChildren())
			if(node instanceof ImageView) {
				ImageView chip = (ImageView) node;
				ImageView chipClone = new ImageView(chip.getImage());
				chipClone.setFitHeight(chip.getFitHeight());
				chipClone.setPreserveRatio(true);
				chipClone.setLayoutX(chip.getBoundsInLocal().getMinX());
				chipClone.setLayoutY(chip.getBoundsInLocal().getMinY());
				secondHandChips.getChildren().add(chipClone);
			}
		
		Pane handsPane = (Pane) secondHandChips.getParent().getParent();

		Bounds bounds = getBoundsInLocal(btnSplit, handsPane);

		// Create a clone of the split image.
		if(imgSplit == null) {
			ImageView btnSplitImageView = (ImageView) btnSplit.getChildrenUnmodifiable().get(0);
			imgSplit = new ImageView(btnSplitImageView.getImage());
			imgSplit.setFitHeight(btnSplitImageView.getFitHeight());
			imgSplit.setPreserveRatio(true);
			imgSplit.setManaged(false);
		}


		imgSplit.setX(bounds.getMinX());
		imgSplit.setY(bounds.getMinY());
		handsPane.getChildren().add(imgSplit);
		
		double splitImageHeight = ChipView.CHIP_DIAMETER;
		double destCenterX = handsPane.getWidth() / 2 - splitImageHeight/2;
		double destCenterY = 0;

		

		btn2x.setVisible(false);
		btnSplit.setVisible(false);


		Timeline animation2 = new Timeline();
		animation2.getKeyFrames().add(new KeyFrame(gameSpeed,
		    		new KeyValue(imgSplit.xProperty(), destCenterX),
		    		new KeyValue(imgSplit.yProperty(), destCenterY),
					new KeyValue(imgSplit.fitHeightProperty(), splitImageHeight)));
		animation2.play();

		animation.getKeyFrames().clear();
		animation.getKeyFrames().add(new KeyFrame(gameSpeed,
		    		new KeyValue(playerHandPane.getParent().translateXProperty(),
		    				-playerHandPane.localToScene(playerHandPane.getBoundsInLocal()).getMinX() + shiftValue),
		    		new KeyValue(player2ndHandPane.getParent().translateXProperty(),
		    				player2ndHandPane.localToScene(playerHandPane.getBoundsInLocal()).getMinX() - shiftValue)));
		animation.play();
	} // playSplitHand()
	
	public synchronized void playRevealDealerCard() {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.REVEAL_DEALER_HAND));
			return;
		} // if
		isPlaying = true;
		CardView secondCard = (CardView) dealerHandPane.getChildren().get(1);
		secondCard.revealImage();
		
		playAnyWaitingNodes();
	} // playRevealDealerCard()

	// This animation changes the opacity of three buttons (2x, split and stand)
	public synchronized void playShowSideButtons(boolean canStand, boolean canDouble, boolean canSplit) {
		
		// Add this play to queue if something is currently playing
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.MAKE_BUTTONS_VISIBLE, canStand, canDouble, canSplit));
			return;
		} // if
		isPlaying = true;
		
		animation.getKeyFrames().clear();
		animation.getKeyFrames().add(new KeyFrame(gameSpeed,
		    		new KeyValue(btn2x.opacityProperty(), 1.0),
		    		new KeyValue(btnSplit.opacityProperty(), 1.0),
		    		new KeyValue(btnStand.opacityProperty(), 1.0)));

		btn2x.setOpacity(0.0);
		btnSplit.setOpacity(0.0);
		btnStand.setOpacity(0.0);

		btn2x.setVisible(canDouble);
		btnSplit.setVisible(canSplit);
		btnStand.setVisible(canStand);

		animation.play();
	} // playMakeSideButtonsVisible()
	
	/*************************************************************
	 *
	 * Chip Animations
	 *
	 *************************************************************
	 */
	
	public void playAddChip(ChipView chipViewPressed) {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.BET_CHIP, chipViewPressed));
			return;
		}
		isPlaying = true;

		ChipView animatedChip = chipViewPressed.clone();
		animatedChip.setIsBet(true);
		Bounds start_Bounds = getBoundsInLocal(chipViewPressed, handChips);
		Bounds dest_Bounds = handChips.getBoundsInLocal();
		double destCenterX = (dest_Bounds.getMinX()+dest_Bounds.getMaxX())/2 - ChipView.CHIP_DIAMETER/2;
		double destCenterY = (dest_Bounds.getMinY()+dest_Bounds.getMaxY())/2 - ChipView.CHIP_DIAMETER/2;

		animatedChip.setManaged(false);
		animatedChip.setX(start_Bounds.getMinX());
		animatedChip.setY(start_Bounds.getMinY());


		animation.getKeyFrames().clear();
		
		// If this chip already exists in the bet chips, put the animated chip to the 
		if(chipViewPressed.isMarkedForRemoval()) {
			Bounds betChipBounds = getChipInBetChips(animatedChip).getBoundsInLocal();
			animation.getKeyFrames().add(new KeyFrame(chipSpeed,
			    		new KeyValue(animatedChip.xProperty(), betChipBounds.getMinX()),
			    		new KeyValue(animatedChip.yProperty(), betChipBounds.getMinY())));
		} // if
		else {
	    	// Move all chips a bit left so that the new chip will enter
	    	for(Node node : handChips.getChildren()) {
	    		ChipView chip = (ChipView) node;
	    		animation.getKeyFrames().add(new KeyFrame(chipSpeed,
		    		new KeyValue(chip.xProperty(), chip.getX()-chip.getBoundsInLocal().getHeight()*0.5)));
	    	} // for
	    	// Animation for making the new chip entering the bet ones.
			animation.getKeyFrames().add(new KeyFrame(chipSpeed,
			    		new KeyValue(animatedChip.xProperty(), destCenterX + handChips.getChildren().size()* ChipView.CHIP_DIAMETER* 0.5),
			    		new KeyValue(animatedChip.yProperty(), destCenterY)));
	    } // else
	    animation.getKeyFrames().add(new KeyFrame(chipSpeed,
	    		new KeyValue(animatedChip.removeFromUIProperty(), chipViewPressed.isMarkedForRemoval())));
	    //animation.getKeyFrames().add(new KeyFrame(startTime, startBetChipAudio));
		//animation.getKeyFrames().add(new KeyFrame(chipSpeed, stopBetChipAudio));

	    
	    if(!handChips.getChildren().contains(animatedChip))
	    	
		handChips.getChildren().add(animatedChip);
	    animation.play();
	} // playAddChip

	// Animation that removes a chip from bet chips to user chips.
	public void playRemoveChip(ChipView chipViewPressed) {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.REMOVE_CHIP, chipViewPressed));
			return;
		} // if
		isPlaying = true;
		
		// If this chip should not be removed from the UI at the end of animation then create a copy of it
		// and animate that. Otherwise animate the one already exists in the UI.
		ChipView animatedChip;
		if(chipViewPressed.isMarkedForRemoval()) {
			animatedChip = chipViewPressed;
			playCenterChips(animatedChip); // This will center the chips after this animation has finished.
		}
		else {
			animatedChip = chipViewPressed.clone();
			animatedChip.setX(chipViewPressed.getX());
			animatedChip.setY(chipViewPressed.getY());
			animatedChip.setManaged(false);
			//handChips.getChildren().add(animatedChip);
		}
			

		ChipView destChipView = null;
		for(Node node : playerChipsPane.getChildren())
			if( ((ChipView) node).getChip().getValue() == chipViewPressed.getChip().getValue()) {
				destChipView = (ChipView) node;
				break;
			} // if

		// Check to see if chipNode does not exist to the currently visible nodes
		Bounds dest_Bounds = getBoundsInLocal(destChipView == null ? playerChipsPane : destChipView, handChips);

		animation.getKeyFrames().clear();
	    animation.getKeyFrames().add(new KeyFrame(gameSpeed,
	    		new KeyValue(animatedChip.xProperty(), dest_Bounds.getMinX()),
	    		new KeyValue(animatedChip.yProperty(), dest_Bounds.getMinY()),
	    		new KeyValue(animatedChip.removeFromUIProperty(), true)));
	    //animation.getKeyFrames().add(new KeyFrame(startTime, startBetChipAudio));
		//animation.getKeyFrames().add(new KeyFrame(chipSpeed, stopBetChipAudio));
	    animation.play();
	}
	
	// Animation that centers chips in bet pane
	private void playCenterChips(ChipView animatedChip) {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.CENTER_CHIPS, animatedChip));
			return;
		} // if
		
		isPlaying = true;
		
		
		int noOfChips = handChips.getChildren().size();
	    double centerX = handChips.getWidth() / 2;
	    double firstChipX = centerX - (noOfChips/2.0*ChipView.CHIP_DIAMETER);

	    KeyValue[] keyValues = new KeyValue[noOfChips];
	    for(int i=0; i < keyValues.length; i++) {
	    	ChipView chip = (ChipView) handChips.getChildren().get(i);
	    	keyValues[i] = new KeyValue(chip.xProperty(), firstChipX + (i*ChipView.CHIP_DIAMETER));
		}
	    
	    animation.getKeyFrames().clear();
	    animation.getKeyFrames().add(new KeyFrame(gameSpeed, keyValues));
		animation.play();
	} // centerChips()

	// Animation where all chips are moved closed to each other
	public void playConsolidateChips() {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.CONSOLIDATE_CHIPS));
			return;
		} // if
		isPlaying = true;
		
		// Gray-out player's chips and disable them
		playerChipsPane.setDisable(true);
		playerChipsPane.setEffect(colorAdjust);
		
		int noOfChips = handChips.getChildren().size();
		double chipDistance = ChipView.CHIP_DIAMETER * 0.25;
		double centerX = handChips.getWidth()/2 - ChipView.CHIP_DIAMETER/2;
		double offSetX = (chipDistance * (noOfChips-1)) / 2.0;
		double firstChipX  = centerX - offSetX;


		KeyValue[] keyValues = new KeyValue[noOfChips];
		for(int i=0; i < keyValues.length; i++) {
	    	ChipView chip = (ChipView) handChips.getChildren().get(i);
	    	keyValues[i] = new KeyValue(chip.xProperty(), firstChipX + (i*chipDistance));
		} // for
		
		animation.getKeyFrames().clear();
		animation.getKeyFrames().add(new KeyFrame(gameSpeed, keyValues));
		animation.play();
	} // playConsolidateChips()
	
	// Check if a chip already exists in a pane
	private ChipView getChipInBetChips(ChipView chip) {
		for(Node node : handChips.getChildren())
			if( ((ChipView) node).getChip().getValue() == chip.getChip().getValue())
				return (ChipView) node;
		return null;
	} // getChipInBetChips()

	
	/*************************************************************
	 *
	 * Message Animations
	 *
	 *************************************************************
	 */
	private final String MSG_WON   = "You Won!",
						 		MSG_TIE   = "It's a Tie!",
								MSG_LOST  = "You Lost!",
								MSG_GOVER = "Game Over",
								MSG_BJ    = "Black Jack";
	
	// Add blur, shadow effects and set alignment on message label.
	private void initMessageLabel() {
		lblMessage = new Label();
		lblMessage.setAlignment(Pos.CENTER);
		lblMessage.setFont(Font.font(null, FontWeight.BOLD, 40));
		lblMessage.setEffect(new DropShadow());
		lblMessage.setCache(true);
		lblMessage.setCacheHint(CacheHint.SPEED);
		lblMessage.managedProperty().bind(lblMessage.visibleProperty());
		
		// Define message color
		lblMessage.textProperty().addListener((a,b,text) -> {
			lblMessage.getStyleClass().clear();
			lblMessage.getStyleClass().add( text.equals(MSG_WON)  ? "label_won"   :
										    text.equals(MSG_TIE)  ? "label_tie"   :
											text.equals(MSG_LOST) ? "label_lost"  :
											text.equals(MSG_BJ)   ? "label_bjack" :
																	"label_gover" );
		});
		//lblMessage.getStyleClass().add("label_won");
	} // initLabel()
	
	// Animates chips to winner's direction and show the corresponding message.
	public void playGiveBetsToWinnerAndShowMessage(int firstHandResult, int secondHandResult) {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.GIVE_BETS_TO_WINNER, firstHandResult, secondHandResult));
			return;
		} // if
		isPlaying = true;
		
		// Calculate where the the chips of the first hand will "go" based on the round result.
		Bounds bounds = handChips.localToScene(handChips.getBoundsInLocal());
		double handA_destX =  firstHandResult == Hand.HAND_TIES ? -bounds.getMinX() - handChips.getWidth()
															    : -bounds.getWidth()/2 + handChips.getWidth()/2;
		double handA_destY =  firstHandResult == Hand.HAND_TIES ? 0 :
						  	  firstHandResult == Hand.HAND_WINS || 
						  	  firstHandResult == Hand.BLACK_JACK_HAND ? +bounds.getMaxY() + handChips.getHeight()
						   		 					            : -bounds.getMinY() - handChips.getHeight();
		double handB_destX = handA_destX;
		double handB_destY = handA_destY;
		
		// If user has split, then calculate where the second set of chips will "go".
		if(playerSplits) {
			bounds = secondHandChips.localToScene(secondHandChips.getBoundsInLocal());
			handB_destX =  secondHandResult == Hand.HAND_TIES ? +secondHandChips.getScene().getWidth() - bounds.getMinX()
					 										  : -bounds.getWidth()/2 + secondHandChips.getWidth()/2;
			handB_destY =  secondHandResult == Hand.HAND_TIES ? 0 :
						   secondHandResult == Hand.HAND_WINS ? +bounds.getMaxY() + secondHandChips.getHeight()
						   									  : -bounds.getMinY() - secondHandChips.getHeight(); 
		} // if
		
		// Decide which label to show based on the round result
		if(playerSplits) {
			int bothHandResults = firstHandResult + secondHandResult;
			switch (bothHandResults) {
				case 0: // Both hands won, so user gets the winnings
				case 1: // One hand wins, the other ties
						lblMessage.setText(MSG_WON);
						break;			
				case 2: // It's a tie (Both hands equals to dealers, or, one hand wins and the other loses)
						lblMessage.setText(MSG_TIE);
						break;
				default: // Both hands lost or one hand is a tie and the other lost
						lblMessage.setText(MSG_LOST);
			} // switch
		} // if
		else
			lblMessage.setText( firstHandResult == Hand.HAND_WINS  ? MSG_WON :
				   		 	   	firstHandResult == Hand.HAND_TIES  ? MSG_TIE :
				   		 	   	firstHandResult == Hand.HAND_LOSES ? MSG_LOST:
				   			 							      		 MSG_BJ  );

		if(!messagePane.getChildren().contains(lblMessage))
			messagePane.getChildren().add(lblMessage);

		
		lblBetMoney.setVisible(false);
		lblMessage.setScaleX(0);
		lblMessage.setScaleY(0);
		lblMessage.setVisible(true);
		
	    
	    // Show message and move chips
	    animation.getKeyFrames().clear();
        animation.getKeyFrames().add(new KeyFrame(new Duration(1000),
        		new KeyValue(lblMessage.scaleXProperty(), 1),
        		new KeyValue(lblMessage.scaleYProperty(), 1)));
        animation.getKeyFrames().add(new KeyFrame(new Duration(1000),
	    		new KeyValue(handChips.translateXProperty(), handA_destX),
	    		new KeyValue(handChips.translateYProperty(), handA_destY),
	    		new KeyValue(secondHandChips.translateXProperty(), handB_destX),
	    		new KeyValue(secondHandChips.translateYProperty(), handB_destY)));
        animation.getKeyFrames().add(new KeyFrame(new Duration(4000),
        		new KeyValue(lblMessage.visibleProperty(), false)));
		animation.getKeyFrames().add(new KeyFrame(new Duration(4000),clearTable));
		// If user has split then remove the split image.
		if(imgSplit != null && imgSplit.getParent() != null) {
			((Pane)imgSplit.getParent()).getChildren().remove(imgSplit);
			playerSplits = false; 
		} // if
				
		animation.play();
	} // playGiveBetsToWinner
	
	public synchronized void playGameOver() {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.GAME_OVER));
			return;
		} // if
		isPlaying = true;
		
		lblBetMoney.setVisible(false);
		lblMessage.setText(MSG_GOVER);
		lblMessage.setScaleX(0);
		lblMessage.setScaleY(0);
		lblMessage.setVisible(true);
	    
	    // Show message and move chips
	    animation.getKeyFrames().clear();
        animation.getKeyFrames().add(new KeyFrame(new Duration(1000),
        		new KeyValue(lblMessage.scaleXProperty(), 1),
        		new KeyValue(lblMessage.scaleYProperty(), 1)));
        animation.getKeyFrames().add(new KeyFrame(new Duration(4000),
        		new KeyValue(lblMessage.visibleProperty(), false),
        		new KeyValue(playerChipsPane.getScene().getRoot().getChildrenUnmodifiable().get(1).visibleProperty(),true)));
        animation.getKeyFrames().add(new KeyFrame(new Duration(4000),clearTable));
		animation.play();
	} // playGameOver()
	
	/*************************************************************
	 *
	 * Hand-Indicator Animations
	 *
	 *************************************************************
	 */
	
	// Shows hand indicator on the last card played.
	public synchronized void playShowHandIndicator(int hand) {
		// If an animation is already playing add this to queue.
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.SHOW_HAND_INDICATOR, hand));
			return;
		} // if
		isPlaying = true;
		CardView lastCardInHand = (hand == Hand.FIRST_HAND) ?
									(CardView) playerHandPane.getChildren().get(playerHandPane.getChildren().size()-1) :
									(CardView) player2ndHandPane.getChildren().get(player2ndHandPane.getChildren().size()-1);
		Bounds cardBounds = lastCardInHand.getBoundsInParent();
		imgHandIndicator.setX(cardBounds.getMinX() + CardView.CARD_WIDTH/2);
		imgHandIndicator.setY(cardBounds.getMaxY() - imgHandIndicator.getFitHeight()/2);
		
		
		((Pane)lastCardInHand.getParent()).getChildren().add(imgHandIndicator);
		handIndicatorAnimation.play();
		
		playAnyWaitingNodes();
	} // playShowHandIndicator()
	
	// Removes hand indicator from cards
	public synchronized void removeHandIndicator() {
		// If an animation is already playing add this to queue.
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.REMOVE_HAND_INDICATOR));
			return;
		} // if
		isPlaying = true;
		if(imgHandIndicator.getParent() != null)
			((Pane)imgHandIndicator.getParent()).getChildren().remove(imgHandIndicator);
		playAnyWaitingNodes();
	} // removeHandIndicator()
	
	// Initializes Hand Indicator
	private void initHandIndicator() {
		imgHandIndicator.setFitHeight(50);
		imgHandIndicator.setPreserveRatio(true);
		imgHandIndicator.setManaged(false);
		imgHandIndicator.setScaleX(0.8);
		imgHandIndicator.setScaleY(0.8);
		handIndicatorAnimation = new Timeline();
		handIndicatorAnimation.setAutoReverse(true);
		handIndicatorAnimation.setCycleCount(javafx.animation.Animation.INDEFINITE);
		handIndicatorAnimation.getKeyFrames().add(new KeyFrame(new Duration(500),
        										  	new KeyValue(imgHandIndicator.scaleXProperty(), 1),
        										  	new KeyValue(imgHandIndicator.scaleYProperty(), 1)));
	} // initHandIndicator()
	
	/*************************************************************
	 *
	 * Other Animations (Doubling the bet,
	 * 					 Give bet to winner,
	 * 					 Clearing the table and
	 * 					 Show hand indicator)
	 *
	 *************************************************************
	 */

	// Animation where 2x sign is moved to bet chips.
	public synchronized void playDoubleBet(ImageView img2x) {
		if(isPlaying) {
			nodesWaitingForAnimation.add(new AnimatingContent(AnimatingContent.DOUBLE_HAND, img2x));
			return;
		} // if
		isPlaying = true;
	
		Bounds bounds = getBoundsInLocal(img2x, handChips);
		ImageView animated2xNode = new ImageView(img2x.getImage());
		animated2xNode.setPreserveRatio(true);
		animated2xNode.setFitHeight(img2x.getFitHeight());
		animated2xNode.setFitWidth(img2x.getFitWidth());

		Bounds dest_Bounds = handChips.getBoundsInLocal();
		double destCenterX = handChips.getChildren().get(handChips.getChildren().size()-1).getBoundsInLocal().getMinX() + ChipView.CHIP_DIAMETER * 0.25;
		double destCenterY = (dest_Bounds.getMinY()+dest_Bounds.getMaxY())/2 - ChipView.CHIP_DIAMETER/2;

		animated2xNode.setManaged(false);
		animated2xNode.setX(bounds.getMinX());
		animated2xNode.setY(bounds.getMinY());
		handChips.getChildren().add(animated2xNode);

		animation.getKeyFrames().clear();
		animation.getKeyFrames().add(new KeyFrame(gameSpeed,
		    		new KeyValue(animated2xNode.xProperty(), destCenterX),
		    		new KeyValue(animated2xNode.yProperty(), destCenterY),
					new KeyValue(animated2xNode.fitHeightProperty(), ChipView.CHIP_DIAMETER)));
		animation.play();
	} // playDoubleBet
	
	// Removes all chips and cards played, and brings panes back to their original position.
	public synchronized void clearTable() {
		
		// Remove chips and cards.
		handChips.getChildren().clear();
		secondHandChips.getChildren().clear();
		playerHandPane.getChildren().clear();
		player2ndHandPane.getChildren().clear();
		dealerHandPane.getChildren().clear();
		
		// Move panes to their original positions.
		handChips.setTranslateX(0);
		handChips.setTranslateY(0);
		secondHandChips.setTranslateX(0);
		secondHandChips.setTranslateY(0);
		
		// In case of split return hand panes to original position.
		playerHandPane.getParent().setTranslateX(0);
		player2ndHandPane.getParent().setTranslateX(0);

		lblBetMoney.setVisible(true);
		playerChipsPane.setDisable(false);
		playerChipsPane.setEffect(null);
	} // clearTable()
	
	// Method that calls the next play in the queue.
	private void playAnyWaitingNodes() {
		isPlaying = false;
		if(nodesWaitingForAnimation.size() != 0) {
			AnimatingContent content = nodesWaitingForAnimation.remove(0);
			switch(content.getType()) {
				case AnimatingContent.BET_CHIP:
					playAddChip((ChipView) content.getNode());
					break;
				case AnimatingContent.REMOVE_CHIP:
					playRemoveChip((ChipView) content.getNode());
					break;
				case AnimatingContent.CONSOLIDATE_CHIPS:
					playConsolidateChips(); break;
				case AnimatingContent.CENTER_CHIPS:
					playCenterChips((ChipView) content.getNode()); break;
				case AnimatingContent.GIVE_PLAYER_CARD:
					playAddCardToPlayerHand((CardView) content.getNode()); break;
				case AnimatingContent.GIVE_DEALER_CARD:
					playAddCardToDealerHand((CardView) content.getNode()); break;
				case AnimatingContent.MAKE_BUTTONS_VISIBLE:
					boolean [] buttonVisibility = content.getAsBooleanArray();
					playShowSideButtons(buttonVisibility[0], buttonVisibility[1], buttonVisibility[2]); break;
				case AnimatingContent.DOUBLE_HAND:
					playDoubleBet((ImageView) content.getNode()); break;
				case AnimatingContent.SPLIT_HAND:
					playSplitHand((CardView) content.getNode()); break;
				case AnimatingContent.CONSOLIDATE_SPLIT_HAND:
					playConsolidateSplitHand((Pane) content.getNode()); break;
				case AnimatingContent.GIVE_CARD_TO_2ND_HAND:
					playAddCardToPlayerSecondHand((CardView) content.getNode()); break;
				case AnimatingContent.REMOVE_CARD_FROM_PLAYER : 
					removeCard(content.getContentAsInteger(), playerHandPane); break;
				case AnimatingContent.REVEAL_DEALER_HAND :
					playRevealDealerCard(); break;
				case AnimatingContent.REMOVE_ALL_BET_CHIPS:
					clearTable(); break;
				case AnimatingContent.REMOVE_HAND_INDICATOR:
					removeHandIndicator(); break;
				case AnimatingContent.GIVE_BETS_TO_WINNER:
					playGiveBetsToWinnerAndShowMessage(content.getAsIntegerArray()[0],
													   content.getAsIntegerArray()[1]); break;
				case AnimatingContent.SHOW_HAND_INDICATOR:
					playShowHandIndicator(content.getContentAsInteger()); break;
				case AnimatingContent.GAME_OVER:
					playGameOver();
			} // switch
		} // if
	} // playAnyWaitingNodes()

	// Method that returns the bounds of a node based on the bounds of another node.
	private Bounds getBoundsInLocal(Node node, Node comparedToParent) {
		Bounds nodeBounds = node.localToScene(node.getBoundsInLocal());
		return comparedToParent.sceneToLocal(nodeBounds);
	} // getBoundsInLocal()
	
	
	/*************************************************************
	 * Media - Commented out as Media is not supported yet on Mobile!
	 **************************************************************/
	/*private void initMediaEventHandlers() {
		startDrawCardAudio = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				cardMediaPlayer.play();
			}
		};

		startBetChipAudio = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				chipMediaPlayer.play();
			}
		};
		
		stopDrawCardAudio = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				cardMediaPlayer.stop();
			}
		};

		stopBetChipAudio = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				chipMediaPlayer.stop();
			}
		};
	} // initMediaEventHandlers()*/	
} // class Animation
