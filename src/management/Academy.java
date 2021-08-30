package management;

import java.util.Scanner;

import personnel.*;
import system.*;

interface LoginFlags {
    final static int NO_LOGIN = 0;
    final static int STUDENT_LOGIN = 1;
    final static int INSTRUCTOR_LOGIN = 2;
}

interface Menu {
    static void mainMenu() {
        System.out.println("1. Register An Account In Infinity Academy");
        System.out.println("2. Log In To Infinity Academy");
        System.out.println("3. About Infinity Academy");
        System.out.println("4. Meet The Team");
        System.out.println("5. Exit");
    }

    static void instructorMenu() {
        System.out.println("1. Reserve a class session");
        System.out.println("2. List reserved sessions");
        System.out.println("3. Change reserved session information");
        System.out.println("5. Log Out");
    }

    static void studentMenu() {
        System.out.println("1. Reserve an instructor's class");
        System.out.println("2. List reserved classes");
        System.out.println("3. Change class");
        System.out.println("5. Log Out");
    }
}

public class Academy {
    private static Student loggedInStudent = null;
    private static Instructor loggedInInstructor = null;
    private static int loginFlag = LoginFlags.NO_LOGIN;
    private static int choice;
    private static boolean sessionEnd = false;

    public static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        while (!sessionEnd) {
            showAcademyBanner();
            showMenu();
            System.out.println();
            System.out.print("Your Choice: ");
            choice = scan.nextInt();
            parseChoice();
            System.out.println();
        }
    }

    private static void showMenu() {
        switch (loginFlag) {
            case LoginFlags.NO_LOGIN:
                Menu.mainMenu();
                break;
            case LoginFlags.INSTRUCTOR_LOGIN:
                Menu.instructorMenu();
                break;
            case LoginFlags.STUDENT_LOGIN:
                Menu.studentMenu();
                break;
        }
    }

    private static void parseChoice() {
        switch (loginFlag) {
            case LoginFlags.NO_LOGIN:
                parseFirstTimeLogin();
                break;
            case LoginFlags.INSTRUCTOR_LOGIN:
                parseInstructorChoice();
                break;
            case LoginFlags.STUDENT_LOGIN:
                parseStudentChoice();
                break;
        }
    }

    private static void parseInstructorChoice() {
        switch (choice) {
            case 1:
                Session createdSession = Session.createSession(loggedInInstructor);
                loggedInInstructor.addReservation(createdSession);
                break;
            case 2:
                loggedInInstructor.listReservation();
                break;
            case 3:
                Session selectedEditSession = selectEditSession(Instructor.class);
                if (selectedEditSession == null) {
                    return;
                }

                Session newSession = Session.createSession(loggedInInstructor);
                System.out.println(newSession.toString());
                boolean confirmNewSession = confirmSession(newSession);

                if (!confirmNewSession) {
                    System.out.println("Aborted.");
                    return;
                }

                loggedInInstructor.editReservation(selectedEditSession, newSession);
                break;
            case 4:
                Session selectedSession = promptSession(Instructor.class);
                boolean confirmRemoveSession = confirmSession(selectedSession);

                if (!confirmRemoveSession) {
                    System.out.println("Session Removal Aborted");
                    return;
                }

                loggedInInstructor.removeReservation(selectedSession);
                break;
        }
    }

    private static <T> Session selectEditSession(Class<T> type) {
        Session selectedSession = promptSession(type);
        if (selectedSession == null) {
            return null;
        }

        System.out.println(selectedSession.toString());
        System.out.print("Confirm current Session? ");
        char confirm = scan.next().charAt(0);
        System.out.println();

        return confirm == 'y' ? selectedSession : null;
    }

    private static <T> Session promptSession(Class<T> type) {
        Student student = null;
        Instructor instructor = null;

        if (type.equals(Student.class)) {
            student = loggedInStudent;
            student.listReservation();
        }
        else if (type.equals(Instructor.class)) {
            instructor = loggedInInstructor;
            instructor.listReservation();
        }

        System.out.print("Enter Session number(0 to abort): ");
        int selection = scan.nextInt();

        return student != null ? student.getReservation(selection - 1) : instructor.getReservation(selection - 1);
    }

    private static void parseStudentChoice() {
        switch (choice) {
            case 1:
                Session.listReservedSessions();
                System.out.println("Select your preferred session: ");
                int selection = scan.nextInt();

                Session selectedSession = Session.getReservedSession(selection - 1);
                System.out.println(selectedSession.toString());
                boolean confirmSession = confirmSession(selectedSession);

                if (!confirmSession) {
                    System.out.println("Aborted.");
                    return;
                }

                loggedInStudent.addReservation(selectedSession);
                break;
            case 2:
                loggedInStudent.listReservation();
                break;
            case 3:
                Session selectedEditSession = promptSession(Student.class);
                if (selectedEditSession == null) {
                    System.out.println("Aborted.");
                    return;
                }

                boolean confirmEditSession = confirmSession(selectedEditSession);

                if (!confirmEditSession) {
                    System.out.println("Aborted.");
                    return;
                }

                Session.listReservedSessions();
                System.out.println("Select your preferred session: ");
                int newSessionSelection = scan.nextInt();

                Session newSelectedSession = Session.getReservedSession(newSessionSelection - 1);
                System.out.println(newSelectedSession.toString());
                boolean confirmSelectedSession = confirmSession(newSelectedSession);

                if (!confirmSelectedSession) {
                    System.out.println("Aborted.");
                    return;
                }

                loggedInStudent.editReservation(newSessionSelection, newSelectedSession);
                break;
            case 4:
                Session selectedSession = promptSession(Student.class);
                boolean confirmRemoveSession = confirmSession(selectedSession);

                if (!confirmRemoveSession) {
                    System.out.println("Aborted.");
                    return;
                }

                loggedInStudent.removeReservation(selectedSession);
                break;
        }
    }

    private static void parseFirstTimeLogin() {
        switch (choice) {
            case 1:
                Registration.performRegistration();
                break;
            case 2:
                logIn();
                break;
            case 3:
                System.out.println("Infinity Academy is an academy where you learn infinite things");
                break;
            case 4:
                System.out.println("our team has 4 people, you may visit us on our github project at: https://github.com/jim-low/infinity-academy");
                System.out.println("# shame less self sponser");
                break;
        }
    }

    private static void logIn() {
        String accountType = Registration.promptAccountType();
        String[] credentials = promptAccountCredentials();

        if (accountType.equals("Student")) {
            loggedInStudent = Person.search(credentials[0], credentials[1], Student.class);
            loginFlag = loggedInStudent != null ? LoginFlags.STUDENT_LOGIN : LoginFlags.NO_LOGIN;
        }
        else if (accountType.equals("Instructor")) {
            loggedInInstructor = Person.search(credentials[0], credentials[1], Student.class);
            loginFlag = loggedInInstructor != null ? LoginFlags.INSTRUCTOR_LOGIN : LoginFlags.NO_LOGIN;
        }

        if (loginFlag == LoginFlags.NO_LOGIN) {
            System.out.printf("Could not find registration with name '%s' in our database.\n", credentials[0]);
            return;
        }

        System.out.println("Successfully logged in!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    private static String[] promptAccountCredentials() {
        System.out.print("Enter your email: ");
        String email = scan.nextLine();

        System.out.print("Enter your password: ");
        String password = new String(System.console().readPassword());
        return new String[]{ email, password };
    }

    private static void showAcademyBanner() {
        System.out.println("__      __   _                    _         ___       __ _      _ _            _              _");
        System.out.println("\\ \\    / /__| |__ ___ _ __  ___  | |_ ___  |_ _|_ _  / _(_)_ _ (_) |_ _  _    /_\\  __ __ _ __| |___ _ __ _  _");
        System.out.println(" \\ \\/\\/ / -_) / _/ _ \\ '  \\/ -_) |  _/ _ \\  | || ' \\|  _| | ' \\| |  _| || |  / _ \\/ _/ _` / _` / -_) '  \\ || |");
        System.out.println("  \\_/\\_/\\___|_\\__\\___/_|_|_\\___|  \\__\\___/ |___|_||_|_| |_|_||_|_|\\__|\\_, | /_/ \\_\\__\\__,_\\__,_\\___|_|_|_\\_, |");
        System.out.println("                                                                      |__/                               |__/");
    }
}

