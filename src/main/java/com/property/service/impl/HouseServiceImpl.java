package com.property.service.impl;

import com.property.common.BaseService;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.common.StatusValidator;
import com.property.entity.House;
import com.property.mapper.HouseMapper;
import com.property.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 房屋Service实现类
 */
@Service
@RequiredArgsConstructor
public class HouseServiceImpl extends BaseService<House, HouseMapper> implements HouseService {

    private final HouseMapper houseMapper;

    @Override
    protected HouseMapper getMapper() {
        return houseMapper;
    }

    @Override
    protected String getEntityName() {
        return "房屋";
    }

    @Override
    public int count() {
        return houseMapper.countAll();
    }

    @Override
    public List<House> getByCondition(String building, String status) {
        return houseMapper.selectByCondition(building, status);
    }

    @Override
    public PageResult<House> getByPage(String building, String status, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> houseMapper.selectByPage(building, status, params[0], params[1]),
                () -> houseMapper.selectCount(building, status));
    }

    @Override
    public List<House> getByOwnerId(Integer ownerId) {
        return houseMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(House house) {
        return houseMapper.insert(house);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(House house) {
        House existing = findExistingOrThrow(house.getId());
        // 验证房屋状态转换合法性
        if (house.getStatus() != null) {
            StatusValidator.validateHouseTransition(existing.getStatus(), house.getStatus());
        }
        return houseMapper.update(house);
    }
}
