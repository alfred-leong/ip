package duke;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandHandler {
    //private static Task[] tasks = new Task[100];
    private static ArrayList<Task> tasks= new ArrayList<>();
    private static int taskCounter = 0;
    public static void drawLine() {    //print underscore symbol 50 times
        System.out.println("__________________________________________________");
    }
    public static void byeUser() {
        System.out.println("Bye. Hope to see you again!");
        drawLine();
    }
    public static void listTasks() {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < taskCounter; ++i) {
            char taskTypeCharacter = tasks.get(i).getType();
            System.out.println(i+1 + ".[" + taskTypeCharacter + "]" +
                    "[" + (tasks.get(i).isDone() ? "X] " : " ] ")
                    + tasks.get(i));
        }
        drawLine();
    }
    public static int getTaskID(String command) {
        command = command.trim();
        String[] words = command.split(" ");
        int taskID = Integer.parseInt(words[1]);
        return taskID;
    }
    public static void unmarkTask(int taskID) throws DukeException {
        if (tasks.get(taskID - 1).isDone() == false) {
            throw new DukeException(":( OOPS!!! Unable to unmark as this task has not been done yet");
        }
        tasks.get(taskID - 1).setDone(false);
        System.out.println("Okay, I've marked this task as not done yet:");
        char taskTypeCharacter = tasks.get(taskID - 1).getType();
        System.out.println("[" + taskTypeCharacter + "]"+ "[ ] " + tasks.get(taskID - 1));
        drawLine();
    }

    public static void markTask(int taskID) throws DukeException{
        if (tasks.get(taskID - 1).isDone() == true) {
            throw new DukeException(":( OOPS!!! Unable to mark as this task has already been done");
        }
        tasks.get(taskID - 1).setDone(true);
        System.out.println("Nice! I've marked this task as done:");
        char taskTypeCharacter = tasks.get(taskID - 1).getType();
        System.out.println("[" + taskTypeCharacter + "]" + "[X] " + tasks.get(taskID - 1));
        drawLine();
    }
    private static void deleteTask(int taskID) {
        System.out.println("Noted. I've removed this task:");
        char taskTypeCharacter = tasks.get(taskID - 1).getType();
        System.out.println("[" + taskTypeCharacter + "]"+ "[" + (tasks.get(taskID - 1).isDone() ? "X] " : " ] ") + tasks.get(taskID - 1));
        tasks.remove(taskID - 1);
        taskCounter--;
        System.out.println("Now you have " + taskCounter + " tasks in the list.");
        drawLine();
    }

    public static void createNewTask(String command) {
        command = command.trim();
        char taskType = command.toUpperCase().charAt(0);    //first char is the type in uppercase
        try {
            command = removeTaskType(command);   //get taskName from command
        } catch (DukeException e) {
            e.printErrorMessage();
            drawLine();
            return;
        }
        String taskDateTime, taskName;
        switch(taskType) {
        case 'T':
            createNewToDo(new ToDo(command, false, taskType));
            break;
        case 'D':
            try {
                taskDateTime = getTaskDateTime(command);
                taskName = getTaskName(command);
                createNewDeadline(new Deadline(taskName, false, taskType, taskDateTime));
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println(":( OOPS!!! Deadline tasks should be of the form 'deadline <task_name> /by <task_deadline>'");
                drawLine();
            }
            break;
        case 'E':
            try {
                taskDateTime = getTaskDateTime(command);
                taskName = getTaskName(command);
                createNewEvent(new Event(taskName, false, taskType, taskDateTime));
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println(":( OOPS!!! Event tasks should be of the form 'event <task_name> /at <task_time>'");
                drawLine();
            }
            break;
        default:
            drawLine();
        }
    }
    public static String removeTaskType(String command) throws DukeException{
        int indexOfFirstBlankSpace = command.indexOf(" ");
        String firstWord;
        if (indexOfFirstBlankSpace == -1) {
            firstWord = command;
        } else {
            firstWord = command.substring(0, indexOfFirstBlankSpace);
        }
        if (indexOfFirstBlankSpace == -1 && (firstWord.equals("todo") || firstWord.equals("deadline") || firstWord.equals("event"))) {
            throw new DukeException(":( OOPS!!! The description of a " + command + " cannot be empty.");
        } else if (!firstWord.equals("todo") && !firstWord.equals("deadline") && !firstWord.equals("event")) {
            throw new DukeException(":( OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
        command = command.substring(indexOfFirstBlankSpace + 1);
        return command;
    }
    private static String getTaskName(String command) {
        int indexOfBackslash = command.indexOf('/');
        return command.substring(0, indexOfBackslash-1);
    }
    private static String getTaskDateTime(String command) {
        int indexOfBackslash = command.indexOf('/');
        command = command.substring(indexOfBackslash + 1);
        int indexOfFirstBlankspace = command.indexOf(' ');
        return command.substring(indexOfFirstBlankspace + 1);
    }

    private static void createNewToDo(ToDo toDo) {
        tasks.add(toDo);
        taskCounter++;
        printAddedTask(toDo);
    }
    private static void createNewDeadline(Deadline deadline) {
        tasks.add(deadline);
        taskCounter++;
        printAddedTask(deadline);
    }
    private static void createNewEvent(Event event) {
        tasks.add(event);
        taskCounter++;
        printAddedTask(event);
    }

    private static void printAddedTask(Task task) {
        System.out.println("Got it. I've added this task:");
        System.out.println("  [" + task.getType() + "]" + "[ ] " + task);
        System.out.println("Now you have " + taskCounter + " tasks in your list.");
        drawLine();
    }

    public static void executeUserCommands() {
        Scanner in = new Scanner(System.in);
        String command;
        do {
            command = in.nextLine();
            drawLine();
            if (command.equals("bye")) {
                byeUser();
                break;
            }
            else if (command.equals("list")) {
                listTasks();
            }
            else if (command.contains("unmark")) {
                int taskID = getTaskID(command);
                try {
                    unmarkTask(taskID);
                } catch (DukeException e) {
                    e.printErrorMessage();
                    drawLine();
                } catch (NullPointerException e) {
                    System.out.println(":( OOPS!!! This task does not exist.");
                    drawLine();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(":( OOPS!!! This task does not exist.");
                    drawLine();
                }
            }
            else if (command.contains("mark")) {
                int taskID = getTaskID(command);
                try {
                    markTask(taskID);
                } catch (DukeException e) {
                    e.printErrorMessage();
                    drawLine();
                } catch (NullPointerException e) {
                    System.out.println(":( OOPS!!! This task does not exist.");
                    drawLine();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(":( OOPS!!! This task does not exist.");
                    drawLine();
                }
            }
            else if (command.contains("delete")) {
                int taskID = getTaskID(command);
                try {
                    deleteTask(taskID);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(":( OOPS!!! This task does not exist.");
                    drawLine();
                }
            }
            else {
                createNewTask(command);
            }
        } while (command != "bye");
    }
}
