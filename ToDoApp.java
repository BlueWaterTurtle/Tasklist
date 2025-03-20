import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ToDoApp extends JFrame {
    private static final String TASKS_FILE = "tasks.json";
    private final DefaultListModel<String> taskListModel;
    private final JList<String> taskList;
    private final List<Task> tasks;
    private final DateTimeFormatter dateFormatter;

    public ToDoApp() {
        tasks = new ArrayList<>();
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);

        setTitle("To-Do List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        loadTasks();

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField descriptionField = new JTextField();
        JFormattedTextField dueDateField = new JFormattedTextField(dateFormatter);
        JTextField priorityField = new JTextField();
        JCheckBox reminderCheckBox = new JCheckBox("Set Reminder");
        JFormattedTextField reminderField = new JFormattedTextField(dateFormatter);
        reminderField.setEnabled(false);

        reminderCheckBox.addItemListener(e -> reminderField.setEnabled(reminderCheckBox.isSelected()));

        inputPanel.add(new JLabel("Task Description:"));
        inputPanel.add(descriptionField);
        inputPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
        inputPanel.add(dueDateField);
        inputPanel.add(new JLabel("Priority (High, Medium, Low):"));
        inputPanel.add(priorityField);
        inputPanel.add(reminderCheckBox);
        inputPanel.add(reminderField);

        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(e -> {
            String description = descriptionField.getText();
            String dueDate = dueDateField.getText();
            String priority = priorityField.getText();
            String reminder = reminderCheckBox.isSelected() ? reminderField.getText() : null;

            if (!description.isEmpty() && !dueDate.isEmpty() && !priority.isEmpty()) {
                addTask(new Task(description, dueDate, priority, reminder));
                descriptionField.setText("");
                dueDateField.setText("");
                priorityField.setText("");
                reminderCheckBox.setSelected(false);
                reminderField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter description, due date, and priority.", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton editTaskButton = new JButton("Edit Task");
        editTaskButton.addActionListener(e -> editTask());

        JButton deleteTaskButton = new JButton("Delete Task");
        deleteTaskButton.addActionListener(e -> deleteTask());

        JButton clearCompletedButton = new JButton("Clear Completed Tasks");
        clearCompletedButton.addActionListener(e -> clearCompletedTasks());

        JButton exportTasksButton = new JButton("Export Tasks to CSV");
        exportTasksButton.addActionListener(e -> exportTasksToCSV());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        buttonPanel.add(addTaskButton);
        buttonPanel.add(editTaskButton);
        buttonPanel.add(deleteTaskButton);
        buttonPanel.add(clearCompletedButton);
        buttonPanel.add(exportTasksButton);

        add(new JScrollPane(taskList), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadTasks() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            tasks.addAll(mapper.readValue(new File(TASKS_FILE), new TypeReference<List<Task>>() {}));
            refreshTaskList();
        } catch (IOException e) {
            System.err.println("Could not load tasks: " + e.getMessage());
        }
    }

    private void saveTasks() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File(TASKS_FILE), tasks);
        } catch (IOException e) {
            System.err.println("Could not save tasks: " + e.getMessage());
        }
    }

    private void refreshTaskList() {
        taskListModel.clear();
        for (Task task : tasks) {
            String status = task.isCompleted() ? "Completed" : "Pending";
            String taskString = String.format("%s - Due: %s - Priority: %s - Reminder: %s - Status: %s",
                    task.getDescription(), task.getDueDate(), task.getPriority(), task.getReminder(), status);
            taskListModel.addElement(taskString);
        }
    }

    private void addTask(Task task) {
        tasks.add(task);
        saveTasks();
        refreshTaskList();
    }

    private void editTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task selectedTask = tasks.get(selectedIndex);
            JTextField descriptionField = new JTextField(selectedTask.getDescription());
            JFormattedTextField dueDateField = new JFormattedTextField(dateFormatter);
            dueDateField.setText(selectedTask.getDueDate());
            JTextField priorityField = new JTextField(selectedTask.getPriority());
            JCheckBox reminderCheckBox = new JCheckBox("Set Reminder");
            JFormattedTextField reminderField = new JFormattedTextField(dateFormatter);
            reminderField.setText(selectedTask.getReminder());
            reminderField.setEnabled(selectedTask.getReminder() != null);

            reminderCheckBox.setSelected(selectedTask.getReminder() != null);
            reminderCheckBox.addItemListener(e -> reminderField.setEnabled(reminderCheckBox.isSelected()));

            JPanel editPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            editPanel.add(new JLabel("Task Description:"));
            editPanel.add(descriptionField);
            editPanel.add(new JLabel("Due Date (YYYY-MM-DD):"));
            editPanel.add(dueDateField);
            editPanel.add(new JLabel("Priority (High, Medium, Low):"));
            editPanel.add(priorityField);
            editPanel.add(reminderCheckBox);
            editPanel.add(reminderField);

            int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                selectedTask.setDescription(descriptionField.getText());
                selectedTask.setDueDate(dueDateField.getText());
                selectedTask.setPriority(priorityField.getText());
                selectedTask.setReminder(reminderCheckBox.isSelected() ? reminderField.getText() : null);
                saveTasks();
                refreshTaskList();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            tasks.remove(selectedIndex);
            saveTasks();
            refreshTaskList();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearCompletedTasks() {
        tasks.removeIf(Task::isCompleted);
        saveTasks();
        refreshTaskList();
    }

    private void exportTasksToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Tasks as CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                writer.println("Description,Due Date,Priority,Reminder,Completed");
                for (Task task : tasks) {
                    writer.printf("%s,%s,%s,%s,%b%n", task.getDescription(), task.getDueDate(), task.getPriority(), task.getReminder(), task.isCompleted());
                }
                JOptionPane.showMessageDialog(this, "Tasks exported to " + fileToSave.getAbsolutePath(), "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Could not export tasks: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoApp::new);
    }
}
