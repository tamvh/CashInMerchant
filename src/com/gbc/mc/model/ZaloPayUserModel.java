/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.model;

import com.gbc.mc.common.AppConst;
import com.gbc.mc.common.CommonFunction;
import com.gbc.mc.common.CommonModel;
import com.gbc.mc.common.Config;
import com.gbc.mc.common.HttpHelper;
import com.gbc.mc.hmacutil.HMACUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author tamvh
 */
public class ZaloPayUserModel {
    private static ZaloPayUserModel _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected static final Logger logger = Logger.getLogger(CashInModel.class);
    private static final Gson gson = new Gson();
    private static String serverUrl = Config.getParam("zalopayserver", "url");

    public static ZaloPayUserModel getInstance() throws IOException {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new ZaloPayUserModel();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }
    public String getUserInfoByZpId(String zalopay_id) {
        String content = null;
        int ret = AppConst.ERROR_GENERIC;
        try {
            long currentTime = CommonFunction.getCurrentDateTimeNum();
            String mid = AppConst.MID;
            String zalopayname = zalopay_id;
            String reqdate = String.valueOf(currentTime);
            String hmac = HMACUtil.HMacHexStringEncode("HmacSHA256", AppConst.HMAC_SHA256_KEY,
                            zalopayname + "|" +
                            mid + "|" +
                            reqdate);
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("mid", mid);
            params.put("zalopayname", zalopayname);
            params.put("reqdate", reqdate);
            params.put("mac", hmac);
            String gsetUserInfoUrl = serverUrl + "getuserinfo";
            String rs = HttpHelper.sendHttpPostFormData(gsetUserInfoUrl, params, 10000);
            content = rs;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ZaloPayUserModel.class.getName()).log(Level.SEVERE, null, ex);
            content = CommonModel.FormatResponseFromZp(ret, ex.getMessage());
        }
        return content;
    }
     public String getUserInfoByPhone(String phone_no) {
        String content = null;
        int ret = AppConst.ERROR_GENERIC;
        try {
            long currentTime = CommonFunction.getCurrentDateTimeNum();
            String mid = AppConst.MID;
            String phone = phone_no;
            String reqdate = String.valueOf(currentTime);
            String hmac = HMACUtil.HMacHexStringEncode("HmacSHA256", AppConst.HMAC_SHA256_KEY,
                            phone + "|" +
                            mid + "|" +
                            reqdate);
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("mid", mid);
            params.put("phone", phone);
            params.put("reqdate", reqdate);
            params.put("mac", hmac);
            String gsetUserInfoUrlByPhone = serverUrl + "getuserinfobyphone";
            String rs = HttpHelper.sendHttpPostFormData(gsetUserInfoUrlByPhone, params, 10000);
            content = rs;
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ZaloPayUserModel.class.getName()).log(Level.SEVERE, null, ex);
            content = CommonModel.FormatResponseFromZp(ret, ex.getMessage());
        }
        return content;
    }
}
