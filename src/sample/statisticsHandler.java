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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class statisticsHandler extends VBox {

    private static statisticsHandler instance = null;   // Singleton that will be returned on getInstance()
    private LineChart<String, Number> chart;            // Chart that shows statistics
    private Method updateMethod;                        // References current method associated with selected radio button (day, week, etc.)
    private DatePicker dateFrom;
    private DatePicker dateTo;
    private String currentProduct = "All";
    private ArrayList<Product> allProductSales = new ArrayList<>();
    private ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();  // Data for chart

    private statisticsHandler() {

        fetchData();

        // Temp margin
        HBox margin = new HBox();
        margin.setPrefHeight(100);
        margin.setMinHeight(100);

        // HBox top
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.setMinHeight(75);

        Label productLabel = new Label("Product");
        ComboBox<String> chooseProduct = new ComboBox<>(getProductListFromDB());
        chooseProduct.setValue("All");
        chooseProduct.setVisibleRowCount(10);
        chooseProduct.setOnAction(event -> {
            currentProduct = chooseProduct.getValue();
            updateChart();
        });

        Label dateFromLabel = new Label("From");
        dateFrom = new DatePicker();
        Label dateToLabel = new Label("To");
        dateTo = new DatePicker();
        dateFrom.setValue(LocalDate.now().minusDays(7));
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

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        chart = new LineChart<>(xAxis, yAxis);
        chart.setPrefWidth(2000);

        mid.getChildren().addAll(chart);
        chart.setData(chartData);
        chart.setAnimated(false);                // Animated changes disabled because it is bugged


        // HBox bottom
        HBox bot = new HBox(10);
        bot.setAlignment(Pos.CENTER);
        bot.setMinHeight(75);

        ToggleGroup radioButtonGroup = new ToggleGroup();
        RadioButton rbDay = new RadioButton("Day");
        RadioButton rbWeek = new RadioButton("Week");
        RadioButton rbMonth = new RadioButton("Month");
        RadioButton rbYear = new RadioButton("Year");
        rbDay.setToggleGroup(radioButtonGroup);
        rbWeek.setToggleGroup(radioButtonGroup);
        rbMonth.setToggleGroup(radioButtonGroup);
        rbYear.setToggleGroup(radioButtonGroup);

        // When a radio button is pressed, it changes the method for chart updates
        radioButtonGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {

            String text = newValue.toString();
            String selectedButtonText = "xAxis_" + text.substring(text.indexOf("'") + 1, text.length() - 1);

            try {
                updateMethod = this.getClass().getDeclaredMethod(selectedButtonText);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            updateChart();

        });

        rbDay.setSelected(true);

        bot.getChildren().addAll(rbDay, rbWeek, rbMonth, rbYear);

        this.getChildren().addAll(margin, top, mid, bot);

    }

    public static statisticsHandler getInstance() {

        if (instance == null) {
            instance = new statisticsHandler();
        }

        return instance;
    }

    private void updateChart() {

        chartData.clear();   // Clear existing data from the observable list

        try {
            updateMethod.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void xAxis_Day() {

        XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

        LocalDate tempDate = dateFrom.getValue();

        do {
            // Convert LocalDate to Date
            Date date = Date.from(tempDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            // Determine the day of the week for display on x-axis
            String dayName = new SimpleDateFormat("EEEE").format(date) + " - " + tempDate.getDayOfMonth() + "/" + tempDate.getMonthValue();
            // Add data to the series
            series.getData().add(new XYChart.Data<>(dayName, getSalesOnDay(tempDate)));

            tempDate = tempDate.plusDays(1);

        } while (tempDate.compareTo(dateTo.getValue()) <= 0);  // Until the whole chosen period is covered

        chartData.add(series); // Add the series to the observable list
    }

    private void xAxis_Week() {

        XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

        LocalDate tempDate = dateFrom.getValue();
        LocalDate lastDate = dateTo.getValue();
        int currentWeekNumber;
        String weekName;

        do {
            currentWeekNumber = getWeekNumber(tempDate);
            weekName = "Week " + getWeekNumber(tempDate) + " " + tempDate.getYear();
            series.getData().add(new XYChart.Data<>(weekName, getSalesOnWeek(tempDate)));

            do {
                tempDate = tempDate.plusDays(1);
            } while (getWeekNumber(tempDate) == currentWeekNumber);
        }
        while (tempDate.compareTo(lastDate) <= 0);

        chartData.add(series); // Add the series to the observable list
    }

    private void xAxis_Month() {

        XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

        LocalDate tempDate = dateFrom.getValue();
        LocalDate lastDate = dateTo.getValue();
        Month currentMonth;
        String monthName;

        do {
            currentMonth = tempDate.getMonth();
            monthName = currentMonth + " " + tempDate.getYear();
            series.getData().add(new XYChart.Data<>(monthName, getSalesOnMonth(tempDate)));

            do {
                tempDate = tempDate.plusDays(1);
            } while (tempDate.getMonth() == currentMonth);
        }
        while (tempDate.compareTo(lastDate) <= 0);

        chartData.add(series); // Add the series to the observable list
    }

    private void xAxis_Year() {

        XYChart.Series<String, Number> series = new XYChart.Series<>();  // New series to be displayed

        int firstYear = dateFrom.getValue().getYear();
        int lastYear = dateTo.getValue().getYear();
        int tempYear = firstYear;

        do {
            series.getData().add(new XYChart.Data<>(String.valueOf(tempYear), getSalesOnYear(tempYear)));
            tempYear++;
        }
        while (tempYear <= lastYear);

        chartData.add(series); // Add the series to the observable list

    }

    private void showAlert(String text) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(text);

        alert.showAndWait();
    }

    private int getWeekNumber(LocalDate date) {
        return date.get(WeekFields.of(new Locale("US")).weekOfWeekBasedYear());
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

    private void fetchData() {

        DB.selectSQL("SELECT tblProductReceipt.fldQuantity, tblProduct.fldName, CAST(tblReceipt.fldDate AS DATE)\n" +
                "FROM ((tblProductReceipt\n" +
                "INNER JOIN tblProduct ON tblProductReceipt.fldProductId = tblProduct.fldProductId)\n" +
                "INNER JOIN tblReceipt ON tblProductReceipt.fldReceiptId = tblReceipt.fldReceiptId)");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String data;

        do {
            data = DB.getData();
            if (data.equals(DB.NOMOREDATA)) {
                break;
            } else {
                allProductSales.add(new Product(Integer.parseInt(data),DB.getData(),LocalDate.parse(DB.getData(),formatter)));
            }
        } while (true);

        Collections.sort(allProductSales);
    }

    private int getSalesOnDay(LocalDate date){

        int totalSales = 0;

        for (Product p : allProductSales){
            if(p.date.compareTo(date) == 0){

                if(currentProduct.equals("All")){
                    totalSales += p.quantity;
                }
                else if(currentProduct.equals(p.name)){
                    totalSales += p.quantity;
                }
            }
        }

        return totalSales;
    }

    private int getSalesOnWeek(LocalDate date){
        int totalSales = 0;

        for (Product p : allProductSales){
            if(p.date.getYear() == date.getYear() && getWeekNumber(p.date) == getWeekNumber(date)){

                if(currentProduct.equals("All")){
                    totalSales += p.quantity;
                }
                else if(currentProduct.equals(p.name)){
                    totalSales += p.quantity;
                }
            }
        }
        return totalSales;
    }

    private int getSalesOnMonth(LocalDate date){
        int totalSales = 0;

        for (Product p : allProductSales){
            if(p.date.getYear() == date.getYear() && p.date.getMonthValue() == date.getMonthValue()){

                if(currentProduct.equals("All")){
                    totalSales += p.quantity;
                }
                else if(currentProduct.equals(p.name)){
                    totalSales += p.quantity;
                }
            }
        }
        return totalSales;
    }

    private int getSalesOnYear(int year){
        int totalSales = 0;

        for (Product p : allProductSales){
            if(p.date.getYear() == year){

                if(currentProduct.equals("All")){
                    totalSales += p.quantity;
                }
                else if(currentProduct.equals(p.name)){
                    totalSales += p.quantity;
                }
            }
        }
        return totalSales;
    }

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
    


}
