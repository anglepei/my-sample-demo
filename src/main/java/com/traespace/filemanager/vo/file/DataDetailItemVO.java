package com.traespace.filemanager.vo.file;

import java.util.Map;

/**
 * 数据详情项VO
 *
 * @author Traespace
 * @since 2024-03-17
 */
public class DataDetailItemVO {

    /**
     * 序号
     */
    private String seqNo;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 自定义字段
     */
    private Map<String, String> customFields;

    public DataDetailItemVO() {
    }

    public DataDetailItemVO(String seqNo, String idCard, String phone, Map<String, String> customFields) {
        this.seqNo = seqNo;
        this.idCard = idCard;
        this.phone = phone;
        this.customFields = customFields;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public DataDetailItemVO setSeqNo(String seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    public String getIdCard() {
        return idCard;
    }

    public DataDetailItemVO setIdCard(String idCard) {
        this.idCard = idCard;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public DataDetailItemVO setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public DataDetailItemVO setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
        return this;
    }
}
