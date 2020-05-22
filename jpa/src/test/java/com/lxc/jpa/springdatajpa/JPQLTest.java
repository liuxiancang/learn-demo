package com.lxc.jpa.springdatajpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.lxc.jpa.JpaApplicationTests;
import com.lxc.jpa.dao.CustomerDao;
import com.lxc.jpa.entity.Customer;

public class JPQLTest extends JpaApplicationTests {
	@Autowired
	private CustomerDao customerDao;

	/**
	 * 测试使用位置参数
	 */
	@Test
	public void testQueryByName() {
		List<Customer> customerList = customerDao.queryByName("张三");
		System.out.println(customerList);
	}

	/**
	 * 测试使用命名参数
	 */
	@Test
	public void testQueryByName2() {
		List<Customer> customers = customerDao.queryByName2("张三");
		System.out.println(customers);
	}

	@Test
	public void testQueryNameAndLevel() {

		List<Customer> queryByNameAndLevel = customerDao.queryByNameAndLevel("张三", "1");
		System.out.println(queryByNameAndLevel);
	}

	/**
	 * 测试更新,执行update或delete操作必须在事务中，所以必须加上@Transactional注解
	 * 并且因为在测试中事务默认是回滚的，所以这个测试不会更改数据库的数据,可以加上@Rollback(false)避免回滚
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void testUpdateById() {
		int effectedNum = customerDao.updateById(1L, "李四1");
		System.out.println(effectedNum);
	}

	// --------------------------测试SQL查询------------------------

	// 测试使用原生SQL
	@Test
	public void testQueryAll() {
		List<Object[]> queryAll = customerDao.queryAll("李四");
		for (Object[] obj : queryAll) {
			System.out.println(Arrays.toString(obj));
		}
	}

	@Test
	public void testQueryAllAndPageable() {
		Long[] arr = new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L };
		List<Long> ids = Arrays.asList(arr);
		int pageNum = 0;
		int pageSize = 2;
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<Customer> page = customerDao.queryAllAndPage(pageable);
		System.out.println(page.getContent());
	}

	@Test
	@Transactional // 调用@Modifying注解标识得方法都要加上注解
	@Rollback(false)
	public void testUpdateCustomer() {
		int effectedNum = customerDao.updateCustomer("lisi", 1L);
		System.out.println(effectedNum);
	}

	// --------------------------测试方法命名查询-------------------------

	@Test
	public void testFindByCustName() {
		List<Customer> customers = customerDao.findByCustNameAndCustLevel("李四", "1");
		for (Customer customer : customers) {
			System.out.println(customer);
		}
	}

	@Test
	public void testFindByCustIdIn() {
		Long[] arr = new Long[] { 1L, 2L, 3L };
		List<Long> ids = new ArrayList<Long>(Arrays.asList(arr));
		List<Customer> customers = customerDao.findByCustIdInAndCustNameLike(ids, "%李四%");
		for (Customer customer : customers) {
			System.out.println(customer);
		}
	}

	// 分页查询+in
	@Test
	public void testInAndPageable() {
		Long[] arr = new Long[] { 1L, 2L, 3L, 4L, 5L, 6L, 7L };
		List<Long> ids = Arrays.asList(arr);
		int pageNum = 0;
		int pageSize = 2;
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<Customer> page = customerDao.findByCustIdIn(ids, pageable);
	}
}
