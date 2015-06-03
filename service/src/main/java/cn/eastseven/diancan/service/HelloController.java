package cn.eastseven.diancan.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/")
public class HelloController {

    private static final Logger log = Logger.getLogger(HelloController.class.getName());

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private SimpleMailMessage templateMessage;

    @RequestMapping(method = RequestMethod.GET, value = "/get")
    public void getMenu(HttpServletRequest req, HttpServletResponse res) {
        Set<Map<Object, Object>> result = Sets.newHashSet();
        String menu = "menu:" + sdf.format(Calendar.getInstance().getTime());
        for (String key : redisTemplate.keys(menu + "*")) {
            Set<Object> keys = redisTemplate.boundHashOps(key).keys();
            Map<Object, Object> rowdata = Maps.newHashMap();
            for(Object k : keys) {
                Object value = redisTemplate.boundHashOps(key).get(k);
                rowdata.put(k, value);
            }
            result.add(rowdata);
        }
        response(req, res, result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/upload")
    public void uploadMenu(@RequestParam(value = "menuContent", required = true) String menuContent, HttpServletRequest req, HttpServletResponse res) {
        log.info(menuContent);

        Set<Map<String, String>> result = Sets.newHashSet();
        if (!StringUtils.isEmpty(menuContent)) {
            String menu = "menu:" + sdf.format(Calendar.getInstance().getTime());
            Set<String> keys = redisTemplate.keys(menu + "*");
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
            }

            int index = 0;
            for (String rowdata : menuContent.split(";")) {
                if(StringUtils.isEmpty(rowdata)) continue;
                Map<String, String> data = Maps.newHashMap();
                String[] values = rowdata.split(",");
                String id = String.valueOf(index);
                String name = values[0].trim();
                String price = values[1].trim();
                data.put("id", id);
                data.put("name", name);
                data.put("price", price);
                redisTemplate.boundHashOps(menu + ":" + id).putAll(data);
                result.add(data);
                index++;
            }

            for (String key : redisTemplate.keys(menu + "*")) {
                log.info(key);
            }
        }

        response(req, res, result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/ordering")
    public void ordering(@RequestParam(value = "email") String user, @RequestParam(value = "id") String id, HttpServletRequest req, HttpServletResponse res) {
        String date = sdf.format(Calendar.getInstance().getTime());
        String menuKey = "menu:"+date+":"+id;
        log.info("ordering");
        String account = "account";
        String ordering = "ordering";
        String orderingHistory = "ordering:"+date;
        redisTemplate.boundValueOps(account).append(user);
        redisTemplate.boundHashOps(ordering).put(user, menuKey);
        redisTemplate.boundHashOps(orderingHistory).put(user, menuKey);
        Map<String, Object> result = Maps.newHashMap();
        result.put("success", Boolean.TRUE);

        String name = (String) redisTemplate.boundHashOps(menuKey).get("name");
        String price = (String) redisTemplate.boundHashOps(menuKey).get("price");
        String mail = user+"@digisky.com";
        String text = name+": "+price+"元";
        sendMail(mail, text);

        response(req, res, result);
    }

    private void sendMail(String mail, String text) {
        Date date = new Date();
        // Create a thread safe "copy" of the template message and customize it
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

        msg.setTo(mail);
        msg.setText(text+"\n"+date);
        msg.setSentDate(date);

        try {
            mailSender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/currentList")
    public void list(HttpServletRequest req, HttpServletResponse res) {
        String date = sdf.format(new Date());
        List<Map<String, Object>> result = Lists.newArrayList();
        String ordering = "ordering";
        Set<Object> users = redisTemplate.boundHashOps(ordering).keys();
        for(Object user : users) {
            Map<String, Object> data = Maps.newHashMap();
            String menuKey = (String) redisTemplate.boundHashOps(ordering).get(user);
            if(!menuKey.contains(date)) continue;
            Object name = redisTemplate.boundHashOps(menuKey).get("name");
            Object price = redisTemplate.boundHashOps(menuKey).get("price");
            data.put("user", user);
            data.put("foodName", name);
            data.put("price", price);
            result.add(data);
        }
        response(req, res, result);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/cancel")
    public void cancel(@RequestParam(value = "account", required = true) String account, HttpServletRequest req, HttpServletResponse res) {
        String date = sdf.format(Calendar.getInstance().getTime());
        List<Map<String, Object>> result = Lists.newArrayList();
        String ordering = "ordering";
        String orderingHistory = "ordering:"+date;
        redisTemplate.boundHashOps(ordering).delete(account);
        redisTemplate.boundHashOps(orderingHistory).delete(account);

        response(req, res, result);
    }

    private void response(HttpServletRequest req, HttpServletResponse res, Object result) {
        res.setContentType("text/plain");
        String callbackFunName = req.getParameter("callbackparam");//得到js函数名称
        PrintWriter pw = null;
        try {
            String resultJSON = callbackFunName + "(" + JSONObject.toJSONString(result) + ")";
            pw = res.getWriter();
            pw.write(resultJSON); //返回jsonp数据
            log.info(resultJSON);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(pw != null) pw.close();
        }
    }
}
