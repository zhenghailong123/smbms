package cn.smbms.service.bill;

import cn.smbms.dao.bill.BillDao;
import cn.smbms.pojo.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("billService")
public class BillServiceImpl implements BillService {
	@Autowired
	private BillDao billDao;
//	public BillServiceImpl(){
//		billDao = new BillDaoImpl();
//	}
	@Override
	public boolean add(Bill bill) {
		boolean flag = false;
		try {
			if(billDao.add(bill) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public List<Bill> getBillList(Bill bill) {
		List<Bill> billList = null;
		System.out.println("query productName ---- > " + bill.getProductName());
		System.out.println("query providerId ---- > " + bill.getProviderId());
		System.out.println("query isPayment ---- > " + bill.getIsPayment());
		try {
			billList = billDao.getBillList(bill);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return billList;
	}

	@Override
	public boolean deleteBillById(String delId) {
		boolean flag = false;
		try {
			if(billDao.deleteBillById(delId) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@Override
	public Bill getBillById(String id) {
		Bill bill = null;
		try{
			bill = billDao.getBillById(id);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return bill;
	}

	@Override
	public boolean modify(Bill bill) {
		boolean flag = false;
		try {
			if(billDao.modify(bill) > 0)
				flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

}
