### Mybatis动态sql是做什么的？都有哪些动态sql？简述一下动态sql的执行原理？
动态sql:便于根据程序动态条件的情况下，生成标准化的sql语句  
常用的有\<if>、\<where>、\<foreach>、\<choose>、\<when>、\<otherwise>、\<trim>、\<set>  
动态sql的执行原理：根据xml解析出对应的节点node，然后根据node的类型做对应的字符串处理



### Mybatis是否支持延迟加载？如果支持，它的实现原理是什么？
支持  
通过动态代理的方式，例：一对多的情况下，子查询可以设置延迟加载，只有程序中主动去获取该元素的时候，mybaties会使用动态代理，获取该子查询的对象是否为空，如果为空，则会去执行该子查询sql，然后将结果封装进来



### Mybatis都有哪些Executor执行器？它们之间的区别是什么？
自带的执行器包括SimpleExecutor、ReuseExecutor、BatchExecutor和CachingExecutor；  
执行器类型 | 描述
----  | ---- 
SimpleExecutor | 每句sql的执行，都会新建Statement去执行 
ReuseExecutor | 同样的sql，如果对应的Statement未关闭前，下一次同sql进来执行时，会复用该Statement，该复用仅限于同一个SqlSession 
BatchExecutor | 支持同时执行多条sql
ReuseExecutor | 先命中缓存，缓存中没有的情况下，再根据不同配置，调用以上三种执行器去操作数据库 



### 简述下Mybatis的一级、二级缓存（分别从存储结构、范围、失效场景。三个方面来作答）？
缓存类型 | 存储结构 | 范围 | 失效场景
----  | ---- | ---- | ---- 
一级缓存 | Map<Object,Object> | sqlSession级别 | 当前sqlSession调用增删改的时候，或者数据库连接调用提交和关闭的时候，会删除当前sqlSession的缓存
二级缓存 | Map<Object,Object> | sqlSessionFactory级别 | 只跟失效时间有关
  


### 简述Mybatis的插件运行原理，以及如何编写一个插件？
插件实现的核心实现是拦截器  
新增插件只需要实现Interceptor接口，同时设置该拦截器切割的类：方法范围，然后将该插件放到sessionFactory实例中即可  
