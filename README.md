# EVA ORM: Reactive ORM based on JDBC driver and [RxJava](https://github.com/ReactiveX/RxJava) project for wrapping and provide async access to databases.

EVA ORM is part of an EVA project that provides the ability to create fully asynchronous reactive services with a minimum of dependencies. 

### At the moment, the project provides (Version 0.1-ALPHA):
 - CRUD operations to the database;
- Transactional;
- Configuring TOMCAT and DBCP pools - Apache Jakarta Commons DBCP (Database connection pooling services)

### At the moment, the project supports only the MySQL database


### Hello World

The **Hello World** program:

```java
package framework.eva.orm.example;

import framework.eva.orm.communication.Transaction;
import framework.eva.orm.table.Column;
import framework.eva.orm.table.Row;
import framework.eva.orm.table.Table;
import framework.eva.orm.table.TableCreator;
import io.reactivex.Completable;
import io.reactivex.Observable;

import java.util.UUID;

public class ExampleDAOLayer {
    private static Column<UUID> USER_ID_COLUMN = Column.UUIDColumn("id");
    private static Column<String> USER_NAME_COLUMN = Column.stringColumn("name");
    private static Column<String> USER_EMAIL_COLUMN = Column.stringColumn("email");
    private static Table<User> USER_TABLE = TableCreator.<User>setup("user_table")
            .addPrimaryColumn(USER_ID_COLUMN)
            .addColumn(USER_NAME_COLUMN)
            .addColumn(USER_EMAIL_COLUMN)
            .addBuilder(ExampleDAOLayer::mapUser)
            .done();

    public ExampleDAOLayer() {
    }

    public Observable<User> getUsers(Transaction transaction) {
        return USER_TABLE.select().buildTypedORx(transaction);
    }


    public Completable updateUser(Transaction transaction, User user) {
        return Completable.defer(() -> USER_TABLE
                .update(
                        USER_NAME_COLUMN.to(user.name),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email),
                        USER_EMAIL_COLUMN.to(user.email)
                )
                .withCondition(USER_ID_COLUMN.eq(user.id))
                .buildCRx(transaction)
        );
    }

    private static User mapUser(Row row) {
        User user = new User();
        user.id = row.getValue(USER_ID_COLUMN);
        user.name = row.getValue(USER_NAME_COLUMN);
        user.email = row.getValue(USER_EMAIL_COLUMN);
        return user;
    }
}
```

EVA ORM provides classes for mapping database tables into DAO layer:
 - [`framework.eva.orm.table.Table`]
 - [`framework.eva.orm.table.Column`]
 - [`framework.eva.orm.table.Row`]

Table can be parameterized by `T` with 
```java 
  TableCreator.<T>setup();
```
### Transactions
Transaction can be create on logic layer by TransactionManager.

```java
package framework.eva.orm.example;

import framework.eva.orm.communication.TransactionManager;
import io.reactivex.Completable;

public class ExampleLogicLayer {

    private ExampleDAOLayer daoLayer;

    public ExampleLogicLayer(ExampleDAOLayer daoLayer) {
        this.daoLayer = daoLayer;
    }

    public Completable replaceAllUserNamesToX() {
        return TransactionManager
                .wrapCompletable(transaction -> daoLayer
                        .getUsers(transaction)
                        .doOnNext(user -> user.name = "x")
                        .flatMapCompletable(user -> daoLayer.updateUser(transaction, user))
                );
    }
}
```

TransactionManager needs to be initialized with DatabaseFactory. DatabaseFactory configurable by path to database.ini file.

example for database.ini file:
```
# ================================================================
# DataBase Settings
# ================================================================

# DBCP - Apache Jakarta Commons DBCP (Database connection pooling services)
# TOMCAT - recomended
ConnectionsPoolType = DBCP
driverClassName=com.mysql.jdbc.Driver
url = jdbc:mysql://192.168.0.1/database_name
username=root
password=1234
# Set the number of connections that will be established when the connection pool is started.
initialSize = 5
# The maximum number of active connections that can be allocated from this pool at the same time
maxActive = 100
# The maximum number of connections that should be kept in the idle pool
maxIdle = 10
# The minimum number of established connections that should be kept in the pool at all times.
minIdle = 0
# The maximum number of milliseconds that the pool will wait (when there are no available connections and the
# {@maxActive} has been reached) for a connection to be returned before throwing an exception.
maxWait = 10000
# Time in milliseconds to keep this connection.
# When a connection is returned to the pool, the pool will check to see if the now - time-when-connected > maxAge has been reached,
# and if so, it closes the connection rather than returning it to the pool.
maxAge = 0

# The indication of whether objects will be validated before being borrowed from the pool.
# If the object fails to validate, it will be dropped from the pool, and we will attempt to borrow another.
# NOTE - for a true value to have any effect, the validationQuery parameter must be set to a non-null string.
# Default value is false In order to have a more efficient validation, see validationInterval Default value is false
testOnBorrow = false
# The indication of whether objects will be validated before being returned to the pool.
# NOTE - for a true value to have any effect, the validationQuery parameter must be set to a non-null string. The default value is true.
testOnReturn = false
# The indication of whether objects will be validated by the idle object evictor (if any).
# If an object fails to validate, it will be dropped from the pool. NOTE - for a true value to have any effect, the validationQuery parameter must be set to a non-null string.
testWhileIdle = true

# The SQL query that will be used to validate connections from this pool before returning them to the caller or pool
validationQuery = SELECT 1
# Avoid excess validation, only run validation at most at this frequency - time in milliseconds.
# If a connection is due for validation, but has been validated previously
# within this interval, it will not be validated again.
validationInterval = 30000

# The default auto-commit state of connections created by this pool.
defaultAutoCommit = true
defaultReadOnly = false
# boolean flag to remove abandoned connections if they exceed the @removeAbandonedTimeout.
removeAbandoned = true
# The time in seconds before a connection can be considered abandoned.
removeAbandonedTimeout = 60
# boolean flag to set if stack traces should be logged for application code which abandoned a Connection.
logAbandoned = false

# The minimum amount of time an object must sit idle in the pool before it is eligible for eviction (Default: 30 min)
minEvictableIdleTimeMillis = 1800000
# The number of milliseconds to sleep between runs of the idle connection validation, abandoned cleaner and idle pool resizing (Default: 10 min)
timeBetweenEvictionRunsMillis = 600000

jmxEnabled = false

# Statistic writing delay
statisticDelay = 300000
```

## LICENSE

    Copyright (c) 2020-present, EVA ORM Contributors.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
