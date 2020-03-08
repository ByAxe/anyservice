package com.anyservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contacts implements Serializable {
    private String phone;
    private String email;
    private String google;
    private String facebook;
}
