package com.cy.generated.mapper;

import com.cy.generated.domain.TtdUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Haechi
 * @since 2023-10-14
 */
@Mapper
public interface TtdUserMapper extends BaseMapper<TtdUser> {
    /**
     * 根据openId获取用户id
     * @param openid openId
     * @return 用户id
     */
    Long getUserIdByOpenId(String openid);
}
