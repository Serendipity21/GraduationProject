package com.miniprogram.miniprogrambackstage.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miniprogram.miniprogrambackstage.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 自定义拦截器
 *
 * @author zds
 */
public class GlobalInterceptor implements HandlerInterceptor {
    /**
     * 请求前拦截器，如果请求头没有 token ，返回208
     *
     * @param request http请求
     * @param response http响应
     * @param handler handler
     * @return 是否继续执行对应路由后续代码
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 Authorization
        String token = request.getHeader("Authorization");
        if (token == null) {
            HttpResponse responseBody = new HttpResponse("",208);
            // 序列化 responseBody
            String data = new ObjectMapper().writeValueAsString(responseBody);
            // 将序列化后的数据写入http响应
            response.getWriter().write(data);
            return false;
        }
        return true;
    }
}
