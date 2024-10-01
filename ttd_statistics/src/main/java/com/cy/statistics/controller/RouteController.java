package com.cy.statistics.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cy.common.api.CommonResult;
import com.cy.statistics.util.GraphLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Lambda
 * @date 2023/11/27
 */
@RestController
public class RouteController
{
    private static final Map<String, Map<String, String>> SHORTEST_ROUTES;
    private static final Map<String, Map<String, String>> MEDIUMN_ROUTES;
    private static final Map<String, Map<String, String>> LONGEST_ROUTES;
    private static final Map<Integer, String> INTEGER_TO_LOCATION;

    static class MyObject
    {
        private String path;

        public MyObject(String path)
        {
            this.path = path;
        }

        public String getPath()
        {
            return path;
        }

        public void setPath(String path)
        {
            this.path = path;
        }
    }

    static

    {
        INTEGER_TO_LOCATION = GraphLoader.getInteger2location("/csv/integer2location.csv");
        SHORTEST_ROUTES = GraphLoader.getRouteFromFile("/csv/route1.csv");
        MEDIUMN_ROUTES = GraphLoader.getRouteFromFile("/csv/route2.csv");
        LONGEST_ROUTES = GraphLoader.getRouteFromFile("/csv/route3.csv");
    }

    @GetMapping(value = "/getShortestPath")
    CommonResult<JSONObject> getShortestPath(@RequestParam("source") Integer source, @RequestParam("destination") Integer destination)
    {
        return  CommonResult.success(string2Json(SHORTEST_ROUTES.get(INTEGER_TO_LOCATION.get(source)).get(INTEGER_TO_LOCATION.get(destination))));
    }

    @GetMapping(value = "/getMediumPath")
    CommonResult<String[]> getMediumPath(@RequestParam("source") Integer source, @RequestParam("destination") Integer destination)
    {
        JSONObject jsonObject = string2Json(MEDIUMN_ROUTES.get(INTEGER_TO_LOCATION.get(source)).get(INTEGER_TO_LOCATION.get(destination)));

        String path = jsonObject.getString("path");

        path=path.substring(1, path.length() - 1);

        String[] split = path.split("->");

        // split里存放的为“福建省福州市闽侯县福州大学学生公寓|11号楼”
        // 提取|后面的数字


        for (int i = 0; i < split.length; i++) {
            if (split[i].startsWith("福建省福州市闽侯县")){
                split[i]=split[i].substring(9);
                if(split[i].contains("|")){
                    // 使用正则表达式提取|后面的数字
                    String[] splitParts = split[i].split("\\|");

                    // 如果确保数字部分存在且为最后一个部分，可以直接取最后一个元素
                    if (splitParts.length > 1) {
                        String numberAfterPipe = splitParts[splitParts.length - 1].replaceAll("\\D", "");
                        split[i]="福州大学学生公寓|"+numberAfterPipe+"号楼";
                    } else {
                        System.out.println("未找到数字部分");
                    }
                }
            }
        }
        return CommonResult.success(split);
    }

    @GetMapping(value = "/getLongestPath")
    CommonResult<JSONObject> getLongestPath(@RequestParam("source") Integer source, @RequestParam("destination") Integer destination)
    {
        String route = LONGEST_ROUTES.get(INTEGER_TO_LOCATION.get(source)).get(INTEGER_TO_LOCATION.get(destination));
        return CommonResult.success(string2Json(route == null ? INTEGER_TO_LOCATION.get(source): route));
    }

    private static JSONObject string2Json(String jsonString)
    {
        ObjectMapper objectMapper = new ObjectMapper();

        try
        {
            MyObject myObject = new MyObject(jsonString);
            return JSON.parseObject(objectMapper.writeValueAsString(myObject));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
