package com.example.database.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.database.domain.LineEndpoint;
import com.example.database.service.LineEndpointService;
import com.example.database.mapper.LineEndpointMapper;
import org.springframework.stereotype.Service;

/**
* @author woshi
* @description 针对表【line_endpoint】的数据库操作Service实现
* @createDate 2023-09-20 09:56:32
*/
@Service
public class LineEndpointServiceImpl extends ServiceImpl<LineEndpointMapper, LineEndpoint>
    implements LineEndpointService{

}




