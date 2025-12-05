package com.restaurant.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility to generate BCrypt password hashes
 */
public class HashGenerator {
    public static void main(String[] args) {
        String[] passwords = {"admin123", "server123", "chef123"};
        
        System.out.println("=== BCrypt Hash Generator ===\n");
        
        for (String password : passwords) {
            String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
            System.out.println("Password: " + password);
            System.out.println("Hash: " + hash);
            System.out.println();
        }
    }
}
