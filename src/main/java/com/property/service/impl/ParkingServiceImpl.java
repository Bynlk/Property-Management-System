package com.property.service.impl;

import com.property.common.BaseService;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.common.StatusValidator;
import com.property.entity.Parking;
import com.property.mapper.ParkingMapper;
import com.property.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 停车位Service实现类
 */
@Service
@RequiredArgsConstructor
public class ParkingServiceImpl extends BaseService<Parking, ParkingMapper> implements ParkingService {

    private final ParkingMapper parkingMapper;

    @Override
    protected ParkingMapper getMapper() {
        return parkingMapper;
    }

    @Override
    protected String getEntityName() {
        return "停车位";
    }

    @Override
    public List<Parking> getByCondition(String spotNumber, String status) {
        return parkingMapper.selectByCondition(spotNumber, status);
    }

    @Override
    public PageResult<Parking> getByPage(String spotNumber, String status, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> parkingMapper.selectByPage(spotNumber, status, params[0], params[1]),
                () -> parkingMapper.selectCount(spotNumber, status));
    }

    @Override
    public List<Parking> getByOwnerId(Integer ownerId) {
        return parkingMapper.selectByOwnerId(ownerId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Parking parking) {
        return parkingMapper.insert(parking);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Parking parking) {
        Parking existing = findExistingOrThrow(parking.getId());
        // 验证停车位状态转换合法性
        if (parking.getStatus() != null) {
            StatusValidator.validateParkingTransition(existing.getStatus(), parking.getStatus());
        }
        return parkingMapper.update(parking);
    }
}
