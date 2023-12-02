package PoolGame;

import PoolGame.config.*;

import java.util.List;

import javafx.application.Application;
import javafx.stage.Stage;

/** Main application entry point. */
public class App extends Application {
    /**
     * @param args First argument is the path to the config file
     */
    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Starts the application.
     *
     * @param primaryStage The primary stage for the application.
     */
    @Override
    public void start(Stage primaryStage) {
        // READ IN CONFIG
        GameManager gameManager = new GameManager();

        List<String> args = getParameters().getRaw();
        String configPath = checkConfig(args);

        ReaderFactory tableFactory = new TableReaderFactory();
        Reader tableReader = tableFactory.buildReader();
        tableReader.parse(configPath, gameManager);

        ReaderFactory ballFactory = new BallReaderFactory();
        Reader ballReader = ballFactory.buildReader();
        ballReader.parse(configPath, gameManager);
        gameManager.buildManager();

        // START GAME MANAGER
        gameManager.run();
        primaryStage.setTitle("Pool");
        primaryStage.setScene(gameManager.getScene());
        primaryStage.show();
        gameManager.run();


    }

    /**
     * Checks if the config file path is given as an argument.
     * 
     * @param args
     * @return config path.
     */
    public static String checkConfig(List<String> args) {
        String configPath;
        if (args.size() > 0) {
            configPath = args.get(0);
        } else {
            configPath = "src/main/resources/config_easy.json";
        }
        return configPath;
    }


}
