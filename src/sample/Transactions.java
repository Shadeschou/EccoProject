package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Transactions extends VBox {

    private static Transactions instance = null;                                              // Singleton instance
    private DatePicker dateFrom;
    private DatePicker dateTo;
    private TableView transactionsTable;
    TableColumn<LocalDateTime, Transaction> dateTimeColumn;
    private ArrayList<Transaction> allData = new ArrayList<>();                               // Stores all transactions from database
    private ObservableList<Transaction> filteredData = FXCollections.observableArrayList();   // Stores transactions within date range

    // Constructor
    private Transactions() {
        //init background color
        this.setStyle("-fx-background-color: white");
        // Setup HBox for date pickers
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.setMinHeight(75);
        dateFrom = new DatePicker();
        dateFrom.setValue(LocalDate.now().minusDays(7));
        dateTo = new DatePicker();
        dateTo.setValue(LocalDate.now());
        Label dateFromLabel = new Label("From");
        Label dateToLabel = new Label("To");

        dateTo.valueProperty().addListener((observable, oldValue, newValue) -> {

            // Assert that "to date" is later than "from date"
            if (newValue.compareTo(dateFrom.getValue()) >= 0) {
                getDisplayData();
            } else {
                dateTo.setValue(oldValue);
                showAlert("\"To\" must be later than \"from\"");
            }

        });
        dateFrom.valueProperty().addListener((observable, oldValue, newValue) -> {

            // Assert that "from date" is earlier than "to date"
            if (newValue.compareTo(dateTo.getValue()) <= 0) {
                getDisplayData();
            } else {
                dateFrom.setValue(oldValue);
                showAlert("\"From\" must be earlier than \"to\"");
            }
        });
        top.getChildren().addAll(dateFromLabel, dateFrom, dateToLabel, dateTo);

        // Setup HBox for table view
        HBox mid = new HBox();
        mid.setAlignment(Pos.CENTER);
        transactionsTable = new TableView();

        TableColumn<String, Transaction> customerColumn = new TableColumn<>("Customer");
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        customerColumn.prefWidthProperty().bind(transactionsTable.widthProperty().divide(4));

        dateTimeColumn = new TableColumn<>("Date/Time");
        dateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        dateTimeColumn.setStyle("-fx-alignment: CENTER");
        dateTimeColumn.prefWidthProperty().bind(transactionsTable.widthProperty().divide(4));

        TableColumn<Double, Transaction> priceColumn = new TableColumn<>("Price/DKK");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setStyle("-fx-alignment: CENTER");
        priceColumn.prefWidthProperty().bind(transactionsTable.widthProperty().divide(4));

        TableColumn<Integer, Transaction> receiptIdColumn = new TableColumn<>("Receipt ID");
        receiptIdColumn.setCellValueFactory(new PropertyValueFactory<>("receiptId"));
        receiptIdColumn.setStyle("-fx-alignment: CENTER");
        receiptIdColumn.prefWidthProperty().bind(transactionsTable.widthProperty().divide(4));

        transactionsTable.setPrefWidth(2000);
        transactionsTable.setPrefHeight(1000);
        transactionsTable.setItems(filteredData);
        transactionsTable.getColumns().addAll(customerColumn, dateTimeColumn, priceColumn, receiptIdColumn);
        mid.getChildren().addAll(transactionsTable);

        this.getChildren().addAll(top, mid);

        fetchData();
        getDisplayData();
    }

    // Returns singleton instance
    public static Transactions getInstance() {
        if (instance == null) {
            instance = new Transactions();
        }
        return instance;
    }

    // Gets all transactions from database
    private void fetchData() {

        // Gets name, date, price and id of the receipt from database
        DB.selectSQL("SELECT tblUser.fldFullName,CONVERT(VARCHAR,tblReceipt.fldDate,120), tblReceipt.fldTotalPrice, tblReceipt.fldReceiptId\n" +
                "FROM tblReceipt\n" +
                "INNER JOIN tblUser ON tblReceipt.fldCustomerId = tblUser.fldUserId");

        // Used to format date string from database
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Inserts data into objects and stores them in array list
        String data;
        do {
            data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                allData.add(new Transaction(data, LocalDateTime.parse(DB.getData(),formatter), Double.parseDouble(DB.getData()), Integer.parseInt(DB.getData())));
            }
        } while (true);

    }

    // Gets all transactions within date picker range and displays on table view
    private void getDisplayData(){

        // Clears existing data
        filteredData.clear();

        // Finds all transactions within range of date pickers
        for (Transaction t : allData){
            if(t.dateTime.toLocalDate().compareTo(dateFrom.getValue()) >= 0 && t.dateTime.toLocalDate().compareTo(dateTo.getValue()) <= 0){
                filteredData.add(t);
            }
        }

        // Sorts table view ascending by date
        transactionsTable.getSortOrder().add(dateTimeColumn);
    }

    // Makes pop up on screen with input as warning message
    private void showAlert(String text) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(text);

        alert.showAndWait();
    }

    // Used to organise data from database
    public class Transaction {

        private String customer;
        private LocalDateTime dateTime;
        private double price;
        private int receiptId;

        public Transaction(String customer, LocalDateTime dateTime, double price, int receiptId) {
            this.customer = customer;
            this.dateTime = dateTime;
            this.price = price;
            this.receiptId = receiptId;
        }

        // Getters are used by table view to get data

        public String getCustomer() {
            return customer;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public double getPrice() {
            return price;
        }

        public int getReceiptId() {
            return receiptId;
        }

    }
}
