package com.property.service.impl;

import com.property.common.BusinessException;
import com.property.common.PageHelper;
import com.property.common.PageResult;
import com.property.entity.Owner;
import com.property.mapper.ComplaintMapper;
import com.property.mapper.FeeMapper;
import com.property.mapper.HouseMapper;
import com.property.mapper.OwnerMapper;
import com.property.mapper.RepairMapper;
import com.property.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 业主Service实现类
 */
@Service
public class OwnerServiceImpl implements OwnerService {

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private FeeMapper feeMapper;

    @Autowired
    private ComplaintMapper complaintMapper;

    @Autowired
    private RepairMapper repairMapper;

    @Override
    public Owner getById(Integer id) {
        return ownerMapper.selectById(id);
    }

    @Override
    public List<Owner> getByCondition(String name, String phone) {
        return ownerMapper.selectByCondition(name, phone);
    }

    @Override
    public PageResult<Owner> getByPage(String name, String phone, Integer pageNum, Integer pageSize) {
        return PageHelper.doPage(pageNum, pageSize,
                params -> ownerMapper.selectByPage(name, phone, params[0], params[1]),
                () -> ownerMapper.selectCount(name, phone));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Owner owner) {
        return ownerMapper.insert(owner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Owner owner) {
        Owner existing = ownerMapper.selectById(owner.getId());
        if (existing == null) {
            throw new BusinessException("业主不存在: id=" + owner.getId());
        }
        return ownerMapper.update(owner);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        Owner existing = ownerMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException("业主不存在: id=" + id);
        }
        // 检查是否有关联数据
        int houseCount = houseMapper.countByOwnerId(id);
        if (houseCount > 0) {
            throw new BusinessException("该业主下有 " + houseCount + " 套房屋，请先解除关联后再删除");
        }
        int feeCount = feeMapper.countByOwnerId(id);
        if (feeCount > 0) {
            throw new BusinessException("该业主下有 " + feeCount + " 条费用记录，请先处理后再删除");
        }
        int complaintCount = complaintMapper.countByOwnerId(id);
        if (complaintCount > 0) {
            throw new BusinessException("该业主下有 " + complaintCount + " 条投诉记录，请先处理后再删除");
        }
        int repairCount = repairMapper.countByOwnerId(id);
        if (repairCount > 0) {
            throw new BusinessException("该业主下有 " + repairCount + " 条报修记录，请先处理后再删除");
        }
        return ownerMapper.deleteById(id);
    }
}
