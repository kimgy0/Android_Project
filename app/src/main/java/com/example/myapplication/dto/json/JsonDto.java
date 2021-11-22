package com.example.myapplication.dto.json;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JsonDto {
    private int httpCode;
    private String json;
    private String token;
}
