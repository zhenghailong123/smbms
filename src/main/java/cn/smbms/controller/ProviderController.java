package cn.smbms.controller;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.tools.Constants;
import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/provider")
public class ProviderController {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    ProviderService providerService = (ProviderService) ctx.getBean("providerService");
    String path=null;

    @RequestMapping("/query")
    public String query(@RequestParam(value = "queryProName",required = false)String queryProName,
                         @RequestParam(value = "queryProCode",required = false)String queryProCode,
                         Model model){
        if(StringUtils.isNullOrEmpty(queryProName)){
            queryProName = "";
        }
        if(StringUtils.isNullOrEmpty(queryProCode)){
            queryProCode = "";
        }
//		ProviderService providerService = new ProviderServiceImpl();
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList(queryProName,queryProCode);
        model.addAttribute("providerList", providerList);
        model.addAttribute("queryProName", queryProName);
        model.addAttribute("queryProCode", queryProCode);
        return "providerlist";
    }

    @RequestMapping("/add")
    public String add(@RequestParam("proCode")String proCode,
                      @RequestParam("proName")String proName,
                      @RequestParam("proContact")String proContact,
                      @RequestParam("proPhone")String proPhone,
                      @RequestParam("proAddress")String proAddress,
                      @RequestParam("proFax")String proFax,
                      @RequestParam("proDesc")String proDesc,
                      HttpSession session){
        Provider provider = new Provider();
        provider.setProCode(proCode);
        provider.setProName(proName);
        provider.setProContact(proContact);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setCreatedBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setCreationDate(new Date());
        boolean flag = false;
        flag = providerService.add(provider);
        if(flag){
            path = "redirect:/provider/query";
        }else{
            path = "provideradd";
        }
        return path;
    }

    @RequestMapping("/modify")
    public String modify(@RequestParam("proCode")String proCode,
                         @RequestParam("proName")String proName,
                         @RequestParam("proContact")String proContact,
                         @RequestParam("proPhone")String proPhone,
                         @RequestParam("proAddress")String proAddress,
                         @RequestParam("proFax")String proFax,
                         @RequestParam("proDesc")String proDesc,
                         @RequestParam("id")String id,
                         HttpSession session){
        Provider provider = new Provider();
        provider.setId(Integer.valueOf(id));
        provider.setProContact(proContact);
        provider.setProName(proName);
        provider.setProPhone(proPhone);
        provider.setProFax(proFax);
        provider.setProAddress(proAddress);
        provider.setProDesc(proDesc);
        provider.setModifyBy(((User)session.getAttribute(Constants.USER_SESSION)).getId());
        provider.setModifyDate(new Date());
        boolean flag = false;
//		ProviderService providerService = new ProviderServiceImpl();
        flag = providerService.modify(provider);
        if(flag){
            path = "redirect:/provider/query";
        }else{
            path = "providermodify";
        }
        return path;
    }

    @RequestMapping("/view")
    public String getProviderById(@RequestParam("proid")String proid,
                                  @RequestParam("path")String path,
                                  Model model){
        if(!StringUtils.isNullOrEmpty(proid)){
            Provider provider = null;
//			ProviderService providerService = new ProviderServiceImpl();
            provider = providerService.getProviderById(proid);
            model.addAttribute("provider", provider);
        }
        return path;
    }

    @RequestMapping("/delprovider")
    @ResponseBody
    public String delProvider(@RequestParam("proid")String proid){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if(!StringUtils.isNullOrEmpty(proid)){
            int flag = providerService.deleteProviderById(proid);
            if(flag == 0){//删除成功
                resultMap.put("delResult", "true");
            }else if(flag == -1){//删除失败
                resultMap.put("delResult", "false");
            }else if(flag > 0){//该供应商下有订单，不能删除，返回订单数
                resultMap.put("delResult", String.valueOf(flag));
            }
        }else{
            resultMap.put("delResult", "notexit");
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "provideradd";
    }
}
