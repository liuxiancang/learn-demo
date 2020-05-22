package com.lxc.jpa.springdatajpa;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.lxc.jpa.JpaApplicationTests;
import com.lxc.jpa.dao.CustomerDao;
import com.lxc.jpa.entity.Customer;

public class JPATest extends JpaApplicationTests {

	@Autowired
	private CustomerDao customerDao;

	/**
	 * 保存
	 */
	@Test
	@Ignore
	public void testSave() {
		Customer customer = new Customer();
		customer.setCustName("张三");
		customerDao.save(customer);

	}

	/**
	 * 更新
	 */
	@Test
	@Ignore
	public void testAlter() {
		Customer customer = new Customer();
		customer.setCustId(7L);
		customer.setCustName("李四");
		customerDao.save(customer);
	}

	/**
	 * 获取一个，延迟加载，一般不使用
	 */
	@Test
	@Transactional
	public void testGetOne() {
		Customer customer = customerDao.getOne(4L);
		System.out.println(customer);

	}

	/**
	 * 获取一个，立即加载
	 */
	@Test
	public void testFindById() {
		Optional<Customer> optional = customerDao.findById(2L);
		Customer customer = optional.get();
		System.out.println(customer);
	}

	/**
	 * 按主键删除
	 */
	@Test
	public void testDelete() {
		Customer customer = new Customer();
		customer.setCustId(3L);
		customerDao.delete(customer);

	}

	/**
	 * 查询所有
	 */
	@Test
	public void testFindAll() {
		List<Customer> findAll = customerDao.findAll();
	}

	/**
	 * 查询记录数
	 */
	@Test
	public void testCount() {
		long count = customerDao.count();
		System.out.println("count:" + count);
	}

	/**
	 * 通过select count(*) from table where id=? 查询该主键的记录是否存在
	 */
	@Test
	public void testExist() {
		boolean existsById = customerDao.existsById(2L);

	}

}
