package com.yishuifengxiao.tool.personalkit.service.esim;

import com.yishuifengxiao.common.jdbc.JdbcUtil;
import com.yishuifengxiao.common.tool.bean.BeanUtil;
import com.yishuifengxiao.common.tool.entity.Page;
import com.yishuifengxiao.common.tool.entity.PageQuery;
import com.yishuifengxiao.tool.personalkit.domain.bo.Profile;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimMon;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimProfile;
import com.yishuifengxiao.tool.personalkit.domain.entity.esim.EsimProfileDetail;
import com.yishuifengxiao.tool.personalkit.domain.enums.esim.ProfileState;
import jakarta.validation.Valid;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EsimProfileService {


    public EsimProfile save(Profile esimProfile) {

        EsimProfile profile = BeanUtil.copy(esimProfile, new EsimProfile());
        String uniqueMatchId = generateUniqueMatchId();
        profile.setMatchingId(uniqueMatchId);
        if (null != esimProfile.getNotificationEvent()) {
            profile.setNotificationEvent(esimProfile.getNotificationEvent().stream().distinct().collect(Collectors.joining(",")));
        } else {
            profile.setNotificationEvent(null);
        }
        profile.setProfileState(ProfileState.Released.name());
        EsimProfileDetail detail = new EsimProfileDetail();
        Profile.V3Support v3Support = esimProfile.getV3Support();
        if (null != v3Support) {

            if (null != v3Support.getFeatures()) {
                detail.setV3Features(v3Support.getFeatures().stream().distinct().collect(Collectors.joining(",")));
            }
            BeanUtil.copy(v3Support.getRpmConfig(), detail);
            BeanUtil.copy(v3Support.getDeviceSwitch(), detail);
            BeanUtil.copy(v3Support.getEnterprise(), detail);
            if (null != v3Support.getRpmConfig()) {
                if (null != v3Support.getRpmConfig().getAllowedTags()) {
                    detail.setAllowedTags(v3Support.getRpmConfig().getAllowedTags().stream().distinct().collect(Collectors.joining(",")));
                }
                if (null != v3Support.getRpmConfig().getRpmType()) {
                    detail.setRpmType(v3Support.getRpmConfig().getRpmType().stream().distinct().collect(Collectors.joining(",")));
                }
            }
            if (null != v3Support.getEnterprise()) {
                if (null != v3Support.getEnterprise().getEnterpriseRules()) {
                    detail.setEnterpriseRules(v3Support.getEnterprise().getEnterpriseRules().stream().distinct().collect(Collectors.joining(",")));
                }
            }
        }
        KeyHolder keyHolder = JdbcUtil.jdbcHelper().insert(profile);
        detail.setProfileId(keyHolder.getKey().longValue());
        JdbcUtil.jdbcHelper().insert(detail);
        return profile;
    }

    /**
     * 生成全局唯一的matchId，格式为WTBIJ-EG8C0-C72NY-VNOPX
     *
     * @return 唯一的matchId
     */
    private String generateUniqueMatchId() {
        String matchId;
        do {
            // 生成格式为 XXXXX-XXXXX-XXXXX-XXXXX 的唯一ID
            UUID uuid = UUID.randomUUID();
            String uuidStr = uuid.toString().toUpperCase().replace("-", "");

            // 使用前20位转换为4组5位的格式
            matchId = String.format("%s-%s-%s-%s",
                    uuidStr.substring(0, 5),
                    uuidStr.substring(5, 10),
                    uuidStr.substring(10, 15),
                    uuidStr.substring(15, 20));
        } while (isMatchIdExists(matchId)); // 检查是否已存在，如果存在则重新生成

        return matchId;
    }


    /**
     * 检查matchId是否已经存在
     *
     * @param matchId 待检查的matchId
     * @return 如果存在返回true，否则返回false
     */
    private boolean isMatchIdExists(String matchId) {
        EsimProfile query = new EsimProfile();
        query.setMatchingId(matchId);
        Long counted = JdbcUtil.jdbcHelper().countAll(query, false);
        return counted > 0;
    }

    public List<EsimProfile> findAll(EsimProfile esimProfile) {
        return JdbcUtil.jdbcHelper().findAll(esimProfile, true);
    }


    public Profile findById(Long id) {
        // 通过主键查找 EsimProfile 对象
        EsimProfile profile = JdbcUtil.jdbcHelper().findByPrimaryKey(EsimProfile.class, id);

        // 如果找不到对应的记录，返回 null
        if (profile == null) {
            return null;
        }

        // 创建 Profile 对象并复制基础属性
        Profile result = BeanUtil.copy(profile, new Profile());

        // 查找对应的 EsimProfileDetail 记录
        EsimProfileDetail detailQuery = new EsimProfileDetail();
        detailQuery.setProfileId(id);
        List<EsimProfileDetail> details = JdbcUtil.jdbcHelper().findAll(detailQuery, true);

        // 如果找到了详情记录，则填充 V3Support 相关信息
        if (!details.isEmpty()) {
            EsimProfileDetail detail = details.get(0);

            // 创建 V3Support 对象
            Profile.V3Support v3Support = new Profile.V3Support();

            // 设置 features 信息（从 detail 的 v3Features 字段获取）
            if (detail.getV3Features() != null && !detail.getV3Features().isEmpty()) {
                List<String> features = Arrays.asList(detail.getV3Features().split(","));
                v3Support.setFeatures(features);
            }

            // 复制 RPM 配置相关信息
            Profile.RpmConfig rpmConfig = BeanUtil.copy(detail, new Profile.RpmConfig());
            if (detail.getAllowedTags() != null && !detail.getAllowedTags().isEmpty()) {
                List<String> allowedTags = Arrays.asList(detail.getAllowedTags().split(","));
                rpmConfig.setAllowedTags(allowedTags);
            }
            if (detail.getRpmType() != null && !detail.getRpmType().isEmpty()) {
                List<String> rpmTypes = Arrays.asList(detail.getRpmType().split(","));
                rpmConfig.setRpmType(rpmTypes);
            }
            v3Support.setRpmConfig(rpmConfig);

            // 复制 DeviceSwitch 配置
            Profile.DeviceSwitch deviceSwitch = BeanUtil.copy(detail, new Profile.DeviceSwitch());
            v3Support.setDeviceSwitch(deviceSwitch);

            // 复制 Enterprise 配置
            Profile.Enterprise enterprise = BeanUtil.copy(detail, new Profile.Enterprise());
            if (detail.getEnterpriseRules() != null && !detail.getEnterpriseRules().isEmpty()) {
                List<String> enterpriseRules = Arrays.asList(detail.getEnterpriseRules().split(","));
                enterprise.setEnterpriseRules(enterpriseRules);
            }
            v3Support.setEnterprise(enterprise);

            // 设置完整的 V3Support 对象
            result.setV3Support(v3Support);
        }

        // 处理 notificationEvent 字段（将其从逗号分隔字符串转回 List）
        if (profile.getNotificationEvent() != null && !profile.getNotificationEvent().isEmpty()) {
            List<String> notificationEvents = Arrays.asList(profile.getNotificationEvent().split(","));
            result.setNotificationEvent(notificationEvents);
        }

        return result;
    }


    public void deleteById(Long id) {
        JdbcUtil.jdbcHelper().deleteByPrimaryKey(EsimProfile.class, id);
    }


    public EsimProfile update(Profile esimProfile) {
        // 首先检查要更新的记录是否存在
        if (esimProfile.getId() == null) {
            throw new IllegalArgumentException("Profile ID cannot be null for update operation");
        }

        EsimProfile existingProfile = JdbcUtil.jdbcHelper().findByPrimaryKey(EsimProfile.class, esimProfile.getId());
        if (existingProfile == null) {
            throw new IllegalArgumentException("Profile with ID " + esimProfile.getId() + " does not exist");
        }

        // 更新 EsimProfile 基本信息
        EsimProfile profile = BeanUtil.copy(esimProfile, existingProfile);

        // 处理 notificationEvent 字段
        if (null != esimProfile.getNotificationEvent()) {
            profile.setNotificationEvent(esimProfile.getNotificationEvent().stream().distinct().collect(Collectors.joining(",")));
        } else {
            profile.setNotificationEvent(null);
        }

        // 更新 EsimProfile 记录
        JdbcUtil.jdbcHelper().updateByPrimaryKey(profile);

        // 处理 EsimProfileDetail 相关信息
        EsimProfileDetail detailQuery = new EsimProfileDetail();
        detailQuery.setProfileId(Long.valueOf(esimProfile.getId()));
        List<EsimProfileDetail> existingDetails = JdbcUtil.jdbcHelper().findAll(detailQuery, true);

        EsimProfileDetail detail;
        if (existingDetails.isEmpty()) {
            // 如果不存在详情记录，则创建新的
            detail = new EsimProfileDetail();
            detail.setProfileId(Long.valueOf(esimProfile.getId()));
        } else {
            // 如果存在详情记录，则更新现有记录
            detail = existingDetails.get(0);
        }

        // 填充 detail 信息
        Profile.V3Support v3Support = esimProfile.getV3Support();
        if (null != v3Support) {
            if (null != v3Support.getFeatures()) {
                detail.setV3Features(v3Support.getFeatures().stream().distinct().collect(Collectors.joining(",")));
            }

            BeanUtil.copy(v3Support.getRpmConfig(), detail);
            BeanUtil.copy(v3Support.getDeviceSwitch(), detail);
            BeanUtil.copy(v3Support.getEnterprise(), detail);

            if (null != v3Support.getRpmConfig()) {
                if (null != v3Support.getRpmConfig().getAllowedTags()) {
                    detail.setAllowedTags(v3Support.getRpmConfig().getAllowedTags().stream().distinct().collect(Collectors.joining(",")));
                }
                if (null != v3Support.getRpmConfig().getRpmType()) {
                    detail.setRpmType(v3Support.getRpmConfig().getRpmType().stream().distinct().collect(Collectors.joining(",")));
                }
            }

            if (null != v3Support.getEnterprise()) {
                if (null != v3Support.getEnterprise().getEnterpriseRules()) {
                    detail.setEnterpriseRules(v3Support.getEnterprise().getEnterpriseRules().stream().distinct().collect(Collectors.joining(",")));
                }
            }
        }

        // 保存或更新 detail 记录
        if (existingDetails.isEmpty()) {
            JdbcUtil.jdbcHelper().insert(detail);
        } else {
            JdbcUtil.jdbcHelper().updateByPrimaryKey(detail);
        }

        return profile;
    }


    public Page<Map<String, Object>> findPage(PageQuery<EsimProfile> pageQuery) {
        Map<String, String> cache = new HashMap<>();
        return JdbcUtil.jdbcHelper().findPage(pageQuery, true).map(s -> {
            Map<String, Object> map = BeanUtil.beanToMap(s);
            String monName = cache.computeIfAbsent(s.getMonOid(), k -> {
                EsimMon esimMon = JdbcUtil.jdbcHelper().findOne(new EsimMon().setMonOid(s.getMonOid()), false);
                return Optional.ofNullable(esimMon).map(EsimMon::getMonName).orElse("");
            });
            map.put("monName", monName);
            return map;
        });
    }

    public void deleteByIds(@Valid List<Long> ids) {
        String deleteDetailSql = "delete from esim_profile_detail where profile_id in (:ids)";

        String sql = "delete from esim_profile where id in (:ids)";
        JdbcUtil.jdbcHelper().namedParameterJdbcTemplate().update(deleteDetailSql, new MapSqlParameterSource("ids", ids));
        JdbcUtil.jdbcHelper().namedParameterJdbcTemplate().update(sql, new MapSqlParameterSource("ids", ids));
    }
}
