package com.starv.task.configuration.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//处理登录成功的。
@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
      
      private final ObjectMapper objectMapper;

      @Autowired
      public MyAuthenticationSuccessHandler(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
      }

      @Override
      public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
                  throws IOException, ServletException {
            //什么都不做的话，那就直接调用父类的方法
            //super.onAuthenticationSuccess(request, response, authentication);
            
            //这里可以根据实际情况，来确定是跳转到页面或者json格式。
            //如果是返回json格式，那么我们这么写
            
            Map<String,Object> responseMap= Maps.newHashMap();
            responseMap.put("code", HttpStatus.OK);
            responseMap.put("msg", "登录成功");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            
            //如果是要跳转到某个页面的，比如我们的那个whoim的则
            //new DefaultRedirectStrategy().sendRedirect(request, response, "/log");
            
      }
}
