package com.baojia.user_manager.service;

import com.baojia.user_manager.model.CmsArticle;
import com.baojia.user_manager_adapter.dto.CmsArticlePageQueryDto;
import com.baojia.user_manager_adapter.dto.CmsArticleSaveDto;
import com.baojia.user_manager_adapter.vo.CmsArticleAdminVo;
import com.baojia.user_manager_adapter.vo.CmsArticlePageResultVo;
import com.baojia.user_manager_adapter.vo.CmsArticlePublicVo;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ICmsArticleService extends IService<CmsArticle> {

    CmsArticleAdminVo saveDraft(CmsArticleSaveDto dto, Long operatorUserId);

    void submitForReview(Long articleId);

    void approve(Long articleId, Long operatorUserId);

    void reject(Long articleId, String reason, Long operatorUserId);

    void withdrawPublish(Long articleId, Long operatorUserId);

    CmsArticleAdminVo getForAdmin(Long articleId);

    CmsArticlePageResultVo<CmsArticleAdminVo> pageForAdmin(CmsArticlePageQueryDto query);

    CmsArticlePageResultVo<CmsArticlePublicVo> pagePublished(int pageNum, int pageSize);

    CmsArticlePublicVo getPublished(Long articleId);
}
