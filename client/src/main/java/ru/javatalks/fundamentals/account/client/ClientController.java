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
        serverUrl.disableProperty().bind(running);
        minimumId.disableProperty().bind(running);
        maximumId.disableProperty().bind(running);

        actualCountOfReaders.textProperty().bind(countOfReadersUpdate.asString());
        actualCountOfWriters.textProperty().bind(countOfWritersUpdate.asString());

        // Используем эту штуку, чтобы не обновлять количество нитей чаще чем нужно.
        // http://stackoverflow.com/questions/22772379/updating-ui-from-different-threads-in-javafx
        AnimationTimer updateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (null != readerTaskManager) {
                    countOfReadersUpdate.setValue(readerTaskManager.getCurrentNumber());
                }
                if (null != writerTaskManager) {
                    countOfWritersUpdate.setValue(writerTaskManager.getCurrentNumber());
                }
            }
        };
        updateTimer.start();

        countOfReaders.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (null == readerTaskManager) {
                    return;
                }
                if (!running.get()) {
                    return;
                }
                readerTaskManager.setMaxNumber(Integer.valueOf(newValue));
            }
        });

        countOfWriters.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (null == writerTaskManager) {
                    return;
                }
                if (!running.get()) {
                    return;
                }
                writerTaskManager.setMaxNumber(Integer.valueOf(newValue));
            }
        });
    }

    @FXML
    private void onStartAction() {
        running.setValue(true);
        log.log(INFO, "Starting...");

        final String url = serverUrl.getText();
        final int minId = Integer.valueOf(minimumId.getText());
        final int maxId = Integer.valueOf(maximumId.getText());

        if (null != readerTaskManager) {
            readerTaskManager.close();
        }
        readerTaskManager = new TaskManager(new TaskFactory() {
            @Nonnull
            @Override
            public AbstractTask createTask(@Nonnull TaskManager taskManager) {
                return new ReaderTask(taskManager, url, minId, maxId);
            }
        });

        if (null != writerTaskManager) {
            writerTaskManager.close();
        }
        writerTaskManager = new TaskManager(new TaskFactory() {
            @Nonnull
            @Override
            public AbstractTask createTask(@Nonnull TaskManager taskManager) {
                return new WriterTask(taskManager, url, minId, maxId);
            }
        });

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
