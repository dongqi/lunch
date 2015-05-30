package cn.eastseven.diancan.service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@Controller
@RequestMapping("/")
public class HelloController {

    private static final Logger log = Logger.getLogger(HelloController.class.getName());

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
    public void ordering(@RequestParam(value = "email") String user, @RequestParam(value = "id") String menuKey, HttpServletRequest req, HttpServletResponse res) {
        log.info("ordering");
        String account = "account";
        String ordering = "ordering";
        String orderingHistory = "ordering:"+sdf.format(Calendar.getInstance().getTime());
        redisTemplate.boundValueOps(account).append(user);
        redisTemplate.boundHashOps(ordering).put(user, menuKey);
        redisTemplate.boundHashOps(orderingHistory).put(user, menuKey);
        Map<String, Object> result = Maps.newHashMap();
        result.put("success", Boolean.TRUE);
        response(req, res, result);
    }

    private void response(HttpServletRequest req, HttpServletResponse res, Object result) {
        res.setContentType("text/plain");
        String callbackFunName = req.getParameter("callbackparam");//得到js函数名称
        try {
            String resultJSON = callbackFunName + "(" + JSONObject.toJSONString(result) + ")";
            res.getWriter().write(resultJSON); //返回jsonp数据
            log.info(resultJSON);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
