package com.example.edutrack.utils;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class ToastUtil {

    public static void showSuccess(String message) {
        Notifications.create()
            .title("Success")
            .text(message)
            .position(Pos.BOTTOM_RIGHT)
            .hideAfter(Duration.seconds(4))
            .showInformation();
    }

    public static void showError(String message) {
        Notifications.create()
            .title("Error")
            .text(message)
            .position(Pos.BOTTOM_RIGHT)
            .hideAfter(Duration.seconds(5))
            .showError();
    }

    public static void showWarning(String message) {
        Notifications.create()
                .title("Warning")
                .text(message)
                .position(Pos.BOTTOM_RIGHT)
                .hideAfter(Duration.seconds(5))
                .showWarning();
    }
}