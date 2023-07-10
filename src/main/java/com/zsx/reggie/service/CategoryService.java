package com.zsx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsx.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * 自定义删除方法
     * @param id
     */
    public void remove(Long id);
}
