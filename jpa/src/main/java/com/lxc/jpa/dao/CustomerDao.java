package com.lxc.jpa.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.lxc.jpa.entity.Customer;

public interface CustomerDao extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

	// --------------------通过JPQL查询-------------------------

	// 使用位置参数，？后面的数字是参数的索引（从1开始）
	@Query("select c from Customer c where c.custName=?1")
	List<Customer> queryByName(String custName);

	// 使用命名参数，方式为":+参数名"，当然也可以不使用@Param标识
//	@Query("from Customer where custName=:custName")
	@Query("select c from Customer c where c.custName=:custName")
	List<Customer> queryByName2(@Param("custName") String custName);

	// 当然也可以不使用@Param标识
	@Query("select c from Customer c where c.custName=:custName and c.custLevel=:custLevel")
	List<Customer> queryByNameAndLevel(String custName, String custLevel);

	// 更新操作,必须加上@Modifying注解，而且只能返回void或int/Integer类型的数据，如果为int代表影响的行数
	@Query("update Customer set custName=:custName where id=:id")
	@Modifying
	int updateById(Long id, String custName);

	// ----------------------通过SQL查询----------------------

	/**
	 * JPQL操作的是对象和属性，而SQL操作的是表和字段List<Customer> R
	 * 
	 * @Query 注解中value赋值JPQL或SQL nativeQuery: false 表示不使用本地查询，即使用JPQL true
	 *        表示使用本地查询，即使用SQL
	 */
	@Query(value = "select * from cst_customer where cust_name=:name", nativeQuery = true)
	List<Object[]> queryAll(String name);

	@Query(value = "select * from cst_customer", nativeQuery = true)
	Page<Customer> queryAllAndPage(Pageable pageable);

	@Query(value = "update cst_customer set cust_name=:name where cust_id=:id", nativeQuery = true)
	@Modifying
	int updateCustomer(String name, Long id);

	// -----------------------方法命名查询-----------------------

	/**
	 * 比如这里通过客户名称和客户等级来查询，约定使用findBy开头的命名方法
	 * 后面接上相对应的属性名称（首字母大写），并且注意参数的顺序必须和方法名中一致（但是具体形参名字可任意） 下面等同于JPQL @Query("from
	 * Customer where custName=?1 and custLevel=?2")
	 */
	List<Customer> findByCustNameAndCustLevel(String custName, String level);

	List<Customer> findByCustIdInAndCustNameLike(List<Long> ids, String name);

	// 分页+in 查询
	Page<Customer> findByCustIdIn(Collection<Long> ids, Pageable pageable);
}
