package com.drore.cloud.tdp.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.drore.cloud.sdk.domain.util.RequestExample;
import com.drore.cloud.tdp.entity.kpi.*;
import com.drore.cloud.tdp.exception.BusinessException;
import com.drore.cloud.tdp.tables.kpi.KpiTables;
import com.drore.cloud.tdp.utils.DateTimeUtil;
import com.drore.cloud.tdp.utils.QueryUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2017/10/27  12:41.
 */
@Component
public class KpiServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(KpiServiceImpl.class);

    private static String url;
    private static String userName;
    private static String password;

    private static String TOURIST_COUNT_MINUTE_KPI_ID = "1001";//同步游客人数(5分钟数据)
    private static String TOURIST_COUNT_DAY_KPI_ID = "1005";//同步游客人数(1天数据)
    private static String TOURIST_SOURCE_KPI_ID = "3005";//同步游客客源地数据(1天数据)
    private static String TOURIST_LOYALTY_KPI_ID = "4007";//同步游客忠诚度
    private static String TOURIST_RETENTION_TIME_KPI_ID = "2005";//同步游客滞留时间(1天数据)
    private static String TOURIST_CONTINUOUS_RETENTION_TIME_KPI_ID = "2007";//同步游客连续滞留时间(有月数据)

    private static String TOURIST_COUNT_MINUTE_KPI_TIME = "2017/11/20 00:00:00";
    private static String TOURIST_COUNT_DAY_KPI_TIME = "2017/11/19 00:00:00";
    private static String TOURIST_SOURCE_KPI_TIME = "2017/11/19";
    private static String TOURIST_LOYALTY_KPI_TIME = "2017/09";
    private static String TOURIST_RETENTION_TIME_KPI_TIME = "2017/11/19";
    private static String TOURIST_CONTINUOUS_RETENTION_TIME_KPI_TIME = "2017/09";

    private static String SUCCESS_CODE = "000000";


    @Autowired
    private QueryUtil queryUtil;
    @Autowired
    private CloudQueryRunner runner;

    private JSONObject initMethod() {
        String factoryModelName = "kpiMobileV1";
        JSONObject object;
        try {
            object = queryUtil.queryConfigByFactoryModelName(factoryModelName);
            url = object.getString("url");
            userName = object.getString("userName");
            password = object.getString("password");
        } catch (Exception e) {
            object = null;
            LOGGER.error("获取url,userName,password参数异常;", e);
        }
        return object;
    }

    /**
     * kpi接口请求公共方法
     *
     * @param kpi_id   KPI标识
     * @param spot_id  景点标识
     * @param date     KPI时间
     * @param url      接口方法
     * @param userName 用户名
     * @param password 密码
     * @return
     */

    public static JSONObject caseData(String kpi_id, String spot_id, String date, String url, String userName, String password) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String createdate = sdf.format(new Date());
        md.update(("MgbcX81qJzPgjTCjtceMRUf0d" + createdate + password).getBytes());
        String pass = com.drore.cloud.sdk.common.security.Base64.encode(md.digest());
        String authorization = "\"App_key\",Username=" + userName + ",PasswordDigest=" + pass
                + ",Nonce=MgbcX81qJzPgjTCjtceMRUf0d,Created=" + createdate + "";
        JSONObject jsonObject = new JSONObject();
        HttpPost httpPost = new HttpPost(url);
        Map<String, Object> nvps = new HashMap<String, Object>();
        nvps.put("spot_id", spot_id);
        nvps.put("kpi_id", kpi_id);
        nvps.put("kpi_time", date);
        String nvpstr = jsonObject.toJSONString(nvps);
        httpPost.setEntity(new StringEntity(nvpstr, "utf-8"));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", authorization);
        CloseableHttpResponse response2 = null;
        try {
            response2 = httpclient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity2 = response2.getEntity();
        String body2 = null;
        try {
            body2 = EntityUtils.toString(entity2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(body2);
        jsonObject = jsonObject.parseObject(body2);
        try {
            EntityUtils.consume(entity2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            response2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 同步游客人数(5分钟数据)
     */
    public void syncTouristNumberMinute5() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        String kpiTime = getTime(KpiTables.KPI_TOURIST_COUNT_MINUTE_DATA, TOURIST_COUNT_MINUTE_KPI_TIME);
        String nowTime = DateTimeUtil.getNowMinuteBefore5("yyyy/MM/dd HH:mm:ss");
        if (StringUtils.isEmpty(kpiTime)) return;
        List<TouristCount> collect = getTouristCount(TOURIST_COUNT_MINUTE_KPI_ID, kpiTime);
        if (collect.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(KpiTables.KPI_TOURIST_COUNT_MINUTE_DATA, JSON.toJSON(collect));
            if (null != insertBatch) {
                LOGGER.info("5分钟数据新增成功,kpiTime=" + kpiTime);
            } else {
                LOGGER.info("5分钟数据新增失败,kpiTime=" + kpiTime);
            }
        } else {
            LOGGER.info("5分钟数据为空;kpiTime=" + kpiTime);
        }
        if (kpiTime.compareTo(nowTime) < 0) {
            syncTouristNumberMinute5();
        }
    }


    /**
     * 同步游客人数(5分钟数据)
     */
    public void syncTouristNumberDay() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        String kpiTime = getTime(KpiTables.KPI_TOURIST_COUNT_DAY_DATA, TOURIST_COUNT_DAY_KPI_TIME);
        String nowTime = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd 00:00:00"));
        if (StringUtils.isEmpty(kpiTime)) return;
        List<TouristCount> collect = getTouristCount(TOURIST_COUNT_DAY_KPI_ID, kpiTime);
        if (collect.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(KpiTables.KPI_TOURIST_COUNT_DAY_DATA, JSON.toJSON(collect));
            if (null != insertBatch) {
                LOGGER.info("1天游客人数数据新增成功,kpiTime=" + kpiTime);
            } else {
                LOGGER.info("1天游客人数数据新增失败,kpiTime=" + kpiTime);
            }
        } else {
            LOGGER.info("1天游客人数数据为空;kpiTime=" + kpiTime);
        }
        if (kpiTime.compareTo(nowTime) < 0) {
            syncTouristNumberDay();
        }
    }

    private List<TouristCount> getTouristCount(String KpiId, String kpiTime) {
        String YKRS = url + "/zjydservice/YKRS/V1";
        List<TouristCount> collect = null;
        if (StringUtils.isEmpty(kpiTime)) return collect;
        List<KpiSpot> allSpots = getAllSpot();
        if (allSpots.isEmpty()) return collect;
        collect = allSpots.stream().map(spot -> {
            TouristCount touristCount = null;
            String spotNo = spot.getSpotNo();
            String spotName = spot.getSpotName();
            JSONObject object = caseData(KpiId, spotNo, kpiTime, YKRS, userName, password);
            if (object.isEmpty()) return touristCount;
            String code = object.getString("code");
            if (SUCCESS_CODE.equals(code)) {
                touristCount = new TouristCount();
                JSONArray result = object.getJSONArray("result");
                Integer kpiValue = result.getJSONObject(0).getInteger("kpi_value");
                touristCount.setKpiId(TOURIST_COUNT_MINUTE_KPI_ID);
                touristCount.setKpiTime(kpiTime);
                touristCount.setKpiValue(kpiValue);
                touristCount.setSpotNo(spotNo);
                touristCount.setSpotName(spotName);
                return touristCount;
            } else {
                LOGGER.info("code=" + code + ",message=" + object.getString("description"));
            }
            return touristCount;
        }).filter(touristCount -> null == touristCount).collect(Collectors.toList());
        return collect;
    }

    /**
     * 同步游客客源地
     */
    public void syncTouristOriginDay() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        String kpiTime = getTime(KpiTables.KPI_TOURIST_SOURCE_DAY, TOURIST_SOURCE_KPI_ID);
        String nowTime = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        if (StringUtils.isEmpty(kpiTime)) return;
        List<TouristOrigin> collect = getTouristOrigin(TOURIST_SOURCE_KPI_ID, kpiTime);
        if (collect.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(KpiTables.KPI_TOURIST_SOURCE_DAY, JSON.toJSON(collect));
            if (null != insertBatch) {
                LOGGER.info("1天游客客源地数据新增成功,kpiTime=" + kpiTime);
            } else {
                LOGGER.info("1天游客客源地数据新增失败,kpiTime=" + kpiTime);
            }
        } else {
            LOGGER.info("1天游客客源地数据为空;kpiTime=" + kpiTime);
        }
        if (kpiTime.compareTo(nowTime) < 0) {
            syncTouristOriginDay();
        }
    }


    private List<TouristOrigin> getTouristOrigin(String KpiId, String kpiTime) {
        String KYD = url + "/zjydservice/LYD/V1";
        List<TouristOrigin> collect = null;
        if (StringUtils.isEmpty(kpiTime)) return collect;
        List<KpiSpot> allSpots = getAllSpot();
        if (allSpots.isEmpty()) return collect;
        collect = allSpots.stream().map(spot -> {
            TouristOrigin touristOriginNull = null;
            String spotNo = spot.getSpotNo();
            String spotName = spot.getSpotName();
            JSONObject data = caseData(KpiId, spotNo, kpiTime, KYD, userName, password);
            if (data.isEmpty()) return touristOriginNull;
            String code = data.getString("code");
            if (SUCCESS_CODE.equals(code)) {
                JSONArray result = data.getJSONArray("result");
                result.stream().map(object -> (JSONObject) object).map(jsonObject -> {
                    TouristOrigin touristOrigin = new TouristOrigin();
                    Integer kpiValue = jsonObject.getInteger("kpi_value");
                    String kpiType = jsonObject.getString("kpi_type");
                    String kpiCity = jsonObject.getString("kpi_city");
                    touristOrigin.setKpiId(TOURIST_SOURCE_KPI_ID);
                    touristOrigin.setKpiTime(kpiTime);
                    touristOrigin.setKpiValue(kpiValue);
                    touristOrigin.setSpotNo(spotNo);
                    touristOrigin.setSpotName(spotName);
                    touristOrigin.setKpiCity(kpiCity);
                    touristOrigin.setKpiType(kpiType);
                    return touristOrigin;
                });
            } else {
                LOGGER.info("code=" + code + ",message=" + data.getString("description"));
            }
            return touristOriginNull;
        }).filter(touristCount -> null == touristCount).collect(Collectors.toList());
        return collect;
    }

    /**
     * 同步游客滞留时间
     */
    public void syncTouristsRetentionTimeDay() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        String kpiTime = getTime(KpiTables.KPI_TOURIST_RETENTION_TIME_DAY, TOURIST_RETENTION_TIME_KPI_ID);
        String nowTime = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        if (StringUtils.isEmpty(kpiTime)) return;
        List<TouristResidenceTime> collect = getTouristResidenceTime(TOURIST_RETENTION_TIME_KPI_ID, kpiTime);
        if (collect.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(KpiTables.KPI_TOURIST_RETENTION_TIME_DAY, JSON.toJSON(collect));
            if (null != insertBatch) {
                LOGGER.info("1天游客滞留时间数据新增成功,kpiTime=" + kpiTime);
            } else {
                LOGGER.info("1天游客滞留时间数据新增失败,kpiTime=" + kpiTime);
            }
        } else {
            LOGGER.info("1天游客滞留时间数据为空;kpiTime=" + kpiTime);
        }
        if (kpiTime.compareTo(nowTime) < 0) {
            syncTouristOriginDay();
        }
    }

    private List<TouristResidenceTime> getTouristResidenceTime(String KpiId, String kpiTime) {
        String KYD = url + "/zjydservice/LYD/V1";
        List<TouristResidenceTime> collect = null;
        if (StringUtils.isEmpty(kpiTime)) return collect;
        List<KpiSpot> allSpots = getAllSpot();
        if (allSpots.isEmpty()) return collect;
        collect = allSpots.stream().map(spot -> {
            TouristResidenceTime touristResidenceTimeNull = null;
            String spotNo = spot.getSpotNo();
            String spotName = spot.getSpotName();
            JSONObject data = caseData(KpiId, spotNo, kpiTime, KYD, userName, password);
            if (data.isEmpty()) return touristResidenceTimeNull;
            String code = data.getString("code");
            if (SUCCESS_CODE.equals(code)) {
                JSONArray result = data.getJSONArray("result");
                result.stream().map(object -> (JSONObject) object).map(jsonObject -> {
                    Integer kpiHour = jsonObject.getInteger("kpi_hour");
                    Integer kpiValue = jsonObject.getInteger("kpi_value");
                    TouristResidenceTime touristResidenceTime = new TouristResidenceTime();
                    touristResidenceTime.setSpotNo(spotNo);
                    touristResidenceTime.setSpotName(spotName);
                    touristResidenceTime.setKpiHour(kpiHour);
                    touristResidenceTime.setKpiValue(kpiValue);
                    touristResidenceTime.setKpiTime(kpiTime);
                    return touristResidenceTime;
                });
            } else {
                LOGGER.info("code=" + code + ",message=" + data.getString("description"));
            }
            return touristResidenceTimeNull;
        }).filter(touristCount -> null == touristCount).collect(Collectors.toList());
        return collect;
    }

    /**
     * 同步连续滞留时间
     */
    public void syncTouristsContinuousResidenceTimeMonth() {
        if (StringUtils.isEmpty(url)) {
            JSONObject object = initMethod();
            if (object.isEmpty()) return;
        }
        String kpiTime = getTime(KpiTables.KPI_TOURIST_CONTINUOUS_RETENTION_TIME, TOURIST_CONTINUOUS_RETENTION_TIME_KPI_ID);
        String nowTime = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy/MM"));
        if (StringUtils.isEmpty(kpiTime)) return;
        List<TouristContinuousDetentionTime> collect = getTouristContinuousResidenceTime(TOURIST_CONTINUOUS_RETENTION_TIME_KPI_ID, nowTime);
        if (collect.size() > 0) {
            RestMessage insertBatch = runner.insertBatch(KpiTables.KPI_TOURIST_CONTINUOUS_RETENTION_TIME, JSON.toJSON(collect));
            if (null != insertBatch) {
                LOGGER.info("1个月游客滞留时间数据新增成功,kpiTime=" + kpiTime);
            } else {
                LOGGER.info("1个月游客滞留时间数据新增失败,kpiTime=" + kpiTime);
            }
        } else {
            LOGGER.info("1个月游客滞留时间数据为空;kpiTime=" + kpiTime);
        }
        if (kpiTime.compareTo(nowTime) < 0) {
            syncTouristOriginDay();
        }
    }

    private List<TouristContinuousDetentionTime> getTouristContinuousResidenceTime(String KpiId, String kpiTime) {
        String LXZLSJ = url + "/zjydservice/LXZLSJ/V1";
        List<TouristContinuousDetentionTime> collect = null;
        if (StringUtils.isEmpty(kpiTime)) return collect;
        List<KpiSpot> allSpots = getAllSpot();
        if (allSpots.isEmpty()) return collect;
        collect = allSpots.stream().map(spot -> {
            TouristContinuousDetentionTime touristContinuousDetentionTimeNull = null;
            String spotNo = spot.getSpotNo();
            String spotName = spot.getSpotName();
            JSONObject data = caseData(KpiId, spotNo, kpiTime, LXZLSJ, userName, password);
            if (data.isEmpty()) return touristContinuousDetentionTimeNull;
            String code = data.getString("code");
            if (SUCCESS_CODE.equals(code)) {
                JSONArray result = data.getJSONArray("result");
                result.stream().map(object -> (JSONObject) object).map(jsonObject -> {
                    Integer kpiDay = jsonObject.getInteger("kpi_day");
                    Integer kpiValue = jsonObject.getInteger("kpi_value");
                    TouristContinuousDetentionTime touristContinuousDetentionTime = new TouristContinuousDetentionTime();
                    touristContinuousDetentionTime.setSpotNo(spotNo);
                    touristContinuousDetentionTime.setSpotName(spotName);
                    touristContinuousDetentionTime.setKpiDay(kpiDay);
                    touristContinuousDetentionTime.setKpiValue(kpiValue);
                    touristContinuousDetentionTime.setKpiTime(kpiTime);
                    return touristContinuousDetentionTime;
                });
            } else {
                LOGGER.info("code=" + code + ",message=" + data.getString("description"));
            }
            return touristContinuousDetentionTimeNull;
        }).filter(touristCount -> null == touristCount).collect(Collectors.toList());
        return collect;
    }

    /**
     * 获取所有的景区
     *
     * @return
     */
    public List<KpiScenic> getAllScenic() {
        RequestExample example = new RequestExample(Integer.MAX_VALUE, 1);
        Pagination<KpiScenic> pagination = runner.queryListByExample(KpiScenic.class, KpiTables.KPI_SCENIC, example);
        List<KpiScenic> data = pagination.getData();
        return data;
    }

    /**
     * 获取所有的景点
     *
     * @return
     */
    public List<KpiSpot> getAllSpot() {
        RequestExample example = new RequestExample(Integer.MAX_VALUE, 1);
        List<KpiSpot> data = null;
        try {
            Pagination<KpiSpot> pagination = runner.queryListByExample(KpiSpot.class, KpiTables.KPI_SPOT, example);
            data = pagination.getData();
        } catch (Exception e) {
            LOGGER.error("获取景点列表异常,error=", e);
        }
        return data;
    }

    public String getTime(String tableName, String kpiId) {
        String kpiTime = null;
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("select max(sync_time) as syncTime from ").append(tableName).append(" where is_deleted='N'");
            Pagination<TouristCount> pagination = runner.sql(TouristCount.class, buffer.toString());
            if (pagination.getCount() > 0) {
                kpiTime = pagination.getData().get(0).getKpiTime();
                switch (kpiId) {
                    case "1001":
                        DateTimeFormatter dateFormat = DateTimeUtil.getDateFormat("yyyy/MM/dd HH:mm:ss");
                        kpiTime = LocalDateTime.parse(kpiTime, dateFormat).plusMinutes(5).format(dateFormat);
                        break;
                    case "1005":
                        dateFormat = DateTimeUtil.getDateFormat("yyyy/MM/dd 00:00:00");
                        kpiTime = LocalDateTime.parse(kpiTime, dateFormat).plusDays(1).format(dateFormat);
                        break;
                    case "3005":
                    case "2005":
                        dateFormat = DateTimeUtil.getDateFormat("yyyy/MM/dd");
                        kpiTime = LocalDateTime.parse(kpiTime, dateFormat).plusDays(1).format(dateFormat);
                        break;
                    case "2007":
                    case "4007":
                        dateFormat = DateTimeUtil.getDateFormat("yyyy/MM");
                        kpiTime = LocalDateTime.parse(kpiTime, dateFormat).plusMonths(1).format(dateFormat);
                        break;
                    default:
                        throw new BusinessException("kpiId 不存在");
                }
            } else {
                switch (kpiId) {
                    case "1001":
                        //DateTimeUtil.firstDayOfLastMonth("yyyy/MM/dd 00:00:00");
                        kpiTime = TOURIST_COUNT_MINUTE_KPI_TIME;
                        break;
                    case "1005":
                        kpiTime = TOURIST_COUNT_DAY_KPI_TIME;
                        break;
                    case "3005":
                        kpiTime = TOURIST_SOURCE_KPI_TIME;
                        break;
                    case "2005":
                        kpiTime = TOURIST_RETENTION_TIME_KPI_TIME;
                        break;
                    case "2007":
                        kpiTime = TOURIST_CONTINUOUS_RETENTION_TIME_KPI_TIME;
                        break;
                    default:
                        throw new BusinessException("kpiId 不存在");
                }
            }
            return kpiTime;
        } catch (BusinessException e) {
            LOGGER.error("获取kpiTime异常,error=", e);
        }
        return kpiTime;
    }

    public static void main(String[] args) {
        String nowTime = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy/MM"));
        System.out.println("nowTime = " + nowTime);
    }
}
