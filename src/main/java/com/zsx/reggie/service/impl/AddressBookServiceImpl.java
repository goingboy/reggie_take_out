package com.zsx.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsx.reggie.entity.AddressBook;
import com.zsx.reggie.mapper.AddressBookMapper;
import com.zsx.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * 地址簿服务
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
