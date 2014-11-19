package com.cdqidi.bb.tools;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.cdqidi.bb.classes.ClassController;
import com.cdqidi.bb.comm.BaseController;
import com.cdqidi.bb.comm.util.ImageCutterUtil;
import com.cdqidi.bb.comm.util.PropertiesFactoryHelper;
import com.cdqidi.bb.comm.util.TestImg;
import com.cdqidi.bb.user.UserController;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.kit.StringKit;
import com.jfinal.render.JsonRender;
import com.jfinal.upload.UploadFile;

@ControllerBind(controllerKey = "/image")
public class ImageTool extends BaseController{

	private static String ROOTPATH =new File(ClassController.class.getClassLoader().getResource("").getPath()).getParentFile().getParent();
	
	
	public void uploadImg() {
		Map<String, Object> map = new HashMap<String, Object>();
		String sy = UserController.class.getClassLoader().getResource("").getPath();
		System.err.println(sy);
		File f = new File(ROOTPATH+"/imgTemp");
		if (!f.exists()) {
			f.mkdirs();
		}
		UploadFile uploadFile = getFile("uploadFile", f.getAbsolutePath());
		String x2 = uploadFile.getFileName();
		Map<String, Long> map2 = TestImg.getImgInfo(uploadFile.getFile()
				.getAbsolutePath());
		System.err.println(map2);
		map.put("res", 1);
		map.put("success",true);
		map.put("des", "上传成功!");
		map.put("url", "imgTemp/" + x2);
		map.put("imginfo", map2);
		render(new JsonRender(map).forIE());
	}
	
	public void cutImg() {
		boolean issuccess = false;
		// 选择框相关参数
		Long selectorX = getParaToLong("selectorX");
		Long selectorY = getParaToLong("selectorY");
		Long selectorW = getParaToLong("selectorW");
		Long selectorH = getParaToLong("selectorH");
		// 预览图片大小
	//	Long viewPortW = getParaToLong("viewPortW");
	//	Long viewPortH = getParaToLong("viewPortH");

		 String simageH = getPara("imageH");
		 String simageW = getPara("imageW");
		 String simageY = getPara("imageY");
		 String simageX = getPara("imageX");
	    

		// 旋转角度
		//Long imageRotate = getParaToLong("imageRotate");
		// 资源路径
		String imageSource = getPara("imageSource");

		String dataid = getPara("dataid");
		String optype = getPara("optype");
		String root = ROOTPATH;
		String srcroot = root + imageSource.substring(2);
		System.err.println(root);
		PropertiesFactoryHelper propFactory = PropertiesFactoryHelper
				.getInstance();

		try {
			String distsrc = "";
			String fileName = "";
			switch(optype){
				case "00":
					distsrc = propFactory.getConfig("HEADER_SAVEPATH") + "/organization/"
							+ dataid + "/";
					fileName = "header150x150.png";
					break;
				case "01":
					distsrc = propFactory.getConfig("HEADER_SAVEPATH") + "/circle/"
							+ dataid + "/";
					fileName = "header128x128.jpg";
					break;
				case "02":
					distsrc = propFactory.getConfig("HEADER_SAVEPATH") + "/"
							+ dataid + "/header/";
					fileName = "header48x48.jpg";
					break;
			}
			File fx = new File(distsrc);
			if (!fx.exists()) {
				fx.mkdirs();
			}
			String newpath = ImageCutterUtil.zoom(srcroot, null, formartString(simageW).intValue(), formartString(simageH).intValue());
			ImageCutterUtil.cutImage(newpath, distsrc + fileName,
					(selectorX.intValue()-formartString(simageX).intValue()), (selectorY.intValue()-formartString(simageY).intValue()),
					selectorW.intValue(), selectorH.intValue());
		    //三类用户均采用固定地址，则无需更新数据库信息了。
			//new ClassInfo().set("ID", dataid).set("FILE_URL", "circle/"+ dataid + "/header128x128.jpg").update();
			issuccess = true;
			File fsrc = new File(srcroot);
			if (fsrc.exists()) {
				fsrc.delete();
			}
			File nsrc = new File(newpath);
			if (nsrc.exists()) {
				nsrc.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		renderJson("msg", issuccess);
	}
	
	private BigDecimal formartString(String s) throws Exception{
		if (StringKit.isBlank(s)) {
			throw new Exception("未发现有需要转换的字符参数");
		}
		BigDecimal bigDecimal=new BigDecimal(s);
		return bigDecimal;
		
	}

	@Override
	public void index() {
		// TODO Auto-generated method stub
		
	}
}
