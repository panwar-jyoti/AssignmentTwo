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

        List<Interviews> interviews= ExcelReader.getInterviewsList();
        System.out.println(interviews.get(1));
        // Insert data into the database using parallel streams
//        for (Interviews interview : interviews) {
//            insertInterview(interview);
//        }

        generateCharts(interviews);
    }
    private static void insertInterview(Interviews interview) {
        String sql = "INSERT INTO interviews (IDate, Imonth,team,PanelName,round,skill,Itime,Clocation,Plocation,Cname) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, interview.getDate());
            statement.setDate(2, interview.getMonth());
            statement.setString(3,interview.getTeam());
            statement.setString(4,interview.getPanelName());
            statement.setString(5,interview.getRound());
            statement.setString(6,interview.getSkill());
            statement.setTime(7,interview.getTime());
            statement.setString(8, interview.getCurrentLoc());
            statement.setString(9, interview.getPreferredLoc());
            statement.setString(10, interview.getCandidateName());
            // Execute the insert statement
            statement.executeUpdate();
            System.out.println("Inserting");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void generateCharts(List<Interviews> interviewList) {
        String pdfPath = "charts.pdf";

        try (OutputStream os = new FileOutputStream(pdfPath);
             PdfWriter writer = new PdfWriter(os);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {


            JFreeChart chart1 = maxInterviewsQuery();
            System.out.println(chart1);
            BufferedImage image = chart1.createBufferedImage(700, 500);
            Image itextImage = new Image(ImageDataFactory.create(image, null));

            document.add(itextImage);

            JFreeChart chart2 = minInterviewsQuery();
            BufferedImage image2 = chart2.createBufferedImage(700, 500);
            Image itextImage2 = new Image(ImageDataFactory.create(image2, null));

            document.add(itextImage2);

            JFreeChart chart5 = getTop3killsForPeakTime();
            BufferedImage image5 = chart5.createBufferedImage(700, 500);
            Image itextImage5 = new Image(ImageDataFactory.create(image5, null));

            document.add(itextImage5);

            JFreeChart chart4 = getTop3kills();
            BufferedImage image4 = chart4.createBufferedImage(700, 500);
            Image itextImage4 = new Image(ImageDataFactory.create(image4, null));

            document.add(itextImage4);

            JFreeChart chart3 = getTop3Panels(interviewList);
            System.out.println(chart3);
            BufferedImage image3 = chart3.createBufferedImage(700, 500);
            Image itextImage3 = new Image(ImageDataFactory.create(image3, null));

            document.add(itextImage3);

            System.out.println("Report successfully saved at location : " + pdfPath);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static JFreeChart maxInterviewsQuery() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection connection = dataSource.getConnection();) {
            String query = "SELECT teamName, COUNT(*) as interviewCount FROM interviews WHERE month IN ('Oct-23', 'Nov-23') GROUP BY teamName ORDER BY COUNT(*) DESC LIMIT 1";
            try (PreparedStatement statement = connection.prepareStatement(query); ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    String category = set.getString("teamName");
                    int value = set.getInt("interviewCount");
                    dataset.addValue(value, "Records", category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Team with maximum Interviews in Oct'23 and Nov'23",
                "Team",
                "Interviews Count",
                dataset
        );

        return chart;
    }

    public static JFreeChart minInterviewsQuery() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try(Connection connection = dataSource.getConnection();) {
            String query = "SELECT teamName, COUNT(*) as interviewCount FROM interviews WHERE month IN ('Oct-23', 'Nov-23') GROUP BY teamName ORDER BY COUNT(*) LIMIT 1";
            try(PreparedStatement statement = connection.prepareStatement(query); ResultSet set = statement.executeQuery()) {
                while(set.next()) {
                    String category = set.getString("teamName");
                    int value = set.getInt("interviewCount");
                    dataset.addValue(value, "Records", category);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Team with minimum Interviews in Oct'23 and Nov'23",
                "Team",
                "Interviews Count",
                dataset
        );
        return chart;
    }

    public static JFreeChart getTop3Panels(List<Interviews> interviewList) {
        Map<String, Integer> panelsTointerviewcounts = interviewList.stream().filter(rec -> rec.getMonth() != null && rec.getMonth().equals("Oct-23") || rec.getMonth() != null && rec.getMonth().equals("Nov-23")).collect(Collectors.groupingBy(record -> record.getPanelName(), Collectors.summingInt(r -> 1)));

        List<Map.Entry<String,Integer>> top3Panels= panelsTointerviewcounts.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed()).limit(3).collect(Collectors.toList());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        System.out.println(dataset);

        top3Panels.forEach(entry -> dataset.addValue(entry.getValue(), "Interviews", entry.getKey()));

        return ChartFactory.createBarChart("Top 3 panels in October and November 2023", "Panel", "Interview Count", dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    public static JFreeChart getTop3kills() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try(Connection connection = dataSource.getConnection();) {
            String viewCreationQuery = "CREATE VIEW my_view AS SELECT skill, month, count(*) as skillCount FROM interviews GROUP BY skill, month";
            String selectQuery = "SELECT skill, COUNT(*) AS skillCount FROM my_view WHERE month IN ('Oct-23', 'Nov-23') GROUP BY skill ORDER BY skillCount DESC LIMIT 3";
            try(Statement statement = connection.createStatement()) {
                statement.executeUpdate(viewCreationQuery);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (PreparedStatement statement = connection.prepareStatement(selectQuery); ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    String category = set.getString("skill");
                    int value = set.getInt("skillCount");
                    dataset.addValue(value, "Records", category);
                }
            }

            return ChartFactory.createBarChart("Top 3 skills in the months October and November", "Skill", "Skill Count", dataset, PlotOrientation.VERTICAL, true, true, false);

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JFreeChart getTop3killsForPeakTime() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try(Connection connection = dataSource.getConnection();) {
            String query = "SELECT skill, COUNT(*) AS skillCount FROM interviews WHERE month IN ('Oct-23', 'Nov-23') AND TIME(time) BETWEEN '17:00:00' AND '18:00:00' GROUP BY skill ORDER BY skillCount DESC LIMIT 3";
            try(PreparedStatement statement = connection.prepareStatement(query); ResultSet set = statement.executeQuery()) {
                while(set.next()) {
                    String category = set.getString("skill");
                    int value = set.getInt("skillCount");
                    dataset.addValue(value, "Records", category);
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Top 3 skills in Peak Time (5:00PM to 6:00PM)",
                "Skill",
                "Skill Count",
                dataset
        );
        return chart;
    }
}