Electricity Billing System - README

## HOW TO RUN

1. Make sure you have JDK 17 (or newer) and Apache Maven installed and configured.
2. Open your terminal or command prompt.
3. Navigate to the root directory of this project (the one containing pom.xml).
4. Run the command: mvn clean javafx:run

The application should start after Maven downloads the required dependencies.

## DEMO CREDENTIALS

- Role: Admin
- Username: admin
- Password: admin

## FEATURES

- User Roles: Admin (full access) and Customer (view-only, not implemented in this demo).
- CRUD Operations: Admins can add, view, update, and delete customer bills.
- Live Theming: Switch between light and dark modes instantly from the top menu.
- Live Localization: Switch between English, Hindi, and Tamil. The entire UI, including form labels, validation messages, and chatbot responses, will update immediately.
- Real-time Form Validation: Input fields provide immediate feedback with tooltips.
- Chatbot Assistant: A collapsible chatbot panel that can answer questions about customer bills (e.g., "What is bill for meter 1002?").
- Text-to-Speech (TTS): The chatbot's responses are spoken aloud in an English voice.
- Keyboard Shortcuts:
  - Tab/Enter: Navigate through form fields.
  - Ctrl+S: Save the form.
  - Esc: Cancel/close the form dialog.

## NOTE ON VOICE FEATURES

- Text-to-Speech (TTS) is implemented using FreeTTS (English voice).
- Speech-to-Text (STT) using CMUSphinx is complex to bundle and has been omitted from this text-only project distribution to ensure it runs out-of-the-box. The microphone button in the UI is a placeholder.