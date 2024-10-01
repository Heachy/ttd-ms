package com.cy.controller;

import com.cy.common.api.CommonResult;
import com.cy.common.utils.OssManagerUtil;
import com.cy.generated.domain.TtdUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Haechi
 */
@RestController
@RequestMapping("/file")
public class FileController {
    @PostMapping("/upload")
    public CommonResult<List<String>> upload(MultipartFile [] files) {
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = OssManagerUtil.getUrl(file);

            urls.add(url);
        }

        return CommonResult.success(urls);
    }
}
