package com.Umentor.UmentorprojectforProgrammingIII.config;

import com.Umentor.UmentorprojectforProgrammingIII.component.URLInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private URLInterceptor urlInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry){
    registry.addInterceptor(urlInterceptor);
  }
}