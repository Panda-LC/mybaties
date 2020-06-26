### 新增、删除和更新的代码思路
1、在XMLMapperBuilder解析UserMapper.xml的时候，将xml的节点类型nodeType记录下来，例如（select、delete、insert、update）；  
2、当通过DAO代理执行增删改查方法的时候，可以在调用invoke的时候，获取到statementId对应的节点类型nodeType，然后执行对应的操作；
