package pm.keypart;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.bos.tis.tools.util.PinyinUtil;

@XmlRootElement
public class Field {

	private String id;
	private String name;
	
	private String type;
	
	private String length;
	
	private String search;
	private String form;
	private String physical="true";//默认都要生成表字段
	private String key="false";//默认都要生成表字段
	
	@XmlAttribute
	public String getSearch() {
		return search;
	}
	public void setSearch(String search) {
		this.search = search;
	}
	@XmlAttribute
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	 @XmlAttribute
	public String getId() {
		 if(StringUtils.isNotEmpty(id)){
			 return id;
		 }
		return PinyinUtil.convert(name); 
	}
	public void setId(String id) {
		this.id = id;
	}
	 @XmlAttribute
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	 @XmlAttribute
	public String getType() {
		if(StringUtils.isNotEmpty(type)){
			return type;
		}
		return "string";
	}
	public void setType(String type) {
		this.type = type;
	}
	 @XmlAttribute
	public String getLength() {
		 if(StringUtils.isNotEmpty(length)){
			 return length;
		 }
		 if(StringUtils.equals("decimal", type)){
			 return "15,2";
		 }
		 if(StringUtils.equals("long", type)){
			 return "10";
		 }
		return "128";
	}
	public void setLength(String length) {
		this.length = length;
	}
	@XmlAttribute
	public String getPhysical() {
		return physical;
	}
	public void setPhysical(String physical) {
		this.physical = physical;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}
