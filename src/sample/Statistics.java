package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class Statistics extends VBox {

    private static Statistics instance = null;                // Singleton that will be returned on getInstance()
    private showDataStrategy strategy;                        // Strategy to update chart. Changes with user input
    private DatePicker dateFrom;
    private DatePicker dateTo;
    private String currentProduct;                            // Stores current chosen product
    private ArrayList<Product> allData;                       // All data from the database
    private ObservableList<XYChart.Series<String, Number>> filteredData = FXCollections.observableArrayList();  // Data for chart filtered by user choices

    //Constructor
    private Statistics() {

        strategy = new dayStrategy();    // Default update strategy set to days
        currentProduct = "All";          // Default show sum of all products
        fetchData();                     // Gets all data from productReceipt in database

        // Temp margin
        HBox margin = new HBox();
        margin.setPrefHeight(100);
        margin.setMinHeight(100);

        // HBox top - Holds date pickers and product drop down menu
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.setMinHeight(75);

        // Create and initialize product drop down menu
        Label productLabel = new Label("Product");
        ComboBox<String> chooseProduct = new ComboBox<>(getProductListFromDB());
        chooseProduct.setVisibleRowCount(10);
        chooseProduct.setValue("All");   // Shows all products (SUM) by default
        chooseProduct.setOnAction(event -> {
            // When a new product is chosen the value is stored and chart updates with the new info
            currentProduct = chooseProduct.getValue();
            updateChart();
        });

        // Create and initialize date pickers
        Label dateFromLabel = new Label("From");
        dateFrom = new DatePicker();
        dateFrom.setValue(LocalDate.now().minusDays(7));

        Label dateToLabel = new Label("To");
        dateTo = new DatePicker();
        dateTo.setValue(LocalDate.now());

        // Chart gets updated if user chooses a new date
        dateTo.valueProperty().addListener((observable, oldValue, newValue) -> {

            // Assert that "to date" is later than "from date"
            if (newValue.compareTo(dateFrom.getValue()) > 0) {
                updateChart();
            } else {
                dateTo.setValue(oldValue);
                showAlert("\"To\" must be later than \"from\"");
            }

        });
        dateFrom.valueProperty().addListener((observable, oldValue, newValue) -> {

            // Assert that "from date" is earlier than "to date"
            if (newValue.compareTo(dateTo.getValue()) < 0) {
                updateChart();
            } else {
                dateFrom.setValue(oldValue);
                showAlert("\"From\" must be earlier than \"to\"");
            }
        });

        top.getChildren().addAll(productLabel, chooseProduct, dateFromLabel, dateFrom, dateToLabel, dateTo);

        // HBox middle
        HBox mid = new HBox(10);
        mid.setAlignment(Pos.CENTER);
        mid.setPrefHeight(1000);
        mid.setPrefWidth(2000);

        // Creates chart to display data
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setPrefWidth(2000);
        chart.setData(filteredData);
        chart.setAnimated(false);                // Animated changes disabled because it is bugged
        mid.getChildren().addAll(chart);


        // HBox bottom
        HBox bot = new HBox(10);
        bot.setAlignment(Pos.CENTER);
        bot.setMinHeight(75);

        // Creating toggle group to choose day, week, month or year display on chart
        ToggleGroup radioButtonGroup = new ToggleGroup();
        RadioButton rbDay = new RadioButton("Day");
        RadioButton rbWeek = new RadioButton("Week");
        RadioButton rbMonth = new RadioButton("Month");
        RadioButton rbYear = new RadioButton("Year");
        rbDay.setToggleGroup(radioButtonGroup);
        rbWeek.setToggleGroup(radioButtonGroup);
        rbMonth.setToggleGroup(radioButtonGroup);
        rbYear.setToggleGroup(radioButtonGroup);

        // Change update strategy and update chart when a new radio button is selected
        rbDay.setOnAction(event -> {
            strategy = new dayStrategy();
            updateChart();
        });

        rbWeek.setOnAction(event -> {
            strategy = new weekStrategy();
            updateChart();
        });

        rbMonth.setOnAction(event -> {
            strategy = new monthStrategy();
            updateChart();
        });

        rbYear.setOnAction(event -> {
            strategy = new yearStrategy();
            updateChart();
        });

        // Day is selected as default
        rbDay.setSelected(true);

        // Adds radio buttons to the bottom HBox
        bot.getChildren().addAll(rbDay, rbWeek, rbMonth, rbYear);

        // Adds all HBoxes to the VBox
        this.getChildren().addAll(margin, top, mid, bot);

        updateChart();

    }

    // Return singleton instance
    public static Statistics getInstance() {

        if (instance == null) {
            instance = new Statistics();
        }

        return instance;
    }

    // When user changes settings this method will clear existing data and run update strategy
    private void updateChart() {
        filteredData.clear();   // Clear existing data from the observable list
        strategy.updateChart();
    }

    // Takes warning message as input and makes pop-up on screen
    private void showAlert(String text) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(text);

        alert.showAndWait();
    }

    // Gets list of all products from database
    private ObservableList<String> getProductListFromDB() {

        ObservableList<String> products = FXCollections.observableArrayList();
        products.add("All");

        DB.selectSQL("SELECT fldName FROM tblProduct");

        do {
            String data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                products.add(data);
            }
        } while (true);

        return products;
    }

    // Gets all relevant data from database and stores in observable list to be displayed in chart
    private void fetchData() {

        allData = new ArrayList<>();

        // Gets raw data - quantity, name, date and receipt ID
        DB.selectSQL("SELECT tblProductReceipt.fldQuantity, tblProduct.fldName, CAST(tblReceipt.fldDate AS DATE)\n" +
                "FROM ((tblProductReceipt\n" +
                "INNER JOIN tblProduct ON tblProductReceipt.fldProductId = tblProduct.fldProductId)\n" +
                "INNER JOIN tblReceipt ON tblProductReceipt.fldReceiptId = tblReceipt.fldReceiptId)");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String data;

        // Distributes data into objects and stores in array list
        do {
            data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                allData.add(new Product(Integer.parseInt(data), DB.getData(), LocalDate.parse(DB.getData(), formatter)));
            }
        } while (true);

        // Sorts the array list by date
        Collections.sort(allData);
    }

    // Method fetchData stores all data as products
    private class Product implements Comparable<Product> {

        private int quantity;
        private String name;
        private LocalDate date;

        public Product(int quantity, String name, LocalDate date) {
            this.quantity = quantity;
            this.name = name;
            this.date = date;
        }

        @Override
        public int compareTo(Product o) {
            return this.date.compareTo(o.date);
        }
    }

    // Strategy pattern interface. Makes it possible for program to update chart differently according to user input
    private interface showDataStrategy {
        void updateChart();
    }

    private class dayStrategy implements showDataStrategy {

        @Override
        public void updateChart() {

            XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

            LocalDate tempDate = dateFrom.getValue();

            do {
                // Convert LocalDate to Date
                Date date = Date.from(tempDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                // Determine the day of the week for display on x-axis
                String dayName = new SimpleDateFormat("EEEE").format(date) + " - " + tempDate.getDayOfMonth() + "/" + tempDate.getMonthValue();
                // Add data to the series
                series.getData().add(new XYChart.Data<>(dayName, getSales(tempDate)));

                tempDate = tempDate.plusDays(1);

            } while (tempDate.compareTo(dateTo.getValue()) <= 0);  // Until the whole chosen period is covered

            filteredData.add(series); // Add the series to the observable list
        }

        private int getSales(LocalDate date) {

            int totalSales = 0;

            for (Product p : allData) {
                if (p.date.compareTo(date) == 0) {

                    if (currentProduct.equals("All")) {
                        totalSales += p.quantity;
                    } else if (currentProduct.equals(p.name)) {
                        totalSales += p.quantity;
                    }
                }
            }

            return totalSales;
        }

    }

    private class weekStrategy implements showDataStrategy {

        @Override
        public void updateChart() {

            XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

            LocalDate tempDate = dateFrom.getValue();
            LocalDate lastDate = dateTo.getValue();
            int currentWeekNumber;
            String weekName;

            do {
                currentWeekNumber = getWeekNumber(tempDate);
                weekName = "Week " + getWeekNumber(tempDate) + " " + tempDate.getYear();
                series.getData().add(new XYChart.Data<>(weekName, getSales(tempDate)));

                do {
                    tempDate = tempDate.plusDays(1);
                } while (getWeekNumber(tempDate) == currentWeekNumber);
            }
            while (tempDate.compareTo(lastDate) <= 0);

            filteredData.add(series); // Add the series to the observable list
        }

        private int getSales(LocalDate date) {

            int totalSales = 0;

            for (Product p : allData) {
                if (p.date.getYear() == date.getYear() && getWeekNumber(p.date) == getWeekNumber(date)) {

                    if (currentProduct.equals("All")) {
                        totalSales += p.quantity;
                    } else if (currentProduct.equals(p.name)) {
                        totalSales += p.quantity;
                    }
                }
            }
            return totalSales;
        }

        // Returns week number from a date
        private int getWeekNumber(LocalDate date) {
            return date.get(WeekFields.of(new Locale("US")).weekOfWeekBasedYear());
        }

    }

    private class monthStrategy implements showDataStrategy {

        @Override
        public void updateChart() {
            XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

            LocalDate tempDate = dateFrom.getValue();
            LocalDate lastDate = dateTo.getValue();
            Month currentMonth;
            String monthName;

            do {
                currentMonth = tempDate.getMonth();
                monthName = currentMonth + " " + tempDate.getYear();
                series.getData().add(new XYChart.Data<>(monthName, getSales(tempDate)));

                do {
                    tempDate = tempDate.plusDays(1);
                } while (tempDate.getMonth() == currentMonth);
            }
            while (tempDate.compareTo(lastDate) <= 0);

            filteredData.add(series); // Add the series to the observable list
        }

        private int getSales(LocalDate date) {

            int totalSales = 0;

            for (Product p : allData) {
                if (p.date.getYear() == date.getYear() && p.date.getMonthValue() == date.getMonthValue()) {

                    if (currentProduct.equals("All")) {
                        totalSales += p.quantity;
                    } else if (currentProduct.equals(p.name)) {
                        totalSales += p.quantity;
                    }
                }
            }
            return totalSales;
        }
    }

    private class yearStrategy implements showDataStrategy {

        @Override
        public void updateChart() {
            XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

            int firstYear = dateFrom.getValue().getYear();
            int lastYear = dateTo.getValue().getYear();
            int tempYear = firstYear;

            do {
                series.getData().add(new XYChart.Data<>(String.valueOf(tempYear), getSales(LocalDate.of(tempYear, 1, 1))));
                tempYear++;
            }
            while (tempYear <= lastYear);

            filteredData.add(series); // Add the series to the observable list
        }

        private int getSales(LocalDate date) {

            int totalSales = 0;

            for (Product p : allData) {
                if (p.date.getYear() == date.getYear()) {

                    if (currentProduct.equals("All")) {
                        totalSales += p.quantity;
                    } else if (currentProduct.equals(p.name)) {
                        totalSales += p.quantity;
                    }
                }
            }
            return totalSales;
        }
    }


}
