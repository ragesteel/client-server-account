package ru.javatalks.fundamentals.account.client;

import lombok.extern.java.Log;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.annotation.Nonnull;

import static java.util.logging.Level.INFO;

@Log
public class ClientController {
    private final BooleanProperty running = new SimpleBooleanProperty(false);

    private final IntegerProperty countOfReadersUpdate = new SimpleIntegerProperty(0);
    private final IntegerProperty countOfWritersUpdate = new SimpleIntegerProperty(0);

    @FXML
    private TextField serverUrl;
    @FXML
    private TextField minimumId;
    @FXML
    private TextField maximumId;
    @FXML
    private TextField countOfReaders;
    @FXML
    private Label actualCountOfReaders;
    @FXML
    private TextField countOfWriters;
    @FXML
    private Label actualCountOfWriters;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;

    private TaskManager readerTaskManager;
    private TaskManager writerTaskManager;

    @FXML
    private void initialize() {
        log.log(INFO, "Initializing...");
        startButton.disableProperty().bind(running);
        stopButton.disableProperty().bind(running.not());
        serverUrl.disableProperty().bind(running);
        actualCountOfReaders.textProperty().bind(countOfReadersUpdate.asString());
        actualCountOfWriters.textProperty().bind(countOfWritersUpdate.asString());

        readerTaskManager = new TaskManager(new TaskFactory() {
            @Nonnull
            @Override
            public AbstractTask createTask(@Nonnull TaskManager taskManager) {
                return new ReaderTask(taskManager);
            }
        });

        writerTaskManager = new TaskManager(new TaskFactory() {
            @Nonnull
            @Override
            public AbstractTask createTask(@Nonnull TaskManager taskManager) {
                return new WriterTask(taskManager);
            }
        });

        // Используем эту штуку, чтобы не обновлять количество нитей чаще чем нужно.
        // http://stackoverflow.com/questions/22772379/updating-ui-from-different-threads-in-javafx
        AnimationTimer updateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                countOfReadersUpdate.setValue(readerTaskManager.getCurrentNumber());
                countOfWritersUpdate.setValue(writerTaskManager.getCurrentNumber());
            }
        };
        updateTimer.start();

        countOfReaders.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                readerTaskManager.setMaxNumber(Integer.valueOf(newValue));
            }
        });

        countOfWriters.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                writerTaskManager.setMaxNumber(Integer.valueOf(newValue));
            }
        });
    }

    @FXML
    private void onStartAction() {
        running.setValue(true);
        log.log(INFO, "Starting...");
        readerTaskManager.setMaxNumber(Integer.valueOf(countOfReaders.getText()));
        writerTaskManager.setMaxNumber(Integer.valueOf(countOfWriters.getText()));
    }

    @FXML
    private void onStopAction() {
        running.setValue(false);
        log.log(INFO, "Stopping...");
        readerTaskManager.setMaxNumber(0);
        writerTaskManager.setMaxNumber(0);
    }
}
