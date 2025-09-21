package com.ebill.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class LocalizationService {
    private static LocalizationService instance;
    private final Map<String, StringProperty> stringProperties = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private LocalizationService() {}

    public static synchronized LocalizationService getInstance() {
        if (instance == null) {
            instance = new LocalizationService();
        }
        return instance;
    }

    public void loadLanguage(String langCode) {
        String resourcePath = "/lang/" + langCode + ".json";
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Cannot find language file: " + resourcePath);
                return;
            }
            JsonNode rootNode = objectMapper.readTree(is);
            updateProperties(rootNode, "");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProperties(JsonNode node, String prefix) {
        node.fields().forEachRemaining(entry -> {
            String currentKey = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            if (entry.getValue().isObject()) {
                updateProperties(entry.getValue(), currentKey);
            } else {
                String value = entry.getValue().asText();
                if (stringProperties.containsKey(currentKey)) {
                    stringProperties.get(currentKey).set(value);
                } else {
                    stringProperties.put(currentKey, new SimpleStringProperty(value));
                }
            }
        });
    }

    public StringProperty getProperty(String key) {
        return stringProperties.computeIfAbsent(key, k -> new SimpleStringProperty("!" + k + "!"));
    }

    public String getString(String key) {
        return getProperty(key).get();
    }
}