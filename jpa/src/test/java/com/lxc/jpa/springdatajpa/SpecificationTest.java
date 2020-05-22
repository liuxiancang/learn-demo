package com.lxc.jpa.springdatajpa;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.lxc.jpa.JpaApplicationTests;
import com.lxc.jpa.dao.CustomerDao;
import com.lxc.jpa.entity.Customer;

public class SpecificationTest extends JpaApplicationTests {

	@Autowired
	private CustomerDao customerDao;

	/**
	 * 因为我们的Dao接口继承了JpaSpecificationExecutor接口，所以findOne,findAll,count这几个方法都可以直接使用
	 */
	@Test
	public void testFindOne() {
		// 使用匿名内部类重写Specification接口中的方法，构造查询条件
		Specification<Customer> spec = new Specification<Customer>() {
			@Override
			public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> newQuery, CriteriaBuilder cb) {
				// 构造第一个查询条件
				Predicate p = cb.like(root.get("custName").as(String.class), "李四%");
				// 构造第二个查询条件，并且和第一个进行组合（and()为与，or()为或）
				p = cb.and(p, cb.equal(root.get("custLevel").as(String.class), "2"));
				return p;
			}
		};
		Optional<Customer> optional = customerDao.findOne(spec);
		System.out.println(optional.get());
	}

	@Test
	public void testFindAll() {
		// 使用匿名内部类重写Specification接口中的方法，构造查询条件
		Specification<Customer> spec = new Specification<Customer>() {
			// 重写toPredicate方法，构造出查询条件
			@Override
			public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// 获取对象属性路径
				Path<Object> path = root.get("custName");
				// 类型转换，Expression为Path的父接口
				Expression<String> as = path.as(String.class);
				// 精确匹配名字，获取查新条件,第一个参数为需要比较的属性，第二个参数为需要比较的值
				Predicate predicate = cb.equal(as, "李四");
				return predicate;
				// return cb.equal(root.get("custName").as(String.class), "李四");
			}
		};
		List<Customer> customers = customerDao.findAll(spec);
		System.out.println(customers);

	}

	/**
	 * Expression<T>接口中方法 Predicate in(Expression<Collection<?>>
	 * values);可以实现使用in构造条件
	 */
	@Test
	public void testFindAllIn() {
		Long[] arr = new Long[] { 1L, 2L, 3L, 4L };
		List<Long> idList = Arrays.asList(arr);
		List<Customer> customers = customerDao.findAll(new Specification<Customer>() {
			@Override
			public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> newQuery, CriteriaBuilder cb) {
				Predicate predicate = root.get("custId").as(Long.class).in(idList);
				return predicate;
			}
		});
		System.out.println(customers);
	}

	/**
	 * 条件+排序查询
	 */
	@Test
	public void testFindAllSort() {
		// 构造查询条件
		Specification<Customer> spec = new Specification<Customer>() {
			@Override
			public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> newQuery, CriteriaBuilder cb) {
				return cb.like(root.get("custName").as(String.class), "李四%");
			}
		};
		// 构造排序 第一个参数:排序规则 Sort.Direction.DESC(降序) Sort.Direction.ASC(升序)
		// 第二个参数:按哪个属性名排序,可以是多个，第一个属性值相同按第二个再排序
		Sort sort = new Sort(Sort.Direction.DESC, "custLevel", "custId");
		List<Customer> customers = customerDao.findAll(spec, sort);
		for (Customer customer : customers) {
			System.out.println(customer);
		}
	}

	/**
	 * 条件+分页查询
	 */
	@Test
	public void testFindAllPageable() {
		// 代表没有条件限制
		Specification<Customer> spec = null;
		int pageNum = 0;
		int pageSize = 3;
		// 使用PageRequest实现Pageable接口,第一个参数为页码（从0开始），第二个参数为每页的数量，这里使用的是无排序，所以无须第三个参数
		Pageable pageable = PageRequest.of(pageNum, pageSize);
		Page<Customer> page = customerDao.findAll(spec, pageable);
		// 获取整页的数据
		List<Customer> customers = page.getContent();
		// 获取下一页的页码，注意页码从0开始算
		int pageNumber = page.nextPageable().getPageNumber();
		// 获取总条数
		long totalElements = page.getTotalElements();
		// 获取总页数
		int totalPages = page.getTotalPages();

	}
}
