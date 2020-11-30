package com.teamcenter.rac.commands.refresh;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentFolder;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.services.loose.core.DataManagementService;
import com.teamcenter.services.loose.core._2008_06.DataManagement.CreateIn;
import com.teamcenter.services.loose.core._2008_06.DataManagement.CreateResponse;

public class TaskApprovalRecordUtil {

	TCSession session;
	TCPreferenceService preerenceService;
	String user_id;
	Calendar calendar = Calendar.getInstance();
	String PROJECT_DEFAULT_MUN = "PROJECT_DEFAULT_MUN";
	String PROJECT_MAX_MUN = "PROJECT_MAX_MUN";
	String TASK_DEFAULT_MUN = "TASK_DEFAULT_MUN";
	String TASK_MAX_MUN = "TASK_MAX_MUN";
	String property_folder = "s7_custom_folder"; // TypeReference/S7_CustomFolder
	String folderType = "S7_CustomFolder";
	String property_type = "s7_type"; // String 类型
	String property_more = "s7_more"; // TypeReference/S7_CustomFolder
	String property_today = "s7_today"; // TypeReference/S7_CustomFolder
	String property_year = "s7_year"; // int 类型
	String property_parent = "s7_parent"; // TypeReference/S7_CustomFolder
	String property_standard = "s7_standard"; // TypeReference/S7_CustomFolder
	String property_other = "s7_other"; // TypeReference/S7_CustomFolder
	String property_contents = "contents";
	
	
	public TaskApprovalRecordUtil() throws TCException {
		session = (TCSession) AIFUtility.getDefaultSession();
		user_id = session.getUser().getUserId();
		preerenceService = session.getPreferenceService();
	}
	
	public void init() throws Exception {
		TCComponent taskinbox = (TCComponent) session.getUser().getUserInBox().getChildren()[0].getComponent();
		TCComponentFolder root = (TCComponentFolder) taskinbox.getReferenceProperty(property_folder);
		if (root == null) {
			root = createFolder(session, folderType, "流程审批记录", "");
			taskinbox.add(property_folder, root);
		}
		refreshTasksCompleteFolder(root);
	}
	
	public void refreshFolders(InterfaceAIFComponent[] coms) throws TCException {
		if (coms != null) {
			for (InterfaceAIFComponent folder : coms) {
				refreshFolder(folder);
			}
		}
	}
	
	public void refreshFolder(InterfaceAIFComponent com) throws TCException {
		if (com instanceof TCComponentFolder) {
			refreshFolder((TCComponentFolder) com);
		}
	}
	
	public void refreshFolder(TCComponentFolder folder) throws TCException {
		if (folderType.equals(folder.getType())) {
			String type = folder.getProperty(property_type);
			switch (type) {
			case "流程审批记录":
				refreshTasksCompleteFolder(folder);
				break;
			case "今天":
				refreshTodayFolder(folder);
				break;
			case "年份":
				refreshYearFolder(folder);
				break;
			case "更多":
				refreshMoreFolder(folder);
				break;
			case "项目":
				refreshProjectFolder(folder);
			case "标准制修订":
				refreshStandardFolder(folder);
				break;
			case "其他流程":
				refreshOtherFolder(folder);
				break;
			default:
				break;
			}
		}
	}
	
	// 1. 更新流程审批记录文件夹下结构
	public void refreshTasksCompleteFolder(TCComponentFolder folder) throws TCException {
		
		System.out.println("refreshTasksCompleteFolder[" + folder + "]");
		
		//获取今天文件夹
		TCComponentFolder today = (TCComponentFolder) folder.getReferenceProperty(property_today);
		if (today == null) {
			today = createFolder(session, folderType, "今天", "今天");
			folder.setReferenceProperty(property_today, today);
		}
		refreshTodayFolder(today);
		
		// 获取更多文件夹
		TCComponentFolder more = (TCComponentFolder) folder.getReferenceProperty(property_more);
		if (more == null || !"更多".equals(more.getProperty(property_type))) {
			more = createFolder(session, folderType, "更多", "更多");
			more.setReferenceProperty(property_parent, folder);
			folder.setReferenceProperty(property_more, more);
		}
		
		// 更新更多文件夹内容
		TCComponent[] dates = more.getReferenceListProperty(property_contents);
		Map<Integer, TCComponentFolder> dateFolders = new HashMap<>();
		for (TCComponent dateCom : dates) {
			if (folderType.equals(dateCom.getType())) {
				int year = dateCom.getIntProperty(property_year);
				dateFolders.put(year, (TCComponentFolder) dateCom);
			}
		}
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int endYear = calendar.get(Calendar.YEAR);
		int startYear = 2016;
		int years = endYear - startYear + 1;
		if (years > 10) {
			years = 10;
		}
		TCComponentFolder[] more_folders = new TCComponentFolder[years];
		TCComponentFolder dateFolder = null;
		for (int i = 0; i < years; i++) {
			dateFolder = dateFolders.get(endYear);
			if (dateFolder == null) {
				dateFolder = createFolder(session, folderType, endYear + "年", "年份");
				dateFolder.setIntProperty("s7_year", endYear);
			}
			more_folders[i] = dateFolder;
			calendar.add(Calendar.YEAR, -1); // 年份-1
			endYear = calendar.get(Calendar.YEAR);
		}
		more.setRelated(property_contents,more_folders);
		
		// 更新内容文件夹
		int default_year = 3;
		if (years < 3) {
			default_year = years;
		}
		TCComponentFolder[] contents = new TCComponentFolder[default_year];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = more_folders[i];
		}
		folder.setRelated(property_contents, contents);
		
		// 更新年份文件夹内容
		for (int i = 0; i < more_folders.length; i++) {
			refreshYearFolder(more_folders[i]);
		}
	}
	
	// 获取日期
	public String getDateString(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
		return format.format(date);
		
	}
	
	// 更新年份数据内容
	public void refreshYearFolder(TCComponentFolder folder) throws TCException {
		
		System.out.println("refreshYearFolder[" + folder + "]");
		int year = folder.getIntProperty(property_year);
		System.out.println(year + "年份数据更新");
		int project_defult_num = preerenceService.getIntegerValue(PROJECT_DEFAULT_MUN);
		int project_max_num = preerenceService.getIntegerValue(PROJECT_MAX_MUN);
		if (project_max_num < project_defult_num) {
			project_defult_num = project_max_num;
		}
		
		// 获取标准制修订文件夹
		TCComponentFolder standard = (TCComponentFolder) folder.getReferenceProperty(property_standard);
		if (standard == null) {
			standard = createFolder(session, folderType, "标准制修订", "标准制修订");
			standard.setIntProperty(property_year, year);
			folder.setReferenceProperty(property_standard, standard);
		}
		refreshStandardFolder(standard);
		
		// 获取其他流程文件夹
		TCComponentFolder other = (TCComponentFolder) folder.getReferenceProperty(property_other);
		if (other == null) {
			other = createFolder(session, folderType, "其他流程", "其他流程");
			other.setIntProperty(property_year, year);
			folder.setReferenceProperty(property_other, other);
		}
		refreshOtherFolder(other);
		
		// 获取年份的更多文件夹
		TCComponentFolder project_more = (TCComponentFolder) folder.getReferenceProperty(property_more);
		if (project_more == null|| !"更多".equals(project_more.getProperty(property_type))) {
			project_more = createFolder(session, folderType, "更多", "更多");
			project_more.setReferenceProperty(property_parent, folder);
			folder.setReferenceProperty(property_more, project_more);
		}
				
		// 更新更多文件夹内容
		TCComponent[] dates = project_more.getReferenceListProperty(property_contents);
		Map<String, TCComponentFolder> project_folders = new HashMap<>();
		for (TCComponent dateCom : dates) {
			String type =dateCom.getProperty(property_type);
			if ("项目".equals(type)) {
				String name = dateCom.getProperty("object_name"); // 项目名称
				project_folders.put(name, (TCComponentFolder) dateCom);
			}
		}
		
		String[] enters = new String[] {"创建时间大于", "创建时间小于"};
		String[] values = new String[] {year + "-1-01 00:00", (year + 1) + "-1-01 00:00"};
		TCComponent[] projects = queryProjects(enters, values);
		if (project_max_num < projects.length) {
			project_max_num = projects.length;
		}
		TCComponentFolder[] more_projects = new TCComponentFolder[project_max_num];
		TCComponentFolder projectFolder = null;
		for (int i = 0; i < project_max_num; i++) {
			TCComponent project = projects[i];
			String name = project.toDisplayString();
			projectFolder = project_folders.get(name);
			if (projectFolder == null) {
				projectFolder = createFolder(session, folderType, name,"项目");
				projectFolder.setProperty(property_type, "项目");
				projectFolder.setIntProperty(property_year, year);
			}
			more_projects[i] = projectFolder;
		}
		project_more.setRelated(property_contents,more_projects);

		// 存储默认显示项目夹数据
		TCComponentFolder[] contents = new TCComponentFolder[project_defult_num];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = more_projects[i];
		}
		folder.setRelated(property_contents,contents);

		for (int i = 0; i < more_projects.length; i++) {
			refreshProjectFolder(more_projects[i]);
		}
				
	}
	
	// 刷新今天文件夹数据
	public void refreshTodayFolder(TCComponentFolder folder) throws TCException {
		Date date = new Date();
		calendar.setTime(date);
		Date start = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		Date end = calendar.getTime();
		String[] enters = new String[]{"用户ID","任务类型","任务开始时间大于","任务开始时间小于"};
		String[] values = new String[]{user_id, "DoTask;ReviewTask", getDateString(start) + " 00:00", getDateString(end) + " 00:00"};
		TCComponent[] tasks = queryTodayTasks(enters, values);
		folder.setRelated(property_contents, tasks);
	}
	
	// 刷新项目文件夹数据
	public void refreshProjectFolder(TCComponentFolder folder) throws TCException {
		int year = folder.getIntProperty(property_year);
		String[] enters = new String[]{"用户ID","任务类型","任务开始时间大于","任务开始时间小于"};
		String[] values = new String[]{user_id,"DoTask;ReviewTask",year + "-1-01 00:00", (year + 1) + "-1-01 00:00"};
		TCComponent[] tasks = queryProjectTasks(enters, values);
		refreshTasksFolder(folder, tasks);
	}
	
	// 刷新标准制修订文件夹数据
	public void refreshStandardFolder(TCComponentFolder folder) throws TCException {
		int year = folder.getIntProperty(property_year);
		String[] enters = new String[]{"用户ID","任务类型","任务开始时间大于","任务开始时间小于"};
		String[] values = new String[]{user_id,"DoTask;ReviewTask",year + "-1-01 00:00", (year + 1) + "-1-01 00:00"};
		TCComponent[] tasks = queryStandardTasks(enters, values);
		refreshTasksFolder(folder, tasks);
	}
	
	// 刷新其他流程文件夹数据
	public void refreshOtherFolder(TCComponentFolder folder) throws TCException {
		int year = folder.getIntProperty(property_year);
		String[] enters = new String[]{"用户ID","任务类型","任务开始时间大于","任务开始时间小于"};
		String[] values = new String[]{user_id,"DoTask;ReviewTask",year + "-1-01 00:00", (year + 1) + "-1-01 00:00"};
		TCComponent[] tasks = queryOtherTasks(enters, values);
		refreshTasksFolder(folder, tasks);
	}
	
	// 刷新流程数据文件夹
	public void refreshTasksFolder(TCComponentFolder folder, TCComponent[] tasks) throws TCException {
		int task_defult_num = preerenceService.getIntegerValue(TASK_DEFAULT_MUN);
		int task_max_num = preerenceService.getIntegerValue(TASK_MAX_MUN);
		if (task_max_num > tasks.length) {
			task_max_num = tasks.length;
		}
		if (task_max_num < task_defult_num) {
			task_defult_num = task_max_num;
		}
		TCComponent more = folder.getReferenceProperty(property_more);
		if (more == null || !"更多".equals(more.getProperty(property_type))) {
			more = createFolder(session, folderType, "更多", "更多");
			more.setReferenceProperty(property_parent, folder);
			folder.setReferenceProperty(property_more, more);
		}
		TCComponent[] tasks_more = new TCComponent[task_max_num];
		for (int i = 0; i < tasks_more.length; i++) {
			tasks_more[i] = tasks[i];
		}
		more.setRelated(property_contents, tasks_more);
		
		TCComponent[] contents = new TCComponent[task_defult_num];
		for (int i = 0; i < contents.length; i++) {
			contents[i] = tasks[i];
		}
		folder.setRelated(property_contents, contents);
	}
	
	// 刷新更多文件夹数据
	public void refreshMoreFolder(TCComponentFolder more) throws TCException {
		TCComponent folder = more.getReferenceProperty(property_more);
		if (folder != null && folder instanceof TCComponentFolder) {
			refreshFolder((TCComponentFolder) folder);
		}
	}
	
	// 查询项目
	public TCComponent[] queryProjects(String[] enters, String[] values) throws TCException {
		try {
			TCComponent[] projects = session.search("SY6_Projects", enters, values);
			return projects;
		} catch (Exception e) {
			throw new TCException(e);
		}
	}
	
	// 查询其他流程任务
	public TCComponent[] queryOtherTasks(String[] enters, String[] values) throws TCException {
		try {
			TCComponent[] projects = session.search("SY6_OtherTasks", enters, values);
			return projects;
		} catch (Exception e) {
			throw new TCException(e);
		}
	}
	
	// 查询标准制修订流程任务
	public TCComponent[] queryStandardTasks(String[] enters, String[] values) throws TCException {
		try {
			TCComponent[] projects = session.search("SY6_Standard", enters, values);
			return projects;
		} catch (Exception e) {
			throw new TCException(e);
		}
	}
	
	// 查询项目流程任务
	public TCComponent[] queryProjectTasks(String[] enters, String[] values) throws TCException {
		try {
			TCComponent[] reviewTasks = session.search("SY6_ProjectTasks", enters, values);
			TCComponent[] doTasks = session.search("SY6_ProjectTasks", enters, values);
			return concatComs(reviewTasks, doTasks);
		} catch (Exception e) {
			throw new TCException(e);
		}
	}
	
	// 查询今天的任务
	public TCComponent[] queryTodayTasks(String[] enters, String[] values) throws TCException {
		try {
			TCComponent[] reviewTasks = session.search("SY6_TodayTasks", enters, values);
			TCComponent[] doTasks = session.search("SY6_TodayTasks", enters, values);
			return concatComs(reviewTasks, doTasks);
		} catch (Exception e) {
			throw new TCException(e);
		}
	}
	
	// 数组合并
	public TCComponent[] concatComs(TCComponent[] coms1, TCComponent[] coms2) {
		TCComponent[] coms = new TCComponent[coms1.length + coms2.length];
		for (int i = 0; i < coms1.length; i++) {
			coms[i] = coms1[i];
		}
		for (int i = 0; i < coms2.length; i++) {
			coms[coms1.length + i] = coms2[i];
		}
		return coms;
	}
	
	// 创建文件夹
	public TCComponentFolder createFolder(TCSession session,String folderType, String folderName, String type) throws TCException {
		try {
			DataManagementService dm = DataManagementService.getService(session.getSoaConnection());
			CreateIn folderDef = new CreateIn();
			folderDef.data.boName = folderType;
			folderDef.data.stringProps.put("object_name", folderName);
			folderDef.data.stringProps.put("object_desc", "");
			folderDef.data.stringProps.put(property_type, type);
			
			CreateIn createin[] = new CreateIn[1];
			createin[0] = folderDef;
			CreateResponse response = dm.createObjects(createin);
			if (response == null || response.serviceData.sizeOfPartialErrors() > 0) {
				throw new TCException(response.serviceData.getPartialError(0).getMessages()[0]);
			}
			TCComponent component = (TCComponent) response.serviceData.getCreatedObject(0);
			return (TCComponentFolder)component;
		} catch (Exception e) {
			throw new TCException(e);
		}
	}
	
}
