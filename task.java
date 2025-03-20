public class Task {
    private String description;
    private String dueDate;
    private boolean completed;
    private String priority;
    private String reminder;

    public Task() {
    }

    public Task(String description, String dueDate, String priority, String reminder) {
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.reminder = reminder;
        this.completed = false;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }
}
