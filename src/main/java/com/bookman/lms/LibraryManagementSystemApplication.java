package com.bookman.lms;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

//import com.bookman.lms.entity.User;

@SpringBootApplication
public class LibraryManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryManagementSystemApplication.class, args);

	}

    @Bean
    CommandLineRunner demo() {
	    return (args) -> {
//	    	User user = new User("abc", "abc@gmail.com", "passWOED");
//	    	System.out.println(user);
	    	System.out.println("App Started");
	    };
	  }


}
