package com.starv.task.configuration.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//登录失败的
@Component("myAuthenticationFailHander")
public class MyAuthenticationFailHander extends SimpleUrlAuthenticationFailureHandler {
      private final ObjectMapper objectMapper;
      private Logger logger = LoggerFactory.getLogger(getClass());

      @Autowired
      public MyAuthenticationFailHander(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
      }

      @Override
      public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                          AuthenticationException exception) throws IOException, ServletException {

            logger.info("登录失败");
            //以Json格式返回
            Map<String,Object> responseMap= Maps.newHashMap();
            responseMap.put("code", HttpStatus.OK);
            responseMap.put("msg", "登录失败");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");   
            response.getWriter().write(objectMapper.writeValueAsString(responseMap));
            
      }
}
