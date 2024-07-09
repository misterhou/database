package com.example.database.fanyumeta.service;

import com.example.database.fanyumeta.entity.MaxLoad;
import com.example.database.fanyumeta.entity.TotalLoad;

import java.time.LocalDate;
import java.util.List;

public interface LoadService {

    /**
     * 根据记录日期获取总负荷
     *
     * @param recordDate 记录日期
     * @return 总负荷信息
     */
    List<MaxLoad> getTotalLoadByRecordDate(LocalDate... recordDate);

    /**
     * 根据记录日期获取区域负荷
     *
     * @param area 区域
     * @param recordDate 记录日期
     * @return 区域负荷信息
     */
    List<MaxLoad> getZoneLoadByRecordDate(String area, LocalDate... recordDate);

    /**
     * 获取年度最大负荷
     *
     * @param area 区域
     * @return 年度最大负荷
     */
    String getYearMaxValue(String area);

    /**
     * 获取历史最大负荷
     *
     * @param area 区域
     * @return 历史最大负荷
     */
    String getHistoryMaxValue(String area);
}
