package org;

import java.security.NoSuchAlgorithmException;

import org.terminal.CLI;

public class Main {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        CLI cli = new CLI();
        while (true){
            cli.run();
        }
    }
}
