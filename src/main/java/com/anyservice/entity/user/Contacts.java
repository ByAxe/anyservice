package com.anyservice.entity.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contacts implements Serializable {
    private String phone;
    private String email;
    private String google;
    private String facebook;
}
