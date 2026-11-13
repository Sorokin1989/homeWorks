package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationEvent {
    private String email;
    private Operation operation;

    public enum Operation { CREATE, DELETE }
}