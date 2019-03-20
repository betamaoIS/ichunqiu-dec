

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.regex.Pattern;


public class MUtils {
	
	public static String escape(String s){
		Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|]");
		return s==null?null:FilePattern.matcher(s).replaceAll("");
	}
    public static byte[] hexS2bytes(String arg7) {
        int hex = 16;
        char[] v1 = arg7.toCharArray();
        byte[] v2 = new byte[arg7.length() / 2];
        int i;
        for(i = 0; i < v2.length; ++i) {
            v2[i] = ((byte)(Integer.parseInt(v1[i * 2] + "", hex) * 16 + Integer.parseInt(v1[i * 2 + 1] + "", hex)));
        }

        return v2;
    }
    public static String strToMd5(String s) {
        String v0_2;
        int v0 = 0;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(s.getBytes("UTF-8"));
            byte[] v1_1 = md5.digest();
            StringBuilder v2 = new StringBuilder();
            int v3 = v1_1.length;
            while(v0 < v3) {
                v2.append(String.format("%02X", Byte.valueOf(v1_1[v0])));
                ++v0;
            }

            v0_2 = v2.toString().toLowerCase();
        }
        catch(Exception v0_1) {
            v0_1.printStackTrace();
            v0_2 = "";
        }

        return v0_2;
    }
	public static String base64Encode(byte[] data) {
		Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(data);
	}
	public static byte[] base64Decode(String base64Str) {
		Decoder decoder = Base64.getDecoder();
		return decoder.decode(base64Str);
	}

    public static boolean clearDir(File dir) {
        if (dir.exists()) {// 判断文件是否存在
            if (dir.isFile()) {// 判断是否是文件
               return dir.delete();// 删除文件
            } else if (dir.isDirectory()) {// 否则如果它是一个目录
                File[] files = dir.listFiles();// 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) {// 遍历目录下所有的文件
                    clearDir(files[i]);// 把每个文件用这个方法进行迭代
                }
                return dir.delete();// 删除文件夹
            }
        }
        return true;
    }

    public static byte[] readFile(String fileName) throws IOException{
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        int length = fis.available();
        byte [] buffer = new byte[length];
        fis.read(buffer);
        fis.close();
        return buffer;
    }

    public static void saveFile(byte[] bytes, String fileName) throws IOException{
        File file = new File(fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

}
