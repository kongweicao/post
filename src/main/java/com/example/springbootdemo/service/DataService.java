package com.example.springbootdemo.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.springbootdemo.util.HttpUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DataService {

  public static void main(String[] args) {
    String fundUrl = "http://fund.eastmoney.com/js/fundcode_search.js";
    String fundResult = HttpUtils.get(fundUrl);
    fundResult = fundResult.substring(fundResult.indexOf("["), fundResult.lastIndexOf("]")+1);
    List<List> fundArray = JSONUtil.toList(fundResult, List.class);
    ConcurrentHashMap<String, AtomicInteger> postMap = new ConcurrentHashMap<>();
    fundArray.parallelStream().forEach(
        list -> {
          String fundCode = (String) list.get(0);
          String codeUrl = "http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jjcc&code=" + fundCode
              + "&topline=10&year=2021&month=";
          String postResult = HttpUtils.get(codeUrl);
          if(postResult.contains("{") && postResult.contains("}")){
            postResult = postResult.substring(postResult.indexOf("{"), postResult.lastIndexOf("}")+1);
            JSONObject postObject = JSONUtil.parseObj(postResult);
            String content = postObject.getStr("content");
            if(!StringUtil.isBlank(content)) {
              Document document = Jsoup.parse(content);
              Elements elements = document.getElementsByTag("a");
              AtomicInteger i = new AtomicInteger(0);
              AtomicInteger j = new AtomicInteger(0);
              elements.parallelStream().forEach(
                  element -> {
                    if (i.get() == 0) {
                      String name = element.html();
                      log.info("==============fund name is:{}==============", name);
                    }
                    //log.info("---------i:{}---------", i);
                    if ((i.get() - 2) % 6 == 0) {
                      j.incrementAndGet();
                      AtomicInteger total = postMap.get(element.html());
                      if (total == null || total.get() == 0) {
                        total.set(1);
                      } else {
                        total.incrementAndGet();
                      }
                      postMap.put(element.html(), total);
                      log.info("---------post name is:{}---------", element.html());
                    }
                    if (j.get() == 10) {
                      return;
                    }
                    i.incrementAndGet();
                  }
              );
            }
          }
        }
    );
    log.info("**************total is:{}****************", postMap);
  }
}
