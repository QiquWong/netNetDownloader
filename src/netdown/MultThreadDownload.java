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
	 * �����ļ� �ڲ�����
	 * @param path �����ļ�·��
	 * @param ThreadCount ���������߳�
	 * DownloadThread �������߳�calss
	 */
	private void download(String path,int ThreadCount) {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET"); 
			if(conn.getResponseCode() == 200){
				//API��ȡ�������������ص��ļ��Ĵ�С,getContentLength();
				int  length = conn.getContentLength();
				//���������������ļ���ͬ��С���ļ�
				File file = new File(getFilename(path));
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(length);//���������ļ�
				accessFile.close();
				//����ÿ���߳����ص�������
				int block = length%ThreadCount == 0 ? length/ThreadCount : length/ThreadCount+1;
				for (int ThreadId = 0; ThreadId < ThreadCount; ThreadId++) {
					new DownloadThread(ThreadId,block,url,file).start();
				}
			}else{
				System.out.println("����ʧ�ܣ�");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @param path ȫ·��
	 * @return path �����ļ�����(���˵�http��ַ��ȡ�������ļ�����)
	 */
	private String getFilename(String path) {
		return path.substring(path.lastIndexOf("/")+1);
	}
}
