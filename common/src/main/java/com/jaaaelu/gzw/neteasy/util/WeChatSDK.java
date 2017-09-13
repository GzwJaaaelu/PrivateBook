package com.jaaaelu.gzw.neteasy.util;

import com.jaaaelu.gzw.neteasy.common.Common;
import com.jaaaelu.gzw.neteasy.common.app.PrivateBookApplication;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Gzw on 2017/9/11 0011.
 */

public class WeChatSDK {
    private static int mTargetScene = SendMessageToWX.Req.WXSceneSession;
    private static IWXAPI sApi;

    static {
        sApi = WXAPIFactory.createWXAPI(PrivateBookApplication.getInstance().getApplicationContext(), Common.Constance.WE_CHAT_APP_ID);
        sApi.registerApp(Common.Constance.WE_CHAT_APP_ID);
    }

    public static void shardText(String text) {
        WXTextObject wxText = new WXTextObject(text);

        WXMediaMessage message = new WXMediaMessage(wxText);
        message.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = message;
        req.scene = mTargetScene;
        sApi.sendReq(req);
    }

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
}
