package com.example.backend.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRequest {
    private long id;
    private String name;
    private String email;
    private String password;
    private String imgName;
    private byte[] profileImg;


}
