package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;


public class AIPlayerTest {
  // ai player
  AIPlayer ai1 = new AIPlayer();
  List<FireworksPile> fPile = new LinkedList<>();
  Map<String, String> bestDiscard = new HashMap<>();
  Map<String, String> bestPlay = new HashMap<>();
  Map<String, String> bestInfo = new HashMap<>();
  Map<String, String> bestMove = new HashMap<>();

  @Test
  void bestPlay_Case1() {
    Hand testHand = new Hand(5);
    Card c1 = new Card(1, "b", true, true);
    Card c2 = new Card(2, "b", true, true);
    Card c3 = new Card(3, "b", true, true);
    Card c4 = new Card(4, "b", true, true);
    Card c5 = new Card(5, "b", true, true);

    // fireworks pile card to be added
    Card c6 = new Card(1,"b", true, true);

    // add cards to AIPlayer hand
    testHand.addCard(c1);
    testHand.addCard(c2);
    testHand.addCard(c3);
    testHand.addCard(c4);
    testHand.addCard(c5);

    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    bluePile.addCard(c6);
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestPlay = ai1.bestPlay(testHand, fPile).asMap();
    assertEquals("2", bestPlay.get("handIndex"));
  }

  @Test
  void bestPlay_Case2() {
    Hand hand = new Hand(5);
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(2, "g", false, false);
    Card c3 = new Card(2, "b", true, false);
    Card c4 = new Card(3, "g", false, false);
    Card c5 = new Card(4, "g", false, false);

    // card to be added in fireworks pile
    Card c6 = new Card(1,"b", true, true);

    // cards added to AI hand
    hand.addCard(c1);
    hand.addCard(c2);
    hand.addCard(c3);
    hand.addCard(c4);
    hand.addCard(c5);

    // blue pile has rank 1 card, everything else empty
    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    bluePile.addCard(c6);
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestPlay = ai1.bestPlay(hand, fPile).asMap();
    assertEquals("3", bestPlay.get("handIndex"));
  }

  @Test
  void bestPlay_Case3() {
    // case 3
    Hand hand = new Hand(5);
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(2, "g", false, false);
    Card c3 = new Card(2, "b", false, true);
    Card c4 = new Card(3, "g", false, false);
    Card c5 = new Card(4, "g", false, false);

    // card to be added in fireworks pile
    Card c6 = new Card(1,"b", true, true);

    // cards added to AI hand
    hand.addCard(c1);
    hand.addCard(c2);
    hand.addCard(c3);
    hand.addCard(c4);
    hand.addCard(c5);

    // blue pile has rank 1 card, everything else empty
    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    bluePile.addCard(c6);
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestPlay = ai1.bestPlay(hand, fPile).asMap();
    assertEquals("3", bestPlay.get("handIndex"));
  }

  @Test
  void bestPlay_Case4() {
    // case 4
    Hand hand = new Hand(5);
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(2, "g", false, false);
    Card c3 = new Card(2, "b", false, false);
    Card c4 = new Card(3, "g", false, false);
    Card c5 = new Card(4, "g", false, false);

    // card to be added in fireworks pile
    Card c6 = new Card(1,"b", true, true);

    // cards added to AI hand
    hand.addCard(c1);
    hand.addCard(c2);
    hand.addCard(c3);
    hand.addCard(c4);
    hand.addCard(c5);

    // blue pile has rank 1 card, everything else empty
    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    bluePile.addCard(c6);
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestPlay = ai1.bestPlay(hand, fPile).asMap();
    assertTrue((Integer.parseInt(bestPlay.get("handIndex")) > 0) &&
            (Integer.parseInt(bestPlay.get("handIndex")) < 6 ));
  }

  @Test
  void bestDiscard_Case1() {
    Hand testHand = new Hand(5);
    Card c1 = new Card(1, "b", true, true);
    Card c2 = new Card(1, "b", true, true);
    Card c3 = new Card(1, "b", true, true);
    Card c4 = new Card(1, "g", true, true);
    Card c5 = new Card(1, "g", true, true);

    // fireworks pile
    Card c6 = new Card(1,"b", true, true);

    // add cards to AIPlayer hand
    testHand.addCard(c1);
    testHand.addCard(c2);
    testHand.addCard(c3);
    testHand.addCard(c4);
    testHand.addCard(c5);

    // empty discard pile
    DiscardPile dPile = new DiscardPile(0);

    // blue pile with blue 1, rest empty
    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    bluePile.addCard(c6);
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestDiscard = ai1.bestDiscard(testHand, fPile, dPile, false).asMap();
    assertEquals("1", bestDiscard.get("handIndex"));
  }

  @Test
  void bestDiscard_Case2() {
    // case 2
    Hand testHand = new Hand(5);
    Card c1 = new Card(1, "r", true, true);
    Card c2 = new Card(1, "b", true, true);
    Card c3 = new Card(1, "b", true, true);
    Card c4 = new Card(1, "g", true, true);
    Card c5 = new Card(1, "g", true, true);

    // add cards to AIPlayer hand
    testHand.addCard(c1);
    testHand.addCard(c2);
    testHand.addCard(c3);
    testHand.addCard(c4);
    testHand.addCard(c5);

    // blue 1 and two red 1s in discard pile
    DiscardPile dPile = new DiscardPile(50);
    dPile.addCard(new Card(1,"b", true, true));
    dPile.addCard(new Card(1, "r", true, true));
    dPile.addCard(new Card(1, "r", true, true));

    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestDiscard = ai1.bestDiscard(testHand, fPile, dPile, false).asMap();
    assertEquals("2", bestDiscard.get("handIndex"));
  }

  @Test
  void bestDiscard_Case3() {
    Hand testHand = new Hand(5);
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(1, "r", false, false);
    Card c3 = new Card(1, "b", false, true);
    Card c4 = new Card(1, "g", false, false);
    Card c5 = new Card(1, "g", false, false);

    // discard
    Card discardBlue1 = new Card(1,"b", true, true);
    Card discardBlue2 = new Card(2,"b", true, true);

    // add cards to AIPlayer hand
    testHand.addCard(c1);
    testHand.addCard(c2);
    testHand.addCard(c3);
    testHand.addCard(c4);
    testHand.addCard(c5);

    // empty discard pile
    DiscardPile dPile = new DiscardPile(50);
    dPile.addCard(discardBlue1);
    dPile.addCard(discardBlue1);
    dPile.addCard(discardBlue2);
    dPile.addCard(discardBlue2);

    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestDiscard = ai1.bestDiscard(testHand, fPile, dPile, false).asMap();
    assertEquals(3,Integer.parseInt(bestDiscard.get("handIndex")));
  }

  @Test
  void bestDiscard_Case4() {
    Hand testHand = new Hand(5);
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(2, "g", false, false);
    Card c3 = new Card(2, "b", false, false);
    Card c4 = new Card(3, "g", false, false);
    Card c5 = new Card(4, "g", false, false);

    // discard
    Card discardBlue1 = new Card(1,"b", true, true);
    Card discardBlue2 = new Card(2,"b", true, true);

    // add cards to AIPlayer hand
    testHand.addCard(c1);
    testHand.addCard(c2);
    testHand.addCard(c3);
    testHand.addCard(c4);
    testHand.addCard(c5);

    // empty discard pile
    DiscardPile dPile = new DiscardPile(50);
    dPile.addCard(discardBlue1);
    dPile.addCard(discardBlue1);
    dPile.addCard(discardBlue2);
    dPile.addCard(discardBlue2);
    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    bestDiscard = ai1.bestDiscard(testHand, fPile, dPile, false).asMap();
    assertTrue((Integer.parseInt(bestDiscard.get("handIndex")) > 0) &&
            (Integer.parseInt(bestDiscard.get("handIndex")) < 6 ));
  }

  @Test
  void bestInfo_Case1() {
    Player p2 = new Player(5);
    Player p3 = new Player(5);
    Hand AIHand = new Hand(5);
    Map<Integer, Hand> playerHands = new HashMap<>();

    // AI's hand, doesn't matter
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(2, "g", false, false);
    Card c3 = new Card(2, "b", false, false);
    Card c4 = new Card(3, "g", false, false);
    Card c5 = new Card(4, "g", false, false);

    // Player 2's cards
    Card p2c1 = new Card(1, "g", false, false);
    Card p2c2 = new Card(2, "g", false, false);
    Card p2c3 = new Card(3, "g", false, false);
    Card p2c4 = new Card(4, "g", false, false);
    Card p2c5 = new Card(5, "g", false, false);

    // Player 3's cards
    Card p3c1 = new Card(1, "y", false, false);
    Card p3c2 = new Card(1, "y", false, false);
    Card p3c3 = new Card(1, "y", false, false);
    Card p3c4 = new Card(4, "b", false, false);
    Card p3c5 = new Card(2, "r", false, false);

    // fireworks cards
    Card b1 = new Card(1, "b", true, true);
    Card b2 = new Card(2, "b", true, true);
    Card b3 = new Card(3, "b", true, true);


    // add cards to ai hand and p2 hand
    AIHand.addCard(c1);
    AIHand.addCard(c2);
    AIHand.addCard(c3);
    AIHand.addCard(c4);
    AIHand.addCard(c5);

    p2.getHand().addCard(p2c1);
    p2.getHand().addCard(p2c2);
    p2.getHand().addCard(p2c3);
    p2.getHand().addCard(p2c4);
    p2.getHand().addCard(p2c5);

    p3.getHand().addCard(p3c1);
    p3.getHand().addCard(p3c2);
    p3.getHand().addCard(p3c3);
    p3.getHand().addCard(p3c4);
    p3.getHand().addCard(p3c5);

    // fire works pile up to blue 3
    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    bluePile.addCard(b1);
    bluePile.addCard(b2);
    bluePile.addCard(b3);
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    //playerHands.put(1, AIHand);
    playerHands.put(2, p2.getHand());
    playerHands.put(3, p3.getHand());

    bestInfo = ai1.bestInfo(fPile, playerHands, false).asMap();
    assertEquals("3", bestInfo.get("playerIndex"));
    assertEquals("b", bestInfo.get("colour"));
  }

  @Test
  void bestInfo_Case2() {
    // other players
    Player p2 = new Player(5);
    Player p3 = new Player(5);
    Hand AIHand = new Hand(5);
    Map<Integer, Hand> playerHands = new HashMap<>();

    // AI's hand, doesn't matter
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(2, "g", false, false);
    Card c3 = new Card(2, "b", false, false);
    Card c4 = new Card(3, "g", false, false);
    Card c5 = new Card(4, "g", false, false);

    // Player 2's cards
    Card p2c1 = new Card(2, "g", false, false);
    Card p2c2 = new Card(2, "g", false, false);
    Card p2c3 = new Card(3, "g", false, false);
    Card p2c4 = new Card(4, "g", false, false);
    Card p2c5 = new Card(5, "g", false, false);

    // Player 3's cards
    Card p3c1 = new Card(1, "y", false, false);
    Card p3c2 = new Card(1, "y", false, false);
    Card p3c3 = new Card(1, "y", false, false);
    Card p3c4 = new Card(4, "b", false, false);
    Card p3c5 = new Card(2, "r", false, false);

    // add cards to ai hand and p2 hand
    AIHand.addCard(c1);
    AIHand.addCard(c2);
    AIHand.addCard(c3);
    AIHand.addCard(c4);
    AIHand.addCard(c5);

    p2.getHand().addCard(p2c1);
    p2.getHand().addCard(p2c2);
    p2.getHand().addCard(p2c3);
    p2.getHand().addCard(p2c4);
    p2.getHand().addCard(p2c5);

    p3.getHand().addCard(p3c1);
    p3.getHand().addCard(p3c2);
    p3.getHand().addCard(p3c3);
    p3.getHand().addCard(p3c4);
    p3.getHand().addCard(p3c5);

    // fire works pile are empty
    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    yellowPile.addCard(new Card(1, "y", true, true));
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    //playerHands.put(1, AIHand);
    playerHands.put(2, p2.getHand());
    playerHands.put(3, p3.getHand());

    bestInfo = ai1.bestInfo(fPile, playerHands, false).asMap();
    // assertEquals("3", bestInfo.get("playerIndex"));
    // assertEquals("y", bestInfo.get("colour"));
    assertEquals("2", bestInfo.get("playerIndex"));
    assertEquals("5", bestInfo.get("rank"));
  }

  @Test
  void bestInfo_Case3() {
    // other players
    Player p2 = new Player(5);
    Player p3 = new Player(5);
    Hand AIHand = new Hand(5);
    Map<Integer, Hand> playerHands = new HashMap<>();

    // AI's hand, doesn't matter
    Card c1 = new Card(1, "r", false, false);
    Card c2 = new Card(2, "g", false, false);
    Card c3 = new Card(2, "b", false, false);
    Card c4 = new Card(3, "g", false, false);
    Card c5 = new Card(4, "g", false, false);

    // Player 2's cards
    Card p2c1 = new Card(2, "g", false, false);
    Card p2c2 = new Card(2, "g", false, false);
    Card p2c3 = new Card(3, "g", false, false);
    Card p2c4 = new Card(4, "g", false, false);
    Card p2c5 = new Card(5, "g", true, false);

    // Player 3's cards
    Card p3c1 = new Card(1, "y", false, false);
    Card p3c2 = new Card(1, "y", false, false);
    Card p3c3 = new Card(1, "y", false, false);
    Card p3c4 = new Card(4, "b", false, false);
    Card p3c5 = new Card(2, "r", false, false);

    // add cards to ai hand and p2 hand
    AIHand.addCard(c1);
    AIHand.addCard(c2);
    AIHand.addCard(c3);
    AIHand.addCard(c4);
    AIHand.addCard(c5);

    p2.getHand().addCard(p2c1);
    p2.getHand().addCard(p2c2);
    p2.getHand().addCard(p2c3);
    p2.getHand().addCard(p2c4);
    p2.getHand().addCard(p2c5);

    p3.getHand().addCard(p3c1);
    p3.getHand().addCard(p3c2);
    p3.getHand().addCard(p3c3);
    p3.getHand().addCard(p3c4);
    p3.getHand().addCard(p3c5);

    FireworksPile bluePile = new FireworksPile("b");
    FireworksPile redPile = new FireworksPile("r");
    FireworksPile yellowPile = new FireworksPile("y");
    FireworksPile greenPile = new FireworksPile("g");
    FireworksPile whitePile = new FireworksPile("w");
    yellowPile.addCard(new Card(1, "y", true, true));
    fPile.add(bluePile);
    fPile.add(redPile);
    fPile.add(yellowPile);
    fPile.add(greenPile);
    fPile.add(whitePile);

    //playerHands.put(1, AIHand);
    playerHands.put(2, p2.getHand());
    playerHands.put(3, p3.getHand());

    bestInfo = ai1.bestInfo(fPile, playerHands, false).asMap();
    assertEquals("3", bestInfo.get("playerIndex"));
    assertEquals("y", bestInfo.get("colour"));
    //assertEquals("2", bestInfo.get("playerIndex"));
    //assertEquals("g", bestInfo.get("colour"));
  }

  @Test
  void bestMoveTesting() {
    // TODO Once the others small methods are fix we will do bestMove() testing
  }
}
