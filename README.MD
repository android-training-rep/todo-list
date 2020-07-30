
### 数据库升级时报错

#### 升级代码
```
static Migration migration_2_to_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("create table task(id int primary key, title text,content text,deadline text, isRemind boolean, isDelete boolean)");
        }
    };
```
#### 错误信息
```
 Expected:

TableInfo{name='Task', columns={
isRemind=Column{name='isRemind', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, 
id=Column{name='id', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=1, defaultValue='null'}, 
title=Column{name='title', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, 
deadline=Column{name='deadline', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, 
isDeleted=Column{name='isDeleted', type='INTEGER', affinity='3', notNull=true, primaryKeyPosition=0, defaultValue='null'}, 
content=Column{name='content', type='TEXT', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}}, 
foreignKeys=[], indices=[]}

 Found:

E/AndroidRuntime: TableInfo{name='Task', columns={
isDelete=Column{name='isDelete', type='boolean', affinity='1', notNull=false, primaryKeyPosition=0, defaultValue='null'}, 
isRemind=Column{name='isRemind', type='boolean', affinity='1', notNull=false, primaryKeyPosition=0, defaultValue='null'}, 
id=Column{name='id', type='int', affinity='3', notNull=false, primaryKeyPosition=1, defaultValue='null'}, 
title=Column{name='title', type='text', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, 
deadline=Column{name='deadline', type='text', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}, 
content=Column{name='content', type='text', affinity='2', notNull=false, primaryKeyPosition=0, defaultValue='null'}
}, foreignKeys=[], indices=[]}
        
```