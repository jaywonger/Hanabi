package controller;

import model.HanabiGame;
import view.DisplayC;

/**
 * The Main class of the Hanabi Client where execution starts. Creates a new
 * SendController and ReceiveController pair with a common data block,
 * creates a new instance of the Model, and starts up the Client's view from
 * the Main Menu screen.
 */
public class MainController {
    public static void main(String[] args) {
        ControllerData controllerData = ControllerData.getInstance();
        ServerComm serverComm = new ServerComm();
        HanabiGame game = new HanabiGame();
        controllerData.setGame(game);
        SendController sendController =
          new SendController(controllerData, serverComm);

        DisplayC display = new DisplayC(sendController);
        game.addObserver(display);

        MainController controller = new MainController();
        display.displayMainMenu();
    }
}
