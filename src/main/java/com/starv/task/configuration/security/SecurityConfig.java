package com.starv.task.configuration.security;

import com.starv.task.api.account.service.UserDetailsServiceImpl;
import com.starv.task.configuration.security.handler.MyAuthenticationFailHander;
import com.starv.task.configuration.security.handler.MyAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //注入我们自己的AuthenticationProvider
    private final MyAuthenticationProvider provider;
    private final MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    private final MyAuthenticationFailHander myAuthenticationFailHander;
    //是在application.properites
    private final DataSource dataSource;
    //是在application.properites
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(MyAuthenticationProvider provider,
                          MyAuthenticationSuccessHandler myAuthenticationSuccessHandler,
                          MyAuthenticationFailHander myAuthenticationFailHander, DataSource dataSource,
                          UserDetailsServiceImpl userDetailsService) {
        this.provider = provider;
        this.myAuthenticationSuccessHandler = myAuthenticationSuccessHandler;
        this.myAuthenticationFailHander = myAuthenticationFailHander;
        this.dataSource = dataSource;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 记住我功能的token存取器配置
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // TODO Auto-generated method stub
//        //super.configure(http);
//        http
//                .formLogin().loginPage("/login").loginProcessingUrl("/login/form")
//                .successHandler(myAuthenticationSuccessHandler)
//                .failureHandler(myAuthenticationFailHander)
//                .permitAll()  //表单登录，permitAll()表示这个不需要验证 登录页面，登录失败页面
//                .and()
//                .authorizeRequests()
//                .antMatchers("/index").permitAll()  //这就表示 /index这个页面不需要权限认证，所有人都可以访问
//                .anyRequest().authenticated()
//                .and()
//                .csrf().disable();
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        // TODO Auto-generated method stub
//        //super.configure(http);
//        http
//                .formLogin().loginPage("/login").loginProcessingUrl("/login/form")
//                .successHandler(myAuthenticationSuccessHandler)
//                .failureHandler(myAuthenticationFailHander)
//                .permitAll()  //表单登录，permitAll()表示这个不需要验证 登录页面，登录失败页面
//                .and()
//                .authorizeRequests()
////                      .antMatchers("/index").permitAll()
////                .antMatchers("/whoim").hasRole("ADMIN")
////                .antMatchers(HttpMethod.POST,"/user/*").hasRole("ADMIN")
////                .antMatchers(HttpMethod.GET,"/user/*").hasRole("USER")
//                .anyRequest().access("@rbacService.hasPermission(request,authentication)")    //必须经过认证以后才能访问
//                .and()
//                .csrf().disable();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //super.configure(http);
        http
                .formLogin()//.loginPage("/login").loginProcessingUrl("/login/form")
                .successHandler(myAuthenticationSuccessHandler)
                .failureHandler(myAuthenticationFailHander)
                .permitAll()  //表单登录，permitAll()表示这个不需要验证 登录页面，登录失败页面
                .and()
                .rememberMe()
                .rememberMeParameter("remember-me").userDetailsService(userDetailsService)
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(60)
                .and()
                .authorizeRequests()
//                      .antMatchers("/index").permitAll()
//                .antMatchers("/whoim").hasRole("ADMIN")
//                .antMatchers(HttpMethod.POST,"/user/*").hasRole("ADMIN")
//                .antMatchers(HttpMethod.GET,"/user/*").hasRole("USER")
                //必须经过认证以后才能访问
                .anyRequest().access("@rbacService.hasPermission(request,authentication)")
                .and()
                .csrf().disable();

    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.authenticationProvider(provider);

//        auth.inMemoryAuthentication()
//                .passwordEncoder(new MyPasswordEncoder()).withUser("admin").password("123456").roles("USER")
//                .and()
//                .passwordEncoder(new MyPasswordEncoder()).withUser("test").password("test123").roles("ADMIN");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/js/**","/images/**","/css/**");
    }
}
