package com.huosuapp.text.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ChannelUtil {
	public static final String AGENT_FILE = "META-INF/gamechannel";
		public static final String AGENT_FILE2 = "META-INF/huosdk";

	public static String getChannel(Context context,String fileName) {
		// 从配置文件中 找到文件
		ApplicationInfo appinfo = context.getApplicationInfo();
		String sourceDir = appinfo.sourceDir;

		String ret = "";
		ZipFile zipfile = null;
		String channelId = "";
		try {
			zipfile = new ZipFile(sourceDir);
			Enumeration<?> entries = zipfile.entries();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while (entries.hasMoreElements()) {
				ZipEntry entry = ((ZipEntry) entries.nextElement());

				String entryName = entry.getName();
				if (entryName.startsWith(fileName)) {
					ret = entryName;
					InputStream is = zipfile.getInputStream(entry);
					try {
						byte[] buffer = new byte[1024];
						int num = 0;
						while (-1 != (num = is.read(buffer))) {
							bos.write(buffer, 0, num);
							bos.flush();
						}
						String jsonStr = bos.toString("utf-8");
						bos.close();
						is.close();
						JSONObject o = new JSONObject(jsonStr);
						channelId = o.getString("agentgame");
						return channelId;
					} catch (Exception e) {
						e.printStackTrace();
						bos.close();
						is.close();
					}
					
				}
			}
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zipfile != null) {
				try {
					zipfile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return channelId;
	}
}
