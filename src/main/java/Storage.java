import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Storage
 *
 * CS2103T AY19/20 Semester 2
 * Individual project
 * Duke project
 *
 * 30 Jan 2020
 *
 * @author Jel
 */
public class Storage {
    protected String filePath;
    protected List<Task> tasks;

    Storage(String filePath) {
        this.tasks = new ArrayList<>();
        this.filePath = filePath;
    }

    protected void saveTask(Task task, boolean isAppendMode) throws IOException {
        FileOutputStream ops = new FileOutputStream(new File(this.filePath), isAppendMode);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops));
        String[] toSave = new String[4];
        toSave[1] = task.isDone ? "1" : "0";
        toSave[2] = task.getDescription();

        if (task instanceof Event) {
            toSave[0] = "E";
            toSave[3] = ((Event) task).getScheduledTime().toString();
        } else if (task instanceof Deadline) {
            toSave[0] = "D";
            toSave[3] = ((Deadline) task).getDueDate().toString();
        } else {
            toSave[0] = "T";
        }

        if (isAppendMode) {
            bw.newLine();
        }
        bw.write(String.join(" | ", toSave));
        bw.close();
    }

    protected void loadTasks() throws IOException {
        FileInputStream ips = new FileInputStream(new File(this.filePath));
        BufferedReader br = new BufferedReader(new InputStreamReader(ips));
        String line;
        while ((line = br.readLine()) != null) {
            String[] arr = line.split(" \\| ");
            String details = arr[2];
            Task getFromDisk;

            switch(arr[0]) {
            case "T":
                getFromDisk = new Todo(details);
                break;
            case "D":
                getFromDisk = new Deadline(details, LocalDate.parse(arr[3]));
                break;
            default:
                getFromDisk = new Event(details, LocalDate.parse(arr[3]));
                break;
            }

            if (arr[1].equals("1")) {
                getFromDisk.markAsDone();
            }
            this.tasks.add(getFromDisk);
        }
        br.close();
    }

    protected void clearAllData() throws IOException {
        FileOutputStream ops = new FileOutputStream(new File(this.filePath));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops));
        bw.close();
    }

    protected void updateData() throws IOException {
        for (int i = 0; i < this.tasks.size(); i++) {
            if (i != 0) {
                saveTask(this.tasks.get(i), true);
            } else {
                saveTask(this.tasks.get(i), false);
            }
        }
    }
}