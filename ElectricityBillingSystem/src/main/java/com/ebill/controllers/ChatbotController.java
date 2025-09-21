package com.ebill.controllers;

import com.ebill.services.ChatbotService;
import com.ebill.services.LocalizationService;
import com.ebill.services.TextToSpeechService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class ChatbotController {

    @FXML private VBox chatBox;
    @FXML private TextField inputField;
    @FXML private Button sendButton;
    @FXML private ScrollPane scrollPane;

    private final ChatbotService chatbotService = new ChatbotService();
    private final TextToSpeechService ttsService = TextToSpeechService.getInstance();
    private final LocalizationService loc = LocalizationService.getInstance();

    @FXML
    public void initialize() {
        sendButton.setGraphic(new FontIcon("fas-paper-plane"));

        inputField.promptTextProperty().bind(loc.getProperty("chatbot.input_placeholder"));

        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> scrollPane.setVvalue(1.0));

        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSend();
            }
        });
        
        addMessage(loc.getString("chatbot.greeting"), false);
    }

    @FXML
    private void handleSend() {
        String userInput = inputField.getText().trim();
        if (!userInput.isEmpty()) {
            addMessage(userInput, true);
            inputField.clear();

            String botResponse = chatbotService.getResponse(userInput);
            addMessage(botResponse, false);
            ttsService.speak(botResponse);
        }
    }

    private void addMessage(String text, boolean isUser) {
        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add("chat-bubble");

        VBox messageContainer = new VBox(messageLabel);

        if (isUser) {
            messageLabel.getStyleClass().add("user-bubble");
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
        } else {
            messageLabel.getStyleClass().add("bot-bubble");
            messageContainer.setAlignment(Pos.CENTER_LEFT);
        }
        
        Platform.runLater(() -> chatBox.getChildren().add(messageContainer));
    }
}