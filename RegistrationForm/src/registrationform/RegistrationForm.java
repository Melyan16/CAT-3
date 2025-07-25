import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class RegistrationForm {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Registration Form");
        frame.setSize(1000, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // ========== FORM UI ==========

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 80, 25);
        JTextField nameField = new JTextField();
        nameField.setBounds(100, 20, 200, 25);

        JLabel mobileLabel = new JLabel("Mobile:");
        mobileLabel.setBounds(20, 60, 80, 25);
        JTextField mobileField = new JTextField();
        mobileField.setBounds(100, 60, 200, 25);

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(20, 100, 80, 25);
        JRadioButton male = new JRadioButton("Male");
        JRadioButton female = new JRadioButton("Female");
        male.setBounds(100, 100, 70, 25);
        female.setBounds(180, 100, 70, 25);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);

        JLabel dobLabel = new JLabel("DOB:");
        dobLabel.setBounds(20, 140, 80, 25);
        JTextField dobField = new JTextField("yyyy-mm-dd");
        dobField.setBounds(100, 140, 200, 25);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(20, 180, 80, 25);
        JTextArea addressArea = new JTextArea();
        addressArea.setBounds(100, 180, 200, 60);

        JCheckBox terms = new JCheckBox("Accept Terms and Conditions");
        terms.setBounds(20, 250, 250, 25);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(100, 290, 100, 30);

        // ========== TABLE UI ==========

        JLabel tableLabel = new JLabel("Registered Students:");
        tableLabel.setBounds(350, 10, 200, 25);
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(350, 40, 600, 300);

        // ========== ADD COMPONENTS ==========

        frame.add(nameLabel); frame.add(nameField);
        frame.add(mobileLabel); frame.add(mobileField);
        frame.add(genderLabel); frame.add(male); frame.add(female);
        frame.add(dobLabel); frame.add(dobField);
        frame.add(addressLabel); frame.add(addressArea);
        frame.add(terms); frame.add(submitButton);
        frame.add(tableLabel); frame.add(scrollPane);

        frame.setVisible(true);

        // ========== FUNCTION TO LOAD DATA INTO TABLE ==========

        Runnable loadTable = () -> {
            try {
                Connection conn = DBConnection.connect();
                String query = "SELECT * FROM registration";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                // Table headers
                ResultSetMetaData rsmd = rs.getMetaData();
                Vector<String> columnNames = new Vector<>();
                int colCount = rsmd.getColumnCount();
                for (int i = 1; i <= colCount; i++) {
                    columnNames.add(rsmd.getColumnName(i));
                }

                // Table rows
                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= colCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    data.add(row);
                }

                table.setModel(new DefaultTableModel(data, columnNames));

                stmt.close(); conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        };

        loadTable.run(); // load when app starts

        // ========== SUBMIT BUTTON LOGIC ==========

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String mobile = mobileField.getText();
                String gender = male.isSelected() ? "Male" : (female.isSelected() ? "Female" : "");
                String dob = dobField.getText();
                String address = addressArea.getText();

                if (!terms.isSelected()) {
                    JOptionPane.showMessageDialog(frame, "Please accept the terms.");
                    return;
                }

                try {
                    Connection conn = DBConnection.connect();
                    String query = "INSERT INTO registration(name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, name);
                    stmt.setString(2, mobile);
                    stmt.setString(3, gender);
                    stmt.setString(4, dob);
                    stmt.setString(5, address);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(frame, "Registration Successful!");
                        loadTable.run(); // refresh table
                    }

                    stmt.close(); conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });
    }

    // Inner DBConnection class
    static class DBConnection {
        static final String DB_URL = "jdbc:mysql://localhost:3306/student_db";
        static final String USER = "root";
        static final String PASS = "";

        public static Connection connect() {
            Connection conn = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected to database.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return conn;
        }
    }
}