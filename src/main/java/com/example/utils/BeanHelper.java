package com.example.utils;

import com.example.entity.BaseData;
import com.example.entity.dto.AccountDTO;
import com.example.entity.vo.response.AuthorizeVO;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



public class BeanHelper {

    public static <V> List<V> asViewObjectCollection(List<BaseData> sourceList, Class<V> target) {
            return sourceList.stream().map(
                    source ->source.asViewObject(target)).collect(Collectors.toList());

    }

//    public static <V> Set<V> asViewObjectCollection(Set<BaseData> sourceList , Class<V> target) {
//        return sourceList.stream().map(source ->source.asViewObject(target) ).collect(Collectors.toSet());
//    }


}
