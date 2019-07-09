package flatmarket;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class FlatMarket extends Application {

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.##");

    public static void main(String[] args) {
        launch(args);
    }

    private static HashMap<String, Integer> readColumns(XSSFSheet sheet) {
        HashMap<String, Integer> columns = new HashMap<>();
        for (Cell cell : sheet.getRow(0)) {
            columns.put(cell.getStringCellValue(), cell.getColumnIndex());
        }
        return columns;
    }

    public static class Table implements Closeable {
        private final XSSFSheet sheet;
        private final HashMap<String, Integer> columns;
        private final XSSFWorkbook workbook;
        private final Date creationTime;

        public Table(File file) {
            String filename = file.getPath();
            creationTime = CopyOffers.getCreationTime(file);
            try {
                this.workbook = new XSSFWorkbook(OPCPackage.open(filename, PackageAccess.READ));
                this.sheet = this.workbook.getSheetAt(0);
                this.columns = readColumns(this.sheet);
            } catch (IOException | InvalidFormatException | RuntimeException e) {
                throw new RuntimeException("Failed to read file " + filename, e);
            }
        }

        public void forEach(Consumer<Row> consumer) {
            for (int i = 1; i <= this.sheet.getLastRowNum(); i++) {
                consumer.accept(new Row(this.sheet.getRow(i)));
            }
        }

        @Override
        public void close() {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close file", e);
            }
        }

        public class Row {
            private XSSFRow row;
            private Date latest;

            public Row(XSSFRow row) {
                this.row = row;
            }

            public String getID() {
                return get("ID");
            }


            public Number getPrice() {
                return getNumber("Цена");
            }

            public Number getArea() {
                return getNumber("Площадь, м2");
            }

            private Number getNumber(String column) {
                try {
                    String value = get(column);
                    if (value.isEmpty()) {
                        return null;
                    }
                    return NUMBER_FORMAT.parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            private String get(String column) {
                return row.getCell(columns.get(column)).getStringCellValue();
            }

            public String getTooltip() {
                StringBuilder tooltip = new StringBuilder("<html>");
                for (String column : columns.keySet()) {
                    if (!column.equals("Описание")) {
                        tooltip.append("<b>").append(column).append("</b>&nbsp;");
                        if (column.equals("Ссылка на объявление")) {
                            String link = get(column);
                            tooltip.append("<a href=\"" + link + "\">" + link + "</a>");
                        } else {
                            tooltip.append(get(column));
                        }
                        tooltip.append("<br>");
                    }
                }
                tooltip.append("</html>");
                return tooltip.toString();
            }

            public boolean after(Row row) {
                return getCreationTime().after(row.getCreationTime());
            }

            private Date getCreationTime() {
                return creationTime;
            }

            public boolean isLatest() {
                return latest == creationTime;
            }

            public void setLatest(Date latest) {
                this.latest = latest;
            }
        }
    }

    //    ID
//    Количество комнат
//    Тип
//            Метро
//    Адрес
//    Площадь, м2
//    Дом
//            Парковка
//    Цена
//            Телефоны
//    Описание
//            Ремонт
//    Площадь комнат, м2
//    Балкон
//            Окна
//    Санузел
//    Есть телефон
//    Название ЖК
//    Серия дома
//    Высота потолков, м
//    Лифт
//            Мусоропровод
//    Ссылка на объявление

    @Override
    public void start(Stage primaryStage) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setData(getChartData());
        primaryStage.setTitle("Flat Market");

        StackPane root = new StackPane();
        root.getChildren().add(scatterChart);
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();

        for (XYChart.Series<Number, Number> s : scatterChart.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                StackPane node = (StackPane) d.getNode();
                Table.Row row = (Table.Row) d.getExtraValue();
                String remont = row.get("Ремонт");
                BackgroundFill fill = node.getBackground().getFills().get(0);
                Color color = Color.WHITE;
                if (row.getID().equals("203393927")) {
                    color = Color.RED;
                } else switch (remont) {
                    case "":
                        color = Color.GRAY;
                        break;
                    case "Без ремонта":
                        color = Color.YELLOW;
                        break;
                    case "Косметический":
                        color = Color.ORANGE;
                        break;
                    case "Евроремонт":
                        color = Color.GREEN;
                        break;
                    case "Дизайнерский":
                        color = Color.BLUE;
                        break;
                    default:
                        System.out.println(remont);
                }
                if (!row.isLatest()) {
                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0.2);
                } else {
//                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);
                }
                node.setBackground(new Background(new BackgroundFill(color, fill.getRadii(), fill.getInsets())));
                d.getNode().addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
                    Alert alert = new Alert(Alert.AlertType.NONE);
                    alert.getButtonTypes().add(ButtonType.CLOSE);
                    WebView webView = new WebView();
                    webView.getEngine().loadContent(row.getTooltip());
                    webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                        @Override
                        public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                            if (newValue == Worker.State.SUCCEEDED) {
                                NodeList nodeList = webView.getEngine().getDocument().getElementsByTagName("a");
                                for (int i = 0; i < nodeList.getLength(); i++) {
                                    Node n = nodeList.item(i);
                                    EventTarget eventTarget = (EventTarget) n;
                                    eventTarget.addEventListener("click", evt -> {
                                        EventTarget target = evt.getCurrentTarget();
                                        HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                                        String href = anchorElement.getHref();
                                        //handle opening URL outside JavaFX WebView
                                        try {
                                            new ProcessBuilder("chromium", href).start();
//                                            Desktop.getDesktop().browse(URI.create(href));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        System.out.println(href);
                                        evt.preventDefault();
                                    }, false);
                                }
                            }
                        }
                    });

                    alert.getDialogPane().setContent(webView);
                    alert.show();
                });
            }
        }

    }

    private ObservableList<XYChart.Series<Number, Number>> getChartData() {
        ObservableList<XYChart.Series<Number, Number>> data = FXCollections.observableArrayList();
        HashMap<String, XYChart.Series<Number, Number>> categories = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            categories.computeIfAbsent("" + i, r -> {
                XYChart.Series<Number, Number> s = new XYChart.Series<>();
                s.setName(r);
                data.add(s);
                return s;
            });
        }

        Date latest = null;
        HashMap<String, Table.Row> records = new HashMap<>();

        File dir = new File("offers");
        for (File file : dir.listFiles()) {
            try (Table table = new Table(file)) {
                table.forEach(row -> {
                    records.merge(row.getID(), row, (row1, row2) -> row1.after(row2) ? row1 : row2);
                });

                if (latest == null || latest.before(table.creationTime)) {
                    latest = table.creationTime;
                    System.out.println(latest);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        for (Table.Row row : records.values()) {
            row.setLatest(latest);
            String rooms = "" + row.getNumber("Количество комнат");
            XYChart.Series<Number, Number> series = categories.computeIfAbsent(rooms, r -> {
                XYChart.Series<Number, Number> s = new XYChart.Series<>();
                s.setName(r);
                data.add(s);
                return s;
            });
            XYChart.Data<Number, Number> d = new XYChart.Data<>(row.getArea().doubleValue(), row.getPrice().doubleValue());
            d.setExtraValue(row);
            series.getData().add(d);
        }
        return data;
    }
}
