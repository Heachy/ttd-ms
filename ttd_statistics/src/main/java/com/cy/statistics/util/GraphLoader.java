package com.cy.statistics.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lambda
 * @date 2023/11/23
 */
public class GraphLoader
{
    public static Map<Integer, String> getInteger2location(String mapFilePath)
    {
        Map<Integer, String> integer2location = new HashMap<>();

        InputStream inputStream = GraphLoader.class.getResourceAsStream(mapFilePath);

        if (inputStream != null) {
            try(BufferedReader mapReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                String line = mapReader.readLine();
                if(line.replaceAll("[^,]", "").length() != 1) {
                    throw new RuntimeException("文件格式错误");
                }
                while((line = mapReader.readLine()) != null) {
                    integer2location.put(Integer.parseInt(line.substring(0, line.indexOf(","))), line.substring(line.indexOf(",") + 1));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return integer2location;
    }

    public static Map<String, Map<String, String>> getRouteFromFile(String routeFilePath)
    {
        Map<String, Map<String, String>> route = new HashMap<>();
        InputStream inputStream = GraphLoader.class.getResourceAsStream(routeFilePath);
        if (inputStream != null) {
            try(BufferedReader graphReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                String line = graphReader.readLine();
                if(line.replaceAll("[^,]", "").length() != 2) {
                    throw new RuntimeException("文件格式错误");
                }
                while((line = graphReader.readLine()) != null)
                {
                    String key = line.substring(0, line.indexOf(","));
                    if(!route.containsKey(key)) {
                        route.put(key, new HashMap<>());
                    }
                    route.get(key).put(line.substring(line.indexOf(",") + 1, line.lastIndexOf(",")), line.substring(line.lastIndexOf(",") + 1));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return route;
    }
}
