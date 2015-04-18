package netdown;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * threadId:������ÿ���̵߳�id
 * block��ÿ���߳����ص�������
 * url	������ԭ��ַ
 * file	��Ҫ����ı����ļ�
 * run() ִ������
 * conn.setRequestMethod("GET")
 * conn.setRequestProperty(Range:��Χ,��ʼλ��-����λ��)
 * InputStream inStream = conn.getInputStream(); ��conn��� ������
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
		int start = threadId * block;//ÿ���߳̿�ʼ����λ��
		int end = (threadId+1) * block -1;//ÿ���߳̽�������λ��
		//�߳�����д��
		try {
			RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
			accessFile.seek(start);//�ļ�ָ��ָ��startλ�ÿ�ʼд��
			//��������
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET"); 
			conn.setRequestProperty("Range", "bytes="+start+"-"+end);
			//�ֶ�������������206
			if(conn.getResponseCode() == 206){
			System.out.println(conn.getResponseCode());
				//��conn��� ������
				InputStream inStream = conn.getInputStream();
				
				byte[] buffer = new byte[1024];//������
				int len = 0;//��ʼ0 --- ��������ȡ����
				while( (len = inStream.read(buffer)) != -1){
					accessFile.write(buffer, 0, len);//buffer д�� �ļ�
				}
				accessFile.close();
				inStream.close();
			}
			System.out.println("Thread:"+(threadId+1)+"�������!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.run();
	}
}
