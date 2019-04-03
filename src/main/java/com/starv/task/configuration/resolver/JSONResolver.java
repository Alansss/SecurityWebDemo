package com.starv.task.configuration.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starv.task.configuration.annotation.JsonArg;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class JSONResolver implements HandlerMethodArgumentResolver {

    private static final String JSON_BODY_ATTRIBUTE = "JSON_BODY_ATTRIBUTE";

    private final ObjectMapper objectMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public JSONResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonArg.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        try {
            String body = getRequestBody(webRequest);
            String arg = parameter.getParameterAnnotation(JsonArg.class).value();
            //如果注解没写值 就用参数名
            if (StringUtils.isEmpty(arg)) {
                arg = parameter.getParameterName();
            }
            String treeValue = readTree(body, arg);
            if (null != treeValue) {
                return objectMapper.readValue(treeValue, parameter.getParameterType());
            }else {
                return null;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    private String readTree(String body, String key) throws IOException {
        String[] args = StringUtils.split(key, ".");
        JsonNode jsonNode = objectMapper.readTree(body);
        if (jsonNode == null) {
            return null;
        }
        if (args.length == 1) {
            return jsonNode.get(key).toString();
        } else {
            for (String arg : args) {
                jsonNode = jsonNode.get(arg);
            }
            return jsonNode.toString();
        }
    }

    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        String jsonBody = (String) webRequest.getAttribute(JSON_BODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        //这里用了一下缓存
        if (jsonBody == null) {
            try {
                jsonBody = IOUtils.toString(servletRequest.getInputStream(), "UTF-8");
                webRequest.setAttribute(JSON_BODY_ATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;
    }
}