package com.hhc.sy.custonms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class FileUtil {
	
    public static boolean copyFile(File oldFile, File newFile){
    	try {
    		if(!oldFile.exists()){
    			System.out.println(oldFile.getPath() + "  文件不存在");
    			return false;
    		}
    		
    		if(!newFile.getParentFile().exists()){
    			newFile.getParentFile().mkdirs();
    		}
    		
    		InputStream is = new FileInputStream(oldFile);
    		FileOutputStream fos = new FileOutputStream(newFile);
    		copyFile(is, fos);
    		return true;
		} catch (Exception e) {
			return false;
		}
    }
    
    public static boolean copyFile(InputStream is, FileOutputStream fos){
    	
    	try {
    		int bytesum = 0; 
    		int byteread = 0; 
    		byte[] buffer = new byte[1444]; 
    		while ( (byteread = is.read(buffer)) != -1) { 
    			bytesum += byteread; //字节数 文件大小 
    			fos.write(buffer, 0, byteread); 
    		} 
    		
    		System.out.println("copy byte " + bytesum);
    		is.close();
    		fos.close();
    		return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
    /**
     * 写文件
     * 
     * @param newStr 新内容
     * @param isCover 是否覆盖原有内容
     * @throws IOException
     */
    @SuppressWarnings("unused")
	public static boolean writeTxtFile(String filePath, String newStr, boolean isCover) throws IOException {
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        
        try {
            // 文件路径
            File file = new File(filePath);
            if(!file.exists()){
            	file.createNewFile();
            }
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            if(!isCover){
            	for (int j = 1; (temp = br.readLine()) != null; j++) {
                    buf = buf.append(temp);
                    // System.getProperty("line.separator")
                    // 行与行之间的分隔符 相当于“\n”
                    buf = buf.append(System.getProperty("line.separator"));
                }
            }
            
            buf.append(newStr);

            fos = new FileOutputStream(file);
            
//            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            throw e1;
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return flag;
    }

    public static String readData(String filePath) {
        // 定义一个待返回的空字符串
        String strs = "";
        FileReader read = null;
        try {
            read = new FileReader(new File(filePath));
            StringBuffer sb = new StringBuffer();
            char ch[] = new char[1024];
            int d = read.read(ch);
            while (d != -1) {
                String str = new String(ch, 0, d);
                sb.append(str);
                d = read.read(ch);
            }
            //System.out.print(sb.toString());
            String a = sb.toString().replaceAll("@@@@@", ",");
            strs = a.substring(0, a.length());
            read.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
        	try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return strs;
    }
}
