import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

class StudentRecord implements Serializable {
    private String name, rollNumber, grade, department;

    public StudentRecord(String name, String roll, String grade, String dept) {
        this.name = name;
        this.rollNumber = roll;
        this.grade = grade;
        this.department = dept;
    }

    public String getName() { return name; }
    public String getRollNumber() { return rollNumber; }
    public String getGrade() { return grade; }
    public String getDepartment() { return department; }

    public void setName(String name) { this.name = name; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "Name: " + name + ", Roll No: " + rollNumber +
               ", Grade: " + grade + ", Dept: " + department;
    }
}

class StudentDataHandler {
    private final String FILE_NAME = "student_data.ser";
    private Map<String, StudentRecord> studentRecords;

    public StudentDataHandler() {
        studentRecords = new HashMap<>();
        loadRecords();
    }

    public void addRecord(StudentRecord record) {
        if (!studentRecords.containsKey(record.getRollNumber())) {
            studentRecords.put(record.getRollNumber(), record);
            saveRecords();
        }
    }

    public void deleteRecord(String roll) {
        if (studentRecords.remove(roll) != null) {
            saveRecords();
        }
    }

    public StudentRecord findRecord(String roll) {
        return studentRecords.get(roll);
    }

    public Collection<StudentRecord> getAllRecords() {
        return studentRecords.values();
    }

    private void saveRecords() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(studentRecords);
        } catch (IOException e) {
            System.err.println("Saving failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")  
    private void loadRecords() {
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
                studentRecords = (Map<String, StudentRecord>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Loading failed: " + e.getMessage());
            }
        }
    }
}

public class StudentApp extends JFrame {
    private JTextField nameField, rollField, gradeField, deptField, queryField;
    private JTextArea displayArea;
    private final StudentDataHandler dataHandler = new StudentDataHandler();

    public StudentApp() {
        setTitle("Student Records Manager");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeUI();
        setVisible(true);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 6, 6));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Student Details"));

        nameField = new JTextField();
        rollField = new JTextField();
        gradeField = new JTextField();
        deptField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Roll Number:"));
        inputPanel.add(rollField);
        inputPanel.add(new JLabel("Grade:"));
        inputPanel.add(gradeField);
        inputPanel.add(new JLabel("Department:"));
        inputPanel.add(deptField);

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(_ -> addStudent());  

        inputPanel.add(addBtn);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search/Delete"));

        queryField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        JButton deleteBtn = new JButton("Delete");
        JButton showAllBtn = new JButton("Show All");
        JButton exitBtn = new JButton("Exit");

        searchBtn.addActionListener(_ -> searchStudent());  
        deleteBtn.addActionListener(_ -> removeStudent());  
        showAllBtn.addActionListener(_ -> listAllStudents()); 
        exitBtn.addActionListener(_ -> System.exit(0)); 

        searchPanel.add(new JLabel("Roll No:"));
        searchPanel.add(queryField);
        searchPanel.add(searchBtn);
        searchPanel.add(deleteBtn);
        searchPanel.add(showAllBtn);
        searchPanel.add(exitBtn);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);
    }

    private void addStudent() {
        String name = nameField.getText().trim();
        String roll = rollField.getText().trim();
        String grade = gradeField.getText().trim();
        String dept = deptField.getText().trim();

        if (name.isEmpty() || roll.isEmpty() || grade.isEmpty() || dept.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StudentRecord newStudent = new StudentRecord(name, roll, grade, dept);
        dataHandler.addRecord(newStudent);
        displayArea.append("Student Added: " + newStudent + "\n");

        nameField.setText("");
        rollField.setText("");
        gradeField.setText("");
        deptField.setText("");
    }

    private void searchStudent() {
        String roll = queryField.getText().trim();
        StudentRecord student = dataHandler.findRecord(roll);

        if (student != null) {
            displayArea.append("Student Found: " + student + "\n");
        } else {
            displayArea.append("No student found with Roll No: " + roll + "\n");
        }
    }

    private void removeStudent() {
        String roll = queryField.getText().trim();
        dataHandler.deleteRecord(roll);
        displayArea.append("Attempted to remove Roll No: " + roll + "\n");
    }

    private void listAllStudents() {
        Collection<StudentRecord> all = dataHandler.getAllRecords();
        displayArea.append("---- All Student Records ----\n");
        for (StudentRecord s : all) {
            displayArea.append(s + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StudentApp::new);
    }
}
