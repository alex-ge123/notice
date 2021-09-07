package com.wafersystems.notice.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket客户端
 * 
 * @author dell
 *
 */
@Slf4j
@Component
public class SMSClientSocket {

	// socket连接超时时间常量
	private static int ENV_CONNECT_TIMEOUT = 5000;

	// socket读取超时时间常量
	private static int ENV_READ_TIMEOUT = 10000;


	public void sendSMSMessageInfo(String ip,int port,String phone,String content) {
		// 封装查询对象
		String queryXMLStr = sendSMSMessage( phone,content);
		String queryEnvDeviceInfoStr = null;
		// 获取查询数据
		if (StringUtils.isNotEmpty(queryXMLStr)) {
			log.info("发送短信数据报文:"+queryXMLStr);
			Socket sk = null;
			DataOutputStream outputStream = null;
			DataInputStream inputStream = null;
			try {
				sk = new Socket();
				sk.connect(new InetSocketAddress(ip, port), ENV_CONNECT_TIMEOUT);
				sk.setSoTimeout(ENV_READ_TIMEOUT);
				
				outputStream = new DataOutputStream(sk.getOutputStream());

				outputStream.write(queryXMLStr.getBytes());
				// 接收返回数据
				inputStream = new DataInputStream(sk.getInputStream());
				byte[] readStreamByte = SMSClientSocket.readStreamToByteArray(inputStream);
				if (readStreamByte != null) {
					queryEnvDeviceInfoStr = new String(readStreamByte);
					log.info("电话号码" + StrUtil.hide(phone, phone.length() - 4, phone.length()) + "接收短信数据实时报文:"+queryEnvDeviceInfoStr);
				}
			} catch (IOException e) {
				log.error("电话号码" + StrUtil.hide(phone, phone.length() - 4, phone.length()) + "发送短信报错! ",e);
			} finally {
				SMSClientSocket.closeUtil(inputStream, outputStream, sk);
			}
		}
	}

  private String sendSMSMessage(String phone,String content) {
//	  "SM20210820102523557""手机号""短信内容"
	  StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"GBK\"?><Comm Name=\"C200\"><PkgDef Name=\"TRNHEAD\"><DataField Name=\"TRCD\" Value=\"C200\"/><DataField Name=\"Timeout\" Value=\"\"/><DataField Name=\"TLSQ\" Value=\"\"/>" +
      "<DataField Name=\"UTNO\" Value=\"0100\"/><DataField Name=\"SBNO\" Value=\"0100\"/><DataField Name=\"TELR\" Value=\"010100\"/><DataField Name=\"TRNTYPE\" Value=\"\"/><DataField Name=\"TRDT\" Value=\"\"/><DataField Name=\"TRTM\" Value=\"\"/><DataField Name=\"DEVNO\" Value=\"\"/><DataField Name=\"BRIEFCODE\" Value=\"\"/><DataField Name=\"MEMO\" Value=\"0\"/></PkgDef>" +
      "<PkgDef Name=\"C200Req\"><DataField Name=\"TASKID\" Value=\"");
	  sb.append("SM"+ DateUtil.format(DateUtil.date(),DatePattern.PURE_DATETIME_MS_FORMAT));
      sb.append("\" /><DataField Name=\"ACCOUNTNO\" Value=\"\" /><DataField Name=\"MSGCLASSID\" Value=\"10000002\" /><DataField Name=\"APPID\" Value=\"10000002\" /><DataField Name=\"ADDRESSTYPE\" Value=\"0\" /><DataField Name=\"ADDRESS\" Value=\"");
      sb.append(phone);
    sb.append("\" /><DataField Name=\"TAMPLATEFLAG\" Value=\"1\" /><DataField Name=\"MSGCONTENT\" Value=\"");
    sb.append(content);
    sb.append("\" /><DataField Name=\"INURETIME\" Value=\"0\" /><DataField Name=\"AVAILHOURS\" Value=\"0\" /></PkgDef></Comm>");
    return sb.toString();
	}

//  public static void main(String[] args) {
//	  EnvClientSocket send = new EnvClientSocket();
//    System.out.println(send.sendSMSMessage("+8617712349876","你好"));
//  }


	/**
	 * 读取字节流数据
	 * 
	 * @param inStream
	 *            字节流
	 * @return 缓存的字节数据
	 * @throws IOException
	 *             异常
	 * 
	 */
	private static byte[] readStreamToByteArray(InputStream inStream) throws IOException {
		if (null == inStream) {
			return null;
		}

		int count = 0;
		// 设置5秒超时时间
		int readCount = 0;
		while (count == 0) {
			count = inStream.available();
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
			if (readCount > 5) {
				return null;
			}
			readCount++;
		}
		byte[] b = new byte[count];
		int i = inStream.read(b);
		if(i==count)
			return b;
		return b;
	}

	/**
	 * 资源关闭工具类
	 * 
	 * @param inputStream
	 *            输入流
	 * @param outputStream
	 *            输出流
	 * @param sk
	 *            socket
	 */
	private static void closeUtil(DataInputStream inputStream, DataOutputStream outputStream, Socket sk) {

		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				log.error("inputStream io异常 ", e);
			}
		}

		if (outputStream != null) {
			try {
				outputStream.close();
			} catch (IOException e) {
        log.error("outputStream io异常 ", e);
			}
		}

		if (sk != null) {
			try {
				sk.close();
			} catch (IOException e) {
        log.error("sk io异常 ", e);
			}
		}
	}

}
