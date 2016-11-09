package d.dao.model;
import java.util.Date;

import d.dao.model.BaseModel;
/**
 * ${table.name}
 * @author su.zhang
 *
 */
public class ${table.id?cap_first} extends BaseModel {
	<#list table.fields as field>
	<#if field.type="string">
	 private String ${field.id};//${field.name}
	 </#if>
	 <#if field.type="long">
	 private Long ${field.id};//${field.name}
	 </#if>
	 <#if field.type="decimal">
	 private Double ${field.id};//${field.name}
	 </#if>
	  <#if field.type="datetime">
	 private Date ${field.id};//${field.name}
	 </#if>
	  <#if field.type="int">
	 private Integer ${field.id};//${field.name}
	 </#if>
	</#list>
	
	<#list table.fields as field>
	<#if field.type="string">
	public void set${field.id?cap_first}(String ${field.id}) {
        this.${field.id} = ${field.id} == null ? null : ${field.id}.trim();
    }
	 public String get${field.id?cap_first}(){
        return ${field.id};
    }
	 </#if>
	 <#if field.type="long">
	 public void set${field.id?cap_first}(Long ${field.id}) {
        this.${field.id} = ${field.id} ;
    }
	 public Long get${field.id?cap_first}(){
		if(${field.id}==null){
			return 0l;
		}
        return ${field.id};
    }
	 </#if>
	 <#if field.type="decimal">
	 public void set${field.id?cap_first}(Double ${field.id}) {
          this.${field.id} = ${field.id} ;
    }
	 public Double get${field.id?cap_first}(){
	 if(${field.id}==null){
			return 0d;
		}
        return ${field.id};
    }
	 </#if>
	  <#if field.type="datetime">
	 public void set${field.id?cap_first}(Date ${field.id}) {
          this.${field.id} = ${field.id} ;
    }
	 public Date get${field.id?cap_first}(){
        return ${field.id};
    }
	 </#if>
	  <#if field.type="int">
	 public void set${field.id?cap_first}(Integer ${field.id}) {
          this.${field.id} = ${field.id} ;
    }
	 public Integer get${field.id?cap_first}(){
        return ${field.id};
    }
	 </#if>
	</#list>
}