package org.example.backend.pojo;

import lombok.*;

@Data
@NoArgsConstructor

public class User {
    public Integer id;
    public String username;
    public String password;

    public User(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
