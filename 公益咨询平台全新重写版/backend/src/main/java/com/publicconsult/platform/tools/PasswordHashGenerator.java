package com.publicconsult.platform.tools;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("用法：PasswordHashGenerator <新密码>");
            return;
        }
        System.out.println(new BCryptPasswordEncoder().encode(args[0]));
    }
}
