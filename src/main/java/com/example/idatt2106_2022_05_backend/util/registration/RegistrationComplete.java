package com.example.idatt2106_2022_05_backend.util.registration;

import com.example.idatt2106_2022_05_backend.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationComplete extends ApplicationEvent {

    private final User user;
    private final String applicationUrl;

    public RegistrationComplete(User user, String applicationUrl) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
    }
}
