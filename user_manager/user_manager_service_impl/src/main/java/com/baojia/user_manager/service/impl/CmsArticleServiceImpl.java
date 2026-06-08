package com.baojia.user_manager.service.impl;

import com.alibaba.fastjson.JSON;
import com.baojia.user_manager.mapper.CmsArticleMapper;
import com.baojia.user_manager.model.CmsArticle;
import com.baojia.user_manager.service.ICmsArticleService;
import com.baojia.user_manager_adapter.dto.CmsArticlePageQueryDto;
import com.baojia.user_manager_adapter.dto.CmsArticleSaveDto;
import com.baojia.user_manager_adapter.vo.CmsArticleAdminVo;
import com.baojia.user_manager_adapter.vo.CmsArticlePageResultVo;
import com.baojia.user_manager_adapter.vo.CmsArticlePublicVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@SuppressWarnings("all")
public class CmsArticleServiceImpl extends ServiceImpl<CmsArticleMapper, CmsArticle> implements ICmsArticleService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CmsArticleAdminVo saveDraft(CmsArticleSaveDto dto, Long operatorUserId) {
        if (dto == null || !StringUtils.hasText(dto.getTitle())) {
            throw new RuntimeException("标题不能为空");
        }
        CmsArticle entity;
        if (dto.getArticleId() == null) {
            entity = new CmsArticle();
            entity.setStatus(CmsArticle.STATUS_DRAFT);
            entity.setCreateBy(operatorUserId);
        } else {
            entity = getById(dto.getArticleId());
            if (entity == null) {
                throw new RuntimeException("文章不存在");
            }
            if (entity.getStatus() == CmsArticle.STATUS_PUBLISHED) {
                throw new RuntimeException("已发布文章请先「撤回发布」再编辑");
            }
        }
        entity.setTitle(dto.getTitle().trim());
        if (dto.getArticleId() == null) {
            entity.setCategory(normalizeCategory(dto.getCategory()));
        } else if (dto.getCategory() != null) {
            entity.setCategory(normalizeCategory(dto.getCategory()));
        }
        entity.setContentHtml(dto.getContentHtml() != null ? dto.getContentHtml() : "");
        entity.setGalleryJson(toGalleryJson(dto.getGalleryUrls()));
        if (dto.getArticleId() == null) {
            entity.setCoverUrl(normalizeCoverUrl(dto.getCoverUrl()));
        } else if (dto.getCoverUrl() != null) {
            entity.setCoverUrl(normalizeCoverUrl(dto.getCoverUrl()));
        }
        entity.setUpdateBy(operatorUserId);
        LocalDateTime now = LocalDateTime.now();
        if (dto.getArticleId() == null) {
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            save(entity);
        } else {
            entity.setUpdateTime(now);
            updateById(entity);
        }
        return toAdminVo(getById(entity.getArticleId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitForReview(Long articleId) {
        CmsArticle a = requireArticle(articleId);
        if (a.getStatus() != CmsArticle.STATUS_DRAFT && a.getStatus() != CmsArticle.STATUS_REJECTED) {
            throw new RuntimeException("仅草稿或驳回状态可提交审核");
        }
        a.setStatus(CmsArticle.STATUS_PENDING);
        a.setRejectReason(null);
        updateById(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long articleId, Long operatorUserId) {
        CmsArticle a = requireArticle(articleId);
        if (a.getStatus() != CmsArticle.STATUS_PENDING) {
            throw new RuntimeException("仅待审核文章可通过审核");
        }
        a.setStatus(CmsArticle.STATUS_PUBLISHED);
        a.setPublishedAt(LocalDateTime.now());
        a.setRejectReason(null);
        a.setUpdateBy(operatorUserId);
        updateById(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reject(Long articleId, String reason, Long operatorUserId) {
        CmsArticle a = requireArticle(articleId);
        if (a.getStatus() != CmsArticle.STATUS_PENDING) {
            throw new RuntimeException("仅待审核文章可驳回");
        }
        a.setStatus(CmsArticle.STATUS_REJECTED);
        a.setRejectReason(StringUtils.hasText(reason) ? reason.trim() : "未填写原因");
        a.setUpdateBy(operatorUserId);
        updateById(a);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawPublish(Long articleId, Long operatorUserId) {
        CmsArticle a = requireArticle(articleId);
        if (a.getStatus() != CmsArticle.STATUS_PUBLISHED) {
            throw new RuntimeException("仅已发布文章可撤回");
        }
        a.setStatus(CmsArticle.STATUS_DRAFT);
        a.setPublishedAt(null);
        a.setUpdateBy(operatorUserId);
        updateById(a);
    }

    @Override
    public CmsArticleAdminVo getForAdmin(Long articleId) {
        CmsArticle a = requireArticle(articleId);
        return toAdminVo(a);
    }

    @Override
    public CmsArticlePageResultVo<CmsArticleAdminVo> pageForAdmin(CmsArticlePageQueryDto query) {
        int pageNum = query.getPageNum() == null || query.getPageNum() < 1 ? 1 : query.getPageNum();
        int pageSize = query.getPageSize() == null || query.getPageSize() < 1 ? 10 : Math.min(query.getPageSize(), 100);
        LambdaQueryWrapper<CmsArticle> w = new LambdaQueryWrapper<CmsArticle>()
                .orderByDesc(CmsArticle::getUpdateTime);
        if (query.getStatus() != null) {
            w.eq(CmsArticle::getStatus, query.getStatus());
        }
        Page<CmsArticle> page = page(new Page<>(pageNum, pageSize), w);
        CmsArticlePageResultVo<CmsArticleAdminVo> vo = new CmsArticlePageResultVo<>();
        vo.setTotal(page.getTotal());
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        vo.setRecords(page.getRecords().stream().map(this::toAdminVo).toList());
        return vo;
    }

    @Override
    public CmsArticlePageResultVo<CmsArticlePublicVo> pagePublished(int pageNum, int pageSize) {
        pageNum = pageNum < 1 ? 1 : pageNum;
        pageSize = pageSize < 1 ? 10 : Math.min(pageSize, 200);
        LambdaQueryWrapper<CmsArticle> w = new LambdaQueryWrapper<CmsArticle>()
                .eq(CmsArticle::getStatus, CmsArticle.STATUS_PUBLISHED)
                .orderByAsc(CmsArticle::getCategory)
                .orderByDesc(CmsArticle::getPublishedAt);
        Page<CmsArticle> page = page(new Page<>(pageNum, pageSize), w);
        CmsArticlePageResultVo<CmsArticlePublicVo> vo = new CmsArticlePageResultVo<>();
        vo.setTotal(page.getTotal());
        vo.setPageNum(pageNum);
        vo.setPageSize(pageSize);
        vo.setRecords(page.getRecords().stream().map(this::toPublicVo).toList());
        return vo;
    }

    @Override
    public CmsArticlePublicVo getPublished(Long articleId) {
        CmsArticle a = requireArticle(articleId);
        if (!Objects.equals(a.getStatus(), CmsArticle.STATUS_PUBLISHED)) {
            throw new RuntimeException("文章不存在或未发布");
        }
        return toPublicVo(a);
    }

    private CmsArticle requireArticle(Long articleId) {
        if (articleId == null) {
            throw new RuntimeException("文章ID不能为空");
        }
        CmsArticle a = getById(articleId);
        if (a == null) {
            throw new RuntimeException("文章不存在");
        }
        return a;
    }

    private static String toGalleryJson(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return "[]";
        }
        return JSON.toJSONString(urls);
    }

    private static List<String> parseGallery(String galleryJson) {
        if (!StringUtils.hasText(galleryJson)) {
            return Collections.emptyList();
        }
        try {
            return JSON.parseArray(galleryJson, String.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private static String statusLabel(int status) {
        return switch (status) {
            case CmsArticle.STATUS_DRAFT -> "草稿";
            case CmsArticle.STATUS_PENDING -> "待审核";
            case CmsArticle.STATUS_PUBLISHED -> "已发布";
            case CmsArticle.STATUS_REJECTED -> "已驳回";
            default -> "未知";
        };
    }

    private static String normalizeCategory(String raw) {
        if (!StringUtils.hasText(raw)) {
            return "其他";
        }
        String t = raw.trim();
        return t.length() > 128 ? t.substring(0, 128) : t;
    }

    private static String normalizeCoverUrl(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String t = raw.trim();
        return t.length() > 512 ? t.substring(0, 512) : t;
    }

    private CmsArticleAdminVo toAdminVo(CmsArticle a) {
        return CmsArticleAdminVo.builder()
                .articleId(a.getArticleId())
                .title(a.getTitle())
                .category(a.getCategory() != null ? a.getCategory() : "其他")
                .contentHtml(a.getContentHtml())
                .galleryUrls(parseGallery(a.getGalleryJson()))
                .coverUrl(a.getCoverUrl())
                .status(a.getStatus())
                .statusLabel(statusLabel(a.getStatus()))
                .rejectReason(a.getRejectReason())
                .publishedAt(a.getPublishedAt())
                .createTime(a.getCreateTime())
                .updateTime(a.getUpdateTime())
                .build();
    }

    private CmsArticlePublicVo toPublicVo(CmsArticle a) {
        return CmsArticlePublicVo.builder()
                .articleId(a.getArticleId())
                .title(a.getTitle())
                .category(a.getCategory() != null ? a.getCategory() : "其他")
                .contentHtml(a.getContentHtml())
                .galleryUrls(parseGallery(a.getGalleryJson()))
                .coverUrl(a.getCoverUrl())
                .publishedAt(a.getPublishedAt())
                .build();
    }
}
