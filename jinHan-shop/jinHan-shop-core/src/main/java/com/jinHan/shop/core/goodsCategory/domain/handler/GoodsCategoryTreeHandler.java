package com.jinHan.shop.core.goodsCategory.domain.handler;

import com.jinHan.shop.core.goodsCategory.domain.mapper.GoodsCategoryMapper;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategory;
import com.jinHan.shop.core.goodsCategory.domain.model.GoodsCategoryTreeVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 类名: GoodsCategoryTreeHandler
 * 描述: 商品分类组装树处理器
 * 作者: xuzeyu
 * 创建时间: 2026/6/17
 */
@Component
public class GoodsCategoryTreeHandler {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    /**
     * 查询全部分类并组装为树形结构。
     * 单次全表查询 + 一次遍历挂载，时间复杂度 O(n)；
     * parentId 为 0、为 null 或父节点不存在（脏数据）的节点均作为根节点返回，避免数据丢失。
     */
    public List<GoodsCategoryTreeVO> treeList() {
        List<GoodsCategory> allList = goodsCategoryMapper.selectList();

        // 先全部转换为 VO，并建立 id -> VO 映射（LinkedHashMap 保留查询顺序）
        Map<Long, GoodsCategoryTreeVO> voMap = new LinkedHashMap<>(allList.size());
        for (GoodsCategory item : allList) {
            voMap.put(item.getId(), toTreeVO(item));
        }

        // 遍历挂载到父节点，根节点单独收集
        List<GoodsCategoryTreeVO> roots = new ArrayList<>();
        for (GoodsCategoryTreeVO node : voMap.values()) {
            Long parentId = node.getParentId();
            GoodsCategoryTreeVO parent = parentId == null ? null : voMap.get(parentId);
            if (parent == null) {
                roots.add(node);
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(node);
        }

        return roots;
    }

    private GoodsCategoryTreeVO toTreeVO(GoodsCategory source) {
        GoodsCategoryTreeVO vo = new GoodsCategoryTreeVO();
        BeanUtils.copyProperties(source, vo);
        return vo;
    }
}
