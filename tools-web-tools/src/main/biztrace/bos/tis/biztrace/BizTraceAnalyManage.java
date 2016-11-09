package bos.tis.biztrace;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JsonConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bos.tis.biztrace.TISLogFile.LogTypeEnum;
import bos.tis.biztrace.analyzer.TransTimeConsumingAnalyzer;
import bos.tis.biztrace.parser.LogFileParser;
import bos.tis.biztrace.report.ShowTransTimeConsumingDetailReport;
import bos.tis.biztrace.report.TopsTransTimeConsumingReport;
import bos.tis.biztrace.report.TotalRequestServiceTypeReport;
import bos.tis.biztrace.report.TotalRequestTimesReport;
import bos.tis.biztrace.report.TotalTransSumReport;
import bos.tis.biztrace.utils.Helper;


/**
 * 解析biztrace.log日志文件，并保存至redis
 * @author megapro
 *
 */
public class BizTraceAnalyManage
{
	
	private final static Logger logger = LoggerFactory.getLogger(BizTraceAnalyManage.class);
	
	//所有的日志分析行为
	private static List<IBizTraceAnalyzer> ayalyzer = new ArrayList<IBizTraceAnalyzer>() ;
	//所有的日志报告行为
	private static List<IBizTraceReporter> reporter = new ArrayList<IBizTraceReporter>() ;
	
	public static final BizTraceAnalyManage instance = new BizTraceAnalyManage() ; 
	
	//所有读到的文件数
	private int fileTotalNum = 0;
	
	private BizTraceAnalyManage(){
		
		//分析过程
		addAyalyzer(new TransTimeConsumingAnalyzer());
		
		//报告过程
		addReporter( new TotalTransSumReport());
		addReporter( new TotalRequestTimesReport());
		addReporter( new TotalRequestServiceTypeReport());
		addReporter( new TopsTransTimeConsumingReport()) ;
		
	}
	
	
	/**
	 * 增加一种日志分析
	 * @param an
	 */
	public void addAyalyzer(IBizTraceAnalyzer an){
		ayalyzer.add(an) ;
	}
	
	/**
	 * 增加一种报告
	 * @param an
	 */
	public void addReporter(IBizTraceReporter an){
		reporter.add(an) ;
	}
	
	/**
	 * 清理某个日期的biztrace记录
	 */
	public void clearBiztrace(String date) {
		
		//todo ....
	}
	
	/**
	 * 查看serialNo交易的详细执行过程
	 * @param serialNo
	 */
	public void showSerialDetail(String serialNo){
		ShowTransTimeConsumingDetailReport sd = new ShowTransTimeConsumingDetailReport() ; 
		sd.setSerialNo(serialNo);
		sd.report("");
		//System.out.println( sd.report("") );
	}
	
	/**
	 * 日志分析
	 * @param date
	 */
	public void analyze(String date){
		logger.info("分析开始...");
		//执行分析
		for( IBizTraceAnalyzer an : ayalyzer ){
			an.analyzed(date);
		}
	}
	
	/**
	 * 报告分析结果
	 * @param date
	 */
	public void report(String date) {

		//查看报告
		StringBuffer sb = new StringBuffer() ;
		for( IBizTraceReporter an : reporter ){
			sb.append( an.report(date) ).append("\n");
		}
		System.out.println(sb.toString());
	}
	
	/**
	 *<pre>
	 * 多线程解析指定路径下所有日志文件
	 * 要求待解析的日志存放在对应的日期目录中,如： /20160523/*.log
	 * 系统会取 20160523 作为该日志文件的发生日
	 * </pre>
	 * @param path  文件所在路径
	 * @param threads 解析线程数
	 */
	public void resolve(String path, int threads) {
		
		logger.debug(Helper.concat("启动",threads,"个线程","解析日志目录:",path));
		
		//日志文件分组，每个线程要处理多少日志文件
		Map<String, List<TISLogFile>> groupFiles;
		try {
			groupFiles = groupLogFiles(path,threads);
		} catch (Exception e1) {
			logger.error("日志文件分组失败！"+e1.getMessage());
			e1.printStackTrace();
			return ; 
		}
		
		Set<Entry<String, List<TISLogFile>>> groups = groupFiles.entrySet() ;
		Iterator<Entry<String, List<TISLogFile>>> i = groups.iterator() ;
		
		//每组启动一个线程并行解析
		while( i.hasNext() ){
			LogFileParser parser = new LogFileParser() ; 
			Entry<String, List<TISLogFile>> e = i.next() ;
			parser.setFiles(e.getValue()) ;//该线程要解析日志文件			
			Thread t = new Thread(parser) ; 
			t.setName("THREAD_LOG_RESLOVE_GROUP:"+e.getKey());//线程名称
			t.start();
		}
	}
	
	/**
	 * 将文件路径下的log分组
	 * @param path
	 * @param groups 组数
	 * @return
	 * @throws Exception 
	 */
	public Map<String, List<TISLogFile>> groupLogFiles(String path,int groups) throws Exception {
		
		Map<String, List<TISLogFile>> groupFiles = new HashMap<String, List<TISLogFile>>() ;
		
		//所有需要解析的日志文件
		List<TISLogFile> allLogFiles = listLogFiles(path) ;
		
		setFileTotalNum(allLogFiles.size());
		
		logger.debug(Helper.concat("一共有 ",allLogFiles.size()," 个日志文件")) ;
		
		//将文件分为groups组
		int num = allLogFiles.size() / groups  ;   //每组文件数
		logger.debug(Helper.concat("每组分配文件数 ",num)) ;
		
		for( int i = 0 ; i < groups ; i ++ ){
			
			List<TISLogFile>  group = null ;
			
			if( i == (groups-1) ){
				group = allLogFiles.subList(i*num, allLogFiles.size()) ;//最后一组
				logger.debug(Helper.concat("最后一组分配文件数 ",group.size())) ;
			}else{
				if( num != 0 ){
					group = allLogFiles.subList(i*num, i*num+num) ;
				}else{
					continue ;//只分一组
				}
			}
			
			groupFiles.put(""+i, group) ;//平均每组放num个文件
		}
		
		return groupFiles;
	}
	
	/**
	 * 列出目录下所有待解析的日志文件
	 * @param path
	 * @return 日志文件list
	 * @throws Exception 
	 */
	public List<TISLogFile> listLogFiles(String path) throws Exception {
		
		List<TISLogFile> l = new ArrayList<TISLogFile>() ;
		
		try {
			
			List<File> allLogFiles = Helper.listFile(path,true,new FileFilter(){
				@Override
				public boolean accept(File pathname) {
					//只列出可以解析的日志文件
					for( LogTypeEnum t : LogTypeEnum.values() ){
						if( pathname.getAbsolutePath().indexOf( t.getSuffixTag() ) > 0 ){
							return true ; 
						}
					}
					return false;
				}
			}) ;
			
			for( File f : allLogFiles ){
				TISLogFile log = new TISLogFile() ;
				log.setLogFile(f);
				l.add(log) ;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return l;
	}


	public int getFileTotalNum() {
		return fileTotalNum;
	}


	public void setFileTotalNum(int fileTotalNum) {
		this.fileTotalNum = fileTotalNum;
	}
}