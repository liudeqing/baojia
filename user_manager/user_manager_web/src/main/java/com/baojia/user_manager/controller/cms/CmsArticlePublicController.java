package com.baojia.user_manager.controller.cms;

import com.baojia.user_manager.service.ICmsArticleService;
import com.baojia.user_manager_adapter.vo.CmsArticlePageResultVo;
import com.baojia.user_manager_adapter.vo.CmsArticlePublicVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pub/cms/articles")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class CmsArticlePublicController {

    private final ICmsArticleService cmsArticleService;

    @GetMapping
    public CmsArticlePageResultVo<CmsArticlePublicVo> list(
            @RequestParam(defaultValue = "1" , name = "pageNum" ) int pageNum,
            @RequestParam(defaultValue = "10" , name = "pageSize" ) int pageSize) {
        return cmsArticleService.pagePublished(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public CmsArticlePublicVo detail(@PathVariable("id") Long id) {
        return cmsArticleService.getPublished(id);
    }
}
