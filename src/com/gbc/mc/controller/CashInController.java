/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gbc.mc.controller;

import com.gbc.mc.common.AppConst;
import com.gbc.mc.common.CommonModel;
import com.gbc.mc.common.JsonParserUtil;
import com.gbc.mc.model.CashInModel;
import com.google.gson.JsonObject;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tamvh
 */
public class CashInController extends HttpServlet {
    protected final Logger logger = Logger.getLogger(this.getClass());
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }
    
    private void handle(HttpServletRequest req, HttpServletResponse resp) {
        try {
            processs(req, resp);
        } catch (IOException ex) {
            logger.error(getClass().getSimpleName() + ".handle: " + ex.getMessage(), ex);
        }
    }

    private void processs(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = (req.getPathInfo() != null) ? req.getPathInfo() : "";
        String cmd = req.getParameter("cm") != null ? req.getParameter("cm") : "";
        String data = req.getParameter("dt") != null ? req.getParameter("dt") : "";
        String content = "";
        logger.info("CashInController.processs, pathInfo:   " + pathInfo);
        logger.info("CashInController.processs, cmd:        " + cmd);
        logger.info("CashInController.processs, data:       " + data);
        CommonModel.prepareHeader(resp, CommonModel.HEADER_JS);
        switch (cmd) {            
            case "transfercash":
                content = transfercash(req, data);
                break;
            case "transfercashtype":
                content = transfercashtype(req, data);
        }
        CommonModel.out(content, resp);
    }

    private String transfercash(HttpServletRequest req, String data) {
        String content = null;
        int ret = AppConst.ERROR_GENERIC;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {                
                String zpid = jsonObject.get("zpid").getAsString();
                String amount = jsonObject.get("amount").getAsString();
                int transfer_type = jsonObject.get("transfer_type").getAsInt();
                String client_id = jsonObject.get("client_id").getAsString();
                if (zpid.isEmpty() || amount.isEmpty() || transfer_type > 0 || client_id.isEmpty()) {
                    content = CommonModel.FormatResponse(ret, "Invalid parameter");
                } else {
                    content = CashInModel.getInstance().transferCash(zpid, amount);
                }
            }
        } catch (Exception ex) {
            logger.error(getClass().getSimpleName() + ".transfercash: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }
        return content;
    }

    private String transfercashtype(HttpServletRequest req, String data) {
        String content = null;
        int ret = AppConst.ERROR_GENERIC;
        try {
            JsonObject jsonObject = JsonParserUtil.parseJsonObject(data);
            if (jsonObject == null) {
                content = CommonModel.FormatResponse(ret, "Invalid parameter");
            } else {                
                String phone_number = jsonObject.get("phone_number").getAsString();
                String amount = jsonObject.get("amount").getAsString();
                int transfer_type = jsonObject.get("transfer_type").getAsInt();
                String client_id = jsonObject.get("client_id").getAsString();
                if (phone_number.isEmpty() || amount.isEmpty() || transfer_type <= 0 || client_id.isEmpty()) {
                    content = CommonModel.FormatResponse(ret, "Invalid parameter");
                } else {
                    content = CashInModel.getInstance().transferCashByType(phone_number, amount);
                }
            }
        } catch (Exception ex) {
            logger.error(getClass().getSimpleName() + ".transfercashtype: " + ex.getMessage(), ex);
            content = CommonModel.FormatResponse(ret, ex.getMessage());
        }
        return content;
    }
}
