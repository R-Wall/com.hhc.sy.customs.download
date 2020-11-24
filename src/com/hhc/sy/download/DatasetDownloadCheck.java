package com.hhc.sy.download;

import java.util.HashMap;

import com.hhc.sy.custonms.util.MyPerference;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;

public class DatasetDownloadCheck {
	
	private static String role = null;
	private static String user = null;
	public static String DownloadCheck(InterfaceAIFComponent[] targets) throws Exception {
		String msg = "";
		TCComponentItemRevision ir = null;

		TCSession session  = (TCSession) AIFUtility.getDefaultSession();
		role = session.getRole().toString();
		user = session.getUser().toString();
		String docName = null;
		for (InterfaceAIFComponent target : targets) {
			docName = target.getProperty("object_name");
			if(target instanceof TCComponentItemRevision) {
				ir = (TCComponentItemRevision) target;	
				 TCComponent[] tccomps = ir.getRelatedComponents("fnd0StartedWorkflowTasks");
				 if(tccomps.length>0) {
					 //流程场景。。。
					 String tempmsg = inProcessCheck(ir);
					 if(tempmsg!=null) {
						 msg = msg+ tempmsg;
					 }
				 }else {
					 //非流程场景。。。
					 String tempmsg = outProcessCheck(ir);
					 if(tempmsg!=null) {
						 msg = msg+ tempmsg;
					 }
				 }
			}else {
				msg = msg+"选择的【"+docName+"】不是文档模板版本，无法下载!"+"\n";
			}
		}		
		return msg;
		
	}
	
	/**流程场景的检查
	 * @return
	 * @throws TCException 
	 */
	public static String inProcessCheck(TCComponentItemRevision ir) throws TCException {
		 String msg = "";
		 String docOwningUser = ir.getProperty("owning_user");
		 String docName = ir.getProperty("object_name");
		 TCComponent[] tccomps = ir.getRelatedComponents("fnd0StartedWorkflowTasks");
		 if(docOwningUser.equals(user)) {
			 for (int i = 0; i < tccomps.length; i++) {
				String jobType = tccomps[i].getType();
				System.out.println(jobType);
				if(!jobType.equals("EPMTask")) {
					 String resp_party = tccomps[i].getTCProperty("resp_party").toString();
					 if(!user.contains(resp_party)) {
						 msg = msg+"当前用户不是【"+tccomps[i].toString()+"】的责任方，无法下载"+"\n";
					 }
				}
			}
		 }else {
			 msg = "当前用户不是【"+docName+"】的所有者，无法下载"+"\n";
		 }
		return msg;
		
	}
	
	/**非流程场景的检查
	 * @return
	 * @throws TCException 
	 */
	public static String outProcessCheck(TCComponentItemRevision ir) throws TCException {
		String msg = "";
		HashMap<String, String> docRoleMap = new HashMap<String, String>();
		String key = null;
		String value= null;
		String type = null;
		String docName = null;
		String[] values = MyPerference.getStringValues("SY_DownloadCheck");
		if(values!=null){
			for (int i = 0; i < values.length; i++) {
				if(values[i].contains(":")) {
					 key = values[i].split(":")[0];
					 value = values[i].split(":")[1];
					 docRoleMap.put(key, value);
				}else if(values[i].contains("：")) {
					 key = values[i].split("：")[0];
					 value = values[i].split("：")[1];
					 docRoleMap.put(key, value);
				}
				
			}
				type = ir.getType();
				docName = ir.getProperty("object_name");
				if(!type.equals("SY6_Template1Revision")) {
					msg = "选择的【"+docName+"】不是文档模板版本，无法下载!"+"\n";
				}else {
					 value = docRoleMap.get(docName);
					 if(value!=null&&value.equals("0")) {
						 msg = "选择的【"+docName+"】没有权限下载，无法下载!"+"\n";
					 }else if(value!=null&&!value.contains(role)) {
						 msg = "选择的【"+docName+"】文档模板，当前角色无法下载!"+"\n";
					 }
				}
		}else {
			msg = "文档模板下载首选项【SY_DownloadCheck】为空，无法下载！请联系管理员！";
		}	
		return msg;
		
	}	
}
