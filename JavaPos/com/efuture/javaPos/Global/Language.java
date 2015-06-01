package com.efuture.javaPos.Global;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Hashtable;

import com.efuture.DeBugTools.PosLog;
import com.efuture.commonKit.ChineseConvertor;
import com.efuture.commonKit.CommonMethod;

/**
 * 多语言翻译
 * @author yw
 *
 */
public class Language
{
	private static Hashtable _htCache = null;
	private static String _language = null;//zh-chs/zh-cht/en
	private static String _classPath = null;
	private static String _methodName = null;
	private static String _customFlag = "custom.localize";
	private static String _Font = "宋体";

	static
	{
		init();
	}

	//*********Language API Start*********
	/**
	 * 翻译函数
	 * @param sourceText 翻译文本内容
	 * @param sourceTextSeq 翻译文本(函数内)序号
	 * @param classPath 当前所在的类名+包类,比如:this.getClass().getName()
	 * @param methodName 当前所在的函数名(不含()括号)
	 * @param formatArgs 格式化参数值(即Object数组传参)
	 * @see 1.翻译文本内容可以传函数; 2.函数支持Object数组传参
	 * @return 翻译后的内容
	 */
	public static String apply(String sourceText, String classPath, String methodName, Object[] formatArgs)
	{
		return getLanguageText(getCurrLanguageText(getSourceText(sourceText), classPath, methodName, formatArgs));
	}

	/**
	 * 翻译函数
	 * @param sourceText 翻译文本内容
	 * @param sourceTextSeq 翻译文本(函数内)序号
	 * @param classPath 当前所在的类名+包类,比如:this.getClass().getName()
	 * @param methodName 当前所在的函数名(不含()括号)
	 * @param formatArgs 格式化参数值(即Object数组传参)
	 * @see 翻译文本内容可以传函数
	 * @return 翻译后的内容
	 */
	public static String apply(String sourceText, String classPath, String methodName)
	{
		return getLanguageText(apply(getSourceText(sourceText), classPath, methodName, new Object[] {}));
	}

	/**
	 * 翻译函数
	 * @param sourceTextSeq 翻译文本(函数内)序号
	 * @param sourceText 翻译文本内容 (注意:文本内容不能是函数,只能是字符串或字符串变量)
	 * @param formatArgs 格式化参数值(即Object数组传参)
	 * @see 1.翻译文本内容不能传函数; 2.函数支持Object数组传参
	 * @return
	 */
	public static String apply(String sourceText, Object[] formatArgs)
	{
		try
		{
			return getLanguageText(applyLanguage(getSourceText(sourceText), formatArgs, 2 + 1));
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
		}
		return getLanguageText(getCurrLanguageText(sourceText, "", "", formatArgs));

	}

	/**
	 * 翻译函数
	 * @param sourceTextSeq 翻译文本(函数内)序号
	 * @param sourceText 翻译文本内容 (注意:文本内容不能是函数,只能是字符串或字符串变量)
	 * @see 翻译文本内容不能传函数
	 * @return
	 */
	public static String apply(String sourceText)
	{
		try
		{
			return getLanguageText(applyLanguage(getSourceText(sourceText), new Object[] {}, 2 + 1));
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
		}
		return getLanguageText(getCurrLanguageText(getSourceText(sourceText), "", "", new Object[] {}));

	}

	//*********Language API End*********

	/**
	 * (内部)翻译函数
	 * @param sourceTextSeq 翻译文本(函数内)序号
	 * @param sourceText 翻译文本内容 (注意:文本内容不能是函数,只能是字符串或字符串变量)
	 * @param formatArgs 格式化参数值(即Object数组传参)
	 * @param stackTraceElementIndex 当前函数索引(一般为2+X, X>=0)
	 * @see 1.翻译文本内容不能传函数; 2.函数支持Object数组传参
	 * @return
	 */
	private static String applyLanguage(String sourceText, Object[] formatArgs, int stackTraceElementIndex)
	{
		if (!getCurrLanguageTextPath(stackTraceElementIndex) || !isExistsLanguageClassAndmethod()) return getCurrLanguageText(sourceText, "", "",
																																formatArgs);

		return apply(sourceText, _classPath, _methodName, formatArgs);
	}

	private static void init()
	{
		try
		{
			_htCache = new Hashtable();

			if (_language == null || _language.trim().length() <= 0)
			{
				//Read local configuration language
				_language = getLanguageConfig();

				//Read the current machine configuration language

				//check en
				if (_language != null && _language.toLowerCase().indexOf("en") >= 0)
				{
					_language = "en";
				}
			}
			
			//Default Chinese Simplified
			if (_language == null || _language.trim().length() <= 0)
			{
				_language = "zh-chs";
			}
			
			if (_language != null) _language = _language.toLowerCase();
			ConfigClass.Language = _language;
			writeLog("language=[" + _language + "]");
			
			loadCommonResToCache();
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
		}
	}
	
	private static void loadCommonResToCache()
	{
		//Load the file system of public resources
	}

	private static String getLanguageConfig()
	{
		try
		{
			if (ConfigClass.Language == null) LoadConfigSet();//ConfigClass.Language = "zh-chs";//liwj 有问题
			return ConfigClass.Language;
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
		}
		return null;
	}

	private static boolean isExistsLanguageClassAndmethod()
	{
		if (_classPath == null || _classPath.trim().length() <= 0 || _methodName == null || _methodName.trim().length() <= 0) return false;
		return true;
	}

	private static String getCustomModuleType(String className)
	{
		if (GlobalInfo.ModuleType == null || GlobalInfo.ModuleType.trim().length() <= 0 || className == null || className.trim().length() <= 0) { return ""; }
		if (className.trim().indexOf(_customFlag) == 0) 
		{ 
			String[] classArr = className.split("\\.");
			if(classArr.length>3) return classArr[2].toUpperCase().trim() + "/";
			return GlobalInfo.ModuleType.trim() + "/"; 
			
		}
		return "";
	}

	private static String getCurrLanguage()
	{
		if (_language == null || _language.trim().length() <= 0) _language = "zh-chs";
		return _language;
	}

	private static boolean getCurrLanguageTextPath(int stackTraceElementIndex)
	{
		try
		{
			StackTraceElement traceElement = ((new Exception()).getStackTrace())[stackTraceElementIndex];//2
			_classPath = traceElement.getClassName().split("\\$")[0];
			_methodName = traceElement.getMethodName();
			return true;
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
			return false;
		}
	}

	private static String getLanguageResPath(String className)
	{
		try
		{
			return "/language/" + getCurrLanguage() + "/" + getCustomModuleType(className) + className + ".res";
		}
		catch(Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
			return "";
		}		
	}

	private static boolean readLanguageResInfoToCache(String languageResPath)
	{
		try
		{
			String filePath = getLanguageResPath(languageResPath);
			writeLog("filePath=" + filePath);
			if (filePath == null || filePath.trim().length() <= 0) return false;

			InputStream is = Language.class.getResourceAsStream(filePath);
			if (is != null)
			{
				BufferedReader br = null;
				try
				{
					br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
					String line;
					String[] lines;
					String[] sp;
					String key;
					String value;
					while ((line = br.readLine()) != null)
					{
						if ((line == null) || (line.length() <= 0))
						{
							continue;
						}
						//System.out.println(line);
						lines = line.split("&&");
						sp = lines[0].split("=");
						if (sp.length < 2) continue;
						/*if( sp[0].indexOf("上次系统被非正常退出")>=0)
						{
							System.out.println();
						}*/
						key = getSourceText(sp[0].trim().toLowerCase());
						value = sp[1].trim();
						if(value!=null && value.length()>0) addLanguageItemCache(key, trimLeft(sp[1]));
					}

					//addLanguageItemCache(languageResPath, languageResPath);
				}
				catch(Exception ex)
				{
					throw ex;
				}
				finally
				{
					if (br != null) br.close();					
				}
				
				writeLog("cache size=[" + _htCache.size() + "],languageResPath=[" + languageResPath + "]");
				
				return true;
			}
			else
			{
				writeLog("languageResPath is null:[" + languageResPath + "]");
			}

		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
		}
		return false;
	}
	
	public static String trimLeft(String s) {	    return s.replaceAll("^\\s+", "");	}	 
	public static String trimRight(String s) {	    return s.replaceAll("\\s+$", "");	} 



	private static boolean isExistsLanguageItemCache(String key)
	{
		if (_htCache == null || _htCache.isEmpty() || _htCache.size() <= 0) return false;
		if (!_htCache.containsKey(key)) return false;
		return true;
	}

	private static boolean addLanguageItemCache(String key, String value)
	{
		if (_htCache == null) _htCache = new Hashtable();
		if (_htCache.containsKey(key))
		{
			_htCache.remove(key);
		}
		writeLog("key=[" + key + "]");
		_htCache.put(key, value);
		return true;
	}

	private static String getLanguageTextCache(String key, String sourceText, Object[] formatArgs)
	{
		if (sourceText==null)return "";
		key = sourceText.trim().toLowerCase();//改为按翻译文本为KEY add 2014.2.11
		if (isExistsLanguageItemCache(key)) { return MessageFormat.format(String.valueOf(_htCache.get(key)), formatArgs); }
		return MessageFormat.format(sourceText, formatArgs);
	}

	private static boolean isCht()
	{
		if (_language != null && _language.toLowerCase().indexOf("zh") >= 0 && _language.toLowerCase().indexOf("cht") >= 0) return true;
		return false;
	}

	private static boolean isChs()
	{
		if ((_language == null || _language.trim().length() <= 0)
				|| (_language != null && _language.toLowerCase().indexOf("zh") >= 0 && _language.toLowerCase().indexOf("chs") >= 0)) return true;
		return false;
	}

	private static String getChtText(String sourceText, Object[] formatArgs)
	{
		try
		{
			return ChineseConvertor.getInstance().s2t(MessageFormat.format(sourceText, formatArgs));
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
		}
		return sourceText;
	}

	private static String getChsText(String sourceText, Object[] formatArgs)
	{
		try
		{
			return MessageFormat.format(sourceText, formatArgs);
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
		}
		return sourceText;
	}

	private static String getCurrLanguageText(String sourceText, String classPath, String methodName, Object[] formatArgs)
	{
		try
		{
			if (isChs())
			{
				return getChsText(sourceText, formatArgs);
			}
			else if (isCht())
			{
				return getChtText(sourceText, formatArgs);
			}
			else
			{
				if (!isExistsLanguageItemCache(classPath))
				{
					readLanguageResInfoToCache(classPath);
				}
				return getLanguageTextCache(classPath, sourceText, formatArgs);
			}
		}
		catch (Exception ex)
		{
			PosLog.getLog(Language.class.getSimpleName()).error(ex);
			ex.printStackTrace();
			return getChsText(sourceText, formatArgs);
		}

	}
	
	public static boolean LoadConfigSet()
	{
		BufferedReader br1=null;
		try
		{		
			ConfigClass.Language = "zh-chs";
			ConfigClass.Language_Font = _Font;
			// 读取Config.ini
			br1 = CommonMethod.readFile(GlobalVar.ConfigFile);

			if (br1 == null)
			{
				//new MessageBox(com.efuture.javaPos.Global.Language.apply("配置文件导入错误,马上退出"), null, false);

				return false;
			}

			String line;
			String[] sp;
			int count=0;
		 
			while ((line = br1.readLine()) != null)
			{
				if ((line == null) || (line.length() <= 0))
				{
					continue;
				}

				String[] lines = line.split("&&");
				sp = lines[0].split("=");
				if (sp.length < 2)
					continue;
				if (sp[0].trim().compareToIgnoreCase("Language") == 0)
				{
					ConfigClass.Language = sp[1].trim();
					writeLog("Language=[" + ConfigClass.Language + "]");
					count++;
				}
				else if (sp[0].trim().compareToIgnoreCase("LanguageFont") == 0)
				{
					ConfigClass.Language_Font = sp[1].trim();
					writeLog("Language_Font=[" + ConfigClass.Language_Font + "]");
					count++;
				}

				if(count>=2) return true;
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//new MessageBox(com.efuture.javaPos.Global.Language.apply("配置文件导入错误") + e.getMessage() + com.efuture.javaPos.Global.Language.apply(",马上退出系统"), null, false);
			PosLog.getLog(Language.class.getSimpleName()).error(e);
			return false;
		}
		finally
		{
			try
			{
				if(ConfigClass.Language==null || 
						(ConfigClass.Language.equalsIgnoreCase("zh-chs") || ConfigClass.Language.equalsIgnoreCase("zh-cht")))
				{
					ConfigClass.Language_Font = _Font;
				}
				if (br1 != null) br1.close();
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return false;

	}

	public static String getLanguageFont(String font)
	{
		if(getLanguageFont()==null || getLanguageFont().length()<=0) return font;
		return getLanguageFont();
	}
	public static String getLanguageFont()
	{
		return ConfigClass.Language_Font;
	}
	
	private static String getSourceText(String sourceText)
	{
		if(sourceText==null) sourceText="";
		//System.out.println("getSourceText = " + sourceText.replace("\n", "|n"));
		return sourceText.replace("\\n", "|n").replace("\n", "|n").replace("\\r", "|r").replace("\r", "|r");
	}
	
	private static String getLanguageText(String LanguageText)
	{
		//if(1==1) return LanguageText;
		if(LanguageText==null) LanguageText="";
		//System.out.println("getLanguageText = " + LanguageText.replace("\n", "|n"));
		LanguageText = LanguageText.replace("|r", "^").replace("\\r", "^").replace('^', '\r');
		LanguageText = LanguageText.replace("|n", "^").replace("\\n", "^").replace('^', '\n');
		writeLog("LanguageText=" + LanguageText);
		return LanguageText;
	}
	
	private static void writeLog(String strLog)
	{
		if(_language!=null && _language.equalsIgnoreCase("en"))
		{
			PosLog.getLog(Language.class.getSimpleName()).info(strLog);
		}
	}
}
