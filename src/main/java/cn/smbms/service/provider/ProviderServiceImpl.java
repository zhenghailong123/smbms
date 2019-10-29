package cn.smbms.service.provider;

import cn.smbms.dao.bill.BillDao;
import cn.smbms.dao.provider.ProviderDao;
import cn.smbms.pojo.Provider;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("providerService")
public class ProviderServiceImpl implements ProviderService {

	@Autowired
	private ProviderDao providerDao;
	@Autowired
	private BillDao  billDao;
//	public ProviderServiceImpl(){
//		providerDao = new ProviderDaoImpl();
//		billDao = new BillDaoImpl();
//	}
    private SqlSession session;
	@Override
	public boolean add(Provider provider) {
		boolean flag = false;
		try {
			if(providerDao.add(provider) > 0){
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public List<Provider> getProviderList(String proName,String proCode) {
		List<Provider> providerList = null;
		System.out.println("query proName ---- > " + proName);
		System.out.println("query proCode ---- > " + proCode);
		try {
			providerList = providerDao.getProviderList(proName,proCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return providerList;
	}

	/**
	 * 业务：根据ID删除供应商表的数据之前，需要先去订单表里进行查询操作
	 * 若订单表中无该供应商的订单数据，则可以删除
	 * 若有该供应商的订单数据，则不可以删除
	 * 返回值billCount
	 * 1> billCount == 0  删除---1 成功 （0） 2 不成功 （-1）
	 * 2> billCount > 0    不能删除 查询成功（0）查询不成功（-1）
	 * 
	 * ---判断
	 * 如果billCount = -1 失败
	 * 若billCount >= 0 成功
	 */
	@Override
	public int deleteProviderById(String delId) {
		int billCount = -1;
		try {
			billCount = billDao.getBillCountByProviderId(delId);
			if(billCount == 0){
				providerDao.deleteProviderById(delId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			billCount = -1;
		}
		return billCount;
	}

	@Override
	public Provider getProviderById(String id) {
		Provider provider = null;
		try{
			provider = providerDao.getProviderById(id);
		}catch (Exception e) {
			e.printStackTrace();
			provider = null;
		}
		return provider;
	}

	@Override
	public boolean modify(Provider provider) {
		boolean flag = false;
		try {
			if(providerDao.modify(provider) > 0){
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
