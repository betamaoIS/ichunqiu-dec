
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

public class M3U8EncryptHelper {
	private static String TMPDIR = null;
	private static String OUTDIR = null;
	private static String FFMPEGPATH = null;
	private static String userId = null;
	private static Map<String, Map<String,String>> courseInfos = null;
	
	static {
		Properties properties = new Properties();
		String configPath = System.getProperty("user.dir") + "\\config.txt";
		try {
			properties.load(new FileInputStream(configPath));
			TMPDIR = properties.getProperty("TMPDIR");
			OUTDIR = properties.getProperty("OUTDIR");
			FFMPEGPATH = properties.getProperty("FFMPEGPATH");
		} catch (IOException e) {
			System.exit(-1);
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		pullData(TMPDIR);
		courseInfos = getCourseInfos(TMPDIR);
		if(courseInfos.values().iterator().hasNext()) {
			userId = courseInfos.values().iterator().next().get("userId");
		}
		String videoDir = TMPDIR + "/VideoCache/" + userId;
		dec(videoDir);
		rename(videoDir, OUTDIR);
	}
	private static int pullData(String tmpDir) throws Exception {
		if(execCmd("adb pull /sdcard/Android/data/com.ni.ichunqiu/VideoCache/ " + TMPDIR, new File(TMPDIR))!=0) {
			System.exit(-1);
		}
		if(execCmd("adb pull /data/data/com.ni.ichunqiu/databases/alldata.db " + TMPDIR, new File(TMPDIR))!=0) {
			System.exit(-1);
		}
		return 0;
	}
	private static int dec(String parentDirPath) {
		File parentDirFile = new File(parentDirPath);
		if (parentDirFile.exists() && parentDirFile.isDirectory()) {
			File[] dirs = parentDirFile.listFiles();
			for (File file : dirs) {
				if (file.isDirectory()) {
					String keyfile = file.getAbsolutePath() + File.separator + "key";
					String fullpath = file.getAbsolutePath() + File.separator;
					String path = file.getName();
					String cmd = String.format(
							"%s -allowed_extensions ALL -i %s.m3u8 -c copy -bsf:a aac_adtstoasc %s.mp4 -loglevel warning",
							FFMPEGPATH, path, "../" + path);
					try {
						String chpier = new String(MUtils.readFile(keyfile), "UTF-8");
						String key = DecryptoKey.decrypt(chpier);
						MUtils.saveFile(MUtils.readFile(keyfile), keyfile + ".bak");
						MUtils.saveFile(MUtils.hexS2bytes(key), keyfile);
						execCmd(cmd, new File(fullpath));
						Thread.sleep(4000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return 0;
	}
	
	private static Map<String, Map<String,String>> getCourseInfos(String dbDir){
		SqliteHelper sqliteHelper;
		try {
			sqliteHelper = new SqliteHelper(dbDir + "\\" + "alldata.db");
			return sqliteHelper.getCourseInfo();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		} 
	}
	
	public static void rename(String srcDir, String outDir) throws ClassNotFoundException, SQLException {
		File root = new File(srcDir);
		if (root.exists() && root.isDirectory()) {
			File[] files = root.listFiles();
			for (File file : files) {
				if (file.getName().endsWith(".mp4")) {
					String name = file.getName().substring(0, file.getName().lastIndexOf(".mp4"));
					Map<String, String> tmp = courseInfos.get(name);
					if(tmp==null||tmp.isEmpty()) {
							System.out.println("未找到相应数据");
						}else{
							String course_name =  MUtils.escape(tmp.get("course_name"));
							String course_title = MUtils.escape(tmp.get("course_title"));
							String chapter_title = MUtils.escape(tmp.get("chapter_title"));
							String section_index = MUtils.escape(tmp.get("section_index"));
						
							File dir = new File(String.format("%s/%s/%s", outDir, course_name, chapter_title));
							if(!dir.exists()) {
								dir.mkdirs();
							}
							String newname = String.format("第%s节-%s.mp4", section_index,course_title);
							if(!file.renameTo(new File(dir.getAbsolutePath()+"/"+newname)))
								System.out.println("出错");
							else
								System.out.println(new File(dir.getAbsolutePath()+"/"+newname).getAbsolutePath());
						}
					}
			}
		}
	}
		
	public static int execCmd(String cmd, File dir) throws Exception {
		int ret = 0;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd, new String[] { "path=E:\\cmdtool;" }, dir);
			final InputStream is1 = process.getInputStream();    
			final InputStream is2 = process.getErrorStream();   
			new Thread() {   
			     public void run() {   
			        BufferedReader br1 = new BufferedReader(new InputStreamReader(is1));   
			         try {   
			             String line1 = null;   
			             while ((line1 = br1.readLine()) != null) {   
			                   if (line1 != null){
			                	   System.out.println(line1);
			                   }   
			               }   
			         } catch (IOException e) {   
			              e.printStackTrace();   
			         }   
			         finally{   
			              try {   
			                is1.close();   
			              } catch (IOException e) {   
			                 e.printStackTrace();   
			             }   
			           }   
			         }   
			      }.start();   
                                 
			new Thread() {    
			       public void  run() {    
			        BufferedReader br2 = new  BufferedReader(new  InputStreamReader(is2));    
			           try {    
			              String line2 = null ;    
			              while ((line2 = br2.readLine()) !=  null ) {    
			                   if (line2 != null){
			                	   System.out.println(line2);
			                   }   
			              }    
			            } catch (IOException e) {    
			                  e.printStackTrace();   
			            }    
			           finally{   
			              try {   
			                  is2.close();   
			              } catch (IOException e) {   
			                  e.printStackTrace();   
			              }   
			            }   
			         }    
			       }.start();     
			// 方法阻塞, 等待命令执行完成（成功会返回0）
			ret = process.waitFor();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
		return ret;
	}
}
