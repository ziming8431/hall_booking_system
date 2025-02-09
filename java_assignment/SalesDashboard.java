
package java_assignment;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Scanner;

public class SalesDashboard extends JFrame implements ActionListener {
    private JFrame previousFrame;
    private JComboBox<String> filterComboBox;
    private JTable salesTable;
    private JTable hallTypeTable;
    private DefaultTableModel salesTableModel;
    private DefaultTableModel hallTypeTableModel;
    private JButton backButton;
    private ChartPanel hallTypeChartPanel;
    private ChartPanel timeChartPanel;

    public SalesDashboard(JFrame previousFrame) {
        this.previousFrame = previousFrame;
        setupUI();
        loadSalesData("Weekly");
    }

    private void setupUI() {
        setTitle("Sales Dashboard");
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(220, 220, 220));
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterComboBox = new JComboBox<>(new String[]{"Weekly", "Monthly", "Yearly"});
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        filterComboBox.addActionListener(this);
        topPanel.add(filterLabel);
        topPanel.add(filterComboBox);

        String[] salesColumns = {"Period", "Total Sales"};
        salesTableModel = new DefaultTableModel(salesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        salesTable = new JTable(salesTableModel);
        salesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane salesScrollPane = new JScrollPane(salesTable);

        String[] hallTypeColumns = {"Hall Type", "Total Sales"};
        hallTypeTableModel = new DefaultTableModel(hallTypeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        hallTypeTable = new JTable(hallTypeTableModel);
        hallTypeTable.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane hallTypeScrollPane = new JScrollPane(hallTypeTable);

        backButton = new JButton("Back");
        backButton.addActionListener(this);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);

        hallTypeChartPanel = new ChartPanel(null);
        hallTypeChartPanel.setPreferredSize(new Dimension(600, 400));
        timeChartPanel = new ChartPanel(null);
        timeChartPanel.setPreferredSize(new Dimension(600, 400));

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        tablesPanel.add(salesScrollPane);
        tablesPanel.add(hallTypeScrollPane);

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        chartsPanel.add(hallTypeChartPanel);
        chartsPanel.add(timeChartPanel);

        mainPanel.add(tablesPanel, BorderLayout.CENTER);
        mainPanel.add(chartsPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadSalesData(String filter) {
        salesTableModel.setRowCount(0);
        hallTypeTableModel.setRowCount(0);
        Map<String, Double> hallTypeSales = new HashMap<>();
        Map<String, Double> timeSales = new TreeMap<>();

        try (Scanner scanner = new Scanner(new File("Bookings.txt"))) {
            while (scanner.hasNextLine()) {
                String[] booking = scanner.nextLine().split(",");
                LocalDate bookingDate = LocalDate.parse(booking[3]); // Start date of booking
                double totalSales = Double.parseDouble(booking[7]); // Total price
                String hallName = booking[0];
                String hallType = getHallType(hallName);

                hallTypeSales.put(hallType, hallTypeSales.getOrDefault(hallType, 0.0) + totalSales);

                String timeKey = getTimeKey(bookingDate, filter);
                timeSales.put(timeKey, timeSales.getOrDefault(timeKey, 0.0) + totalSales);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update hall type sales table
        for (Map.Entry<String, Double> entry : hallTypeSales.entrySet()) {
            hallTypeTableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue())});
        }

        // Update sales table and chart
        for (Map.Entry<String, Double> entry : timeSales.entrySet()) {
            salesTableModel.addRow(new Object[]{entry.getKey(), String.format("%.2f", entry.getValue())});
        }

        updateHallTypeChart(hallTypeSales);
        updateTimeChart(timeSales, filter);
    }

    private String getTimeKey(LocalDate date, String filter) {
        switch (filter) {
            case "Weekly":
                return date.with(java.time.DayOfWeek.MONDAY).format(DateTimeFormatter.ofPattern("yyyy-'W'ww"));
            case "Monthly":
                return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            case "Yearly":
                return date.format(DateTimeFormatter.ofPattern("yyyy"));
            default:
                throw new IllegalArgumentException("Invalid filter: " + filter);
        }
    }

    private String getHallType(String hallName) {
        try (Scanner scanner = new Scanner(new File("Hall.txt"))) {
            while (scanner.hasNextLine()) {
                String[] hallInfo = scanner.nextLine().split(",");
                if (hallInfo[0].equals(hallName)) {
                    return hallInfo[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    private void updateHallTypeChart(Map<String, Double> hallTypeSales) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : hallTypeSales.entrySet()) {
            dataset.addValue(entry.getValue(), "Sales", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Sales by Hall Type",
                "Hall Type",
                "Total Sales",
                dataset
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.1);

        Color[] colors = {
            new Color(255, 99, 71),   // Tomato
            new Color(30, 144, 255),  // Dodger Blue
            new Color(50, 205, 50),   // Lime Green
            new Color(255, 215, 0),   // Gold
            new Color(138, 43, 226)   // Blue Violet
        };

        int colorIndex = 0;
        for (int i = 0; i < dataset.getRowCount(); i++) {
            renderer.setSeriesPaint(i, colors[colorIndex % colors.length]);
            colorIndex++;
        }

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        hallTypeChartPanel.setChart(chart);
        hallTypeChartPanel.revalidate();
        hallTypeChartPanel.repaint();
    }

    private void updateTimeChart(Map<String, Double> timeSales, String filter) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, Double> entry : timeSales.entrySet()) {
            dataset.addValue(entry.getValue(), "Sales", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Sales Over Time",
                filter,
                "Total Sales",
                dataset
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.1);

        Color[] colors = {
            new Color(255, 99, 71),   // Tomato
            new Color(30, 144, 255),  // Dodger Blue
            new Color(50, 205, 50),   // Lime Green
            new Color(255, 215, 0),   // Gold
            new Color(138, 43, 226)   // Blue Violet
        };

        int colorIndex = 0;
        for (int i = 0; i < dataset.getRowCount(); i++) {
            renderer.setSeriesPaint(i, colors[colorIndex % colors.length]);
            colorIndex++;
        }

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        timeChartPanel.setChart(chart);
        timeChartPanel.revalidate();
        timeChartPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == filterComboBox) {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            loadSalesData(selectedFilter);
        } else if (e.getSource() == backButton) {
            dispose();
            previousFrame.setVisible(true);
        }
    }
}

