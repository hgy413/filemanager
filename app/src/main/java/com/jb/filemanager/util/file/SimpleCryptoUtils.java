package com.jb.filemanager.util.file;

import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;


/**
 * 简单加解密工具类
 * @author zhanghuijun
 */
public class SimpleCryptoUtils {
	
	/**
	 * 解密处理
	 * @param hex 需要进行解密的字符串
	 * @return
	 */
	public static String fromHex(String hex) {
		if (TextUtils.isEmpty(hex)) {
			return hex;
		}
		return new String(toByte(hex));
	}
	/**
	 * 
	 * @param hexString
	 * @return
	 */
	public static byte[] toByte(String hexString) {
		int len = hexString.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
		}
		return result;
	}
	/**
	 * 将InputStream解压缩成String
	 * 
	 * @param inStream
	 * @return
	 */
	public static String unzipData(InputStream inStream) {
		try {
			byte[] old_bytes = toByteArray(inStream);
			byte[] new_bytes = ungzip(old_bytes);
			return new String(new_bytes, "utf-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	/**
	 * 将inputStream转换成byte[]
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		FileUtil.copy(input, output);
		return output.toByteArray();
	}
	/**
	 * 将byte[]进行解压缩
	 * @param bs
	 * @return
	 * @throws Exception
	 */
	public static byte[] ungzip(byte[] bs) throws Exception {
		GZIPInputStream gzin = null;
		ByteArrayInputStream bin = null;
		try {
			bin = new ByteArrayInputStream(bs);
			gzin = new GZIPInputStream(bin);
			return toByteArray(gzin);
		} catch (Exception e) {
			throw e;
		} finally {
			if (bin != null) {
				bin.close();
			}
			if (gzin != null) {
				gzin.close();
			}
		}
	}
}