package com.csu.mypetstore.controller;

import com.csu.mypetstore.config.Result;
import com.csu.mypetstore.domain.Category;
import com.csu.mypetstore.domain.Item;
import com.csu.mypetstore.domain.Product;
import com.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CatalogService catalogService;

    /**
     * 获得宠物种类
     * @param categoryId
     * @return
     */
    @GetMapping("/findCate")
    public Result<?> getCatelog(@RequestParam String categoryId) {
        Category category = catalogService.getCategory(categoryId);
        return Result.success(category);
    }

    /**
     * 通过种类名，得到该种类下的各种商品列表
     * @param categoryId
     * @return
     */
    @GetMapping("/findProd")
    public Result<?> getProd(@RequestParam String categoryId) {
        List<Product> productList = catalogService.getProductListByCategory(categoryId);
        return Result.success(productList);
    }

    /**
     * 通过商品id获得产品列表
     * @param productId
     * @return
     */
    @GetMapping("/findItems")
    public Result<?> getItems(@RequestParam String productId) {
        List<Item> itemList = catalogService.getItemListByProduct(productId);
        return Result.success(itemList);
    }

    /**
     * 通过产品id获得详细信息
     * @param itemId
     * @return
     */
    @GetMapping("/findItem")
    public Result<?> getItem(@RequestParam String itemId) {
        Item item = catalogService.getItem(itemId);
        return Result.success(item);
    }


}
