package com.starv.task.configuration.security.interceptor;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String requestURI = httpServletRequest.getRequestURI();
//        if (StarVConst.isSlave()) {
//            if (StringUtils.endsWith(requestURI, "/")) {
//                requestURI = requestURI.substring(0, requestURI.length() - 1);
//            }
/*
        if (!requestURI.equals(StarVConst.PROJECT_URL + StarVConst.LOGIN_URL) &&
                !requestURI.equals(StarVConst.PROJECT_URL + StarVConst.LOGOUT_URL)) {
            HttpSession session = httpServletRequest.getSession();
            String userId = httpServletRequest.getHeader("userId");
            if(StringUtils.isBlank(userId)) {
                return true;
            }
            SystemUser systemUser = (SystemUser) session.getAttribute(StarVConst.SESSION_SYSUSER);
            if (systemUser == null || !systemUser.getUserId().toString().equals(userId)) {
                throw new StarVException("没有登录", StarVConst.NO_LOGIN_ERROR_CODE);
            }
        }
*/

        if (staticResFilter(requestURI)) {
            logger.info("to:{}", requestURI);
            logger.info("ip:{}", getIpAddress(httpServletRequest));
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private boolean staticResFilter(String url) {
        Set<String> filterSet = Sets.newHashSet("/css", "/images", "/js");
        for (String str : filterSet) {
            if (url.startsWith(str)) {
                return false;
            }
        }
        if (url.endsWith(".html") || url.endsWith(".htm")) {
            return false;
        }
        return true;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     *
     * @param request 请求
     * @return ip
     * @throws IOException
     */
    private String getIpAddress(HttpServletRequest request) throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

}