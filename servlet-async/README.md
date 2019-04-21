# Servlet 异步请求

1. 开始异步处理

   ```java
   @WebServlet(value = "/async", asyncSupported = true)
   ```

2. 获取异步 Context

   ```java
   // 2. 开启异步模式
   AsyncContext asyncContext = req.startAsync();
   ```

3. 对业务逻辑进行异步处理

   ```java
   // 3. 业务逻辑开始进行异步处理
   asyncContext.start(() -> {
     
   });
   ```

4. 获取异步响应请求

   ```java
   asyncContext.start(() -> {
       try {
           sayHello();
           asyncContext.complete();
           // 4. 获取异步响应
           ServletResponse response = asyncContext.getResponse();
           response.getWriter().write("hello async....");
       } catch (Exception e) {
           e.printStackTrace();
       }
   });
   ```