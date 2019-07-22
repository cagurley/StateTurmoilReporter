package com.cagurley;

import java.util.HashMap;
import java.util.Scanner;

public class InputManager {
    private Scanner scanner;
    private HashMap<String, String> inputs;
    public final String yRegex;
    public final String nRegex;

    public InputManager() {
        this.initScanner();
        this.inputs = new HashMap();
        this.yRegex = "^[yY].*$";
        this.nRegex = "^[nN].*$";
    }

    /* Scanner Methods */
    public void initScanner() {
        if (this.scanner == null) {
            this.scanner = new Scanner(System.in);
        }
    }

    public void closeScanner() {
        if (this.scanner != null) {
            this.scanner.close();
            this.scanner = null;
        }
    }

    /* Input Methods */
    public boolean evaluate(String key, String regex) {
        if (this.getInput(key) == null) {
            return false;
        }
        return getInput(key).matches(regex);
    }

    public boolean popEvaluate(String key, String regex) {
        if (this.getInput(key) == null) {
            return false;
        }
        return popInput(key).matches(regex);
    }

    private void addInput(String key, String value) { inputs.put(key, value); }

    public String getInput(String key) { return inputs.get(key); }

    public String popInput(String key) { return inputs.remove(key); }

    /* Prompting Methods */
    public void storePrompt(String prompt, String key) {
        if (this.scanner != null) {
            System.out.println(prompt);
            this.addInput(key, this.scanner.nextLine());
        } else {
            throw new NullPointerException("Prompt failed: scanner not initialized");
        }
    }

    public void waitForInput() {
        if (this.scanner != null) {
            System.out.println("Press ENTER to continue.");
            this.scanner.nextLine();
        } else {
            throw new NullPointerException("Prompt failed: scanner not initialized");
        }
    }
}
