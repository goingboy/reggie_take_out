//package com.zsx.reggie.utils;
//
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
//import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.profile.DefaultProfile;
//
///**
// * 短信发送工具类
// */
//public class SMSUtils {
//
//	/**
//	 * 发送短信
//	 * @param signName 签名
//	 * @param templateCode 模板
//	 * @param phoneNumbers 手机号
//	 * @param param 参数
//	 */
//	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
//		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
//		IAcsClient client = new DefaultAcsClient(profile);
//
//		SendSmsRequest request = new SendSmsRequest();
//		request.setSysRegionId("cn-hangzhou");
//		request.setPhoneNumbers(phoneNumbers);
//		request.setSignName(signName);
//		request.setTemplateCode(templateCode);
//		request.setTemplateParam("{\"code\":\""+param+"\"}");
//		try {
//			SendSmsResponse response = client.getAcsResponse(request);
//			System.out.println("短信发送成功");
//		}catch (ClientException e) {
//			e.printStackTrace();
//		}
//	}
//
//}

// This file is auto-generated, don't edit it. Thanks.


package com.zsx.reggie.utils;

import com.aliyun.tea.*;
import org.springframework.beans.factory.annotation.Value;

public class SMSUtils {

    /**
     * accessId
     */
    @Value("${aliyun.sms.accessId}")
    private String accessId;

    /**
     * accessKey
     */
    @Value("${aliyun.sms.accessKey}")
    private String accessKey;

    /**
     * 短信签名
     */
    @Value("${aliyun.sms.signName}")
    private String signName;

    /**
     * 短信模板
     */
    @Value("${aliyun.sms.templateCode}")
    private String templateCode;

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public void sendSms(String phoneNumbers, String code) throws Exception {
        com.aliyun.dysmsapi20170525.Client client = createClient(accessId, accessKey);
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(phoneNumbers)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(code);
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.sendSmsWithOptions(sendSmsRequest, new com.aliyun.teautil.models.RuntimeOptions());
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }
}


