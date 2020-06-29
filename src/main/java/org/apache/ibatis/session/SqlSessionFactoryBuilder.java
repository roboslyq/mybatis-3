/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;

/**
 * Builds {@link SqlSession} instances.
 * 1、{@link SqlSessionFactory}的构造器，使用建造者模式实现对SqlSessionFactory对象的创建 ， 是Mybatis的重要的面向用户的一个实现类。
 * 2、使用默认的构造函数创建SqlSessionFactoryBuilder实例
 * 3、创建SqlSessionFactory时，支持多种配置源，比如Reader,InputStream和Configuration等。其中Configuration是org.apache.ibatis.session包中的类。
 * 4、
 * @author Clinton Begin
 */
public class SqlSessionFactoryBuilder {

  public SqlSessionFactory build(Reader reader) {
    return build(reader, null, null);
  }

  public SqlSessionFactory build(Reader reader, String environment) {
    return build(reader, environment, null);
  }

  public SqlSessionFactory build(Reader reader, Properties properties) {
    return build(reader, null, properties);
  }

  public SqlSessionFactory build(InputStream inputStream, String environment) {
    return build(inputStream, environment, null);
  }

  /**
   * 最常用的使用方法之一，使用伪代码如下：
   *   String resource = "mybatis-config.xml";
   *  InputStream inputStream = Resources.getResourceAsStream(resource);
   *  SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
   *  try (SqlSession session = sqlSessionFactory.openSession()) {
   *      BlogMapper mapper = session.getMapper(BlogMapper.class);
   *      Blog blog = mapper.selectBlog(1);
   *      System.out.println(blog.getId());
   *      System.out.println(blog.getTitle());
   * }
   * @param inputStream
   * @return
   */
  public SqlSessionFactory build(InputStream inputStream) {
    return build(inputStream, null, null);
  }

  /**
   * 使用Reader数据源具体的构造方法实现（最终是调用 build(Configuration config)实现）
   * @param reader     Reader数据源
   * @param environment
   * @param properties
   * @return
   */
  public SqlSessionFactory build(Reader reader, String environment, Properties properties) {
    try {
      XMLConfigBuilder parser = new XMLConfigBuilder(reader, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        reader.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  public SqlSessionFactory build(InputStream inputStream, Properties properties) {
    return build(inputStream, null, properties);
  }

  /**
   * 使用InputStream配置源的构造器，最终是调用 build(Configuration config)实现
   * @param inputStream
   * @param environment
   * @param properties
   * @return
   */
  public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
    try {
      // 构建XMLConfigBuilder，此时还没有开始解析xml,调用XMLConfigBuilder.parse()方法时时，才会解析
      XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
      return build(parser.parse());
    } catch (Exception e) {
      throw ExceptionFactory.wrapException("Error building SqlSession.", e);
    } finally {
      ErrorContext.instance().reset();
      try {
        inputStream.close();
      } catch (IOException e) {
        // Intentionally ignore. Prefer previous error.
      }
    }
  }

  /**
   * 使用Configuration构造器(最常用)
   * @param config
   * @return
   */
  public SqlSessionFactory build(Configuration config) {
    return new DefaultSqlSessionFactory(config);
  }

}
