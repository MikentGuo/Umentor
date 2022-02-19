package com.Umentor.UmentorprojectforProgrammingIII.component;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class URLInterceptor implements HandlerInterceptor {

  private Map<String,String> urlMaps = new HashMap<>() {{
    put("index", "home");
    put("courses", "courses");
    put("trainers", "trainers");
    put("events", "events");
    put("about", "about");
    put("contact", "contact");

    put("chatroom", "courses");
    put("classroom", "courses");
    put("course-details", "courses");
  }};

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    if(modelAndView != null) {
      modelAndView.addObject("navbarid", "");
      for (Map.Entry<String, String> entry : urlMaps.entrySet()) {
        if(modelAndView.getViewName().contains(entry.getKey())){
          modelAndView.addObject("navbarid", entry.getValue());
          break;
        }
      }
    }
  }
}
