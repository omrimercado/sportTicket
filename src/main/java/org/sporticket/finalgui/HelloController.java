package org.sporticket.finalgui;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloController {

    @FXML private VBox start, loginSection, signUpSection, dashboardSection, buySection, sellSection, adminPanel,Tickets;
    @FXML private TextField newUsernameField,newPasswordField,fullNameField,emailField,phoneNumberField,usernameField, passwordField, searchField, eventNameField, eventDateField, eventGateField, eventPriceField;
    @FXML private Label userGreeting, adminGreeting, loginError, sellError, searchError;
    @FXML private BorderPane usersSection;
    @FXML private StackPane tickets;
    @FXML
    private TableView<Map<String, Object>> ticketTable;

    @FXML
    private TableColumn<Map<String, Object>, String> eventColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> dateColumn;
    @FXML
    private TableColumn<Map<String, Object>, Number> priceColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> gateColumn;
    @FXML
    private TableColumn<Map<String, Object>, Void> actionColumn;
    @FXML
    private TableView<Map<String, Object>> userTable;
    @FXML
    private TableColumn<Map<String, Object>, String> usernameColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> fullNameColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> emailColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> phoneColumn;
    @FXML
    private TableColumn<Map<String, Object>, Boolean> adminColumn;
    @FXML
    private TableColumn<Map<String, Object>, Void> deleteColumn;
    @FXML
    private TableView<Map<String, Object>> allTicketsTable;

    @FXML
    private TableColumn<Map<String, Object>, String> TicketNameColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> dateTicketColumn;
    @FXML
    private TableColumn<Map<String, Object>, Number> priceTicketColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> gateTicketColumn;
    @FXML
    private TableColumn<Map<String, Object>, String> usernameTicketColumn;



    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;
    private final Gson gson = new Gson();

    @FXML
    public void initialize() {
        showStartScreen();
        initializeTable();
        initializeUserTable();
        initializeTicketTable();

    }

    private int parseResponseCode(String jsonResponse) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            String status = jsonObject.get("status").getAsString();
            if ("OK".equalsIgnoreCase(status)) {
                // If status is OK, return the integer value in the "data" field.
                return jsonObject.get("data").getAsInt();
            } else {
                // If status is not OK, treat it as an error (invalid credentials)
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String sendPostRequest(String endpoint, Map<String, String> data) {
        try (Socket socket = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

            // Create JSON from data map
            JsonObject jsonRequest = new JsonObject();
            jsonRequest.addProperty("endpoint", endpoint);
            data.forEach(jsonRequest::addProperty);

            // Send JSON request to server
            output.writeObject(jsonRequest.toString());
            output.flush();

            // Read response from server
            return (String) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    @FXML
    private void initializeTable() {
        // For each column, define how to extract data from the Map<String, Object>

        eventColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            // 'eventName' is the JSON key in your response
            String eventName = row.get("eventName") != null ? row.get("eventName").toString() : "";
            return new ReadOnlyStringWrapper(eventName);
        });

        dateColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            // 'date' is the JSON key in your response
            String date = row.get("date") != null ? row.get("date").toString() : "";
            return new ReadOnlyStringWrapper(date);
        });

        priceColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            // 'price' might be a Double (or Number)
            double price = 0.0;
            if (row.get("price") instanceof Number) {
                price = ((Number) row.get("price")).doubleValue();
            }
            return new ReadOnlyObjectWrapper<>(price);
        });

        gateColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            // 'gate' is a String in your JSON
            String gate = row.get("gate") != null ? row.get("gate").toString() : "";
            return new ReadOnlyStringWrapper(gate);
        });
        actionColumn.setCellFactory(col -> new TableCell<Map<String, Object>, Void>() {
            private final Button buyButton = new Button("Buy");

            {
                // Style the button if you like
                buyButton.setStyle("-fx-background-color: #2E86C1; -fx-text-fill: white;");

                // Handle click events
                buyButton.setOnAction(e -> {
                    // Get the row data for this button
                    Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                    handleBuy(rowData);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buyButton);
                }
            }
        });


        ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        eventColumn.setPrefWidth(150);
        dateColumn.setPrefWidth(100);
        priceColumn.setPrefWidth(80);
        gateColumn.setPrefWidth(60);



        ticketTable.setVisible(false);
    }



    @FXML
    public void handleLogin() {
        sellError.setVisible(false);
        usersSection.setVisible(false);
        tickets.setVisible(false);
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage(loginError, "Username and password cannot be empty.");
            return;
        }

        // Create headers JSON object
        JsonObject headers = new JsonObject();
        headers.addProperty("action", "user/login");

        // Create body JSON object
        JsonObject body = new JsonObject();
        body.addProperty("username", username);
        body.addProperty("password", password);

        // Combine into a single JSON request object
        JsonObject request = new JsonObject();
        request.add("headers", headers);
        request.add("body", body);

        new Thread(() -> {
            ticketTable.setVisible(false);
            ticketTable.getItems().clear();
            String jsonResponse = sendToServer(request.toString());

            if (jsonResponse == null) {
                showErrorMessage(loginError, "Error connecting to server.");
                return;
            }
            int responseCode = parseResponseCode(jsonResponse);
            javafx.application.Platform.runLater(() -> {
                if (responseCode == 1) {
                    userGreeting.setText("HELLO, " + username.toUpperCase());
                    showDashboard();
                } else if (responseCode == 2) {
                    adminGreeting.setText("HELLO, ADMIN");
                    showAdminPanel();
                } else {
                    showErrorMessage(loginError, "Invalid username or password.");
                }
            });
        }).start();
    }
    @FXML
    public void handleSignUp() {
        hideAllSections();
        String username = newUsernameField.getText().trim();
        String password = newPasswordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneNumberField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            showErrorMessage(loginError, "All fields are required.");
            showSignUpSection();
            return;
        }

        JsonObject headers = new JsonObject();
        headers.addProperty("action", "user/signUp");
        JsonObject body = new JsonObject();
        body.addProperty("username",username);
        body.addProperty("password",password);
        body.addProperty("fullName",fullName);
        body.addProperty("email",email);
        body.addProperty("phone",phone);
        JsonObject request = new JsonObject();
        request.add("headers",headers);
        request.add("body",body);
        new Thread(() -> {
            String jsonResponse = sendToServer(request.toString());

            if (jsonResponse == null) {
                showErrorMessage(loginError, "Error connecting to server.");
                return;
            }
            int responseCode = parseResponseCode(jsonResponse);
            javafx.application.Platform.runLater(() -> {
                if (responseCode == 1) {
                    userGreeting.setText("HELLO, " + username.toUpperCase());
                    showDashboard();
                } else {
                    showErrorMessage(loginError, "Signup failed. Try a different username.");
                    showSignUpSection();
                }
            });
        }).start();
    }

    @FXML
    public void handleSearch() {
        String eventName = searchField.getText().trim();
        if (eventName.isEmpty()) {
            showErrorMessage(searchError, "Event name cannot be empty.");
            return;
        }

        JsonObject request = new JsonObject();
        JsonObject headers = new JsonObject();
        JsonObject body = new JsonObject();
        headers.addProperty("action", "ticket/search");

        body.addProperty("eventName", eventName);
        request.add("headers",headers);
        request.add("body",body);
        new Thread(() -> {
            String jsonResponse = sendToServer(request.toString());
            if (jsonResponse == null) {
                showErrorMessage(searchError, "Error retrieving events from server.");
                return;
            }

            Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);
            String status = (String) responseMap.get("status");
            String message = (String) responseMap.get("message");
            Object data = responseMap.get("data");

            // Convert 'response.getData()' into a List<Map<String, Object>>
            Type mapListType = new TypeToken<List<Map<String, Object>>>() {}.getType();
            List<Map<String, Object>> ticketList = gson.fromJson(gson.toJson(data), mapListType);

            // Optionally clear the table initially
            ticketTable.getItems().clear();
            javafx.application.Platform.runLater(() -> {

                ticketTable.setVisible(true);
                ticketTable.getItems().clear();
                ticketTable.setItems(FXCollections.observableArrayList(ticketList));
            });
        }).start();
    }
    @FXML
    public void handleSell() {
        String event = eventNameField.getText().trim();
        String date = eventDateField.getText().trim();
        String gate = eventGateField.getText().trim();
        String price = eventPriceField.getText().trim();
        String username = usernameField.getText().trim();

        if (event.isEmpty() || date.isEmpty() || gate.isEmpty() || price.isEmpty()) {
            showErrorMessage(sellError, "All fields must be filled out.");
            return;
        }

        JsonObject request = new JsonObject();
        JsonObject headers = new JsonObject();
        JsonObject body = new JsonObject();

        headers.addProperty("action", "ticket/add");
        body.addProperty("username", username);
        body.addProperty("eventName", event);
        body.addProperty("date", date);
        body.addProperty("gate", gate);
        body.addProperty("price", price);

        request.add("headers", headers);
        request.add("body", body);

        new Thread(() -> {
            String jsonResponse = sendToServer(request.toString());

            // Handle null or empty response
            if (jsonResponse == null || jsonResponse.isEmpty()) {
                javafx.application.Platform.runLater(() -> showErrorMessage(sellError, "Error: No response from server."));
                return;
            }

            // Parse JSON response properly
            try {
                Gson gson = new Gson();
                JsonObject responseObj = gson.fromJson(jsonResponse, JsonObject.class);

                if (responseObj.has("status")) {
                    String status = responseObj.get("status").getAsString();

                    if (status.equalsIgnoreCase("ERROR")) {
                        // Show error message from the server
                        String errorMessage = responseObj.has("message") ? responseObj.get("message").getAsString() : "Unknown error occurred.";
                        javafx.application.Platform.runLater(() -> showErrorMessage(sellError, errorMessage));
                    } else if (status.equalsIgnoreCase("OK")) {
                        // Show success message
                        javafx.application.Platform.runLater(() -> {
                            sellError.setText("Ticket posted successfully!");
                            sellError.setStyle("-fx-text-fill: green;");
                            sellError.setVisible(true);
                        });
                    } else {
                        javafx.application.Platform.runLater(() -> showErrorMessage(sellError, "Unexpected response from server."));
                    }
                } else {
                    javafx.application.Platform.runLater(() -> showErrorMessage(sellError, "Invalid server response."));
                }
            } catch (JsonSyntaxException e) {
                javafx.application.Platform.runLater(() -> showErrorMessage(sellError, "Error: Invalid response format from server."));
            }
        }).start();
    }

    private String sendToServer(String message) {
        try {
            System.out.println("Connecting to server..."); // Debug Print
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT); // Debug Print

            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // Read server connection message
            String serverMessage = (String) input.readObject();
            System.out.println("Message from server: " + serverMessage); // Debug Print

            // Send request
            System.out.println("Sending request to server: " + message); // Debug Print
            output.writeObject(message);
            output.flush();

            // Read response
            String response = (String) input.readObject();
            System.out.println("Response from server: " + response); // Debug Print

            // Close resources
            output.close();
            input.close();
            socket.close();
            return response;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage()); // Debug Print
        }
        return null;
    }
    private void handleBuy(Map<String, Object> ticketData) {
        // 1. Extract relevant fields from the map
        String eventName = ticketData.get("eventName").toString();
        String gate = ticketData.get("gate").toString();
        String ticketId = (String) ticketData.get("ticketId");
        String date = ticketData.get("date").toString();
        double price  = (double) ticketData.get("price");
        String sellerUsername = ticketData.get("username").toString();
        String buyerUsername = usernameField.getText();
        // 2. Build the JSON request
        JsonObject headers = new JsonObject();
        headers.addProperty("action", "ticket/delete");

        // Create a sub-object for the ticket
        JsonObject ticketObj = new JsonObject();
        ticketObj.addProperty("ticketId", ticketId);
        ticketObj.addProperty("eventName", eventName);
        ticketObj.addProperty("gate", gate);
        ticketObj.addProperty("date", date);
        ticketObj.addProperty("price", price);
        JsonObject body = new JsonObject();
        // Put the ticket object under "ticket"
        body.add("ticket", ticketObj);
        body.addProperty("buyerUsername", buyerUsername);
        body.addProperty("sellerUsername", sellerUsername);
        JsonObject request = new JsonObject();
        request.add("headers", headers);
        request.add("body", body);

        new Thread(() -> {
            String jsonResponse = sendToServer(request.toString());
            if (jsonResponse == null) {
                // Handle error (e.g., show a label or alert)
                javafx.application.Platform.runLater(() -> {
                    showErrorMessage(searchError, "Error deleting ticket from server.");
                });
                return;
            }


            Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);
            String status = (String) responseMap.get("status");
            String message = (String) responseMap.get("message");
            if ("OK".equalsIgnoreCase(status)) {
                // Ticket was deleted/bought successfully
                javafx.application.Platform.runLater(() -> {
                    showErrorMessage(searchError, "Ticket buy successfully: " + eventName);
                    ticketTable.getItems().remove(ticketData);
                });
                // Possibly refresh the table or remove this row from ticketTable
            } else {
                // Something went wrong
                javafx.application.Platform.runLater(() -> {
                    showErrorMessage(searchError, "Failed to delete ticket: " + message);
                });
            }
        }).start();
    }



    private void showErrorMessage(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    @FXML
    public void showDashboard() {
        hideAllSections();
        dashboardSection.setVisible(true);
    }

    @FXML
    public void showAdminPanel() {
        hideAllSections();
        adminPanel.setVisible(true);
    }

    @FXML
    public void showStartScreen() {
        hideAllSections();
        start.setVisible(true);
    }

    private void hideAllSections() {
        start.setVisible(false);
        loginSection.setVisible(false);
        signUpSection.setVisible(false);
        dashboardSection.setVisible(false);
        buySection.setVisible(false);
        sellSection.setVisible(false);
        adminPanel.setVisible(false);
    }
    @FXML
    public void showLoginSection() {
        hideAllSections();
        loginSection.setVisible(true);
    }
    @FXML
    public void showSignUpSection() {
        hideAllSections();
        loginError.setVisible(false);
        signUpSection.setVisible(true);
    }

    @FXML
    public void showBuySection() {
        hideAllSections();
        buySection.setVisible(true);
    }

    @FXML
    public void showSellSection() {
        hideAllSections();
        sellSection.setVisible(true);
    }

    @FXML
    public void handleLogout() {
        hideAllSections();
        showStartScreen();
        userGreeting.setText("");
        adminGreeting.setText("");
    }

    private void initializeUserTable() {
        // Set up cell value factories for each column based on map keys
        usernameColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String username = row.get("username") != null ? row.get("username").toString() : "";
            return new ReadOnlyStringWrapper(username);
        });

        fullNameColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String fullName = row.get("fullName") != null ? row.get("fullName").toString() : "";
            return new ReadOnlyStringWrapper(fullName);
        });

        emailColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String email = row.get("email") != null ? row.get("email").toString() : "";
            return new ReadOnlyStringWrapper(email);
        });

        phoneColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String phone = row.get("phone") != null ? row.get("phone").toString() : "";
            return new ReadOnlyStringWrapper(phone);
        });

        // For the admin column, convert boolean to "Yes"/"No"
        adminColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            boolean isAdmin = row.get("isAdmin") instanceof Boolean ? (Boolean) row.get("isAdmin") : false;
            return new ReadOnlyObjectWrapper<>(isAdmin);
        });
        adminColumn.setCellFactory(col -> new TableCell<Map<String, Object>, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Yes" : "No");
                }
            }
        });

        // Set up the Delete button column with a custom cell factory
        deleteColumn.setCellFactory(col -> new TableCell<Map<String, Object>, Void>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.getStyleClass().add("red-button");
                Tooltip tooltip = new Tooltip("Delete this user");
                deleteButton.setTooltip(tooltip);
                deleteButton.setOnAction(e -> {
                    Map<String, Object> rowData = getTableView().getItems().get(getIndex());
                    handleDeleteUser(rowData);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        // Initially clear and hide the table
        userTable.getItems().clear();
        userTable.setVisible(false);
    }


    private void initializeTicketTable() {
        // Let columns fill the table width if desired
        allTicketsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 1) Event Name
        TicketNameColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String eventName = row.get("eventName") != null ? row.get("eventName").toString() : "";
            return new ReadOnlyStringWrapper(eventName);
        });

        // 2) Date
        dateTicketColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String date = row.get("date") != null ? row.get("date").toString() : "";
            return new ReadOnlyStringWrapper(date);
        });

        // 3) Price (numeric)
        priceTicketColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            double price = 0.0;
            if (row.get("price") instanceof Number) {
                price = ((Number) row.get("price")).doubleValue();
            }
            return new ReadOnlyObjectWrapper<>(price);
        });

        // 4) Gate
        gateTicketColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String gate = row.get("gate") != null ? row.get("gate").toString() : "";
            return new ReadOnlyStringWrapper(gate);
        });

        // 5) Username
        usernameTicketColumn.setCellValueFactory(cellData -> {
            Map<String, Object> row = cellData.getValue();
            String username = row.get("username") != null ? row.get("username").toString() : "";
            return new ReadOnlyStringWrapper(username);
        });


        // Hide or clear the table initially
        allTicketsTable.setVisible(false);
        allTicketsTable.getItems().clear();
    }


    @FXML
    public void handleGetAllUsers() {
        JsonObject headers = new JsonObject();
        headers.addProperty("action", "user/getAll");


        JsonObject requestJson = new JsonObject();
        requestJson.add("headers", headers);

        new Thread(() -> {
            String jsonResponse = sendToServer(requestJson.toString());
            if (jsonResponse == null) {
                Platform.runLater(() -> showErrorMessage(searchError, "Error retrieving users from server."));
                return;
            }

            // Parse the server response into a Map
            Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);
            String status = (String) responseMap.get("status");
            if ("OK".equalsIgnoreCase(status)) {
                // Parse the data field as a List<Map<String, Object>>
                Type mapListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> userList = gson.fromJson(gson.toJson(responseMap.get("data")), mapListType);

                Platform.runLater(() -> {
                    searchError.setText("Found " + userList.size() + " users");
                    searchError.setVisible(true);
                    userTable.setVisible(true);
                    userTable.getItems().clear();
                    userTable.setItems(FXCollections.observableArrayList(userList));
                });
            } else {
                String message = (String) responseMap.get("message");
                Platform.runLater(() -> showErrorMessage(searchError, "Failed to retrieve users: " + message));
            }
            adminPanel.setVisible(false);
            usersSection.setVisible(true);
        }).start();
    }

    private void handleDeleteUser(Map<String, Object> userData) {
        String username = userData.get("username").toString(); // assuming username is the unique key

        JsonObject headers = new JsonObject();
        headers.addProperty("action", "user/delete");

        JsonObject body = new JsonObject();
        body.addProperty("username", username);

        JsonObject requestJson = new JsonObject();
        requestJson.add("headers", headers);
        requestJson.add("body", body);

        new Thread(() -> {
            String jsonResponse = sendToServer(requestJson.toString());
            if (jsonResponse == null) {
                Platform.runLater(() -> showErrorMessage(searchError, "Error deleting user from server."));
                return;
            }

            Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);
            String status = (String) responseMap.get("status");
            String message = (String) responseMap.get("message");
            if ("OK".equalsIgnoreCase(status)) {
                Platform.runLater(() -> {
                    showErrorMessage(searchError, "User deleted successfully: " + username);
                    userTable.getItems().remove(userData);
                });
            } else {
                Platform.runLater(() -> {
                    showErrorMessage(searchError, "Failed to delete user: " + message);
                });
            }
        }).start();
    }

    @FXML
    public void handleGetAllTickets() {
        // 1) Build JSON request
        JsonObject headers = new JsonObject();
        headers.addProperty("action", "ticket/getAll");

        JsonObject requestJson = new JsonObject();
        requestJson.add("headers", headers);

        // 2) Send in a separate thread
        new Thread(() -> {
            String jsonResponse = sendToServer(requestJson.toString());
            if (jsonResponse == null) {
                // Show an error if needed
                Platform.runLater(() -> showErrorMessage(searchError, "Error retrieving tickets from server."));
                return;
            }

            // 3) Parse the response
            Map<String, Object> responseMap = gson.fromJson(jsonResponse, Map.class);
            String status = (String) responseMap.get("status");
            String message = (String) responseMap.get("message");
            if ("OK".equalsIgnoreCase(status)) {
                // Convert the data field to a list of maps
                Type mapListType = new TypeToken<List<Map<String, Object>>>(){}.getType();
                List<Map<String, Object>> ticketList = gson.fromJson(gson.toJson(responseMap.get("data")), mapListType);

                // 4) Update the table on the JavaFX thread
                Platform.runLater(() -> {
                    showErrorMessage(searchError, "Found " + ticketList.size() + " tickets");
                    allTicketsTable.setVisible(true);
                    allTicketsTable.getItems().clear();
                    allTicketsTable.setItems(FXCollections.observableArrayList(ticketList));
                });
            } else {
                // Error from the server
                Platform.runLater(() -> {
                    showErrorMessage(searchError, "Failed to retrieve tickets: " + message);
                });

            }
            adminPanel.setVisible(false);
            tickets.setVisible(true);
        }).start();
    }


}