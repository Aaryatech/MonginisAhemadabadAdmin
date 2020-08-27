package com.ats.adminpanel.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.ats.adminpanel.commons.AccessControll;
import com.ats.adminpanel.commons.Constants;
import com.ats.adminpanel.commons.DateConvertor;
import com.ats.adminpanel.model.AllFrIdNameList;
import com.ats.adminpanel.model.AllspMessageResponse;
import com.ats.adminpanel.model.ErrorMessage;
import com.ats.adminpanel.model.FlavourConf;
import com.ats.adminpanel.model.FlavourList;
import com.ats.adminpanel.model.Info;
import com.ats.adminpanel.model.Main;
import com.ats.adminpanel.model.SearchSpCakeResponse;
import com.ats.adminpanel.model.SpCakeOrder;
import com.ats.adminpanel.model.SpCakeOrderRes;
import com.ats.adminpanel.model.SpCakeResponse;
import com.ats.adminpanel.model.accessright.ModuleJson;
import com.ats.adminpanel.model.flavours.Flavour;
import com.ats.adminpanel.model.franchisee.AllMenuResponse;
import com.ats.adminpanel.model.franchisee.FranchiseeList;
import com.ats.adminpanel.model.franchisee.Menu;
import com.ats.adminpanel.model.grngvn.FrSetting;
import com.ats.adminpanel.model.manspbill.SpecialCake;
import com.ats.adminpanel.model.masters.SpMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AbcController {

	FlavourList flavourList;
	int billNo = 0;
	public AllFrIdNameList allFrIdNameList = new AllFrIdNameList();
	SpecialCake specialCake = new SpecialCake();
	List<SpMessage> spMessageList;

	List<Menu> menuList;

	@RequestMapping(value = "/showAddMultiSpManBill", method = RequestMethod.GET)
	public ModelAndView showAddMultiSpManBill(HttpServletRequest request, HttpServletResponse response) {

		spOrderList = new ArrayList<SpCakeOrder>();
		tempSpOrdList = new ArrayList<TempSpOrder>();

		ModelAndView model = null;
		specialCake = new SpecialCake();
		HttpSession session = request.getSession();

		List<ModuleJson> newModuleList = (List<ModuleJson>) session.getAttribute("newModuleList");
		Info view = AccessControll.checkAccess("showAddMultiSpManBill", "showAddMultiSpManBill", "1", "0", "0", "0",
				newModuleList);

		if (view.getError() == false) {

			model = new ModelAndView("accessDenied");

		} else {
			try {
				model = new ModelAndView("mas_sp_ord/multi_man_spOrd");
				RestTemplate restTemplate = new RestTemplate();
				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

				allFrIdNameList = new AllFrIdNameList();
				try {

					allFrIdNameList = restTemplate.getForObject(Constants.url + "getAllFrIdName",
							AllFrIdNameList.class);

				} catch (Exception e) {
					System.out.println("Exception in getAllFrIdName" + e.getMessage());
					e.printStackTrace();

				}

				SpCakeResponse spCakeResponse = restTemplate
						.getForObject(Constants.url + "showSpecialCakeListOrderBySpCode", SpCakeResponse.class);
				System.out.println("SpCake Controller SpCakeList Response " + spCakeResponse.toString());
				List<com.ats.adminpanel.model.SpecialCake> specialCakeList = new ArrayList<com.ats.adminpanel.model.SpecialCake>();

				specialCakeList = spCakeResponse.getSpecialCake();

				model.addObject("specialCakeList", specialCakeList);// 1 object to be used in jsp 2 actual object
				model.addObject("specialCake", specialCake);// 1 object to be used in jsp 2 actual object
				model.addObject("frId", 0);// 1 object to be used in jsp 2 actual object

				flavourList = restTemplate.getForObject(Constants.url + "/showFlavourList", FlavourList.class);

				AllspMessageResponse allspMessageList = restTemplate.getForObject(Constants.url + "getAllSpMessage",
						AllspMessageResponse.class);

				spMessageList = allspMessageList.getSpMessage();
				model.addObject("eventList", spMessageList);

				// System.out.println("Special Cake List:" + specialCakeList.toString());
				model.addObject("spNo", "0");
				String pattern = "dd-MM-yyyy";
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

				String date = simpleDateFormat.format(new Date());
				model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());
				model.addObject("billBy", 1);
				model.addObject("date", date);

				System.err
						.println("allFrIdNameList.getFrIdNamesList() " + allFrIdNameList.getFrIdNamesList().toString());
				AllMenuResponse allMenuResponse = restTemplate.getForObject(Constants.url + "getAllMenu",
						AllMenuResponse.class);

				menuList = allMenuResponse.getMenuConfigurationPage();
				model.addObject("unSelectedMenuList", menuList);
				ObjectMapper objMapper=new ObjectMapper();
				model.addObject("menuJson", objMapper.writeValueAsString(allMenuResponse.getMenuConfigurationPage()));

			} catch (Exception e) {

				System.err.println("Exce in showManualBill" + e.getMessage());
				e.printStackTrace();

			}
			model.addObject("billNo", billNo);
			billNo = 0;
		}

		return model;
	}

	@RequestMapping(value = "/getSpCakeForMultiSpBill", method = RequestMethod.POST)
	public @ResponseBody Object getSpCakeForMultiSpBill(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView model = null;

		TempData tempData = new TempData();
		try {
			specialCake = new SpecialCake();
			model = new ModelAndView("manualBill/add_man_bill");
			RestTemplate restTemplate = new RestTemplate();

			String spCode = request.getParameter("sp_cake_id");
			List<Float> weightList = new ArrayList<>();

			int frId = Integer.parseInt(request.getParameter("fr_id"));
			int billBy = Integer.parseInt(request.getParameter("sel_rate"));
			System.err.println("Bill By " + billBy);

			model.addObject("frId", frId);
			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			map.add("spCode", spCode);
			try {
				SearchSpCakeResponse searchSpCakeResponse = restTemplate
						.postForObject(Constants.url + "/searchSpecialCake", map, SearchSpCakeResponse.class);
				ErrorMessage errorMessage = searchSpCakeResponse.getErrorMessage();
				System.err.println("Selected Special Cake 111111111111" + searchSpCakeResponse.toString());

				specialCake = searchSpCakeResponse.getSpecialCake();
				tempData.setSpecialCake(specialCake);
				model.addObject("specialCake", specialCake);
				int cutSec = searchSpCakeResponse.getSpCakeSup().getCutSection();
				model.addObject("cutSec", cutSec);
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DATE, Integer.parseInt(specialCake.getSpBookb4()));
				model.addObject("convDate", new SimpleDateFormat("dd-MM-yyyy").format(c.getTime()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			model.addObject("unSelectedFrList", allFrIdNameList.getFrIdNamesList());

			SpCakeResponse spCakeResponse = restTemplate
					.getForObject(Constants.url + "showSpecialCakeListOrderBySpCode", SpCakeResponse.class);
			// System.out.println("SpCake Controller SpCakeList Response 0000000" +
			// spCakeResponse.toString());

			List<com.ats.adminpanel.model.SpecialCake> specialCakeList = new ArrayList<com.ats.adminpanel.model.SpecialCake>();

			specialCakeList = spCakeResponse.getSpecialCake();

			model.addObject("specialCakeList", specialCakeList);

			FranchiseeList frDetails = restTemplate.getForObject(Constants.url + "getFranchisee?frId={frId}",
					FranchiseeList.class, frId);

			float sprRate;
			float spBackendRate;

			/*
			 * float minWt = Float.valueOf(specialCake.getSpMinwt());
			 * 
			 * float maxWt = Float.valueOf(specialCake.getSpMaxwt());
			 * 
			 * weightList.add(minWt); float currentWt = minWt; while (currentWt < maxWt) {
			 * currentWt = currentWt + specialCake.getSpRate2();// spr rate 2 means weight
			 * increment by weightList.add(currentWt); } while (currentWt < 2) { currentWt =
			 * currentWt + 0.5f;//spr rate 2 means weight increment by if(currentWt<=2) {
			 * weightList.add(currentWt); }
			 * 
			 * } float max=2;
			 */

			// float minWt = Float.valueOf(specialCake.getSpMinwt());
			float minWt = 1;
			float maxWt = 15;
			// float maxWt = Float.valueOf(specialCake.getSpMaxwt());

			weightList.add(minWt);
			float currentWt = minWt;
			/*
			 * while (currentWt < maxWt) { currentWt = currentWt +
			 * specialCake.getSpRate2();// spr rate 2 means weight increment by
			 * weightList.add(currentWt); }
			 */
			while (currentWt < 15) {
				currentWt = currentWt + 0.5f;// spr rate 2 means weight increment by
				if (currentWt <= 15) {
					weightList.add(currentWt);
				}

			}
			float max = 15;
			while (max < maxWt) {
				max = max + specialCake.getSpRate2();
				weightList.add(max);
			}
			System.out.println("Weight List for SP Cake: " + weightList.toString());

			if (frDetails.getFrRateCat() == 1) {
				System.err.println("Rate cat 1");

				if (billBy == 0) { // means calc by mrp
					sprRate = specialCake.getMrpRate1();
					spBackendRate = specialCake.getMrpRate1();
				} else {// means calc by rate

					sprRate = specialCake.getSpRate1();
					spBackendRate = specialCake.getSpRate1();
				}

			} else {
				System.err.println("Rate cat no");

				if (billBy == 0) {

					sprRate = specialCake.getMrpRate3();
					spBackendRate = specialCake.getMrpRate3();
				} else {

					sprRate = specialCake.getSpRate3();
					spBackendRate = specialCake.getSpRate3();
				}

			}
			// --------------------------New For
			// Flavors----------------------------------------
			List<Flavour> filterFlavoursList = new ArrayList<>();
			map = new LinkedMultiValueMap<String, Object>();
			map.add("spId", specialCake.getSpId());
			flavourList = restTemplate.postForObject(Constants.url + "/showFlavourListBySpId", map, FlavourList.class);
			List<Flavour> flavoursArrayList = flavourList.getFlavour();

			// for (int i = 0; i < flavoursArrayList.size(); i++) {
			// filterFlavoursList.add(flavoursArrayList.get(i));
			// }
			List<String> list = Arrays.asList(specialCake.getErpLinkcode().split(","));
			System.err.println("list" + specialCake.getErpLinkcode());
			for (Flavour flavour : flavoursArrayList) {

				if (list.contains("" + flavour.getSpfId())) {
					flavour.setSpfAdonRate(0.0);

				}

				filterFlavoursList.add(flavour);

			}

			// ------------------------------------------------------------------
			System.err.println("sprRate " + sprRate);

			tempData.setBillBy(billBy);
			tempData.setFilterFlavoursList(filterFlavoursList);
			tempData.setSpBackendRate(spBackendRate);

			tempData.setSprRate(sprRate);
			tempData.setWeightList(weightList);

			model.addObject("sprRate", sprRate);
			model.addObject("spBackendRate", spBackendRate);
			model.addObject("filterFlavoursList", filterFlavoursList);
			model.addObject("weightList", weightList);
			model.addObject("billBy", billBy);
			String spNo = "";
			try {
				spNo = getSpNo(frId);
			} catch (Exception e) {
				spNo = "";
				e.printStackTrace();
			}

			tempData.setSpNo(spNo);

			/* Sachin comment 27-08-2020
			 * List<Menu> allMenuList = restTemplate.getForObject(Constants.url +
			 * "getAllMenuList", List.class); model.addObject("frMenuList", allMenuList);
			 * System.err.println("frMenuList" + allMenuList.toString()); //
			 * System.out.println("Special Cake List:" + specialCakeList.toString());
			 * model.addObject("spNo", spNo); AllspMessageResponse allspMessageList =
			 * restTemplate.getForObject(Constants.url + "getAllSpMessage",
			 * AllspMessageResponse.class);
			 * 
			 * spMessageList = allspMessageList.getSpMessage(); model.addObject("eventList",
			 * spMessageList); model.addObject("frId", frId); model.addObject("billBy",
			 * billBy); String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new
			 * Date()); model.addObject("currentDate", currentDate); model.addObject("date",
			 * currentDate);
			 */

		} catch (Exception e) {
			System.err.println("Exce in getSpCakeForManBill" + e.getMessage());
			e.printStackTrace();
		}
		return tempData;

	}

	List<SpCakeOrder> spOrderList = new ArrayList<SpCakeOrder>();
	List<TempSpOrder> tempSpOrdList = new ArrayList<TempSpOrder>();

	@RequestMapping(value = "/addSpOrderInList", method = RequestMethod.POST)
	public @ResponseBody Object addSpOrderInList(HttpServletRequest request, HttpServletResponse response) {
		SpCakeOrder spCakeOrder = null;
		try {

			RestTemplate restTemplate = new RestTemplate();

			int frId = Integer.parseInt(request.getParameter("fr_id"));
			int menuId = Integer.parseInt(request.getParameter("selectMenu"));
			String spCode = request.getParameter("sp_cake_id");
			String flav_name = request.getParameter("flav_name");

			String spProdDate = request.getParameter("spProdDate");
String inputDeliveryDate=request.getParameter("datepicker");
			Date prodDate = Main.stringToDate(spProdDate);
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Date currentDate = new Date();

			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);

			// Current Date
			Date orderDate = c.getTime();
//java.sql.Date delDate=(java.sql.Date) dateFormat.parse(inputDeliveryDate);
			System.err.println("inputDeliveryDate " +inputDeliveryDate);
			
SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
java.util.Date date = sdf1.parse(inputDeliveryDate);
java.sql.Date delDate = new java.sql.Date(date.getTime()); 
System.err.println("del sql dt " +delDate);
			java.sql.Date sqlProdDate = new java.sql.Date(prodDate.getTime());

			FranchiseeList frDetails = restTemplate.getForObject(Constants.url + "getFranchisee?frId={frId}",
					FranchiseeList.class, frId);

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

			map = new LinkedMultiValueMap<String, Object>();
			map.add("spCode", spCode);

			SearchSpCakeResponse searchSpCakeResponse = restTemplate.postForObject(Constants.url + "/searchSpecialCake",
					map, SearchSpCakeResponse.class);
			ErrorMessage errorMessage = searchSpCakeResponse.getErrorMessage();
			System.err.println("Selected Special Cake 111111111111" + searchSpCakeResponse.toString());

			specialCake = searchSpCakeResponse.getSpecialCake();

			int spfId = Integer.parseInt(request.getParameter("spFlavour"));
			FlavourConf flavourConf = new FlavourConf();
			float spWeight = Float.parseFloat(request.getParameter("spwt"));
			map = new LinkedMultiValueMap<String, Object>();
			map.add("spId", specialCake.getSpId());
			map.add("spfId", spfId);
			flavourConf = restTemplate.postForObject(Constants.url + "getFlConfByIds", map, FlavourConf.class);
			System.err.println("flavour is " + flavourConf.toString());

			float spBackendRate = spWeight * flavourConf.getRate();
			int rateType = Integer.parseInt(request.getParameter("sel_rate"));
			float spGrandTotal = spWeight * flavourConf.getMrp();

			spCakeOrder = new SpCakeOrder();
			String na = "-";
			spCakeOrder.setSpId(specialCake.getSpId());
			spCakeOrder.setCustEmail(na);
			spCakeOrder.setCustGstin(na);
			spCakeOrder.setDisc(0);
			spCakeOrder.setExInt1(0);
			spCakeOrder.setExInt2(0);
			spCakeOrder.setExtraCharges(0);
			spCakeOrder.setExVar1(na);
			spCakeOrder.setExVar2(na);

			spCakeOrder.setIsAllocated(0);
			spCakeOrder.setIsBillGenerated(0);
			spCakeOrder.setIsSlotUsed(0);

			spCakeOrder.setOrderPhoto(na);
			spCakeOrder.setOrderPhoto2(na);

			// Sachin 27-8-2020

			float wt = spWeight;// sac1

			float flavourAdonRate = 0;// data.spfAdonRate;

			float tax3 = (float) specialCake.getSpTax3();
			float tax1 = (float) specialCake.getSpTax1();
			float tax2 = (float) specialCake.getSpTax2();
			float sp_ex_charges = 0;
			float sp_disc = 0;

			float price = flavourConf.getMrp();

			float totalFlavourAddonRate = wt * flavourAdonRate;
			float billBy = 1;

			if (billBy == 1) {
				totalFlavourAddonRate = (float) (wt * (flavourAdonRate * 0.8));
			}
			float totalCakeRate = wt * price;
			float totalAmount = totalCakeRate + totalFlavourAddonRate + sp_ex_charges;
			float disc_amt = (totalAmount * sp_disc) / 100;
			totalAmount = totalAmount - disc_amt;

			float mrpBaseRate = (totalAmount * 100) / (tax3 + 100);
			float gstInRs = 0;
			float taxPerPerc1 = 0;
			float taxPerPerc2 = 0;
			float tax1Amt = 0;
			float tax2Amt = 0;
			if (tax3 == 0) {
				gstInRs = 0;
			} else {
				gstInRs = (mrpBaseRate * tax3) / 100;

				if (tax1 == 0) {
					taxPerPerc1 = 0;
				} else {
					taxPerPerc1 = (tax1 * 100) / tax3;
					tax1Amt = (gstInRs * taxPerPerc1) / 100;

				}
				if (tax2 == 0) {
					taxPerPerc2 = 0;
				} else {
					taxPerPerc2 = (tax2 * 100) / tax3;
					tax2Amt = (gstInRs * taxPerPerc2) / 100;
				}
			}

			float grandTotal = totalCakeRate + totalFlavourAddonRate;

			float sp_add_rate = totalFlavourAddonRate; // sac2

			// document.getElementById("sp_sub_total").setAttribute('value',totalAmount);
			float sp_sub_total = totalAmount; // Sac3
			// document.getElementById("sp_grand").setAttribute('value',totalAmount);
			float sp_grand = totalAmount;

			// document.getElementById("total_amt").setAttribute('value',totalAmount);
			float total_amt = totalAmount;

			// document.getElementById("t1amt").setAttribute('value',tax1Amt.toFixed(2));
			float t1amt = tax1Amt;
			// document.getElementById("t2amt").setAttribute('value',tax2Amt.toFixed(2));
			float t2amt = tax2Amt;
			// document.getElementById("gst_rs").setAttribute('value',gstInRs.toFixed(2));
			float gst_rs = gstInRs;

			// document.getElementById("m_gst_amt").setAttribute('value',mrpBaseRate.toFixed(2));
			float m_gst_amt = mrpBaseRate;

			float floatBackEndRate = ((spBackendRate + flavourAdonRate) * spWeight) + 0;

			spCakeOrder.setRmAmount(sp_sub_total); // Ok

			spCakeOrder.setSlipNo("0");// Ok
			spCakeOrder.setSpAdvance(0);// Ok
			spCakeOrder.setSpBookedForName(na); // ok

			spCakeOrder.setSpCustMobNo(na);// Ok
			spCakeOrder.setSpCustName(na); // Ok

			spCakeOrder.setSpEvents(na);// Ok
			spCakeOrder.setSpInstructions(na);// Ok
															// insert

			spCakeOrder.setFrCode(frDetails.getFrCode());
			spCakeOrder.setFrId(frId);

			spCakeOrder.setItemId(spCode);
			spCakeOrder.setMenuId(menuId);

			spCakeOrder.setSpBackendRate(floatBackEndRate);// Ok

			// spCakeOrder.setSpBookForDob(new java.sql.Date("2019","01","01"));
			spCakeOrder.setSpBookForDob(sqlProdDate);

			spCakeOrder.setSpCustDob(sqlProdDate);

			

			spCakeOrder.setSpFlavourId(spfId);
			spCakeOrder.setSpMaxWeight(Float.parseFloat(specialCake.getSpMaxwt()));
			spCakeOrder.setSpMinWeight(Float.parseFloat(specialCake.getSpMinwt()));

			spCakeOrder.setSpOrderNo(0); // PK

			spCakeOrder.setSpPrice(totalCakeRate);
			spCakeOrder.setSpProdTime(Integer.parseInt(specialCake.getSpBookb4()));
			spCakeOrder.setSpSelectedWeight(spWeight);
			spCakeOrder.setSpSubTotal(sp_sub_total);
			spCakeOrder.setSpTotalAddRate(sp_add_rate);
			spCakeOrder.setSpType(1); // Ok

			spCakeOrder.setTax2Amt(roundUp(tax2Amt));
			spCakeOrder.setTax2((float) specialCake.getSpTax2());
			spCakeOrder.setTax1Amt(roundUp(tax1Amt));
			spCakeOrder.setTax1((float) specialCake.getSpTax1());
			spCakeOrder.setSpGrandTotal(sp_grand);

			//Sp Table Dates
			
			spCakeOrder.setSpDeliveryDate(delDate);
			
			java.sql.Date estDelDate=null;
			Date curDate = c.getTime();
			SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
			String strCurDate =sdf.format(curDate);
			String  strEstDelDate=incrementDate(strCurDate,  Integer.parseInt(specialCake.getSpBookb4()));
			sdf=new SimpleDateFormat("yyyy-MM-dd");
	        java.sql.Date todaysDate = new java.sql.Date(new java.util.Date().getTime());

			//java.sql.Date sqlEstDelDate=(java.sql.Date) sdf.parse(strEstDelDate);
			java.sql.Date sqlEstDelDate=addDays(todaysDate, Integer.parseInt(specialCake.getSpBookb4()));
			
			
			
			spCakeOrder.setSpEstDeliDate(sqlEstDelDate);
			
			spCakeOrder.setOrderDate(dateFormat.format(orderDate));
			Menu menu=null;int isSameDay=0;
			for(int i=0;i<menuList.size();i++) {
				if(menuList.get(i).getMenuId()==menuId) {
					menu =menuList.get(i);
					break;
				}else {
					
				}
			}
			if(menu.getIsSameDayApplicable().equals("1")) {
				isSameDay=1;
			}else {
				
			}
			
			spCakeOrder.setSpProdDate(sqlProdDate);
			if(isSameDay==0) {
				
				//String strDelDate =sdf.format(sqlEstDelDate);
				//String  strProdDate=incrementDate(strDelDate,  -Integer.parseInt(specialCake.getSpBookb4()));
				sdf=new SimpleDateFormat("yyyy-MM-dd");
			//	java.sql.Date sqlProdDate1=(java.sql.Date) sdf.parse(strProdDate);
				java.sql.Date sqlProdDate1=addDays(sqlEstDelDate,-Integer.parseInt(specialCake.getSpBookb4()));
				
				spCakeOrder.setSpProdDate(sqlProdDate1);
			}
			
			
			System.err.println("sp data  " + spCakeOrder.toString());
			// Sachin
			TempData tempData = new TempData();

			TempSpOrder tempSpOrd = new TempSpOrder();

			tempSpOrd.setFlavour(flav_name);
			tempSpOrd.setIndex(spOrderList.size());
			tempSpOrd.setSpAmt(spBackendRate);
			tempSpOrd.setSpCode(specialCake.getSpCode());
			tempSpOrd.setSpId(specialCake.getSpId());
			tempSpOrd.setSpName(specialCake.getSpName());
			tempSpOrd.setSpWeight(spWeight);
			tempSpOrdList.add(tempSpOrd);

		} catch (Exception e) {
e.printStackTrace();
		}

		spOrderList.add(spCakeOrder);

		return tempSpOrdList;

	}
	public String incrementDate(String date, int day) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(sdf.parse(date));

		} catch (ParseException e) {
			System.out.println("Exception while incrementing date " + e.getMessage());
			e.printStackTrace();
		}
		c.add(Calendar.DATE, day); // number of days to add
		date = sdf.format(c.getTime());

		return date;

	}
	
	public static java.sql.Date addDays(java.sql.Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return new java.sql.Date(c.getTimeInMillis());
    }
	
	public String getSpNo(int frId) {
		String spNoNewStr = "";
		try {

			MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
			RestTemplate restTemplate = new RestTemplate();
			map.add("frId", frId);
			FrSetting frSetting = restTemplate.postForObject(Constants.url + "getFrSettingValue", map, FrSetting.class);

			int spNo = frSetting.getSpNo();

			int length = String.valueOf(spNo).length();
			length = 5 - length;
			StringBuilder spNoNew = new StringBuilder(frSetting.getFrCode() + "-");

			for (int i = 0; i < length; i++) {
				String j = "0";
				spNoNew.append(j);
			}
			spNoNew.append(String.valueOf(spNo));
			spNoNewStr = "" + spNoNew;

		} catch (Exception e) {

		}

		return spNoNewStr;

	}

	public String getInvoiceNo(int frId, String frCode) {

		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
		RestTemplate restTemplate = new RestTemplate();

		map.add("frId", frId);
		FrSetting frSetting = restTemplate.postForObject(Constants.url + "getFrSettingValue", map, FrSetting.class);

		int settingValue = frSetting.getSellBillNo();

		System.out.println("Setting Value Received " + settingValue);
		int year = Year.now().getValue();
		String curStrYear = String.valueOf(year);
		curStrYear = curStrYear.substring(2);

		int preMarchYear = Year.now().getValue() - 1;
		String preMarchStrYear = String.valueOf(preMarchYear);
		preMarchStrYear = preMarchStrYear.substring(2);

		System.out.println("Pre MArch year ===" + preMarchStrYear);

		int nextYear = Year.now().getValue() + 1;
		String nextStrYear = String.valueOf(nextYear);
		nextStrYear = nextStrYear.substring(2);

		System.out.println("Next  year ===" + nextStrYear);

		int postAprilYear = nextYear + 1;
		String postAprilStrYear = String.valueOf(postAprilYear);
		postAprilStrYear = postAprilStrYear.substring(2);

		System.out.println("Post April   year ===" + postAprilStrYear);

		java.util.Date date = new Date();
		Calendar cale = Calendar.getInstance();
		cale.setTime(date);
		int month = cale.get(Calendar.MONTH);

		if (month <= 3) {

			curStrYear = preMarchStrYear + curStrYear;
			System.out.println("Month <= 3::Cur Str Year " + curStrYear);
		} else if (month >= 4) {

			curStrYear = curStrYear + nextStrYear;
			System.out.println("Month >=4::Cur Str Year " + curStrYear);
		}
		int length = String.valueOf(settingValue).length();

		String invoiceNo = null;

		if (length == 1)

			invoiceNo = curStrYear + "-" + "0000" + settingValue;
		if (length == 2)

			invoiceNo = curStrYear + "-" + "000" + settingValue;

		if (length == 3)

			invoiceNo = curStrYear + "-" + "00" + settingValue;

		if (length == 4)

			invoiceNo = curStrYear + "-" + "0" + settingValue;

		invoiceNo = frCode + invoiceNo;
		System.out.println("*** settingValue= " + settingValue);
		return invoiceNo;

	}

	public static float roundUp(float d) {
		return BigDecimal.valueOf(d).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	// submitMultiManSPOrder Sachin 27-08-2020

	@RequestMapping(value = "/submitMultiManSPOrder", method = RequestMethod.POST)
	public String submitMultiManSPOrder(HttpServletRequest request, HttpServletResponse response) {

		RestTemplate restTemplate = new RestTemplate();
		MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();

		try {

			for (int i = 0; i < spOrderList.size(); i++) {

				SpCakeOrderRes spCakeOrderRes = new SpCakeOrderRes();

				HttpHeaders httpHeaders = new HttpHeaders();
				httpHeaders.set("Content-Type", "application/json");
				ObjectMapper mapper = new ObjectMapper();
				
				spOrderList.get(i).setSpDeliveryPlace(getSpNo(spOrderList.get(i).getFrId())); // Ok but to be calculated ON insert
				spOrderList.get(i).setSpBookForMobNo(getInvoiceNo(spOrderList.get(i).getFrId(), spOrderList.get(i).getFrCode()));// Ok but to be calculated On
										
				
				String jsonInString = mapper.writeValueAsString(spOrderList.get(i));
				System.err.println("spCakeOrder sachin for loop " + spOrderList.get(i).toString());
				HttpEntity<String> httpEntity = new HttpEntity<String>(jsonInString.toString(), httpHeaders);
				spCakeOrderRes = restTemplate.postForObject(Constants.url + "/placeSpCakeOrder", httpEntity,
						SpCakeOrderRes.class);

				System.out.println("ORDER PLACED Response " + spCakeOrderRes.toString());

				SpCakeOrder spCake = spCakeOrderRes.getSpCakeOrder();

				if (spCakeOrderRes.getErrorMessage().isError() != true) {

					// System.out.println("ORDER PLACED " + spCakeOrderRes.toString());
					map = new LinkedMultiValueMap<String, Object>();

					map.add("frId", spCake.getFrId());
					FrSetting frSetting = restTemplate.postForObject(Constants.url + "getFrSettingValue", map,
							FrSetting.class);

					int sellBillNo = frSetting.getSellBillNo();

					sellBillNo = sellBillNo + 1;

					map = new LinkedMultiValueMap<String, Object>();

					map.add("frId", spCake.getFrId());
					map.add("sellBillNo", sellBillNo);

					Info info = restTemplate.postForObject(Constants.url + "updateFrSettingBillNo", map, Info.class);

					map = new LinkedMultiValueMap<String, Object>();
					map.add("frId", spCake.getFrId());

					Info updateFrSettingGrnGvnNo = restTemplate.postForObject(Constants.url + "updateFrSettingSpNo",
							map, Info.class);

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println("hello In ");
		return "redirect:/spCakeOrders";

	}
}
