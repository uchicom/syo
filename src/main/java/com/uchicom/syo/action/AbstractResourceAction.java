/* (c) 2015 uchicom */
package com.uchicom.syo.action;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.uchicom.syo.util.ImageUtil;
import com.uchicom.syo.util.UIStore;

public abstract class AbstractResourceAction extends AbstractAction {

	protected UIStore uiStore;
	public AbstractResourceAction(UIStore uiStore) {
		this.uiStore = uiStore;
		StringBuffer strBuff = new StringBuffer(128);
		String className = getClass().getCanonicalName();
		Field[] fields = getClass().getFields();
		for (Field field : fields) {
			//staticかを判定する
			int mod = field.getModifiers();
			if (Modifier.isFinal(mod) &&
				Modifier.isStatic(mod) &&
				Modifier.isPublic(mod)) {
				//staticならプロパティを設定する。
				setValue(uiStore.getActionResource(), strBuff, className, field);
			}
		}
	}

	protected void setValue(Properties resource, StringBuffer strBuff, String className, Field field) {
		strBuff.setLength(0);
		strBuff.append(className);
		strBuff.append(".");
		strBuff.append(field.getName());
		String key = strBuff.toString();
		//キーが存在していたら設定する
		System.out.println(key);
		if (resource.containsKey(key)) {
			try {
				String value = resource.getProperty(key);
				System.out.println(value);
				if (value.startsWith("img:")) {
					//img:で始まっている場合イメージファイルをロードする。
					putValue((String)field.get(null), ImageUtil.getImageIcon(value.substring(4)));
				} else if (value.startsWith("key:")) {
					putValue((String)field.get(null), KeyStroke.getKeyStroke(value.substring(4)));
				} else if (value.startsWith("int:")) {
					putValue((String)field.get(null), new Integer(value.substring(4).toCharArray()[0]));
				} else {
					putValue((String)field.get(null), value);
				}
			} catch (IllegalArgumentException e) {
				//握りつぶしてなにもしない。
			} catch (IllegalAccessException e) {
				//握りつぶしてなにもしない。
			}
		}
	}

}
