package com.miniprogram.miniprogrambackstage;

import lombok.Data;

@Data
public class WxLoginResponse {
    private String session_key;
    private String openid;
}
