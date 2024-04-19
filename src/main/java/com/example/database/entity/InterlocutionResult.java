package com.example.database.entity;

import lombok.Data;

import java.util.List;

@Data
public class InterlocutionResult {
    private String id;
    private String question;
    private String cate;
    private String directive;
    private String directiveType;
    private String tips; //提示
}
