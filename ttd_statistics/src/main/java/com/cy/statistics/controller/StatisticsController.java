package com.cy.statistics.controller;


import com.cy.common.api.CommonResult;
import com.cy.statistics.dao.Statistics;
import com.cy.statistics.service.ReviewService;
import com.cy.statistics.service.ScoreService;
import com.cy.statistics.service.StatisticsService;
import com.cy.statistics.vo.ReviewVO;
import com.cy.statistics.vo.StatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Lambda
 * &#064;date  2023/11/12
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController
{
    final StatisticsService statisticsService;

    ReviewService reviewService;

    ScoreService scoreService;

    public StatisticsController(@Autowired StatisticsService statisticsService, @Autowired ReviewService reviewService, @Autowired ScoreService scoreService)
    {
        this.statisticsService = statisticsService;
        this.reviewService = reviewService;
        this.scoreService = scoreService;
    }

    @PostMapping("/comment/get")
    public CommonResult<List<ReviewVO>> getComment(@RequestBody Map<String,Object> map){
        Integer page= (Integer) map.get("page");

        Integer pageSize= (Integer) map.get("pageSize");

        Long receiverId= Long.valueOf(map.get("receiverId").toString());
        return CommonResult.success(reviewService.getReviewList(receiverId,page,pageSize));
    }



    @PostMapping("/init")
    public CommonResult<String> init(){
        for(long i=11;i<=32;i++){
            scoreService.createScore(i);
        }
        return CommonResult.success("初始化成功");
    }

    @PostMapping("/get")
    CommonResult<StatisticsVO> getStatistics(@RequestBody Statistics statistics)
    {
        Optional<Statistics> res = statisticsService.getStatistics(statistics.getId());

        StatisticsVO statisticsVO=new StatisticsVO(Objects.requireNonNull(res.map(CommonResult::success).orElse(null)).getData());

        statisticsVO.setScore(scoreService.getScore(Long.valueOf(statistics.getId())));

        return CommonResult.success(statisticsVO);
    }

    @PostMapping("/score/get")
    CommonResult<Double> getScore(@RequestBody Map<String,Object> map)
    {
        Long id= ((Integer) map.get("id")).longValue();

        System.out.println(id);

        return CommonResult.success(scoreService.getScore(id));

    }
}
