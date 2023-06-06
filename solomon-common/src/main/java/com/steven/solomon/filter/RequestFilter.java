package com.steven.solomon.filter;

import cn.hutool.core.lang.UUID;
import com.steven.solomon.exception.ExceptionUtil;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class RequestFilter implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    ExceptionUtil.requestId.set(UUID.randomUUID().toString());
    chain.doFilter(request, response);
  }
}
