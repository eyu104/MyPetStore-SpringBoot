package com.csu.mypetstore.mapper;

import com.csu.mypetstore.domain.LineItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface LineItemMapper {

    List<LineItem> getLineItemsByOrderId(int orderId);

    void insertLineItem(LineItem lineItem);

}
