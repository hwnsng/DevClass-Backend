package com.hwnsng.devclass;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PrintHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        System.out.println("admin: " + enc.encode("1234"));
        System.out.println("teacher: " + enc.encode("1234"));
    }
}
