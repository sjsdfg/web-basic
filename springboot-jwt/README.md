# Spring Boot - JWT

```shell
curl -X POST \
  http://localhost:8080/register \
  -H 'Content-Type: application/json' \
  -H 'Postman-Token: 0641c7fa-8946-49dd-83d7-0e194c5afaa7' \
  -H 'cache-control: no-cache' \
  -d '{
	username: "test",
	password: "test"
}'
```

```shell
curl -X GET \
  http://localhost:8080/secure/users/user \
  -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZXMiOiJtZW1iZXIiLCJpYXQiOjE1NTY0NDM2NTl9.-TQe5EozZQKKYEo9-NDYivoLDzm6o5BlGOZij5oDYY0' \
  -H 'Postman-Token: 2f6c3455-1727-4799-9065-6de0f4afcefd' \
  -H 'cache-control: no-cache'
```

# 参考资料

1. [Spring Boot整合JWT实现用户认证](https://blog.csdn.net/ltl112358/article/details/79507148)
2. [JwtBearer认证](https://www.cnblogs.com/Leo_wl/p/7792046.html#_label4)