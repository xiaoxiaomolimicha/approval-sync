package com.erplus.sync.mybatis;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.erplus.sync.utils.DataSourceSingleton;
import com.erplus.sync.utils.ForwardPortUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.logging.log4j2.Log4j2Impl;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class MybatisManager {

    private static volatile SqlSession sqlSession;

    public static SqlSession getSqlSession() {
        if (sqlSession == null) {
            synchronized (MybatisManager.class) {
                if (sqlSession == null) {
                    ForwardPortUtils.forwardMysqlPort();
                    SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
                    //这是mybatis-plus的配置对象，对mybatis的Configuration进行增强
                    MybatisConfiguration configuration = new MybatisConfiguration();
                    //这是初始化配置，后面会添加这部分代码
                    initConfiguration(configuration);
                    //这是初始化连接器，如mybatis-plus的分页插件
                    //configuration.addInterceptor(initInterceptor());
                    //配置日志实现
                    configuration.setLogImpl(Log4j2Impl.class);
                    //扫描mapper接口所在包
                    configuration.addMappers("com.lhstack.mybatis.mapper");
                    //构建mybatis-plus需要的globalconfig
                    GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
                    //此参数会自动生成实现baseMapper的基础方法映射
                    globalConfig.setSqlInjector(new DefaultSqlInjector());
                    //设置id生成器
                    //globalConfig.setIdentifierGenerator(new DefaultIdentifierGenerator());
                    //设置超类mapper
                    globalConfig.setSuperMapperClass(BaseMapper.class);
                    //设置数据源
                    Environment environment = new Environment("molimicha", new JdbcTransactionFactory(), DataSourceSingleton.getDataSource());
                    configuration.setEnvironment(environment);
                    registryMapperXml(configuration, "mapper");
                    //构建sqlSessionFactory
                    SqlSessionFactory sqlSessionFactory = builder.build(configuration);
                    //创建session
                    sqlSession = sqlSessionFactory.openSession();
                }
            }
        }
        return sqlSession;
    }

    /**
     * 初始化配置
     *
     * @param configuration
     */
    private static void initConfiguration(MybatisConfiguration configuration) {
        //开启驼峰大小写转换
        configuration.setMapUnderscoreToCamelCase(true);
        //配置添加数据自动返回数据主键
        configuration.setUseGeneratedKeys(true);
    }

    /**
     * 解析mapper.xml文件
     *
     * @param configuration
     * @param classPath
     * @throws IOException
     */
    private static void registryMapperXml(MybatisConfiguration configuration, String classPath) {
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> mapper = contextClassLoader.getResources(classPath);
            while (mapper.hasMoreElements()) {
                URL url = mapper.nextElement();
                if (url.getProtocol().equals("file")) {
                    String path = url.getPath();
                    File file = new File(path);
                    File[] files = file.listFiles();
                    for (File f : files) {
                        FileInputStream in = new FileInputStream(f);
                        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, f.getPath(), configuration.getSqlFragments());
                        xmlMapperBuilder.parse();
                        in.close();
                    }
                } else {
                    JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = urlConnection.getJarFile();
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        if (jarEntry.getName().endsWith(".xml")) {
                            InputStream in = jarFile.getInputStream(jarEntry);
                            XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(in, configuration, jarEntry.getName(), configuration.getSqlFragments());
                            xmlMapperBuilder.parse();
                            in.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void closeSqlSession() {
        if (sqlSession != null) {
            sqlSession.close();
            log.info("我关闭了:mybatis-plus的连接");
        }
    }

    public static <T> T getMapper(Class<T> mapper ) {
        return getSqlSession().getMapper(mapper);
    }

}
