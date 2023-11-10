package com.cs_liudi.community.controller;

import com.cs_liudi.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/test")
public class HelloController {

    @Autowired
    private AlphaService alphaService;

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "hello,spring boot!!";
    }
    @ResponseBody
    @RequestMapping("/data")
    public String getData(){
        return alphaService.find();
    }
    //原生请求数据 返回数据
    @ResponseBody
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name+" "+value);
        }
        System.out.println(request.getParameter("code"));

        try (
                PrintWriter writer = response.getWriter();
                )
        {
            writer.write("<h1>Spring MVC</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //发出请求
    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(@RequestParam(name = "current",required = false,defaultValue = "1") int current,
                              @RequestParam(name = "limit",required = false,defaultValue = "10")int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable(name = "id",required = false) int id){
        System.out.println(id);
        return "a students";
    }

    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String getStudent(String name,String password){
        System.out.println(name);
        System.out.println(password);
        return "success";
    }

    //响应数据
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age","30");
        mav.setViewName("/demo/view");
        return mav;
    }

    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","清华大学");
        model.addAttribute("age","100");
        return "/demo/view";
    }

    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public HashMap getEmp(){
        HashMap<String,String> map = new HashMap<>();
        map.put("id","1");
        map.put("name","张三");
        map.put("salary","7000");
        return map;
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List getEmps(){
        List <HashMap<String,String>> list = new ArrayList<>();
        HashMap<String,String> emp = new HashMap<>();
        emp.put("id","1");
        emp.put("name","张三");
        emp.put("salary","7000");
        list.add(emp);
        emp = new HashMap<>();
        emp.put("id","2");
        emp.put("name","李四");
        emp.put("salary","9000");
        list.add(emp);
        emp = new HashMap<>();
        emp.put("id","3");
        emp.put("name","王五");
        emp.put("salary","11000");
        list.add(emp);
        return list;
    }
}
