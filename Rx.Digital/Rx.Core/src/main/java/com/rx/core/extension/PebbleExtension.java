//package com.rx.core.extension;
//
//import java.util.Map;
//
//import com.mitchellbosecke.pebble.extension.AbstractExtension;
//import com.systex.citizen.core.extension.filter.*;
//import com.systex.citizen.core.support.collect.Maps;
//
//
//public class PebbleExtension extends AbstractExtension {
//
//    @Override
//    public Map<String, Filter> getFilters() {
//        Map<String, Filter> map = super.getFilters();
//        if (map == null) {
//            map = Maps.newHashMap();
//        }
//        map.put(Nl2brFilter.FILTER_NAME, new Nl2brFilter());
//        map.put(NumberFilter.FILTER_NAME, new NumberFilter());
//        map.put(MaskFilter.FILTER_NAME, new MaskFilter());
//        map.put(DecryptHexFilter.FILTER_NAME, new DecryptHexFilter());
//        map.put(CompressUUIDFilter.FILTER_NAME, new CompressUUIDFilter());
//        return map;
//    }
//
//}