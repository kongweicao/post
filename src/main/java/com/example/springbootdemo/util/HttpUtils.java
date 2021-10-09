package com.example.springbootdemo.util;

import cn.hutool.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * description .
 */
@Slf4j
public class HttpUtils {
  /**
   * post without token.
   */
  public static String get(String url) {
    String result = HttpRequest.get(url)
        .execute().body();
    return result;
  }
}