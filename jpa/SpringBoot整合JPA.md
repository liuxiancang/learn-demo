[TOC]



### 1. Spring Data JPA、JPA和Hibernate的关系

关于这三者的关系网上已经有很多解释了，我就简单说一下吧。`JPA`是一套规范（提供统一的接口和抽象类），`Hibernate`正是实现`JPA`规范的优秀`ORM`框架之一，而`Spring Data JPA`进一步对`Hibernate`进行了封装，是`Spring`提供的一套简化的 `JPA` 开发的框架，使其操作起来更简单。所以`Spring Data JPA`的提供商是`Hibernate`，即干活的其实是`Hibernate`。



### 2. 相关依赖

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```



### 3. 配置数据库连接和JPA

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jpa?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    driver-class-name: com.mysql.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate #等同于hibernate.hbm2ddl.auto
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect  #配置数据库的方言，因为不同的数据库有不同的语法
    open-in-view: true #对hibernate来说ToMany关系默认是延迟加载，而ToOne关系则默认是立即加载；而在mvc的controller中脱离了persisent contenxt，于是entity变成了detached状态，这个时候要使用延迟加载的属性时就会抛出LazyInitializationException异常，而Open Session In View指在解决这个问题
    show-sql: true #在控制台中打印sql语句
```

上面的配置中需要单独说一下 `spring.jpa.hibernate.ddl-auto=create`这个配置选项。

这个属性常用的选项有五种：

1. `create`:每次重新启动项目都会重新创新表结构，会导致数据丢失
2. `create-drop`:每次启动项目时创建表结构，关闭项目时删除表结构
3. `update`:每次启动项目会更新表结构，如果表存在只是更新而不是重新创建
4. `validate`:验证表结构，不对数据库进行任何更改
5. `none`:不使用`Hibernate Auto DDL`功能，:bulb:在生产环境中最好使用这个

但是，**一定要不要在生产环境使用 ddl 自动生成表结构，一般推荐手写 SQL 语句配合 Flyway 来做这些事情。**



### 4. 创建数据表

```sql
/*创建客户表*/
    CREATE TABLE cst_customer (
      cust_id bigint(32) NOT NULL AUTO_INCREMENT COMMENT '客户编号(主键)',
      cust_name varchar(32) NOT NULL COMMENT '客户名称(公司名称)',
      cust_source varchar(32) DEFAULT NULL COMMENT '客户信息来源',
      cust_industry varchar(32) DEFAULT NULL COMMENT '客户所属行业',
      cust_level varchar(32) DEFAULT NULL COMMENT '客户级别',
      cust_address varchar(128) DEFAULT NULL COMMENT '客户联系地址',
      cust_phone varchar(64) DEFAULT NULL COMMENT '客户联系电话',
      PRIMARY KEY (`cust_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```



### 5. 创建实体类

```java
/**
 * 实体类 
 * 1.主要建立实体类和数据表之间的映射关系：
 *   @Entity 指明当前类为实体类
 *   @Table 指定实体类和哪个数据表建立映射关系
 * 
 * 2.建立实体类成员变量和数据表字段之间的映射关系
 *   @Id 声明当前成员变量对应数据表中的主键
 *   @GeneratedValue 指定主键的生成策略
 *   @column 指明当前成员变量具体和数据表中哪个字段建立映射关系 以上注解都来自javax.persistence包
 */
@Entity
@Table(name = "tb_customer")
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cust_id")
	private Long custId;

	@Column(name = "cust_name")
	private String custName;

	@Column(name = "cust_source")
	private String custSource;

	@Column(name = "cust_industry")
	private String custIndustry;

	@Column(name = "cust_level")
	private String custLevel;

	@Column(name = "cust_address")
	private String custAddress;

	@Column(name = "cust_phone")
	private String custPhone;
    
    //省略getter和setter
```

对于上面的注解，:bulb:重点说一下`@GeneratedValue`的生成策略：

基于`annotation`的`hibernate`主键标识为`@Id`, 其生成规则由`@GeneratedValue`设定。这里的`@id`和`@GeneratedValue`都是`JPA`的标准用法。

`@GeneratedValue JPA`提供的四种标准用法为`TABLE,SEQUENCE,IDENTITY,AUTO`。

:one:**IDENTITY**

主键由数据库自动生成（主要是自动增长型，即自增主键，`mysql`支持，`oracle`不支持）

```java
@Id  
@GeneratedValue(strategy = GenerationType.IDENTITY) 
private Long custId;
```

:two: ​**SEQUENCE**

根据底层数据库的序列来生成主键，条件是数据库支持序列（`mysql`不支持，`oracle`支持）

```java
@Id  
@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="payablemoney_seq")  
@SequenceGenerator(name="payablemoney_seq", sequenceName="seq_payment")  
private Long custId;

//@SequenceGenerator源码中的定义
@Target({TYPE, METHOD, FIELD})   
@Retention(RUNTIME)  
public @interface SequenceGenerator {  
   //表示该表主键生成策略的名称，它被引用在@GeneratedValue中设置的“generator”值中
   String name();  
   //属性表示生成策略用到的数据库序列名称。
   String sequenceName() default "";  
   //表示主键初识值，默认为0
   int initialValue() default 0;  
   //表示每次主键值增加的大小，例如设置1，则表示每次插入新记录后自动加1，默认为50
   int allocationSize() default 50;  
 }
```

:bulb:其他`JPA`中的注解可以看看[JPA常用注解](https://www.jianshu.com/p/1b759ef26ff3)这篇文章



### 6. 创建操作数据库的Dao接口

`Spring Data JPA`是`spring`提供的一款对于数据访问层（`Dao`层）的框架，使用`Spring Data JPA`，只需要按照框架的规范提供`dao`接口，不需要实现类就可以完成数据库的增删改查、分页查询等方法的定义，极大的简化了我们的开发过程。只要定义一个`dao`接口继承`JpaRepository`和`JpaSpecificationExecutor`接口就行，等到调用的时候会通过动态代理实现相对应的接口。

:one:**JpaResponse<所操作的实体类的类型，相对应主键的类型>​，主要实现对数据库的增删改查。**

:two:**​JpaSpecificationExecutor<所操作的实体类的类型>，主要用于复杂查询，比如分页。**

```java
public interface CustomerDao extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

}
```

#### 6.1  JPA自带方法实战

**1.保存用户到数据库**

```java
Customer customer = new Customer();
customer.setCustName("张三");
customerDao.save(customer);  //<S extends T> S save(S entity); T为实体类类型
```

**2.更新用户**

更新操作也要通过 `save()`方法来实现，比如：

```java
Customer customer = new Customer();
customer.setCustId(7L);
//更新客户名字
customer.setCustName("李四");
customerDao.save(customer); //按customer对象进行所有字段的更新
```

这里的`save()`方法，先会到数据库中查询是否有这个`id`的客户，如果有就进行更新操作，如果没有则进行保存操作。

**3.根据 id 查找用户**

```java
Optional<Customer> optional = customerDao.findById(2L);
Customer customer = optional.get();
System.out.println(customer);
```

:x:在老的`API`中，可以通过`T findOne(ID id)`方法进行查询一个的操作，但是最新的`API`对这进行了更改，变成了`<S extends T> Optional<S> findOne(Example<S> example);`，大家务必要注意。

:bulb:除了`findById()`可以查询一个，方法`T getOne(ID id)`也可以实现这个功能，但是这个方法采用的是延迟加载（得到的是一个代理对象，而不是实体对象本身，所以如果没有查询到该记录就会抛出异常。并且容易抛出`LazyInitializationException`异常，可以加上`@Transaction`来解决。因为这个方法的复杂性，**所以该方法尽量少用**）。而`findById()`采用的是立即加载，得到的是一个对象，如果没有查询到该记录，返回`null`。

**4.根据 id 删除用户**

```java
customerDao.deleteById(3L);  //void deleteById(ID id);
//或者
Customer customer = new Customer();
customer.setCustId(3L);
customerDao.delete(customer);   //void delete(T entity);
//上面两种方法本质都是通过主键删除
```

**5.查询所有**

```java
List<Customer> findAll = customerDao.findAll();
```



### 7. 使用JPQL查询

使用`Spring Data JPA`提供的查询方法已经可以解决大部分的应用场景，但是对于某些业务来说，我们还需要灵活的构造查询条件，这时就可以使用`@Query`注解，结合`JPQL`的语句方式完成查询。`JPQL`与原生`SQL`语句类似，并且完全面向对象，通过类名和属性访问，而不是表名和表的属性，这种语言编写的查询语句具有可移植性，能编译成多个主流数据库使用的`SQL`。

:bulb:`JPQL`语句支持两种方式的参数定义方式: 命名参数和位置参数。

```java
public interface CustomerDao extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

	//使用位置参数，？后面的数字是参数的索引（从1开始）
	@Query("select c from Customer c where c.custName=?1")
	List<Customer> queryByName(String custName);

	//使用命名参数，方式为":+参数名"，当然也可以不使用@Param标识
    //@Query("from Customer where custName=:custName") 这个也可以
	@Query("select c from Customer c where c.custName=:custName")
	List<Customer> queryByName2(@Param("custName") String custName);
    
    // 当然也可以不使用@Param标识，但是不可以位置参数和命名参数同时混合用
	@Query("select c from Customer c where c.custName=:custName and c.custLevel=:custLevel")
	List<Customer> queryByNameAndLevel(String custName, String custLevel);
}
```

对于更新和删除操作，需要多添加一个`@Modifying`注解

```java
// 更新操作,必须加上@Modifying注解，而且只能返回void或int/Integer类型的数据，如果为int代表影响的行数
@Query("update Customer set custName=:name where id=:id")
@Modifying
int updateById(Long id, String name);

//同时更新和删除的测试用例也有些差别
/**
 * 测试更新,执行update或delete操作必须在事务中，所以必须加上@Transactional注解
 * 并且因为在测试中事务默认是回滚的，所以这个测试不会更改数据库的数据,可以加上@Rollback(false)避免回滚
 */
@Test
@Transactional
@Rollback(false)
public void testUpdateById() {
	int effectedNum = customerDao.updateById(1L, "李四");
	System.out.println(effectedNum);
}
```

其他关于`JPQL`的操作可以查看[jpql的学习](<https://www.jianshu.com/p/4a4410075bab>)



### 8. 使用SQL查询

`Spring Data Jpa`不仅支持`JPQL`查询，还支持原生的`SQL`查询。同样在`Dao`的自定义方法上面加上`@Query`声明。

```java
/**
 * JPQL操作的是对象和属性，而SQL操作的是表和字段List<Customer>
 * 
 * @Query 注解中value赋值JPQL或SQL 
 *		  nativeQuery: false 表示不使用本地查询，即使用JPQL 
 *					   true  表示使用本地查询，即使用SQL
 *  从下面的返回值我们可以看到，返回的对象只能拆分成一个个属性保存在数组中 
 */
@Query(value = "select * from cst_customer where cust_name=:name", nativeQuery = true)
List<Object[]> queryAll(String name);

// 测试使用原生SQL
@Test
public void testQueryAll() {
	List<Object[]> queryAll = customerDao.queryAll("李四");
	for (Object[] obj : queryAll) {
		System.out.println(Arrays.toString(obj));
	}
}

//同样也支持分页，具体使用方法见下面
@Query(value = "select * from cst_customer", nativeQuery = true)
Page<Customer> queryAllAndPage(Pageable pageable);
```

:bulb:和`JPQL`一样，更新和删除操作必须加上`@Modifying`注解，并且如果一个方法调用声明了`@Modifying`注解的方法，那么该方法必须加上事务。





### 9. 方法名命名规则查询

方法命名规则查询就是根据方法的名字(**约定命名规范**)，就能创建查询，本质是对`JPQL`语句的进一步封装，会自动生成`JPQL`语句，`JPQL`在运行时编译成`SQL`。只需要按照`Spring Data JPA`提供的方法命名规则定义方法的名称，就可以完成查询工作。`Spring Data JPA`在程序执行的时候会根据方法名称进行解析，并自动生成查询语句进行查询。

```java
public interface CustomerDao extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
	/**
	 * 比如这里通过客户名称和客户等级来查询，约定使用findBy开头的命名方法
	 * 后面接上相对应的属性名称（首字母大写），并且注意参数的顺序必须和方法名中一致（但是具体形参名字可任意） 下面等同于JPQL @Query("from Customer where custName=?1 and custLevel=?2")
	 */
	List<Customer> findByCustNameAndCustLevel(String custName, String level);
}

// 等同于JPQL:from Customer where custId in(?1) and custName like ?2
List<Customer> findByCustIdInAndCustNameLike(List<Long> ids, String name);
```

:bulb:**也可以使用方法命名实现分页查询**

```java
//在Dao接口中定义方法
// 分页+in 查询
Page<Customer> findByCustIdIn(Collection<Long> ids, Pageable pageable);

//测试
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
```

命名规范如下：(大概为`findBy+属性名`和`findBy+属性名+查询方式`)

| Keyword                | Sample                                      | JPQL snippet                                                 |
| :--------------------- | :------------------------------------------ | :----------------------------------------------------------- |
| `And`                  | `findByLastnameAndFirstname`                | `… where x.lastname = ?1 and x.firstname = ?2`               |
| `Or`                   | `findByLastnameOrFirstname`                 | `… where x.lastname = ?1 or x.firstname = ?2`                |
| `Is`, `Equals`         | `findByFirstnameIs`,`findByFirstnameEquals` | `… where x.firstname = ?1`                                   |
| `Between`              | `findByStartDateBetween`                    | `… where x.startDate between ?1 and ?2`                      |
| `LessThan`             | `findByAgeLessThan`                         | `… where x.age < ?1`                                         |
| `LessThanEqual`        | `findByAgeLessThanEqual`                    | `… where x.age <= ?1`                                        |
| `GreaterThan`          | `findByAgeGreaterThan`                      | `… where x.age > ?1`                                         |
| `GreaterThanEqual`     | `findByAgeGreaterThanEqual`                 | `… where x.age >= ?1`                                        |
| `After`                | `findByStartDateAfter`                      | `… where x.startDate > ?1`                                   |
| `Before`               | `findByStartDateBefore`                     | `… where x.startDate < ?1`                                   |
| `IsNull`, `Null`       | `findByAge(Is)Null`                         | `… where x.age is null`                                      |
| `IsNotNull`, `NotNull` | `findByAge(Is)NotNull`                      | `… where x.age not null`                                     |
| `Like`                 | `findByFirstnameLike`                       | `… where x.firstname like ?1`                                |
| `NotLike`              | `findByFirstnameNotLike`                    | `… where x.firstname not like ?1`                            |
| `StartingWith`         | `findByFirstnameStartingWith`               | `… where x.firstname like ?1` (parameter bound with appended `%`) |
| `EndingWith`           | `findByFirstnameEndingWith`                 | `… where x.firstname like ?1` (parameter bound with prepended `%`) |
| `Containing`           | `findByFirstnameContaining`                 | `… where x.firstname like ?1` (parameter bound wrapped in `%`) |
| `OrderBy`              | `findByAgeOrderByLastnameDesc`              | `… where x.age = ?1 order by x.lastname desc`                |
| `Not`                  | `findByLastnameNot`                         | `… where x.lastname <> ?1`                                   |
| `In`                   | `findByAgeIn(Collection<Age> ages)`         | `… where x.age in ?1`                                        |
| `NotIn`                | `findByAgeNotIn(Collection<Age> ages)`      | `… where x.age not in ?1`                                    |
| `True`                 | `findByActiveTrue()`                        | `… where x.active = true`                                    |
| `False`                | `findByActiveFalse()`                       | `… where x.active = false`                                   |
| `IgnoreCase`           | `findByFirstnameIgnoreCase`                 | `… where UPPER(x.firstame) = UPPER(?1)`                      |



### 10. Specification动态查询

有时我们在查询某个实体的时候，给定的条件是不固定的，这时就需要动态构建相应的查询语句，在`Spring Data JPA`中可以通过`JpaSpecificationExecutor`接口查询。相比`JPQL`，其优势是类型安全，更加的面向对象。对于`JpaSpecificationExecutor`，这个接口基本是围绕着`Specification`接口来定义的。我们可以简单的理解为，`Specification`构造的就是查询条件。

:bulb:其中​`Specification`接口中有一个方法，只要重写这个方法就可以构造出查询条件

```java
 //构造查询条件
    /**
    *	Root	        ：Root接口，代表查询的根对象，可以通过root获取实体中的属性
    *	CriteriaQuery	：代表一个顶层查询对象，用来自定义查询
    *	CriteriaBuilder	：用来构建查询，此对象里有很多条件方法，比如like(模糊查询)，equal(精确查询)     *                     等构造条件的方法，也有组合条件的方法and()、or()
    **/
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb);
```

##### 条件查询

下面我们将展示如何实现单个条件的查询：

```java
/**
 * 因为我们的Dao接口继承了JpaSpecificationExecutor接口，所以findOne,findAll,count这几个方法都可以直接使用
 */
@Test
public void testFindAll() {
	// 使用匿名内部类重写Specification接口中的方法，构造查询条件
    // 其中Specification需要提供泛型，是实体类的类型
	Specification<Customer> spec = new Specification<Customer>() {
		// 重写toPredicate方法，构造出查询条件
		@Override
		public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			// 获取对象属性路径
			Path<Object> path = root.get("custName");
			// 类型转换，Expression为Path的父接口，参数为属性类型字节码
			Expression<String> as = path.as(String.class);
			// 精确匹配名字，获取查新条件,第一个参数为需要比较的属性，第二个参数为需要比较的值
			Predicate predicate = cb.equal(as, "李四");
			return predicate;
			//上面的操作等同于 return cb.equal(root.get("custName").as(String.class), "李四");
		}
	};
	List<Customer> customers = customerDao.findAll(spec);
	System.out.println(customers);
}
```

也可以同时组合几个条件进行查询：

```java
@Test
public void testFindOne() {
	// 使用匿名内部类重写Specification接口中的方法，构造查询条件
	Specification<Customer> spec = new Specification<Customer>() {
		@Override
		public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> newQuery, CriteriaBuilder cb) {
			//构造第一个查询条件
			Predicate p=cb.like(root.get("custName").as(String.class),"李四%");
			//构造第二个查询条件，并且和第一个进行组合（and()为与，or()为或）
			p=cb.and(p,cb.equal(root.get("custLevel").as(String.class), "2"));
			return p;
		}
	};
	Optional<Customer> optional = customerDao.findOne(spec);
	System.out.println(optional.get());
}
```

:o:构造跟`SQL`语句中`in`匹配相类似的查询条件和上面方法有些区别，具体如下：

```java
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
            //只需要使用Root<Customer> root这一个参数
			Predicate predicate = root.get("custId").as(Long.class).in(idList);
			return predicate;
		}
	});
	System.out.println(customers);
}
```

##### 条件+排序查询

```java
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
	// 构造排序 第一个参数:排序规则 Sort.Direction.DESC(降序) Sort.Direction.ASC(升序) 第二个参数:按哪个属性名排序,可以是多个，第一个属性值相同按第二个再排序(排序规则依然是第一个)
	Sort sort = new Sort(Sort.Direction.DESC, "custLevel", "custId");
	List<Customer> customers = customerDao.findAll(spec, sort);
	for (Customer customer : customers) {
		System.out.println(customer);
	}
}
```

##### 条件+分页查询

```java
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
```



