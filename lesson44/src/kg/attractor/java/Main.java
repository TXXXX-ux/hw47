package kg.attractor.java;

import kg.attractor.java.handler.Lesson44Server;
import kg.attractor.java.handler.Lesson46;
import kg.attractor.java.handler.Lesson47;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new Lesson47("localhost", 9889).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
