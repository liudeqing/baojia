package com.baojia.user_manager.controller.cms;

import com.baojia.user_manager.security.CurrentUserDetail;
import com.baojia.user_manager.service.ICmsArticleService;
import com.baojia.user_manager_adapter.dto.CmsArticlePageQueryDto;
import com.baojia.user_manager_adapter.dto.CmsArticleRejectDto;
import com.baojia.user_manager_adapter.dto.CmsArticleSaveDto;
import com.baojia.user_manager_adapter.vo.CmsArticleAdminVo;
import com.baojia.user_manager_adapter.vo.CmsArticlePageResultVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文章管理接口。路径使用 /body/cms/ 前缀，与 /body/user 等已稳定透传鉴权头的路由一致，
 * 避免部分网关/代理对 /api/admin/** 单独处理导致 401。
 */
@RestController
@RequestMapping("/body/cms/articles")
@RequiredArgsConstructor
@SuppressWarnings("all")
public class CmsArticleAdminController {

    private final ICmsArticleService cmsArticleService;

    @PostMapping("/save")
    public CmsArticleAdminVo save(@RequestBody CmsArticleSaveDto dto) {
        Long uid = CurrentUserDetail.userIdOrNull();
        return cmsArticleService.saveDraft(dto, uid);
    }

    @PostMapping("/page")
    public CmsArticlePageResultVo<CmsArticleAdminVo> page(@RequestBody CmsArticlePageQueryDto query) {
        return cmsArticleService.pageForAdmin(query);
    }

    @GetMapping("/{id}")
    public CmsArticleAdminVo get(@PathVariable("id") Long id) {
        return cmsArticleService.getForAdmin(id);
    }

    @PostMapping("/{id}/submit")
    public void submit(@PathVariable("id") Long id) {
        cmsArticleService.submitForReview(id);
    }

    @PostMapping("/{id}/approve")
    public void approve(@PathVariable("id") Long id) {
        cmsArticleService.approve(id, CurrentUserDetail.userIdOrNull());
    }

    @PostMapping("/{id}/reject")
    public void reject(@PathVariable("id") Long id, @RequestBody(required = false) CmsArticleRejectDto body) {
        String reason = body != null ? body.getReason() : null;
        cmsArticleService.reject(id, reason, CurrentUserDetail.userIdOrNull());
    }

    @PostMapping("/{id}/withdraw")
    public void withdraw(@PathVariable("id") Long id) {
        cmsArticleService.withdrawPublish(id, CurrentUserDetail.userIdOrNull());
    }
}
