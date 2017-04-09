package dmpApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

public class FileUtility {

	/**
	 * 上传文件
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void postFile() throws ParseException, IOException{
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    try {
	        // 要上传的文件的路径
	        String filePath = new String("F:/pic/001.jpg");
	        // 把一个普通参数和文件上传给下面这个地址 是一个servlet
	        HttpPost httpPost = new HttpPost(
	                "http://localhost:8080/xxx/xxx.action");
	        // 把文件转换成流对象FileBody
	        File file = new File(filePath);
	        FileBody bin = new FileBody(file);
	        StringBody uploadFileName = new StringBody(
	                "把我修改成文件名称", ContentType.create(
	                        "text/plain", Consts.UTF_8));
	        //以浏览器兼容模式运行，防止文件名乱码。
	           HttpEntity reqEntity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
	                .addPart("uploadFile", bin) //uploadFile对应服务端类的同名属性<File类型>
	                .addPart("uploadFileName", uploadFileName)//uploadFileName对应服务端类的同名属性<String类型>
	                .setCharset(CharsetUtils.get("UTF-8")).build();

	        httpPost.setEntity(reqEntity);

	        System.out.println("发起请求的页面地址 " + httpPost.getRequestLine());
	        // 发起请求 并返回请求的响应
	        CloseableHttpResponse response = httpClient.execute(httpPost);
	        try {
	            System.out.println("----------------------------------------");
	            // 打印响应状态
	            System.out.println(response.getStatusLine());
	            // 获取响应对象
	            HttpEntity resEntity = response.getEntity();
	            if (resEntity != null) {
	                // 打印响应长度
	                System.out.println("Response content length: "
	                        + resEntity.getContentLength());
	                // 打印响应内容
	                System.out.println(EntityUtils.toString(resEntity,
	                        Charset.forName("UTF-8")));
	            }
	            // 销毁
	            EntityUtils.consume(resEntity);
	        } finally {
	            response.close();
	        }
	    } finally {
	        httpClient.close();
	    }
	}

	 /**
	 * 下载文件
	 * @param url            http://www.xxx.com/img/333.jpg
	 * @param destFileName   xxx.jpg/xxx.png/xxx.txt
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static void getFile(String url, String destFileName)
	        throws ClientProtocolException, IOException {
	    // 生成一个httpclient对象
	    CloseableHttpClient httpclient = HttpClients.createDefault();
	    HttpGet httpget = new HttpGet(url);
	    HttpResponse response = httpclient.execute(httpget);
	    HttpEntity entity = response.getEntity();
	    InputStream in = entity.getContent();
	    File file = new File(destFileName);
	    try {
	        FileOutputStream fout = new FileOutputStream(file);
	        int l = -1;
	        byte[] tmp = new byte[1024];
	        while ((l = in.read(tmp)) != -1) {
	            fout.write(tmp, 0, l);
	            // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
	        }
	        fout.flush();
	        fout.close();
	    } finally {
	        // 关闭低层流。
	        in.close();
	    }
	    httpclient.close();
	}

}
