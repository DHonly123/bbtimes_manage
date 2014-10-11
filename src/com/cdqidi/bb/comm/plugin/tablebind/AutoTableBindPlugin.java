package com.cdqidi.bb.comm.plugin.tablebind;

import java.util.List;

import javax.sql.DataSource;



import com.cdqidi.bb.comm.annotation.ClassSearcher;
import com.cdqidi.bb.comm.annotation.TableBind;
import com.cdqidi.bb.comm.annotation.TableNameStyle;
import com.jfinal.kit.StringKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.activerecord.Model;
/**
 * 
 * @类名字：AutoTableBindPlugin
 * @类描述：自动绑定model与数据库表
 * @author:Carl.Wu
 * @版本信息：
 * @日期：2013-9-11
 * @Copyright 足下 Corporation 2013 
 * @版权所有
 *
 */
public class AutoTableBindPlugin extends ActiveRecordPlugin {
	private TableNameStyle tableNameStyle;
	public AutoTableBindPlugin(DataSource dataSource) {
		super(dataSource);
	}

	public AutoTableBindPlugin(IDataSourceProvider dataSourceProvider, TableNameStyle tableNameStyle) {
		super(dataSourceProvider);
		this.tableNameStyle = tableNameStyle;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean start() {
		try {
			List<Class> modelClasses = ClassSearcher.findClasses(Model.class);
			TableBind tb = null;
			for (Class modelClass : modelClasses) {
				tb = (TableBind) modelClass.getAnnotation(TableBind.class);
				if (tb == null) {
					this.addMapping(tableName(modelClass), modelClass);
				} else {
					if(StringKit.notBlank(tb.name())){
						if (StringKit.notBlank(tb.pk())) {
							this.addMapping(tb.name(), tb.pk(), modelClass);
						} else {
							this.addMapping(tb.name(), modelClass);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//			throw new RuntimeException(e);
		}
		return super.start();
	}

	@Override
	public boolean stop() {
		return super.stop();
	}

	private String tableName(Class<?> clazz) {
		String tableName = clazz.getSimpleName();
		if (tableNameStyle == TableNameStyle.UP) {
			tableName = tableName.toUpperCase();
		} else if (tableNameStyle == TableNameStyle.LOWER) {
			tableName = tableName.toLowerCase();
		} else {
			tableName = StringKit.firstCharToLowerCase(tableName);
		}
		return tableName;
	}
}
