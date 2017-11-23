package com.drore.cloud.tdp.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class RestMessage implements Serializable {
    private boolean success = true;
    private Object data;
    private Integer Code;
    private String message;
    private String id;
}