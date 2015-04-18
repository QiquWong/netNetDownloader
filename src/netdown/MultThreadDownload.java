package netdown;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultThreadDownload {
	
	public static void main(String[] args) {
		String path = "http://192.168.1.4:8080/apache-tomcat-8.0.11-windows-i64.zip";
		new MultThreadDownload().download(path,3);
	}

	/**
	 * 下载文件 内部方法
	 * @param path 网络文件路径
	 * @param ThreadCount 开启几条线程
	 * DownloadThread 创建的线程calss
	 */
	private void download(String path,int ThreadCount) {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET"); 
			if(conn.getResponseCode() == 200){
				//API获取服务器返回下载的文件的大小,getContentLength();
				int  length = conn.getContentLength();
				//本地生成与网络文件相同大小的文件
				File file = new File(getFilename(path));
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(length);//本地生成文件
				accessFile.close();
				//计算每条线程下载的数据量
				int block = length%ThreadCount == 0 ? length/ThreadCount : length/ThreadCount+1;
				for (int ThreadId = 0; ThreadId < ThreadCount; ThreadId++) {
					new DownloadThread(ThreadId,block,url,file).start();
				}
			}else{
				System.out.println("下载失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param path 全路径
	 * @return path 下载文件名称(过滤掉http地址，取得下载文件名称)
	 */
	private String getFilename(String path) {
		return path.substring(path.lastIndexOf("/")+1);
	}
}
