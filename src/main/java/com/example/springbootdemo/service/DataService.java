package com.example.springbootdemo.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.springbootdemo.util.HttpUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    for(List list: fundArray){
      String fundCode = (String) list.get(0);
      String codeUrl = "http://fundf10.eastmoney.com/FundArchivesDatas.aspx?type=jjcc&code=" + fundCode
          + "&topline=10&year=2021&month=";
      String postResult = HttpUtils.get(codeUrl);
      postResult = postResult.substring(postResult.indexOf("{"), postResult.lastIndexOf("}")+1);
      JSONObject postObject = JSONUtil.parseObj(postResult);
      String content = postObject.getStr("content");
      if(!StringUtil.isBlank(content)) {
        Document document = Jsoup.parse(content);
        Elements elements = document.getElementsByTag("a");
        int i = 0;
        for (Element element : elements) {
          if (i == 0) {
            log.info("---------fund name is:{}---------", element.html());
          }
          log.info("---------i:{}---------", i);
          if ((i - 2) % 6 == 0) {
            log.info("---------post name is:{}---------", element.html());
          }
          i++;
        }
        System.out.println("--------begin:" + elements + "----------end");
      }
    }
    //log.info(JSONUtil.toJsonStr(fundArray));
  }
}
