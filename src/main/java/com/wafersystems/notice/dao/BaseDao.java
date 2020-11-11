package com.wafersystems.notice.dao;

import com.wafersystems.notice.model.PaginationDTO;
import org.hibernate.criterion.DetachedCriteria;

import java.io.Serializable;
import java.util.List;


/**
 * Created with Intellij IDEA. Description: 数据库操作基础类 Author: waferzy DateTime: 2016/6/23 16:29
 * Company: wafersystems
 * @author wafer
 */
public interface BaseDao<T> {

  /**
   * 根据id取得实体.
   * 
   * @param clasz 实体
   * @param id 实体主键
   * @return 返回实体
   */
  T load(Class<T> clasz, Serializable id);

  /**
   * 根据HQL取得实体列表.
   * 
   * @param resultSql 查询语句
   * @return 查询结果
   */
  List<T> findBySql(String resultSql);

  /**
   * 根据HQL取得实体数量.
   * 
   * @param criteria 查询条件
   * @return 查询结果
   */
  Object getCount(DetachedCriteria criteria);

  /**
   * 根据DetachedCriteria查询.
   * 
   * @param criteria 查询条件
   * @return 查询结果
   */
  List<T> findByCriteria(DetachedCriteria criteria);

  /**
   * 保存实体.
   * 
   * @param clazs 实体对象
   */
  void save(Object clazs);

  /**
   * 根据id删除指定实体.
   * 
   * @param clazs 实体的类别
   * @param id 主键
   */
  void delete(Class<T> clazs, Serializable id);

  /**
   * 更新实体对象.
   * 
   * @param clazs 实体对象
   */
  void update(Object clazs);

  /**
   * 使用Sql语句进行更新 Description: author dingfeng DateTime 2016年4月11日 下午2:46:15.
   * 
   * @param sql 执行的sql
   * @return 更新记录数
   */
  int updateBySql(String sql);

  /**
   * 更新或新增实体.
   * 
   * @param clazs 实体对象
   */
  void saveOrUpdate(Object clazs);

  /**
   * flush hibernate缓存.
   */
  void flush();

  /**
   * Title: selectPage. Description: 使用查询条件对象分页查询
   * 
   * @param detachedCriteria 查询组合条件
   * @param pageSize 分页大小
   * @param startIndex 起始页
   * @return PaginationDTO
   */
  PaginationDTO<T> selectPage(DetachedCriteria detachedCriteria, int pageSize, int startIndex);
}
