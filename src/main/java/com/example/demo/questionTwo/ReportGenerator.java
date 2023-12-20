package com.example.demo.questionTwo;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/prac";
    private static final String USER = "root";
    private static final String PASSWORD = "acc0@user";

    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setUrl(JDBC_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASSWORD);
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    public static void main(String[] args) {

        List<Interviews> interviews = ExcelReader.getInterviewsList("C:\\Users\\jyoti1\\Downloads\\AccoliteInterviewData.xlsx");
        System.out.println(interviews.get(1));
        // Insert data into the database using parallel streams
//        insertInterviews(interviews);

        generateCharts(interviews);
    }

    public static void insertInterviews(List<Interviews> interviews) {
        interviews.parallelStream().forEach(ReportGenerator::insertInterview);
    }

    private static void insertInterview(Interviews interview) {
        String sql = "INSERT INTO interviews (Idate, Imonth,team,PanelName,round,skill,Itime,Clocation,Plocation,Cname) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            // Set parameters for the prepared statement
            setInterviewParameters(statement, interview);

            // Execute the query
            int rowsAffected = statement.executeUpdate();

            // Output result
            if (rowsAffected > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Insertion Failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void setInterviewParameters(PreparedStatement statement, Interviews interview) throws SQLException {
        statement.setDate(1, interview.getDate());
        statement.setDate(2, interview.getMonth());
        statement.setString(3, interview.getTeam());
        statement.setString(4, interview.getPanelName());
        statement.setString(5, interview.getRound());
        statement.setString(6, interview.getSkill());
        statement.setTime(7, interview.getTime());
        statement.setString(8, interview.getCurrentLoc());
        statement.setString(9, interview.getPreferredLoc());
        statement.setString(10, interview.getCandidateName());
    }

    private static DefaultCategoryDataset executeChartQuery(String sql, String chartTitle, String categoryLabel, String valueLabel) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                // Process result set and populate dataset
                String column1Value = resultSet.getString(1);
                int column2Value = resultSet.getInt(2);
                dataset.addValue(column2Value, valueLabel, column1Value);
                System.out.println(column1Value + ": " + column2Value);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return dataset;
    }

    private static void generateCharts(List<Interviews> interviews) {
        String pdfPath = "Report.pdf";

        try (OutputStream os = new FileOutputStream(pdfPath); PdfWriter writer = new PdfWriter(os); PdfDocument pdfDocument = new PdfDocument(writer); Document document = new Document(pdfDocument)) {

            addChartToDocument(maximumInterviews(), document);
            addChartToDocument(minimumInterviews(), document);
            addChartToDocument(findTopThreeSkills(), document);
            addChartToDocument(peakTimeTopThreeSkills(), document);
//            addChartToDocument(findTopThreePanels(interviews), document);

            System.out.println("PDF Path: " + pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addChartToDocument(JFreeChart chart, Document document) {
        try {
            BufferedImage image = chart.createBufferedImage(800, 500);
            Image itextImage = new Image(ImageDataFactory.create(image, null));
            document.add(itextImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JFreeChart maximumInterviews() {
        String sql = "SELECT team , COUNT(*) as count from interviews " + "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 " + "GROUP BY team " + "ORDER BY count DESC " + " Limit 1";
        return createBarChart(sql, "Team with Maximum Interviews in Oct'23 and Nov'23", "Team", "Total Number Of Interviews");
    }

    static public JFreeChart minimumInterviews() {
        String sql = "SELECT team , COUNT(*) as count from interviews " + "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 " + "GROUP BY team " + "ORDER BY count" + " Limit 1";
        return createBarChart(sql, "Team with Minimum Interviews in Oct'23 and Nov'23", "Team", "Total Number Of Interviews");
    }

    static public JFreeChart findTopThreeSkills() {
//        createTopSkillsView();
        String sql = "SELECT skill, skill_count FROM top_skills_view ORDER BY skill_count DESC LIMIT 3";
        return createBarChart(sql, "Top 3 skills in the months October and November", "Skill", "Skill Count");
    }

    static void createTopSkillsView() {
        String createViewSql = "CREATE VIEW IF NOT EXISTS top_skills_view AS " + "SELECT skill, COUNT(*) as skill_count " + "FROM interviews " + "WHERE MONTH(Imonth) IN (10, 11) AND YEAR(Imonth) = 2023 " + "GROUP BY skill";
        executeViewCreation(createViewSql);
    }

    static public JFreeChart peakTimeTopThreeSkills() {
//        createPeakTimeInterviewsView();
        String sql = "SELECT skill, skill_count FROM peak_time_interviews ORDER BY skill_count DESC LIMIT 3";
        return createBarChart(sql, "Top 3 skills in Peak Time BETWEEN (9 AND 17 )", "Skill", "Skill Count");
    }

    static void createPeakTimeInterviewsView() {
        String createViewSql = "CREATE VIEW peak_time_interviews AS " + "SELECT skill, COUNT(*) as skill_count " + "FROM interviews " + "WHERE EXTRACT(HOUR FROM Itime) BETWEEN 9 AND 17 " + "GROUP BY skill";
        executeViewCreation(createViewSql);
    }

    static public JFreeChart findTopThreePanels(List<Interviews> interviews) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
        Map<String, Integer> result = interviews.stream().filter(interview -> {
            dateFormat.format(interview.getMonth());
            return dateFormat.format(interview.getMonth()).equals("2023-10-01 00:00:00") || dateFormat.format(interview.getMonth()).equals("2023-11-01 00:00:00");
        }).collect(Collectors.groupingBy(Interviews::getPanelName, Collectors.summingInt(r -> 1)));

        List<Map.Entry<String, Integer>> top3Panels = result.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).limit(3).collect(Collectors.toList());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        top3Panels.forEach(entry -> dataset.addValue(entry.getValue(), "Interviews", entry.getKey()));

        return ChartFactory.createBarChart("Top 3 panels in October and November 2023", "Panel", "Interview Count", dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    private static JFreeChart createBarChart(String sql, String chartTitle, String categoryLabel, String valueLabel) {
        DefaultCategoryDataset dataset = executeChartQuery(sql, chartTitle, categoryLabel, valueLabel);
        return ChartFactory.createBarChart(chartTitle, categoryLabel, valueLabel, dataset);
    }

    private static void executeViewCreation(String createViewSql) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement createViewStatement = connection.prepareStatement(createViewSql)) {
            createViewStatement.execute();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}