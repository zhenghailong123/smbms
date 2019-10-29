package cn.smbms.controller;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.user.UserService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/bill")
public class BillController {

    ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    BillService billService = (BillService) ctx.getBean("billService");
    ProviderService providerService = (ProviderService) ctx.getBean("providerService");
    String path=null;

    @RequestMapping("/query")
    public String query(@RequestParam(value = "queryProductName",required = false)String queryProductName,
                        @RequestParam(value = "queryProviderId",required = false)String queryProviderId,
                        @RequestParam(value = "queryIsPayment",required = false)String queryIsPayment,
                        Model model){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("", "");
        model.addAttribute("providerList", providerList);
        if (StringUtils.isNullOrEmpty(queryProductName)) {
            queryProductName = "";
        }

        List<Bill> billList = new ArrayList<Bill>();
        Bill bill = new Bill();
        if (StringUtils.isNullOrEmpty(queryIsPayment)) {
            bill.setIsPayment(0);
        } else {
            bill.setIsPayment(Integer.parseInt(queryIsPayment));
        }

        if (StringUtils.isNullOrEmpty(queryProviderId)) {
            bill.setProviderId(0);
        } else {
            bill.setProviderId(Integer.parseInt(queryProviderId));
        }
        bill.setProductName(queryProductName);
//		BillService billService = new BillServiceImpl();
        billList = billService.getBillList(bill);
        model.addAttribute("billList", billList);
        model.addAttribute("queryProductName", queryProductName);
        model.addAttribute("queryProviderId", queryProviderId);
        model.addAttribute("queryIsPayment", queryIsPayment);
        return "billlist";
    }

    @RequestMapping("/add")
    public String add(@RequestParam("billCode")String billCode,
                      @RequestParam("productName")String productName,
                      @RequestParam(value = "productDesc",required = false)String productDesc,
                      @RequestParam("productUnit")String productUnit,
                      @RequestParam(value = "productCount",required = false)String productCount,
                      @RequestParam("totalPrice")String totalPrice,
                      @RequestParam("providerId")String providerId,
                      @RequestParam("isPayment")String isPayment,
                      HttpSession session){
        Bill bill = new Bill();
        bill.setBillCode(billCode);
        bill.setProductName(productName);
        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));
        bill.setCreatedBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
        bill.setCreationDate(new Date());
        System.out.println(bill.getProductName());
        boolean flag = false;
//		BillService billService = new BillServiceImpl();
        flag = billService.add(bill);
        System.out.println("add flag -- > " + flag);
        if (flag) {
            path = "redirect:/bill/query";
        } else {
            path="billadd";
        }
        return path;
    }

    @RequestMapping("/getproviderlist")
    @ResponseBody
    public String getProviderlist(){
        List<Provider> providerList = new ArrayList<Provider>();
        providerList = providerService.getProviderList("", "");
        return JSONArray.toJSONString(providerList);
    }

    @RequestMapping("/view")
    public String getBillById(@RequestParam("billid")String billid,
                              Model model,@RequestParam("path") String path){
        if (!StringUtils.isNullOrEmpty(billid)) {
//			BillService billService = new BillServiceImpl();
            Bill bill = null;
            bill = billService.getBillById(billid);
            model.addAttribute("bill", bill);
        }
        return path;
    }

    @RequestMapping("/modify")
    public String modify(@RequestParam("id")String id,
                         @RequestParam(value = "productName",required = false)String productName,
//                         @RequestParam(value = "productDesc",required = false)String productDesc,
                         @RequestParam(value = "productUnit",required = false)String productUnit,
                         @RequestParam(value = "productCount",required = false)String productCount,
                         @RequestParam(value = "totalPrice",required = false)String totalPrice,
                         @RequestParam(value = "providerId",required = false)String providerId,
                         @RequestParam(value = "isPayment",required = false)String isPayment,
                         HttpSession session){
        Bill bill = new Bill();
        bill.setId(Integer.valueOf(id));
        bill.setProductName(productName);
//        bill.setProductDesc(productDesc);
        bill.setProductUnit(productUnit);
        bill.setProductCount(new BigDecimal(productCount).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setIsPayment(Integer.parseInt(isPayment));
        bill.setTotalPrice(new BigDecimal(totalPrice).setScale(2, BigDecimal.ROUND_DOWN));
        bill.setProviderId(Integer.parseInt(providerId));

        bill.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
        bill.setModifyDate(new Date());
        boolean flag = false;
        flag = billService.modify(bill);
        if (flag) {
            path = "redirect:/bill/query";
        } else {
            path = "billmodify";
        }
        return path;
    }

    @RequestMapping("/delbill")
    @ResponseBody
    public String delBill(@RequestParam("billid")String billid){
        HashMap<String, String> resultMap = new HashMap<String, String>();
        if (!StringUtils.isNullOrEmpty(billid)) {
//			BillService billService = new BillServiceImpl();
            boolean flag = billService.deleteBillById(billid);
            if (flag) {//删除成功
                resultMap.put("delResult", "true");
            } else {//删除失败
                resultMap.put("delResult", "false");
            }
        } else {
            resultMap.put("delResult", "notexit");
        }
        return JSONArray.toJSONString(resultMap);
    }

    @RequestMapping("/toAdd")
    public String toAdd(){
        return "billadd";
    }
}
