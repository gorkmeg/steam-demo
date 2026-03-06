package com.steampowered.steam_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class SteamDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SteamDemoApplication.class, args);
	}

}
