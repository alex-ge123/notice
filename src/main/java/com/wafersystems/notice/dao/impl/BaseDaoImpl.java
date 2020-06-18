package com.wafersystems.notice.dao.impl;

import com.wafersystems.notice.dao.BaseDao;
import com.wafersystems.notice.model.PaginationDto;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * Created with Intellij IDEA. Description: Author: waferzy DateTime: 2016/6/23 16:29 Company: wafersystems
 *
 * @author wafer
 */
@Repository
public class BaseDaoImpl<T> implements BaseDao<T> {

  @Autowired
  @Qualifier("sessionFactory")
  private SessionFactory sessionFactory;

  /**
   * 获取session对象.
   *
   * @return session对象
   */
  public Session getSession() {
    // 事务必须是开启的(Required)，否则获取不到
    return sessionFactory.getCurrentSession();
  }

  private Criteria getCriteria(DetachedCriteria criteria) {
    return criteria.getExecutableCriteria(getSession());
  }

  /**
   * 根据id取得实体.
   *
   * @param clasz 实体
   * @param id    实体主键
   * @return 返回实体
   */
  @SuppressWarnings("unchecked")
  @Override
  public T load(Class<T> clasz, Serializable id) {
    return getSession().get(clasz, id);
  }

  /**
   * 根据HQL取得实体列表.
   *
   * @param resultSql 查询语句
   * @return 查询结果
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<T> findBySql(String resultSql) {
    return getSession().createQuery(resultSql).list();
  }

  /**
   * 根据HQL取得实体数量.
   *
   * @param criteria 查询条件
   * @return 查询结果
   */
  @SuppressWarnings("unchecked")
  @Override
  public Object getCount(DetachedCriteria criteria) {
    return getCriteria(criteria).setProjection(Projections.rowCount()).uniqueResult();
  }

  /**
   * 根据DetachedCriteria查询.
   *
   * @param criteria 查询条件
   * @return 查询结果
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<T> findByCriteria(DetachedCriteria criteria) {
    return getCriteria(criteria).list();
  }

  /**
   * 保存实体.
   *
   * @param clazs 实体对象
   */
  @Override
  public void save(Object clazs) {
    getSession().save(clazs);
  }

  /**
   * 根据id删除指定实体.
   *
   * @param clazs 实体的类别
   * @param id    主键
   */
  @Override
  public void delete(Class<T> clazs, Serializable id) {
    Object obj = getSession().get(clazs, id);
    if (obj != null) {
      getSession().delete(obj);
    }

  }

  /**
   * 更新实体对象.
   *
   * @param clazs 实体对象
   */
  @Override
  public void update(Object clazs) {
    getSession().update(clazs);

  }

  /**
   * Title: updateBySql. Description: 使用Sql语句进行更新
   *
   * @param sql Sql语句
   * @return 更新记录数
   */
  @Override
  public int updateBySql(String sql) {
    return getSession().createQuery(sql).executeUpdate();
  }

  /**
   * 更新或新增实体.
   *
   * @param clazs 实体对象
   */
  @Override
  public void saveOrUpdate(Object clazs) {
    getSession().saveOrUpdate(clazs);
  }

  /**
   * flush hibernate缓存.
   */
  @Override
  public void flush() {
    getSession().flush();
  }

  /**
   * Title: selectPage. Description: 使用查询条件对象分页查询
   *
   * @param detachedCriteria 查询组合条件
   * @param pageSize         分页大小
   * @param startIndex       起始页
   * @return PaginationDTO
   */
  @SuppressWarnings("unchecked")
  @Override
  public PaginationDto<T> selectPage(DetachedCriteria detachedCriteria, int pageSize, int startIndex) {
    // 如果翻页则只在第一次计算总页数，总记录数需要页面回传。
    Criteria criteria = getCriteria(detachedCriteria);
    Long tempTotalCount = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    int totalCount = Integer.parseInt(String.valueOf(tempTotalCount));
    PaginationDto<T> paginationDto = new PaginationDto<>();
    paginationDto.setRecords(totalCount);
    criteria.setProjection(null);
    criteria.setFirstResult((startIndex - 1) * pageSize);
    criteria.setMaxResults(pageSize);
    criteria.setResultTransformer(Criteria.ROOT_ENTITY);
    List<T> result = criteria.list();
    paginationDto.setLimit(pageSize);
    paginationDto.setPage(startIndex);
    paginationDto.setRows(result);
    return paginationDto;
  }
}
