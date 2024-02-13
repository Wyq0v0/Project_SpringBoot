package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Address;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserAddressMapper {
    int insertAddress(Address address);
    Address selectAddress(@Param("addressId")int addressId);
    List<Address> selectAddressList(@Param("addressIdList")List addressIdList);
}
