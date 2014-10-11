package com.cdqidi.bb.codetable;

import java.util.ArrayList;
import java.util.List;

import com.cdqidi.bb.comm.BaseController;
import com.jfinal.ext.route.ControllerBind;
import com.jfinal.plugin.ehcache.CacheKit;

@ControllerBind(controllerKey = "area")
public class AreaController extends BaseController {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void index() {
		List list = (List) CacheKit.get("area", "pcdselect");
		if (list == null) {
			list = new ArrayList<Object>();
			List<Provinces> provinces = Provinces.dao
					.find("SELECT DISTINCT  concat('p',p.ProvinceID) id ,p.ProvinceID provinceID,\n" +
							"p.ProvinceName name ,'true' as 'extend' from sys_province p \n" +
							"INNER  JOIN be_schoolinfo s ON s.ProvinceID=p.ProvinceID");
			for (int i = 0; i < provinces.size(); i++) {
				Provinces provinces2 = provinces.get(i);
				// 把省级数据放入集合中
				list.add(provinces2);
				int xid = provinces.get(i).getInt("provinceID");
				List<Cities> cities = Cities.dao
						.find("SELECT DISTINCT concat('c',c.CityID) id,c.CityID cityID,c.CityName name ,concat('p',?) as 'pid' from sys_city c INNER  JOIN be_schoolinfo s ON s.CityID=c.CityID WHERE c.ProvinceID=?",
								new Object[] { xid, xid });
				if (cities.size() > 0) {
					for (Cities cities2 : cities) {
						// 把城市数据放入集合中
						list.add(cities2);
						int cid = cities2.getInt("cityID");
						List<Districts> districts = Districts.dao
								.find("SELECT DISTINCT concat('d',d.DistrictID) id,d.DistrictID districtID,d.DistrictName name ,concat('c',?) as 'pid' from sys_district d INNER  JOIN be_schoolinfo s ON s.DistrictID=d.DistrictID WHERE d.CityID=?",
										new Object[] { cid, cid });
						if (districts.size() > 0) {
							for (Districts districts2 : districts) {
								// 把区域数据放入集合中
								list.add(districts2);
							}
						}
					}
				}
			}
			CacheKit.put("area", "pcdselect", list);
		}
		renderJson(list);
	}

	/**
	 * 
	 * @方法名:getProvinces
	 * @方法描述：获取省份列表
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-13 下午12:04:09
	 */
	public void getProvinces() {
		List<Provinces> list = Provinces.dao.findByCache("area", "provinces",
				"select * from sys_province");
		renderJson(list);

	}

	/**
	 * @方法名:getCities
	 * @方法描述：获取城市列表
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-13 下午12:04:01
	 */
	public void getCities() {
		Integer provinceID = getParaToInt("provinceID");
		if (provinceID != null) {
			List<Cities> cities = Cities.dao.findByCache("area", "cities_"
					+ provinceID.intValue(),
					"select * from sys_city where ProvinceID=?",
					provinceID.intValue());
			renderJson(cities);
		} else
			return;

	}

	/**
	 * @方法名:getDistricts
	 * @方法描述：获取区县列表
	 * @author: Carl.Wu
	 * @return: void
	 * @version: 2013-9-13 下午12:05:18
	 */
	public void getDistricts() {
		Integer cityID = getParaToInt("cityID");
		if (cityID != null) {
			List<Districts> districts = Districts.dao.findByCache("area",
					"districts_" + cityID.intValue(),
					"select * from sys_district where CityID=?",
					cityID.intValue());
			renderJson(districts);
		} else
			return;

	}

}
