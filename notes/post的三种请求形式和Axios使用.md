# post的三种请求形式和Axios 跨域问题

## post的三种请求

### 1 application/x-www-form-urlencoded

1. 浏览器原生的form表单，enctype 的值不指定的话，默认
2. 数据编码采用key1=val1&key2=val2的形式，其中键值对都需要使用url Encode编码一下。其实就是和get的请求数据提交格式是一样的， 只不过从Request URL上换到了Request Body中
3. 数据层级很深的时候，或者需要上传二进制数据，就不太好用
4. 对应后端：

```java
@PostMapping("/test")
public String test(@RequesParam Map<String,String> mp){
    
}
```

5. 对应axios：axios默认是`application/json`的格式上传。如果需要上传该格式，需要使用qs进行序列化。

```javascript
// 对象data的属性名uname和upwd引号可加可不加
let data = {uname:"dingding",upwd:"123456"};

axios.post("/date.json", qs.stringify({ data }))
.then(res => {
    console.log(res);            
}
```



### 2 application/json

1. 上传数据格式是`{"name":"xfly","age": 24, "hobby":["x","xx","xxx"]}`
2. 是用于数据结构比较复杂，层级较深的情况
3. 对应后端（springboot会自动反序列化）

```java
@PostMapping("/test")
public String test(@RequestBody User user){
    
}
```

4. axios

```js
axios.post("/date.json", {
    username:"root1",
    password:"root1"
})
.then(res => {
    console.log(res);            
}
```





### 3 multipart/form-data

1. 需要上传二进制文件的时候





跨域 这个可以

```java
package org.example.backend.config;
//确保导入的包与你创建的包名一致
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
                .maxAge(3600);
    }
}

```

跨域 这个不行

```java
package org.example.backend.config;

import org.springframework.context.annotation.Configuration;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;s

@Configuration
public class CorsConfig implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PATCH, DELETE, PUT");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
    }
}
```

```java
package org.example.backend.config;

import org.springframework.context.annotation.Configuration;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class CorsConfig implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        if(origin!=null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        System.out.println("origin"+origin);

        String headers = request.getHeader("Access-Control-Request-Headers");
        if(headers!=null) {
            response.setHeader("Access-Control-Allow-Headers", headers);
            response.setHeader("Access-Control-Expose-Headers", headers);
        }

        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
    }
}
```

