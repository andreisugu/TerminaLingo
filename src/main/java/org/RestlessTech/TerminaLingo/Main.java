package org.RestlessTech.TerminaLingo;

public class Main {
    public static void main(String[] args) {
        InterfaceManager manager = new InterfaceManager();
        manager.lessonParser = new LessonParser();

        while (true) {
            manager.stateSelect();
        }

    }
}