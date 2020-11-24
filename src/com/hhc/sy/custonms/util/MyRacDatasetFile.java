package com.hhc.sy.custonms.util;

import java.io.File;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentTcFile;
import com.teamcenter.rac.kernel.TCException;

public class MyRacDatasetFile {
	
	private TCComponent parent;
	private TCComponentTcFile tcFile;
	private String fileSize;
	private String originalFileName;
	private String fileExt;
	
	public MyRacDatasetFile(TCComponentTcFile tcFile) throws TCException {
		load(tcFile);
	}
	
	public MyRacDatasetFile(TCComponentTcFile tcFile, TCComponent parent) throws TCException {
		load(tcFile);
		this.parent = parent;
	}
	
	public void load(TCComponentTcFile tcFile) throws TCException {
		this.tcFile = tcFile;
		fileSize = tcFile.getProperty("file_size");
		originalFileName = tcFile.getProperty("original_file_name");
		fileExt = tcFile.getProperty("file_ext");
	}
	
	public void reload() throws TCException {
		load(this.tcFile);
	}
	
	/**
	 * 下载数据集
	 * @param path 下载路径
	 * @param idPrefix 是否将引用项的ID作为下载后的文件名前缀
	 * @return
	 * @throws TCException
	 */
	public File download(String path, boolean idPrefix) throws TCException {
		File fmsFile = tcFile.getFmsFile();
		if(!path.endsWith("/") && !path.endsWith("\\")) {
			path = path + "/";
		}
		String name = "";
		if(idPrefix && parent != null) {
			String itemId = parent.getProperty("item_id");
			name = originalFileName.startsWith(itemId) ? originalFileName : itemId + "-" + originalFileName;
		}else {
			name = originalFileName;
		}
		
		//如果文件名中包含/或者\\，需要替换掉，否则会被当做路径的一部分
		name = name.replace("/", "_").replace("\\", "_");
		File newFile = new File(path+name);
		FileUtil.copyFile(fmsFile, newFile);
		return newFile;
	}
	
	public File download(String path) throws TCException {
		return download(path, false);
	}

	public TCComponent getParent() {
		return parent;
	}

	public void setParent(TCComponent parent) {
		this.parent = parent;
	}

	public TCComponentTcFile getTcFile() {
		return tcFile;
	}

	public void setTcFile(TCComponentTcFile tcFile) {
		this.tcFile = tcFile;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getOriginalFileName() {
		return originalFileName;
	}

	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

}
