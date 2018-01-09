/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.model;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import com.gbc.mc.common.AppConst;
import com.gbc.mc.common.CommonFunction;
import com.gbc.mc.common.CommonModel;
import com.gbc.mc.common.Config;
import com.gbc.mc.common.HttpHelper;
import com.gbc.mc.hmacutil.HMACUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

/**
 *
 * @author tamvh
 */
public class CashInModel {

    private static CashInModel _instance = null;
    private static final Lock createLock_ = new ReentrantLock();
    protected static final Logger logger = Logger.getLogger(CashInModel.class);
    private static final Gson gson = new Gson();
    private static String serverUrl = Config.getParam("zalopayserver", "url");

    public static CashInModel getInstance() throws IOException {
        if (_instance == null) {
            createLock_.lock();
            try {
                if (_instance == null) {
                    _instance = new CashInModel();
                }
            } finally {
                createLock_.unlock();
            }
        }
        return _instance;
    }

    public String transferCash(String zpid, String amout, String transfer_type, String invoice_code) {
        String content;
        int ret = AppConst.ERROR_GENERIC;
        try {
            long currentTime = CommonFunction.getCurrentDateTimeNum();
            String mid = AppConst.MID;            
            String mtransid = CommonFunction.getCurrentDayFormat() + "_" + mid + "_" + invoice_code;
            String mzalopayid = AppConst.M_ZALOPAY_ID;
            String mzalopaypin = AppConst.M_ZALOPAY_P_IN;
            String receiverzalopayname = zpid;
            String amount = amout;
            String reqdate = String.valueOf(currentTime);
            String description = AppConst.TRANSFER_DESC;
            String hmac = HMACUtil.HMacHexStringEncode("HmacSHA256", AppConst.HMAC_SHA256_KEY,
                    mtransid + "|"
                    + mid + "|"
                    + mzalopayid + "|"
                    + mzalopaypin + "|"
                    + receiverzalopayname + "|"
                    + amount + "|"
                    + reqdate + "|"
                    + description);
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("mtransid", mtransid);
            params.put("mid", mid);
            params.put("mzalopayid", mzalopayid);
            params.put("mzalopaypin", mzalopaypin);
            params.put("receiverzalopayname", receiverzalopayname);
            params.put("amount", amount);
            params.put("reqdate", reqdate);
            params.put("description", description);
            params.put("mac", hmac);
            String url_transfer_cash = serverUrl + "transfercash";
            String rs = HttpHelper.sendHttpPostFormData(url_transfer_cash, params, 10000);
            content = rs;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CashInModel.class.getName()).log(Level.SEVERE, null, ex);
            content = CommonModel.FormatResponseFromZp(ret, ex.getMessage());
        }
        return content;
    }
    
    public String transferCashByType(String phone_no, String amout, String transfer_type, String invoice_code) {
        String content;
        int ret = AppConst.ERROR_GENERIC;
        try {
            long currentTime = CommonFunction.getCurrentDateTimeNum();
            String mid = AppConst.MID;
            String mtransid = CommonFunction.getCurrentDayFormat() + "_" + mid + "_" + invoice_code;
            String mzalopayid = AppConst.M_ZALOPAY_ID;
            String mzalopaypin = AppConst.M_ZALOPAY_P_IN;
            String receiver = phone_no;
            String amount = amout;
            String reqdate = String.valueOf(currentTime);
            String description = AppConst.TRANSFER_DESC;
            String hmac = HMACUtil.HMacHexStringEncode("HmacSHA256", AppConst.HMAC_SHA256_KEY,
                    mtransid + "|"
                    + mid + "|"
                    + mzalopayid + "|"
                    + mzalopaypin + "|"
                    + receiver + "|"
                    + amount + "|"
                    + reqdate + "|"
                    + description);
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("mtransid", mtransid);
            params.put("mid", mid);
            params.put("mzalopayid", mzalopayid);
            params.put("mzalopaypin", mzalopaypin);
            params.put("type", transfer_type);
            params.put("receiver", receiver);
            params.put("amount", amount);
            params.put("reqdate", reqdate);
            params.put("description", description);
            params.put("mac", hmac);
            String transerByTypeURL = serverUrl + "transfercashbytype";
            String rs = HttpHelper.sendHttpPostFormData(transerByTypeURL, params, 10000);
            content = rs;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CashInModel.class.getName()).log(Level.SEVERE, null, ex);
            content = CommonModel.FormatResponseFromZp(ret, ex.getMessage());
        }
        return content;
    }
    
    public String getTransferStatus(String zptransid) {
        String content;
        int ret = AppConst.ERROR_GENERIC;
        try {
            long currentTime = CommonFunction.getCurrentDateTimeNum();
            String mid = AppConst.MID;
            String mzalopayid = AppConst.M_ZALOPAY_ID;
            String reqdate = String.valueOf(currentTime);
            String hmac = HMACUtil.HMacHexStringEncode("HmacSHA256", AppConst.HMAC_SHA256_KEY,
                            mid + "|" +
                            mzalopayid + "|" +
                            zptransid + "|" +
                            reqdate);
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("mid", mid);
            params.put("mzalopayid", mzalopayid);
            params.put("zptransid", zptransid);
            params.put("time", reqdate);
            params.put("mac", hmac);
            String getStatusUrl = serverUrl + "gettransferstatus";
            String rs = HttpHelper.sendHttpPostFormData(getStatusUrl, params, 10000);
            content = rs;
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(CashInModel.class.getName()).log(Level.SEVERE, null, ex);
            content = CommonModel.FormatResponseFromZp(ret, ex.getMessage());
        }
        return content;
    }
    
    
}
