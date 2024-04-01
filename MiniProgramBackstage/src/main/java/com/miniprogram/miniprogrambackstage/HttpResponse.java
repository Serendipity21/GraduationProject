package com.miniprogram.miniprogrambackstage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpResponse<T> {
    private T data;
    private int code;
}