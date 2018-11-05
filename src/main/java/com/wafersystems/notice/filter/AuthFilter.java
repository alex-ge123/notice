package com.wafersystems.notice.filter;

import com.alibaba.fastjson.JSON;
import com.wafersystems.notice.base.controller.BaseController;
import com.wafersystems.notice.util.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: roc Date: 13-5-10 Time: 下午5:08 Email: liu.pengcheng@live.cn
 */
@Slf4j
public class AuthFilter extends BaseController implements Filter {

  /**
   * Description: author dingfeng DateTime 2016年4月11日 下午3:04:05.
   * 
   * @param filterConfig -
   * @throws ServletException -
   */
  public void init(FilterConfig filterConfig) throws ServletException {
    // To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Description: author dingfeng DateTime 2016年4月11日 下午3:03:57.
   * 
   * @param servletRequest -
   * @param servletResponse -
   * @param filterChain -
   * @throws IOException -
   * @throws ServletException -
   */
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                       FilterChain filterChain) throws IOException, ServletException {
    // 解析出token中的userId放到session中，在controller中获取。
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String token = request.getHeader("token");
    servletResponse.setCharacterEncoding("UTF-8");
    servletResponse.setContentType("text/html;charset=UTF-8");
    String url = request.getServletPath();
    log.debug("客户端IP为：" + getIp(request) + ",当前拦截的url为：" + url);
    if (ParamConstant.getUNCHECK_URL().contains(url) || url.endsWith("jpg") || url.endsWith("jpeg")
        || url.endsWith("png")) {
      filterChain.doFilter(servletRequest, servletResponse);
      return;
    }
    if (url.contains("/websocket")) {
      token = request.getParameter("token");
    }
    if (!StrUtil.isEmptyStr(token)) {
      Accesstoken tokenInfo = AccesstokenUtil.parseToken(token, Accesstoken.TokenType.FULL);
      String userId = !StrUtil.isNullObject(tokenInfo) ? tokenInfo.getUid() : null;
      if (!StrUtil.isEmptyStr(userId)) {
        filterChain.doFilter(servletRequest, servletResponse);
      } else {
        servletResponse.getWriter()
            .write(JSON.toJSONString(getForceLogoutMsg()));
      }
    } else {
      servletResponse.getWriter()
          .write(JSON.toJSONString(getForceLogoutMsg()));
    }
  }

  /**
   * 获取客户端ip
   * 
   * @param request -
   * @return -
   */
  private static String getIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (!StrUtil.isEmptyStr(ip) && !"unKnown".equalsIgnoreCase(ip)) {
      // 多次反向代理后会有多个ip值，第一个ip才是真实ip
      int index = ip.indexOf(ConfConstant.COMMA);
      if (index != -1) {
        return ip.substring(0, index);
      } else {
        return ip;
      }
    }
    ip = request.getHeader("X-Real-IP");
    if (!StrUtil.isEmptyStr(ip) && !"unKnown".equalsIgnoreCase(ip)) {
      return ip;
    }
    return request.getRemoteAddr();
  }

  /**
   * Description:
   * 
   * @return Object
   */
  private Object getForceLogoutMsg() {
    return returnBackMap("Token invalid", ConfConstant.RESULT_FAIL);
  }

  public void destroy() {}
}
