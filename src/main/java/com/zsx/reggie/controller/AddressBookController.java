package com.zsx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.zsx.reggie.common.BaseContext;
import com.zsx.reggie.common.R;
import com.zsx.reggie.entity.AddressBook;
import com.zsx.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿控制器
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    AddressBookService addressBookService;

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("新增地址成功");
    }

    /**
     * 查询地址列表
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(){

        Long currentUserId = BaseContext.getCurrentId();

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(currentUserId!=null, AddressBook::getUserId, currentUserId);
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //select * from address_book where user_id = ? order by update desc;
        List<AddressBook> addressBookList = addressBookService.list(queryWrapper);

        return R.success(addressBookList);
    }

    /**
     * 根据id删除地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(Long ids){

        log.info("删除id：{}", ids);

        boolean flag = addressBookService.removeById(ids);

        if(!flag){
            return R.error("没有找到该对象");
        }

        return R.success("删除成功");
    }

    /**
     * 更新地址
     * @param addressBook
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){

        log.info("更新地址簿：{}", addressBook);

        boolean flag = addressBookService.updateById(addressBook);

        if(!flag){
            return R.error("没有找到该对象");
        }

        return R.success("更新成功");
    }

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable Long id){

        log.info("查询id：{}", id);

        AddressBook addressBook = addressBookService.getById(id);

        if(addressBook == null){
            return R.error("没有找到该对象");
        }

        return R.success(addressBook);
    }

    @PutMapping("/default")
    public R<String> setDefault(@RequestBody AddressBook addressBook){

        log.info("address：{}", addressBook);

        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper();
        updateWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        updateWrapper.set(AddressBook::getIsDefault, 0);

        //update address_book set is_default = 0 where user_id = ?
        addressBookService.update(updateWrapper);

        addressBook.setIsDefault(1);
        //update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);

        return R.success("设置默认地址成功");
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public R<AddressBook> getDefault(){
        log.info("查询默认地址");

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if(addressBook == null){
            return R.error("没有找到该对象");
        }

        return R.success(addressBook);
    }



}
