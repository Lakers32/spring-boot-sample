package com.cn.boot.sample.server.dynamic.service;

import com.cn.boot.sample.api.model.po.SensitiveWord;
import com.cn.boot.sample.api.service.SensitiveWord2Service;
import com.cn.boot.sample.api.service.SensitiveWordService;
import com.cn.boot.sample.dal.mapper.SensitiveWordMapper;
import com.cn.boot.sample.server.dynamic.config.dds.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Chen Nan
 */
@Service(timeout = 300000)
@Slf4j
public class SensitiveWordServiceImpl extends BaseServiceImpl<SensitiveWordMapper, SensitiveWord, Long>
        implements SensitiveWordService {

    @Resource
    private SensitiveWordMapper sensitiveWordMapper;
    @Reference
    private SensitiveWord2Service sensitiveWord2Service;

    @Override
    @DynamicDataSource
    public int insertSensitiveWord(String appId, SensitiveWord sensitiveWord) {
        return sensitiveWordMapper.insertSelective(sensitiveWord);
    }

    @Override
    @DynamicDataSource
    @Transactional(rollbackFor = Exception.class)
    public int insertWithTransaction(String appId, SensitiveWord sensitiveWord) {
        sensitiveWord2Service.insertSensitiveWord(appId, sensitiveWord);
        int result = sensitiveWordMapper.insertSelective(sensitiveWord);
        if ("3".equals(appId)) {
            int i = 1 / 0;
        }
        return result;
    }

    @Override
    @DynamicDataSource
    public List<SensitiveWord> list(String appId, SensitiveWord sensitiveWord) {
        return sensitiveWordMapper.select(sensitiveWord);
    }
}
