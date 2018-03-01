package com.mfg.Filter;

import com.mfg.Entity.User;
import com.mfg.config.Constants;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by I309908 on 6/1/2017.
 */
@Component
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest)servletRequest;
        HttpServletResponse rep=(HttpServletResponse)servletResponse;
        String url=req.getRequestURL().toString();
        HttpSession session=req.getSession();
        if (!url.equals("http://localhost:8090/view/Login.view.xml") &&
                !url.equals("http://localhost:8090/view/App.view.xml") &&
                    url.startsWith("http://localhost:8090/view/") &&
                        session.getAttribute(Constants.USER_SESSION_KEY) == null) {

        }else if(session.getAttribute("USER_SESSION_KEY") != null){
            User sessionUser = (User) session.getAttribute(Constants.USER_SESSION_KEY);
            Pattern pattern = Pattern.compile("[IDCidc][0-9]{6}");
            Matcher matcher = pattern.matcher(url);
            while (matcher.find()){
                String result = matcher.group();
                if (!result.equalsIgnoreCase(sessionUser.getSapId())){
                    System.out.println("This user has no right to access this view.");
                }
            }
        }

        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
