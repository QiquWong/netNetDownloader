package netdown;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * threadId:开启的每条线程的id
 * block：每条线程下载的数据量
 * url	：下载原地址
 * file	：要保存的本地文件
 * run() 执行下载
 * conn.setRequestMethod("GET")
 * conn.setRequestProperty(Range:范围,开始位置-结束位置)
 * InputStream inStream = conn.getInputStream(); 从conn获得 输入流
 * inStream.read();
 */
public class DownloadThread extends Thread{
	int threadId; 
	int block;
	URL url; 
	File file;
	public DownloadThread(int threadId, int block, URL url, File file) {
		this.threadId = threadId;
		this.block = block;
		this.url = url;
		this.file = file;
	}
	public void run() {
		int start = threadId * block;//每条线程开始下载位置
		int end = (threadId+1) * block -1;//每条线程结束下载位置
		//线程数据写入
		try {
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			accessFile.seek(start);//文件指针指向start位置开始写入
			//发起请求
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET"); 
			conn.setRequestProperty("Range", "bytes="+start+"-"+end);
			//分段下载请求码是206
			if(conn.getResponseCode() == 206){
			System.out.println(conn.getResponseCode());
				//从conn获得 输入流
				InputStream inStream = conn.getInputStream();
				
				byte[] buffer = new byte[1024];//缓冲区
				int len = 0;//起始0 --- 缓冲区读取结束
				while( (len = inStream.read(buffer)) != -1){
					accessFile.write(buffer, 0, len);//buffer 写入 文件
				}
				accessFile.close();
				inStream.close();
			}
			System.out.println("Thread:"+(threadId+1)+"下载完成!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.run();
	}
}
