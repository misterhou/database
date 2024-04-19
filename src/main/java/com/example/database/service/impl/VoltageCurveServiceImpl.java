package com.example.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.domain.VoltageCurve;
import com.example.database.domain.VoltageCurve;
import com.example.database.service.VoltageCurveService;
import com.example.database.mapper.VoltageCurveMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author woshi
* @description 针对表【voltage_curve】的数据库操作Service实现
* @createDate 2023-09-20 09:56:44
*/
@Service
public class VoltageCurveServiceImpl extends ServiceImpl<VoltageCurveMapper, VoltageCurve>
    implements VoltageCurveService{

    @Override
    public List<VoltageCurve> getList(String date) {
        //年-月-日
        String regex1 = "\\d{4}年\\d{1,2}月\\d{1,2}日";
        Pattern pattern1 = Pattern.compile(regex1);
        Matcher matcher1=pattern1.matcher(date);
        String regex11 = "\\d{4}-\\d{1,2}-\\d{1,2}";
        Pattern pattern11 = Pattern.compile(regex11);
        Matcher matcher11=pattern11.matcher(date);
        String regex22 = "\\d{1,2}-\\d{1,2}";
        Pattern pattern22 = Pattern.compile(regex22);
        Matcher matcher22=pattern22.matcher(date);

        //年-月
        String regex2="\\d{4}年\\d{1,2}月";
        Pattern pattern2 =Pattern.compile(regex2);
        Matcher matcher2=pattern2.matcher(date);
        //年
        String regex3="\\d{4}年";
        Pattern pattern3=Pattern.compile(regex3);
        Matcher matcher3=pattern3.matcher(date);
        //月-日
        String regex4="\\d{1,2}月\\d{1,2}日";
        Pattern pattern4=Pattern.compile(regex4);
        Matcher matcher4=pattern4.matcher(date);
        //月或日
        String regex5="\\d{1,2}月";
        Pattern pattern5=Pattern.compile(regex5);
        Matcher matcher5=pattern5.matcher(date);
        //月或日
        String regex6="\\d{1,2}日";
        Pattern pattern6=Pattern.compile(regex6);
        Matcher matcher6=pattern6.matcher(date);

        if (matcher1.matches()){
            String a=matcher1.group();
            String year=a.substring(0,a.indexOf("年"));
            String month=a.substring(a.indexOf("年")+1,a.indexOf("月"));
            String day=a.substring(a.indexOf("月")+1,a.indexOf("日"));
            if(Integer.parseInt(month)<10&&!(month.contains("0"))){
                month="0"+month;
            }
            if(Integer.parseInt(day)<10&&!(day.contains("0"))){
                day="0"+day;
            }
            String d=year+"-"+month+"-"+day;
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",d);

            return this.list(queryWrapper);
        }
        if (matcher2.matches()){
            String a=matcher2.group();
            String year=a.substring(0,a.indexOf("年"));
            String month=a.substring(a.indexOf("年")+1,a.indexOf("月"));
            if(Integer.parseInt(month)<10&&!(month.contains("0"))){
                month="0"+month;
            }
            String d=year+"-"+month+"-";
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",d);
            return this.list(queryWrapper);
        }
        if(matcher3.matches()){
            String a=matcher3.group();
            String year=a.substring(0,a.indexOf("年"));
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",year);
            return this.list(queryWrapper);
        }
        if(matcher4.matches()){
            String a=matcher4.group();
            String month=a.substring(0,a.indexOf("月"));
            String day=a.substring(a.indexOf("月")+1,a.indexOf("日"));
            if(Integer.parseInt(month)<10&&!(month.contains("0"))){
                month="0"+month;
            }
            if(Integer.parseInt(day)<10&&!(day.contains("0"))){
                day="0"+day;
            }
            String d=month+"-"+day;
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",d);
            return this.list(queryWrapper);
        }
        if(matcher5.matches()){
            String a=matcher5.group();
            String month=a.substring(0,a.indexOf("月"));
            if(Integer.parseInt(month)<10&&!(month.contains("0"))){
                month="0"+month;
            }
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",month);
            return this.list(queryWrapper);
        }
        if(matcher6.matches()){
            String a=matcher6.group();
            String day=a.substring(0,a.indexOf("日"));
            if(Integer.parseInt(day)<10&&!(day.contains("0"))){
                day="0"+day;
            }
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",day);
            return this.list(queryWrapper);
        }
        if(matcher11.matches()){
            String d=matcher11.group();
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",d);
            return this.list(queryWrapper);
        }
        if(matcher22.matches()){
            String a=matcher22.group();
            String month=a.substring(0,a.indexOf("-"));
            String day=a.substring(a.indexOf("-"));
            if(Integer.parseInt(month)<10&&!(month.contains("0"))){
                month="0"+month;
            }
            if(Integer.parseInt(day)<10&&!(day.contains("0"))){
                day="0"+day;
            }
            String d=month+"-"+day;
            QueryWrapper<VoltageCurve> queryWrapper=new QueryWrapper<>();
            queryWrapper.like("time",d);
            return this.list(queryWrapper);
        }
        return null;
    }
}




