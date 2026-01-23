package org.example;

import org.example.framework.annotation.ComponentScan;
import org.example.server.SeungPringApplication;

@ComponentScan("org.example")
public class Application {
    public static void main(String[] args) {
        new SeungPringApplication().run(Application.class, args);
    }
}